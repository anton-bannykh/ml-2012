import java.io.IOException;
import java.io.PrintWriter;

public class Learning {
	static final int ITERATIONS = 100;

	public static void main(String[] args) {
		Reader r = new Reader();

		Perceptron[] perceptrons = new Perceptron[10];
		for (int i = 0; i < perceptrons.length; i++) {
			perceptrons[i] = new Perceptron(r.check[0].length());
		}

		for (int i = 0; i < ITERATIONS; i++) {
			System.err.println("Calculating " + (i + 1) + " out of "
					+ ITERATIONS);
			for (int j = 0; j < r.train.length; j++) {
				for (int k = 0; k < perceptrons.length; k++) {
					int y = r.train_label[j] == k ? 1 : -1;
					if (perceptrons[k].get(r.train[j].vector) != y) {
						perceptrons[k].update(y, r.train[j].vector);
					}
				}
			}
		}

		try {
			PrintWriter out = new PrintWriter("learn");
			for (int i = 0; i < perceptrons.length; i++) {
				out.print(i + 1);
				int m = r.train[i].m;
				for (int j = 0; j < perceptrons[i].theta.length; j++) {
					if (j % m == 0) {
						out.println();
					}
					out.print(perceptrons[i].theta[j] + " ");
				}
				out.println();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			double error = 0;
			double better_error = 0;
			PrintWriter out = new PrintWriter("result");
			for (int i = 0; i < r.check.length; i++) {
				double max = 0;
				int mj = 0;
				for (int j = 0; j < perceptrons.length; j++) {
					int y = r.check_label[i] == j ? 1 : -1;
					if (perceptrons[j].get(r.check[i].vector) != y) {
						error += 1;
						out.print("-1 ");
					} else {
						out.print("1 ");
					}

					double z = perceptrons[j].getBetter(r.check[i].vector);
					if (max < z) {
						max = z;
						mj = j;
					}
				}

				if (r.check_label[i] != mj) {
					better_error++;
				}

				for (int j = 0; j < perceptrons.length; j++) {
					out.print(r.check_label[i] == j ? 1 : -1);
				}

				out.print(" > " + mj);
				out.println();
			}
			out.close();
			out = new PrintWriter("error");
			out.println(error / r.check.length);
			out.println(better_error / r.check.length);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
