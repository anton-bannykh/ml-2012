package ru.ifmo.ctddev.baidarov.logisticregression;

import ru.ifmo.ctddev.baidarov.util.Reader;
import ru.ifmo.ctddev.baidarov.util.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 23:44
 */
public class Main {
    private static Classifier classifier;

    public static void teach() {
        Test[] trainingSet = Reader.read("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
        classifier = new Classifier(trainingSet, 10);
    }

    public static void test() {
        if (classifier == null) {
            System.out.println("First teach, then test!");
            return;
        }

        Test[] tests = Reader.read("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
        int errors = 0;

        for (Test test : tests) {
            if (!classifier.test(test)) {
                errors++;
            }
        }

        System.out.println("Errors: " + errors + "(" + errors * 100 / tests.length + "%)");
    }


    public static void main(String[] args) {
        teach();
        test();
    }

}
