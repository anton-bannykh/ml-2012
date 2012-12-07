package ru.ifmo.ctddev.baidarov.logisticregression;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.util.FastMath;
import ru.ifmo.ctddev.baidarov.util.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 04.12.12
 * Time: 0:31
 */
public class CostFunction implements DifferentiableMultivariateFunction {
    private final Test[] tests;
    private final int numOfFeatures;
    private final int curLabel;
    private final double lambda = 0;

    public CostFunction(Test[] tests, int curLabel) {
        this.tests = tests;
        this.curLabel = curLabel;
        numOfFeatures = tests[0].getImg().length;
    }

    @Override
    public MultivariateFunction partialDerivative(final int k) {
        return new MultivariateFunction() {
            @Override
            public double value(double[] point) {
                double res = 0;
                for (Test test : tests) {
                    double x = test.getImg()[k];
                    int y = test.getLabel() == curLabel ? 1 : -1;
                    double hx = h(test.getImg(), point, y);
                    res -= y * x * (1 - hx);
                }
                res += lambda * point[k];
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
                for (Test test : tests) {
                    int y = test.getLabel() == curLabel ? 1 : -1;
                    double hx = h(test.getImg(), point, y);
                    for (int j = 0; j < numOfFeatures; j++) {
                        double x = test.getImg()[j];
                        res[j] -= y * x * (1 - hx);
                    }
                }

                for (int i = 0; i < numOfFeatures; i++) {
                    res[i] += lambda * point[i];
                }
                return res;
            }
        };
    }

    @Override
    public double value(double[] point) {
        double res = 0;
        for (Test test : tests) {
            int y = test.getLabel() == curLabel ? 1 : -1;
            double hx = h(test.getImg(), point, y);
            res -= FastMath.log(hx);
        }

        for (int i = 0; i < numOfFeatures; i++) {
            res += lambda * point[i] * point[i] / 2;
        }
        return res;
    }

    private double h(double[] x, double[] theta, double y) {
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
