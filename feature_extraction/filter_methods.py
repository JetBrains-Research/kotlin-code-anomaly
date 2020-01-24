from argparse import ArgumentParser


BUFSIZE = 2 ** 20


def filter(args):
	seen_hashes = set()
	with open(args.vectors_path, buffering=BUFSIZE, encoding='utf8') as vectors_file, \
			open(args.names_path, buffering=BUFSIZE, encoding='utf8') as names_file, \
			open(args.out_vectors_path, 'wt', buffering=BUFSIZE, encoding='utf8') as out_vectors_file, \
			open(args.out_names_path, 'wt', buffering=BUFSIZE, encoding='utf8') as out_names_file:
		for vector, name in zip(vectors_file, names_file):
			vector_hash = hash(vector)
			if vector_hash not in seen_hashes and not vector.startswith('nan'):
				out_vectors_file.write(vector)
				out_names_file.write(name)
				seen_hashes.add(vector_hash)


if __name__ == '__main__':
	arg_parser = ArgumentParser()
	arg_parser.add_argument('vectors_path')
	arg_parser.add_argument('names_path')
	arg_parser.add_argument('out_vectors_path')
	arg_parser.add_argument('out_names_path')
	args = arg_parser.parse_args()
	
	filter(args)
