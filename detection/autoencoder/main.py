from argparse import ArgumentParser
from pathlib import Path

import torch

import anomalies_search
import autoencoder_model
import data_read


DEFAULT_EPOCHS = 5
DEFAULT_FEATURES_NUMBER = 384
DEFAULT_ENCODING_DIM_PERCENT = 0.5
DEFAULT_VAL_SHARE = 0.2
DEFAULT_STD_DIVS_THRESHOLD = 3


def train_model(args):
    data_set = data_read.TrainValDataSet(args.data_sources, size=args.size_limit, batch_size=args.batch_size)
    encoding_dim = int(args.features_number * args.encoding_dim_percent)
    if args.epochs_passed:
        model = torch.load(f'{args.save_pref}{args.epochs_passed}.pth')
    else:
        model = autoencoder_model.AutoencoderModel(args.features_number, encoding_dim)
    done_epochs = args.epochs_passed if args.epochs_passed else 0
    save_dir = Path(args.save_pref).parent
    save_dir.mkdir(parents=True, exist_ok=True)
    autoencoder_model.train(model, data_set, args.n_epochs, args.save_pref, done_epochs, args.lr)


def find_anomalies(args):
    # Data sources are pairs of a data path and a names path
    data_sources = list(zip(args.data_sources[0::2], args.data_sources[1::2]))
    model = torch.load(args.model_load_path)
    anomalies_search.search_anomalies(model, data_sources, args.stds_threshold, args.save_path, args.batch_size)


if __name__ == '__main__':
    argument_parser = ArgumentParser()
    subparsers = argument_parser.add_subparsers()
    
    train_parser = subparsers.add_parser('train')
    train_parser.add_argument('data_sources', nargs='*', type=Path)
    train_parser.add_argument('--n_epochs', type=int, default=DEFAULT_EPOCHS)
    train_parser.add_argument('--encoding_dim_percent', type=float, default=DEFAULT_ENCODING_DIM_PERCENT)
    train_parser.add_argument('--val_share', type=float, default=DEFAULT_VAL_SHARE)
    train_parser.add_argument('--size_limit', type=int, default=None)
    train_parser.add_argument('--features_number', type=int, default=DEFAULT_FEATURES_NUMBER)
    train_parser.add_argument('--batch_size', type=int, default=data_read.DEFAULT_BATCH_SIZE)
    train_parser.add_argument('--save_pref')
    train_parser.add_argument('--epochs_passed', type=int)
    train_parser.add_argument('--lr', type=float, default=autoencoder_model.DEFAULT_LEARNING_RATE)
    train_parser.set_defaults(func=train_model)
    
    anomalies_parser = subparsers.add_parser('find_anomalies')
    anomalies_parser.add_argument('data_sources', nargs='*', type=Path)
    anomalies_parser.add_argument('model_load_path')
    anomalies_parser.add_argument('save_path', type=Path)
    anomalies_parser.add_argument('--size_limit', type=int, default=None)
    anomalies_parser.add_argument('--batch_size', type=int, default=data_read.DEFAULT_BATCH_SIZE)
    anomalies_parser.add_argument('--stds_threshold', type=float, default=DEFAULT_STD_DIVS_THRESHOLD)
    anomalies_parser.set_defaults(func=find_anomalies)

    arguments = argument_parser.parse_args()
    arguments.func(arguments)
