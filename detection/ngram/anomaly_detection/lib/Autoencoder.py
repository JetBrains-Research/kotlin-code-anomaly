import numpy as np

from tensorflow.keras.layers import Input, Dense
from tensorflow.keras.models import Model

from scipy.spatial import distance


BATCH_SIZE = 10000


class AutoencoderModel:
    def __init__(self, features_number, encoding_dim):
        self.features_number = features_number
        self.encoding_dim = encoding_dim

        input_img = Input(shape=(features_number,))
        encoded = Dense(encoding_dim, activation='relu')(input_img)
        decoded = Dense(features_number, activation='sigmoid')(encoded)

        self.autoencoder = Model(input_img, decoded)

        self.encoder = Model(input_img, encoded)

        encoded_input = Input(shape=(encoding_dim,))
        decoder_layer = self.autoencoder.layers[-1]
        self.decoder = Model(encoded_input, decoder_layer(encoded_input))

    def compile(self, optimizer='adadelta', loss='binary_crossentropy'):
        self.autoencoder.compile(optimizer=optimizer, loss=loss)

    def summary(self):
        self.autoencoder.summary()

    def get_autoencoder(self):
        return self.autoencoder

    def get_decoder(self):
        return self.decoder


class Autoencoder:
    def __init__(self, features_number, encoding_dim, data):
        self.model = AutoencoderModel(features_number=features_number, encoding_dim=encoding_dim)
        self.model.compile()
        self.predicted = None

        (train_data, all_data, features_number) = data
        self.train_data = train_data
        self.all_data = all_data
        self.features_number = features_number

    def print_model_summary(self):
        self.model.summary()

    def fit(self, epochs=5, shuffle=True):
        self.model.get_autoencoder().fit(self.train_data, self.train_data,
                                         epochs=epochs,
                                         shuffle=shuffle,
                                         verbose=2,
                                         batch_size=1024,
                                         validation_data=(self.all_data, self.all_data))

    def predict(self, data=None):
        self.predicted = self.model.get_autoencoder().predict(self.all_data if data is None else data)

    def calc_differences(self, full_differences):
        difference = []
        item_index = 0
        samples_number = len(self.all_data)
        # for item in self.predicted:
            # if full_differences:
                # difference_element = np.divide(np.power(item - self.all_data[item_index], 2), samples_number)
            # else:
                # difference_element = distance.euclidean(item, self.all_data[item_index]) / len(self.all_data[item_index])
            # difference.append(difference_element)
            # item_index += 1
        for batch_start in range(0, samples_number, BATCH_SIZE):
            batch = self.all_data[batch_start:batch_start + BATCH_SIZE]
            self.predict(batch)
            for real_item, predicted_item in zip(batch, self.predicted):
                if full_differences:
                    difference_element = np.divide(np.power(predicted_item - real_item, 2), samples_number)
                else:
                    difference_element = distance.euclidean(predicted_item, real_item) / len(real_item)
                difference.append(difference_element)

        return np.array(difference)
