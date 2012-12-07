package ru.ifmo.ctddev.baidarov.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 06.12.12
 * Time: 23:12
 */
public class Reader {
    public static Test[] read(final String imgs, final String labs) {
        Test[] res = null;
        try (
                DataInputStream images = new DataInputStream(new FileInputStream(imgs));
                DataInputStream labels = new DataInputStream(new FileInputStream(labs))
        ) {
            try {
                images.skip(4);
                labels.skip(4);
                int imagesN = images.readInt();
                int labelsN = labels.readInt();
                assert imagesN == labelsN;
                res = new Test[imagesN];
                int n = images.readInt();
                int m = images.readInt();
                int imgSize = n * m;
                for (int i = 0; i < imagesN; i++) {
                    double img[] = new double[imgSize];
                    for (int j = 0; j < imgSize; j++) {
                        img[j] = images.read();
                    }
                    int label = labels.read();
                    res[i] = new Test(img, label);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
