# -*- coding: utf-8 -*-

__author__ = 'eugene'

import numpy

from Reader.reader import MNISTReader


class Perceptron():
    def __init__(self, maxPasses=10, reader=MNISTReader()):
        self.maxPasses = maxPasses
        self.reader = reader
        self.theta = numpy.zeros((10, reader.row_len * reader.col_len))
        self.curPass = 0

    def improve(self, image, label):
        x = []
        for i in range(len(image)):
            x += list(image[i])
        scalar = [numpy.dot(x, self.theta[i]) for i in range(10)]
        argmax = scalar.index(max(scalar))
        if not argmax == label:
            #self.theta[argmax] -= x
            self.theta[label] += x

    def nextPass(self):
        if self.curPass == self.maxPasses:
            print "Toooooo much steps!"
        else:
            self.curPass += 1
            print 'Step %d of %d' % (self.curPass, self.maxPasses)
            for imn in range(self.reader.count):
                if imn % 10000 == 0:
                    print '%d of %d images on step %d' % (imn, self.reader.count, self.curPass)
                self.improve(self.reader.images[imn], self.reader.labels[imn])
            print 'Step %d of %d ended' % (self.curPass, self.maxPasses)

    def train(self):
        for p in range(self.maxPasses):
            print 'Step %d of %d' % (p + 1, self.maxPasses)
            for imn in range(self.reader.count):
                if imn % 1000 == 0:
                    print '%d of %d images on step %d' % (imn, self.reader.count, p + 1)
                self.improve(self.reader.images[imn], self.reader.labels[imn])
        print 'Training done'

    def getResult(self, inx):
        if isinstance(inx, numpy.ndarray):
            x = []
            for i in range(self.reader.row_len):
                x += list(inx[i])
        else:
            x = list(inx)
        scalar = [numpy.dot(x, self.theta[i]) for i in range(10)]
        return scalar.index(max(scalar))
