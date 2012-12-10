#! /usr/bin/python

from numpy import *
import struct

ROW = 28
COL = 28

def readLabels(path):
    with open(path) as f:
        label_str = f.read()
    f.close()
    magic_number = struct.unpack("!l", label_str[0:4])[0];
    label_str = label_str[4:]
    amount = struct.unpack("!l", label_str[0:4])[0]
    label_str = label_str[4:]
    label = [0]*amount
    for i in range(amount):
        label[i] = struct.unpack("!B", label_str[i:i+1])[0]
    return (label, amount)

def readImages(path):
    with open(path) as f:
        images_str = f.read()
    f.close()
    magic_number = struct.unpack("!l", images_str[0:4])[0];
    images_str = images_str[4:]
    amount = struct.unpack("!l", images_str[0:4])[0]
    images_str = images_str[4:]
    row = struct.unpack("!l", images_str[0:4])[0]
    images_str = images_str[4:]
    col = struct.unpack("!l", images_str[0:4])[0]
    images_str = images_str[4:]
    images = ones( (row*col+1, amount), dtype = float64)
    for i in range(amount):
        prefix = images_str[i*row*col:(i+1)*row*col]
        for j in range(row*col):
            val = struct.unpack("!B", prefix[j : j + 1])[0]
            images[j,i] = (val - 128.0)/128.0
    return (images, amount, row, col) 

if __name__ == "__main__":
    teta = zeros((ROW*COL + 1, 10), dtype = float64)
    with open('teta.out') as f:
        data = f.read()
    f.close()
    count = 0
    for fl in data.split(' '):
        if '.' in fl:
            val = float(fl)
            teta[count / 10, count % 10] = val
            count = count + 1
    label, amount = readLabels('../../data/mnist/t10k-labels.idx1-ubyte')
    images, amount, row, col = readImages('../../data/mnist/t10k-images.idx3-ubyte')
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
        print i

    print succ / total
