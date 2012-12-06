package ru.ifmo.ctddev.baidarov.logisticregression;

import ru.ifmo.ctddev.baidarov.util.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 23:54
 */
public class Classifier {
    private final Test[] trainingSet;
    private final int numOfFeatures;
    private final int numOfLabels;
    private final double[][] theta;
    private final double MEDIUM = 128;
    private final double RANGE = 1000;


    public Classifier(Test[] trainingSet, int numOfLabels) {
        this.numOfLabels = numOfLabels;
        this.trainingSet = trainingSet;
        numOfFeatures = trainingSet[0].getImg().length;
        theta = new double[numOfLabels][numOfFeatures];

        for (int i = 0; i < trainingSet.length; i++) {
            trainingSet[i] = scale(trainingSet[i]);
        }

        learn();
    }

    private Test scale(Test test) {
        double[] img = test.getImg();
        int label = test.getLabel();
        double[] newImg = new double[img.length];

        for (int i = 0; i < img.length; i++) {
            newImg[i] = (img[i] - MEDIUM) / RANGE;
        }

        return new Test(newImg, label);
    }

    private void learn() {
        int proc = Runtime.getRuntime().availableProcessors();
        CountDownLatch lock = new CountDownLatch(numOfLabels);
        ExecutorService executors = Executors.newFixedThreadPool(proc);
        for (int i = 0; i < numOfLabels; i++) {
            executors.execute(new Teacher(trainingSet, i, theta, lock));
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executors.shutdownNow();
        }
    }

    public boolean test(Test test) {
        test = scale(test);
        double[] img = test.getImg();
        int label = test.getLabel();
        int prediction = predict(img);

        return prediction == label;
    }

    private int predict(double[] img) {
        if (img.length != numOfFeatures) {
            throw new IllegalArgumentException("Img must have " + numOfFeatures + " feature(s)");
        }

        double bestGuessScore = Double.NEGATIVE_INFINITY;
        int bestGuess = -1;

        for (int i = 0; i < numOfLabels; i++) {
            double[] thetaI = theta[i];
            double curGuessScore = 0;
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
}
