package svm;

public class Scalar implements Kernel {

	@Override
	public double mult(double[] x, double[] y) {
		double ans = 0;
		for (int i = 0; i < SVM.N * SVM.M; ++i) {
			ans += x[i] * y[i];
		}
		return ans;
	}
}
