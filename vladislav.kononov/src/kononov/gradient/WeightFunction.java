package kononov.gradient;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 30.01.13
 * Time: 22:52
 * To change this template use File | Settings | File Templates.
 */
public class WeightFunction implements DifferentiableMultivariateFunction {
    private final double[][] data;
    private final int[] labels;
    private final int numOfFeatures;
    private final int curLabel;

    public WeightFunction(double[][] data, int[] labels, int curLabel) {
        this.data = data;
        this.labels = labels;
        this.curLabel = curLabel;
        numOfFeatures = data[0].length;
    }

    @Override
    public MultivariateFunction partialDerivative(final int k) {
        return new MultivariateFunction() {
            @Override
            public double value(double[] point) {
                double res = 0;
                for (int i = 0; i < data.length; ++i){
                    double x = data[i][k] ;
                    int y = labels[i] == curLabel ? 1 : -1;
                    double hx = p(data[i], point, y);
                    res -= y * x * (1 - hx);
                }
                return res;
            }
        };
    }

    @Override
    public MultivariateVectorFunction gradient() {
        return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] point) throws IllegalArgumentException {
                double res[] = new double[numOfFeatures];
                for (int i = 0; i < data.length; ++i){
                    int y = labels[i] == curLabel ? 1 : -1;
                    double hx = p(data[i], point, y);
                    for (int j = 0; j < numOfFeatures; j++){
                        double x = data[i][j];
                        res[j] -= y * x * (1 - hx);
                    }
                }
                return res;
            }
        };
    }

    @Override
    public double value(double[] point) {
        double res = 0;
        for (int i = 0; i < data.length; ++i){
            int y = labels[i] == curLabel? 1 : -1;
            double hx = p(data[i], point, y);
            res -= FastMath.log(hx);
        }
        return res;
    }

    private double p(double[] x, double[] theta, double y) {
        return g(y * dotProduct(x, theta));
    }

    private double g(double x) {
        return 1 / (1 + FastMath.exp(-x));
    }

    private double dotProduct(double[] x, double[] y) {
        double res = 0;
        for (int i = 0; i < x.length; i++) {
            res += x[i] * y[i];
        }
        return res;
    }
}
