package logit;

import org.apache.commons.math3.analysis.MultivariateFunction;

public class Derivative implements MultivariateFunction {

	public double x[][];
	public int y[], myNum, k;

	public Derivative(int myNum, double[][] x, int[] y, int k) {
		this.x = x;
		this.y = y;
		this.myNum = myNum;
		this.k = k;
	}

	@Override
	public double value(double[] point) {
		double ans = 0;
		for (int i = 0; i < x.length; ++i) {
			double scalar = Runner.mult(point, x[i]);
			double trY = y[i] == myNum ? 1.0 : -1.0;
			double exp = Math.exp(-1.0 * trY * scalar);
			ans -= trY * x[i][k] * exp / (1 + exp);
		}
		return ans;
	}
}
