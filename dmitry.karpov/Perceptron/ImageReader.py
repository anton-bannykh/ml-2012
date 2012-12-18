#! /usr/bin/python

from numpy import *
import struct

def readLabels(path):
    with open(path) as f:
        label_str = f.read()
    f.close()
    [magic_number, amount] = struct.unpack("!ll", label_str[0:8]);
    label = zeros(amount, dtype = int32)
    for i in range(amount):
        label[i] = struct.unpack("!B", label_str[8+i:9+i])[0]
    print "Labels have been read"
    return (label, amount)

def readImages(path):
    with open(path) as f:
        images_str = f.read()
    f.close()
    [magic_number, amount, row, col]  = struct.unpack("!llll", images_str[0:4*4])
    images = ones( (row*col+1, amount), dtype = float64)
    for i in range(amount):
        for j in range(row*col):
            val = struct.unpack("!B", images_str[16+j + row*col*i : row*col*i + j + 17])[0]
            images[j,i] = (float(val) - 128.0)/128.0
    print "Images have been read"
    return (images, amount, row, col) 
