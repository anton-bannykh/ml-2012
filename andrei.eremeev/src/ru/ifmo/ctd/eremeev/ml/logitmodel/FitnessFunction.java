package ru.ifmo.ctd.eremeev.ml.logitmodel;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.util.FastMath;

import ru.ifmo.ctd.eremeev.ml.util.Digit;

public class FitnessFunction implements DifferentiableMultivariateFunction {

	private Digit[] ds;
	private int label;
	private int n;
	
	private double lambda;
	private double eta;
	
	private long cnt = 0;
	
	public FitnessFunction(Digit[] ds, int label, double lambda, double eta) {
		this.ds = ds;
		this.label = label;
		this.n = ds[0].getN();
		this.lambda = lambda;
		this.eta = eta;
	}
	
	@Override
	public double value(double[] x) {
		double ans = 0;
		for (Digit d : ds) {
			ans -= FastMath.log(P(d.imageToDoubleArray(), x, d.getLabel() == label ? 1 : -1));
		}
		for (int i = 0; i < n; ++i) {
			ans += lambda * x[i] * x[i] / 2;
		}
		return ans;
	}

	@Override
	public MultivariateVectorFunction gradient() {
		return new MultivariateVectorFunction() {
			@Override
			public double[] value(double[] x) throws IllegalArgumentException {
				double[] ans = new double[n];
				for (Digit d : ds) {
					int y = d.getLabel() == label ? 1 : -1;
					double p = P(d.imageToDoubleArray(), x, y);
					for (int i = 0; i < n; ++i) {
						ans[i] += eta * y * d.getDouble(i) * (1 - p);
					}
				}
				for (int i = 0; i < n; ++i) {
					ans[i] += lambda * x[i];
				}
				++cnt;
				if (cnt % 10 == 0) {
					System.out.println("Label : " + label + " count : " + cnt);
				}
				return ans;
			}
		};
	}

	@Override
	public MultivariateFunction partialDerivative(final int k) {
		return new MultivariateFunction() {
			@Override
			public double value(double[] x) {
				double ans = 0;
				for (Digit d : ds) {
					int y = d.getLabel() == label ? 1 : -1;
					double p = P(d.imageToDoubleArray(), x, y);
					ans += eta * y * d.getDouble(k) * (1 - p);
				}
				ans += lambda * x[k];
				return ans;
			}
		};
	}

	private static double P(double[] x, double[] teta, int y) {
		double ans = 0;
		for (int i = 0; i < x.length; ++i) {
			ans += x[i] * teta[i];
		}
		return g(y * ans);
	}
	
	private static double g(double z) {
		return 1 / (1 + FastMath.exp(-z));
	}
}
