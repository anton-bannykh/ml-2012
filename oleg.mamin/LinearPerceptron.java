import java.io.*;
import java.util.*;
		
public class LinearPerceptron {
	
	private static final int testSize = 60000;
	
	private Reader reader;
	private boolean inf;
	private int iterations, dimension;
	private int theta[][];
		
	LinearPerceptron(Reader reader, int dimension, boolean inf, int iterations) {
		this.reader = reader;
		this.dimension = dimension;
		this.inf = inf;
		this.iterations = iterations;
		theta = new int[10][dimension];
		for (int i = 0; i < 10; ++i) {
			Arrays.fill(theta[i], 0);
		}
	}
		
	private int[][] getTheta() {
		int counter = 0;
		while (inf || counter < iterations) {
			System.out.println("Iteration: " + (counter + 1));
			for (int t = 0; t < testSize; ++t) {
				int test[] = reader.getTest(t);
				for (int j = 0; j < 10; ++j) {
					int label = reader.getLabel(t) == j ? 1 : -1;
					long tmp = 0;
					for (int i = 0; i < dimension; ++i) {
						tmp += theta[j][i] * test[i];
					}
					if (tmp * label <= 0) {
						for (int i = 0; i < dimension; ++i) {
							theta[j][i] += test[i] * label;
						}
					}
				}
			}
			++counter;
		}
		return theta;
	}
		
	public void calculate() {
		int theta[][] = getTheta();
		for (int j = 0; j < 10; ++j) {
			try {
				PrintWriter pw = new PrintWriter(new File("D:\\Results\\LinearPerceptronFor_" + j + ".txt"));
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
