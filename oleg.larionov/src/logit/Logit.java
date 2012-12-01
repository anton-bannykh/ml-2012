package logit;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.util.FastMath;

public class Logit implements DifferentiableMultivariateFunction {

	private double x[][], c;
	private int y[], myNum;
	private long count;

	public Logit(int myNum, double[][] x, int[] y, double c) {
		this.x = x;
		this.y = y;
		this.myNum = myNum;
		count = 0;
		this.c = c;
	}

	@Override
	public double value(double[] point) {
		++count;
		double log = 0, reg = 0;
		for (int i = 0; i < x.length; ++i) {
			double trY = y[i] == myNum ? 1.0 : -1.0;
			double scalar = Runner.mult(x[i], point);
			log += FastMath.log(1 + FastMath.exp(-1.0 * scalar * trY));
		}
		for (int i = 0; i < point.length; ++i) {
			reg += FastMath.abs(point[i]);
		}
		reg /= c;
		double ans = log + reg;
		System.out.println("num " + myNum + " run " + count + " log value "
				+ log + " reg value " + reg + " ans " + ans);
		return ans;
	}

	@Override
	public MultivariateFunction partialDerivative(int k) {
		System.err.println("Partial derivative error");
		return null;
	}

	@Override
	public MultivariateVectorFunction gradient() {
		return new Gradient(myNum, x, y, c);
	}

}