public class Perceptron {
	double[] theta;

	public Perceptron(int n) {
		theta = new double[n];
//		Arrays.fill(theta, 1);
	}

	public double getBetter(int[] vector) {
		double ans = 0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return ans;
	}
	
	public int get(int[] vector) {
		double ans = 0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return (int) Math.signum(ans);
	}

	public void update(int y, int[] vector) {
		for (int i = 0; i < vector.length; i++) {
			theta[i] += y * vector[i];
		}
	}
}
