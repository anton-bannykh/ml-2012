import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class LogisticTester {
	
	private static final int testsSize = 10000;
	private static final int dimension = 28 * 28;
	
	private Reader reader;
	private double theta[][];
	private double normSq[];
	
	LogisticTester(Reader reader) {
		Locale.setDefault(Locale.US);
		this.reader = reader;
		theta = new double[10][dimension];
		normSq = new double[10];
		Arrays.fill(normSq, 0);
		for (int i = 0; i < 10; ++i) {
			try {
				Scanner s = new Scanner(new BufferedInputStream(new FileInputStream(new File("D:\\Results\\Regression\\LogisticRegressionFor_" + i + ".txt"))));
				for (int j = 0; j < dimension; ++j) {
					theta[i][j] = s.nextDouble();
					normSq[i] += theta[i][j] * theta[i][j];
				}
				s.close();
			} catch (IOException e) {
				System.err.println("Error on reading perceptrons' results");
				e.printStackTrace();
			}
		}
	}
	
	public void test() {
		try {
			int good = 0;
			PrintWriter pw = new PrintWriter(new File("D:\\Results\\Regression\\Report.txt"));
			for (int i = 0; i < testsSize; ++i) {
				pw.println("Test: " + (i + 1));
				int test[] = reader.getTest(i);
				byte label = reader.getLabel(i);
				int maxInd = -1;
				double max = 0;
				for (int j = 0; j < 10; ++j) {
					double tmp = 0;
					for (int k = 0; k < dimension; ++k) {
						tmp += theta[j][k] * test[k];
					}
					//tmp /= Math.sqrt(normSq[j]);
					if (maxInd == -1 || tmp > max) {
						max = tmp;
						maxInd = j;
					}
					pw.println("Perceptron: " + j + (tmp > 0 ? " answer: true" : " answer: false"));
				}
				if (maxInd == label) {
					++good;
				}
				pw.println((maxInd == label ? "GOOD" : "BAD") + " Answer: " + (maxInd == -1 ? "NONE" : ("" + maxInd)) + "(" + label + ")");
				pw.println();
			}
			pw.println("SUCCESSES: " + good);
			pw.println("ERRORS: " + (testsSize - good));
			pw.close();
		} catch (IOException e) {
			System.err.println("Error on outputting report");
			e.printStackTrace();
		}
	}
	
}
