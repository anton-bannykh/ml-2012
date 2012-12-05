package ru.ifmo.ctddev.baidarov.logisticregression;

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
    private final Test[] tests;
    private final int numOfFeatures;
    private final int numOfLabels;
    private final double[][] theta;


    public Classifier(Test[] tests) {
        this.tests = tests;
        numOfLabels = 10;
        numOfFeatures = tests[0].getImg().length;
        theta = new double[numOfLabels][numOfFeatures];
        learn();
    }

    private void learn() {
        int proc = Runtime.getRuntime().availableProcessors();
        CountDownLatch lock = new CountDownLatch(numOfLabels);
        ExecutorService executors = Executors.newFixedThreadPool(proc);
        for (int i = 0; i < numOfLabels; i++) {
            executors.execute(new Teacher(tests, i, theta, lock));
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
        } finally {
            executors.shutdownNow();
        }
    }

    public boolean test(double[] img, int label) {
        double max = Double.NEGATIVE_INFINITY;
        int maxI = -1;
        for (int i = 0; i < numOfLabels; i++) {
            double[] thetaI = theta[i];
            double resI = 0;
            for (int j = 0; j < numOfFeatures; j++) {
                resI += thetaI[j] * img[j];
            }
            if (max < resI) {
                max = resI;
                maxI = i;
            }
        }
        return maxI == label;
    }
}
