# -*- coding: utf-8 -*-
from audioop import maxpp
from numpy import argmax

__author__ = 'eugene'

import numpy

from Reader.reader import MNISTReader


class Precepthron():
    def __init__(self, maxPasses=10, reader=MNISTReader()):
        self.maxPasses = maxPasses
        self.reader = reader
        self.theta = numpy.zeros((10, reader.row_len * reader.col_len))

    def improve(self, image, label):
        x = []
        for i in range(len(image)):
            x += list(image[i])
        scalar = [numpy.dot(x, self.theta[i]) for i in range(10)]
        argmax = scalar.index(max(scalar))
        if not argmax == label:
            self.theta[argmax] -= x
            self.theta[label] += x

    def train(self):
        for p in range(self.maxPasses):
            print 'Step %d of %d' % (p+1, self.maxPasses)
            for imn in range(self.reader.images):
                self.improve(self.reader.images[imn], self.reader.labels[imn])
        print 'Training done'

    def getResult(self, inx):
        if isinstance(inx, numpy.ndarray):
            x = []
            for i in range(len(self.reader.row_len)):
                x += list(inx[i])
        else:
            x = list(inx)
        scalar = [numpy.dot(x, self.theta[i]) for i in range(10)]
        return scalar.index(max(scalar))
