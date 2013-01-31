# -*- coding: utf-8 -*-
from threading import Thread

__author__ = 'eugene'

from perceptron import *

tests = 0
p = 0


def readTests():
    global tests
    tests = MNISTReader(prefix='t10k')


def createPerceptron(maxPasses):
    global p
    p = Perceptron(maxPasses=maxPasses)


def test(maxPasses=1):
    tth = Thread(target=readTests())
    tth.start()
    pth = Thread(target=(lambda: createPerceptron(maxPasses)))
    pth.start()
    tth.join()
    pth.join()
    global tests
    global p
    #tests = MNISTReader(prefix='t10k')
    #p = Perceptron(maxPasses=maxPasses)
    for pa in range(maxPasses):
        p.nextPass()
        good = 0
        for i in range(tests.count):
            if p.getResult(tests.images[i]) == tests.labels[i]:
                good += 1
            if (i + 1) % 1000 == 0:
                print '%d of %d tested, %f of all tested are good' % (i + 1, tests.count, float(good) / (i + 1))
        print 'Finally: %f after %d steps of perceptron' % (float(good) / tests.count, pa + 1)

if __name__ == '__main__':
    test(10)