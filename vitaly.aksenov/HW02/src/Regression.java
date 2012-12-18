public class Regression {
	double[] theta;
	double theta0;
	private double lambda, nu;

	public Regression(int n, double lambda, double nu) {
		theta = new double[n];
		this.lambda = lambda;
		this.nu = nu;
	}

	public double getBetter(int[] vector) {
		double ans = theta0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return ans;
	}

	public int get(int[] vector) {
		double ans = theta0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return (int) Math.signum(ans);
	}

	public void gradient(int y, int[] vector) {
		double p = P(y, vector);
		for (int i = 0; i < vector.length; i++) {
			theta[i] += nu * (y * vector[i] * (1 - p) - lambda * theta[i]);
		}
		theta0 += nu * y * (1 - p);
	}

	private double P(int y, int[] vector) {
		return 1 / (1 + Math.exp(-y * getBetter(vector)));
	}
}
