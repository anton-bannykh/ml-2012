package svm;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.util.FastMath;

public class Func implements MultivariateFunction {

	private double x[][], c;
	private int y[], myNum;
	private long count;

	public Func(int myNum, double x[][], int y[], double c) {
		this.x = x;
		this.y = y;
		this.myNum = myNum;
		count = 0;
		this.c = c;
	}

	@Override
	public double value(double[] point) {
		++count;
		double sum = 0;
		for (int i = 0; i < SVM.COUNT; ++i) {
			sum += loss(x[i], y[i], point);
		}
		double norm = 0;
		for (int i = 0; i < SVM.N * SVM.M; ++i) {
			norm += point[i] * point[i];
		}
		sum += norm / (2 * c);
		
		System.out
				.println("num " + myNum + " count " + count + " value " + sum);
		return sum;
	}

	private double loss(double[] x, double y, double[] w) {
		double trY = y == myNum ? 1.0 : -1.0;
		double l = 1 - trY * (SVM.k.mult(x, w) - w[SVM.N * SVM.M]);
		return FastMath.max(0, l);
	}
}
