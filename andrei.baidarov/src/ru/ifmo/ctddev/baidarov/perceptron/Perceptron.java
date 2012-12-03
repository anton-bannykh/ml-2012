package ru.ifmo.ctddev.baidarov.perceptron;

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

    public Perceptron(int numOfFeatures, int numOfLabels) {
        this.numOfFeatures = numOfFeatures;
        this.numOfLabels = numOfLabels;
        theta = new int[numOfLabels][numOfFeatures];
    }

    private int calc(int[] img) {
        if (img.length != numOfFeatures) {
            throw new IllegalArgumentException("Img must have " + numOfFeatures + " feature(s)");
        }

        long max = Long.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < numOfLabels; i++) {
            int[] thetaI = theta[i];
            long resI = 0;
            for (int j = 0; j < numOfFeatures; j++) {
                resI += thetaI[j] * img[j];
            }
            if (max < resI) {
                max = resI;
                maxI = i;
            }
        }

        return maxI;
    }

    public void learn(int[] img, int label) {
        int ans = calc(img);
        if (ans != label) {
            for (int i = 0; i < numOfFeatures; i++) {
                theta[ans][i] -= img[i];
                theta[label][i] += img[i];
            }
        }
    }

    public boolean test(int[] img, int label) {
        return label == calc(img);
    }
}
