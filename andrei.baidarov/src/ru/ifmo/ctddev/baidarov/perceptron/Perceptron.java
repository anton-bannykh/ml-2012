package ru.ifmo.ctddev.baidarov.perceptron;

import ru.ifmo.ctddev.baidarov.util.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 17:43
 */
public class Perceptron {
    private final int numOfFeatures;
    private final int numOfLabels;
    private final int[][] theta;
    private final Test[] trainingSet;

    public Perceptron(Test[] trainingSet, int numOfLabels) {
        if (trainingSet == null || trainingSet.length == 0)
            throw new IllegalArgumentException("Training set can't be empty!");
        if (numOfLabels <= 0)
            throw new IllegalArgumentException("Number of labels must be positive!");

        this.trainingSet = trainingSet;
        this.numOfLabels = numOfLabels;
        numOfFeatures = trainingSet[0].getImg().length;
        theta = new int[numOfLabels][numOfFeatures];
        learn();
    }

    private int predict(double[] img) {
        if (img.length != numOfFeatures) {
            throw new IllegalArgumentException("Img must have " + numOfFeatures + " feature(s)");
        }

        long bestGuessScore = Long.MIN_VALUE;
        int bestGuess = -1;

        for (int i = 0; i < numOfLabels; i++) {
            int[] thetaI = theta[i];
            long curGuessScore = 0;
            for (int j = 0; j < numOfFeatures; j++) {
                curGuessScore += thetaI[j] * img[j];
            }
            if (bestGuessScore < curGuessScore) {
                bestGuessScore = curGuessScore;
                bestGuess = i;
            }
        }

        return bestGuess;
    }

    private void learn() {
        for (int i = 0; i < 1; i++) {
            int errors = 0;
            for (Test test : trainingSet) {
                if (learn(test.getImg(), test.getLabel())) {
                    errors++;
                }
            }
            System.out.println("Round " + i + ", errors = " + errors);
            if (errors == 0)
                break;
        }
    }

    private boolean learn(double[] img, int label) {
        int prediction = predict(img);
        if (prediction != label) {
            for (int i = 0; i < numOfFeatures; i++) {
                theta[prediction][i] -= img[i];
                theta[label][i] += img[i];
            }
            return true;
        }
        return false;
    }

    public boolean test(Test test) {
        double[] img = test.getImg();
        int label = test.getLabel();
        return label == predict(img);
    }
}
