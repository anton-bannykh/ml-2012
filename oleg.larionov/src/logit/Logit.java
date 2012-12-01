package logit;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.util.FastMath;

public class Logit implements DifferentiableMultivariateFunction {

	public double x[][];
	public int y[], myNum;
	public long count;

	public Logit(int myNum, double[][] x, int[] y) {
		this.x = x;
		this.y = y;
		this.myNum = myNum;
		count = 0;
	}

	@Override
	public double value(double[] point) {
		++count;
		double log = 0;
		for (int i = 0; i < x.length; ++i) {
			double trY = y[i] == myNum ? 1.0 : -1.0;
			double scalar = Runner.mult(x[i], point);
			log += FastMath.log(1 + Math.exp(-1.0 * scalar * trY));
		}
		System.out.println("num " + myNum + " run " + count + " log value "
				+ log);
		return log;
	}

	@Override
	public MultivariateFunction partialDerivative(int k) {
		System.err.println("Partial derivative error");
		return null;
	}

	@Override
	public MultivariateVectorFunction gradient() {
		return new Gradient(myNum, x, y);
	}

}