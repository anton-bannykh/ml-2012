package logit;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;

public class Gradient implements MultivariateVectorFunction {

	public double x[][];
	public int y[], myNum;

	public Gradient(int myNum, double[][] x, int[] y) {
		this.x = x;
		this.y = y;
		this.myNum = myNum;
	}

	@Override
	public double[] value(double[] point) throws IllegalArgumentException {
		double ans[] = new double[point.length];
		for (int i = 0; i < x.length; ++i) {
			double trY = y[i] == myNum ? 1.0 : -1.0;
			double scalar = Runner.mult(x[i], point);
			for (int j = 0; j < x[i].length; ++j) {
				ans[j] += -1.0 * trY * x[i][j]
						* Runner.sigma(-1.0 * trY * scalar);
			}
		}
		return ans;
	}
}
