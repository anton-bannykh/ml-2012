package ru.ifmo.ctddev.baidarov.logisticregression;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 23:44
 */
public class Main {
    public static final String TRAIN_IMAGES = "train-images.idx3-ubyte";
    public static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
    public static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
    public static final String TEST_LABELS = "t10k-labels.idx1-ubyte";
    public static final double MEDIUM = 128;
    public static final double RANGE = 1000;
    private static Test[] tests;
    private static Classifier classifier;

    public static void teach() {
        try (
                DataInputStream images = new DataInputStream(new FileInputStream(TRAIN_IMAGES));
                DataInputStream labels = new DataInputStream(new FileInputStream(TRAIN_LABELS))
        ) {
            try {
                images.skip(4);
                labels.skip(4);
                int imagesN = images.readInt();
                int labelsN = labels.readInt();
                assert imagesN == labelsN;
                tests = new Test[imagesN];
                int n = images.readInt();
                int m = images.readInt();
                int imgSize = n * m;
                for (int i = 0; i < imagesN; i++) {
                    double img[] = new double[imgSize + 1];
                    img[0] = 1;
                    for (int j = 1; j <= imgSize; j++) {
                        img[j] = (images.read() - MEDIUM) / RANGE;
                    }
                    int label = labels.read();
                    tests[i] = new Test(img, label);
                }
                classifier = new Classifier(tests);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        if (classifier == null) {
            System.out.println("First teach, then test!");
            return;
        }
        try (
                DataInputStream images = new DataInputStream(new FileInputStream(TEST_IMAGES));
                DataInputStream labels = new DataInputStream(new FileInputStream(TEST_LABELS))
        ) {
            try {
                images.skip(4);
                labels.skip(4);
                int imagesN = images.readInt();
                int labelsN = labels.readInt();
                assert imagesN == labelsN;
                int n = images.readInt();
                int m = images.readInt();
                int imgSize = n * m;
                int numOfErrors = 0;
                for (int i = 0; i < imagesN; i++) {
                    double img[] = new double[imgSize + 1];
                    img[0] = 1;
                    for (int j = 1; j <= imgSize; j++) {
                        img[j] = (images.read() - MEDIUM) / RANGE;
                    }
                    int label = labels.read();
                    if (!classifier.test(img, label))
                        numOfErrors++;
                }
                System.out.println("Errors: " + numOfErrors + ", " + (numOfErrors * 100 / imagesN) + "%");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        teach();
        test();
    }

}
