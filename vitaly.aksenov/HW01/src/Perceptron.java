import java.util.Arrays;

public class Perceptron {
	double[] theta;

	public Perceptron(int n) {
		theta = new double[n];
//		Arrays.fill(theta, 1);
	}

	public double getBetter(byte[] vector) {
		double ans = 0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return ans;
	}
	
	public int get(byte[] vector) {
		double ans = 0;
		for (int i = 0; i < vector.length; i++) {
			ans += theta[i] * vector[i];
		}
		return (int) Math.signum(ans);
	}

	public void update(int y, byte[] vector) {
		for (int i = 0; i < vector.length; i++) {
			theta[i] += y * vector[i];
		}
	}
}
