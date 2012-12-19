#! /usr/bin/python

from numpy import *

def check(images, label, amount, row, col, teta):
    succ = 0.0
    total = 0.0
    for i in range(amount):
        good = -1;        
        for j in range(10):
            sign = dot(teta[:, j].T, images[:, i])
            if sign > 0:
                good = j
        if label[i] == good:
            succ += 1
        total += 1
    print "Error percent " + str(1. - float(succ) / total)
    return (1. - float(succ)/total)
