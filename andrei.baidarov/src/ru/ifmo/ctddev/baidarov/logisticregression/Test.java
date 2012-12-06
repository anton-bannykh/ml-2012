package ru.ifmo.ctddev.baidarov.logisticregression;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 03.12.12
 * Time: 23:55
 */
public class Test {
    private final double[] img;

    private final int label;

    public Test(double[] img, int label) {
        this.img = img;
        this.label = label;
    }

    public double[] getImg() {
        return img;
    }

    public int getLabel() {
        return label;
    }

}
