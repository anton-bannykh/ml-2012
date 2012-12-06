package ru.ifmo.ctddev.baidarov.perceptron;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 17:14
 */
public class Main {
    public static final String TRAIN_IMAGES = "train-images.idx3-ubyte";
    public static final String TRAIN_LABELS = "train-labels.idx1-ubyte";
    public static final String TEST_IMAGES = "t10k-images.idx3-ubyte";
    public static final String TEST_LABELS = "t10k-labels.idx1-ubyte";
    public static Perceptron perceptron;

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
                int n = images.readInt();
                int m = images.readInt();
                int imgSize = n * m;
                perceptron = new Perceptron(imgSize, 10);
                for (int i = 0; i < imagesN; i++) {
                    int img[] = new int[imgSize];
                    for (int j = 0; j < imgSize; j++) {
                        img[j] = images.read();
                    }
                    int label = labels.read();
                    perceptron.learn(img, label);
                }

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
        if (perceptron == null) {
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
                    int img[] = new int[imgSize];
                    for (int j = 0; j < imgSize; j++) {
                        img[j] = images.read();
                    }
                    int label = labels.read();
                    if (!perceptron.test(img, label))
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
