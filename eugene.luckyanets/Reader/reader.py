__author__ = 'eugene'

import struct
import gzip
import numpy


def readLabels(path='../../data/mnist/', prefix='train'):
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


def readImages(path='../../data/mnist/', prefix='train'):
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

def readLabelsAndImages(path='../../data/mnist/', prefix='train'):
    return [readLabels(path, prefix), readImages(path, prefix)]