import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class LogisticRegression {
	
	private static final int testSize = 60000;
	private static final double l = 1.0 / (60000 * 100), rate = 0.7;
	
	private Reader reader;
	private int iterations, dimension;
	private double theta[][];
	
	LogisticRegression(Reader reader, int dimension, boolean inf, int iterations) {
		this.reader = reader;
		this.dimension = dimension;
		this.iterations = iterations;
		theta = new double[10][dimension];
		for (int i = 0; i < 10; ++i) {
			Arrays.fill(theta[i], 0);
		}
	}
	
	private void modify(int testNum, int thetaNum) {
		int test[] = reader.getTest(testNum);
		int label = reader.getLabel(testNum) == thetaNum ? 1 : -1;
		double tmp = 0;
		for (int i = 0; i < dimension; ++i) {
			tmp += theta[thetaNum][i] * test[i];
		}
		for (int i = 0; i < dimension; ++i) {
			theta[thetaNum][i] = (1 - rate * l) * theta[thetaNum][i] + rate * test[i] * label / (1 + Math.exp(tmp * label));
		}
	}
	
	public void calculate() {
		int num = 0;
		for (int i = 0; i < iterations; ++i) {
			System.out.println("Iteration: " + i);
			for (int j = 0; j < 10; ++j) {
				modify(num, j);
			}
			num = (num + 1) % testSize;
		}
		for (int j = 0; j < 10; ++j) {
			try {
				PrintWriter pw = new PrintWriter(new File("D:\\Results\\Regression\\LogisticRegressionFor_" + j + ".txt"));
				for (int i = 0; i < dimension; ++i) {
					pw.write(theta[j][i] + " ");
				}
				pw.close();
			} catch (IOException e) {
				System.err.println("Error on writing theta for " + j);
				e.printStackTrace();
			}
		}
	}

}