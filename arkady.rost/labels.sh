#!/bin/sh
(cd /home/ark/Projects/Study/MachineLearning && zcat data/mnist/train-labels-idx1-ubyte.gz | scala arkady.rost/target/scala-2.9.2/machine_learning_2.9.2-0.1.jar)
