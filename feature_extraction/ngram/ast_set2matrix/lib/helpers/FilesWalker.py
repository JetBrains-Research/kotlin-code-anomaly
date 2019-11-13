from os import path
import glob


class FilesWalker:
    @staticmethod
    def walk(folder, callback, extension='json'):
        for filename in glob.iglob(folder + '/**/*.' + extension, recursive=True):
            if path.isfile(filename):
                callback(filename)
