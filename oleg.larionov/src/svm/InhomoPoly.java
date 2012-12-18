package svm;

import org.apache.commons.math3.util.FastMath;

public class InhomoPoly implements Kernel {

	public int deg;

	public InhomoPoly(int deg) {
		this.deg = deg;
	}

	@Override
	public double mult(double[] x, double[] y) {
		double scalar = 1;
		for (int i = 0; i < x.length; ++i) {
			scalar += x[i] * y[i];
		}

		return FastMath.pow(scalar, deg);
	}

	@Override
	public String getParams() {
		return "" + deg;
	}
}
