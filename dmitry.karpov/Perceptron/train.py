#! /usr/bin/python


import sys
import ImageReader
import check
from numpy import *
import os


def fprintMat(fname, x, row, col):
    f = open(fname, 'w')
    for i in range(row):
        s = ''
        for j in range(col):
            s = s + str(x[i, j]) + " "
        s = s + '\n'
        f.write(s)
    f.close
        
if __name__ == "__main__":
    label, amount = ImageReader.readLabels('../../data/mnist/train-labels.idx1-ubyte')
    images, amount, row, col = ImageReader.readImages('../../data/mnist/train-images.idx3-ubyte')
    q_label, q_amount = ImageReader.readLabels('../../data/mnist/t10k-labels.idx1-ubyte')
    q_images, q_amount, row, col = ImageReader.readImages('../../data/mnist/t10k-images.idx3-ubyte')
    it = int(sys.argv[1])
    teta = zeros((row*col + 1, 10), dtype = float64)
    best_it = -1
    min_error = 1.
    if os.path.exists("out"):
        for i in os.listdir("out"):
            fn = "out/" + i
            os.remove(fn)
        os.removedirs("out")
    os.makedirs("out")
    for h in range(it):
        for i in range(amount):
            dig = label[i]
            if dot(teta[:, dig].T, images[:, i]) <= 0:
                teta[:, dig] = teta[:, dig] + images[:, i]
            for d in range(10):
                if (d != dig) and (dot(teta[:, d].T, images[:, i]) > 0):
                     teta[:, d] = teta[:, d] - images[:, i]
        print "At iteration " + str(h)
        curr_error = check.check(q_images, q_label, q_amount, row, col, teta)
        if curr_error < min_error:
            min_error = curr_error
            best_it = h
        fname = "out/teta_" + str(h);
        fprintMat(fname, teta, row*col + 1, 10)
    print "Best iteration is " + str(best_it)
    print "Minimal error is " + str(min_error)
    print "Finished"
