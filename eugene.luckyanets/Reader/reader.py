__author__ = 'eugene'

import struct
import gzip
import numpy

from exceptions import CountMismatchReaderException


class Reader():
    path = ''
    prefix = ''
    count = 0
    labels = []
    images = []

    def __init__(self, path='../../data/mnist/', prefix='train'):
        self.path = path
        self.prefix = prefix
        labelsResult = Reader.readLabels(path, prefix)
        imagesResult = Reader.readImages(path, prefix)
        if not labelsResult[1] == imagesResult[1]:
            raise CountMismatchReaderException(labelsResult[1], imagesResult[1])
        self.count = labelsResult[1]
        self.labels = labelsResult[2]
        self.images = imagesResult[4]

    @classmethod
    def readLabels(cls, path='../../data/mnist/', prefix='train'):
        filename = path + prefix + '-labels.idx1-ubyte.gz'
        f = gzip.open(filename)
        content = f.read()
        f.close()
        [magic_number, count] = struct.unpack('!ll', content[0:8])
        labels = []
        for i in range(count):
            labels.append(struct.unpack('!B', content[i + 8])[0])
        print 'Labels read'
        return [magic_number, count, labels]

    @classmethod
    def readImages(cls, path='../../data/mnist/', prefix='train'):
        def normalize(a):
            return (float(a) - 128.0) / 128.0

        filename = path + prefix + '-images-idx3-ubyte.gz'
        f = gzip.open(filename)
        content = f.read()
        f.close()
        [magic_number, count, row_len, col_len] = struct.unpack('!llll', content[0:16])
        images = []
        for i in range(count):
            images.append(numpy.zeros((row_len, col_len)))
            for row in range(row_len):
                for col in range(col_len):
                    images[i][row][col] = normalize(
                        struct.unpack('!B', content[16 + i * row_len * col_len + row * row_len + col])[0])
        print 'Images read'
        return [magic_number, count, row_len, col_len, images]