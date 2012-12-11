package svm;

import org.apache.commons.math3.util.FastMath;

public class Gaussian implements Kernel {

	private double gamma;

	public Gaussian(double gamma) {
		this.gamma = gamma;
	}

	@Override
	public double mult(double[] x, double[] y) {
		double norm = 0;
		for (int i = 0; i < x.length; ++i) {
			norm += (x[i] - y[i]) * (x[i] - y[i]);
		}

		return FastMath.exp(-gamma * norm);
	}
}
