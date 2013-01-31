# -*- coding: utf-8 -*-

__author__ = 'eugene'

import struct
import gzip
import numpy
import os

from exceptions import CountMismatchReaderException


ROOT_PATH = os.path.dirname(os.path.realpath(__file__))


class MNISTReader():
    path = ''
    prefix = ''
    count = 0
    labels = []
    images = []
    row_len = 0
    col_len = 0

    def __init__(self, path='../../data/mnist/', prefix='train'):
        self.path = path
        self.prefix = prefix
        labelsResult = MNISTReader.readLabels(path, prefix)
        imagesResult = MNISTReader.readImages(path, prefix)
        if not labelsResult[1] == imagesResult[1]:
            raise CountMismatchReaderException(labelsResult[1], imagesResult[1])
        self.count = labelsResult[1]
        self.labels = labelsResult[2]
        self.row_len = imagesResult[2]
        self.col_len = imagesResult[3]
        self.images = imagesResult[4]
        self.info = 'path: %s, prefix: %s, count: %d, row length: %d, column length: %d' % \
            (os.path.normpath(path), prefix, self.count, self.row_len, self.col_len)
        print 'Data read. Info: ' + self.info

    @classmethod
    def readLabels(cls, path='../../data/mnist/', prefix='train'):
        filename = os.path.join(ROOT_PATH, path + prefix + '-labels-idx1-ubyte.gz')
        filename = os.path.normpath(filename)
        f = gzip.open(filename)
        content = f.read()
        f.close()
        [magic_number, count] = struct.unpack('!ll', content[0:8])
        labels1 = []
        for i in range(count):
            labels1.append(struct.unpack('!B', content[i + 8])[0])
        labels = list(struct.unpack('!' + 'B' * count, content[8:]))
        for i in range(count):
            if labels1[i] != labels[i]:
                print i
        print 'Labels read'
        return [magic_number, count, labels]

    @classmethod
    def readImages(cls, path='../../data/mnist/', prefix='train'):
        def normalize(a):
            return (float(a) - 128.0) / 128.0

        filename = os.path.join(ROOT_PATH, path + prefix + '-images-idx3-ubyte.gz')
        filename = os.path.normpath(filename)
        f = gzip.open(filename)
        content = f.read()
        f.close()
        [magic_number, count, row_len, col_len] = struct.unpack('!llll', content[0:16])
        images = []
        for i in range(count):
            images.append(numpy.zeros((row_len, col_len), dtype=float))
            unpacked = struct.unpack('!' + 'B' * row_len * col_len,
                                     content[16 + i * row_len * col_len: 16 + (i + 1) * row_len * col_len])
            for row in range(row_len):
                for col in range(col_len):
                    images[i][row][col] = normalize(unpacked[row * row_len + col])
        print 'Images read'
        return [magic_number, count, row_len, col_len, images]