import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Learning {
	static final int ITERATIONS = 1000000;
	static final double LAMBDA = 3e-7;// 0;// 0.01;
	static final double NU = 0.4;
	static final Random rnd = new Random(239);

	public static void main(String[] args) {
		Reader r = new Reader();

		Regression[] regressions = new Regression[10];
		for (int i = 0; i < regressions.length; i++) {
			regressions[i] = new Regression(r.check[0].length(), LAMBDA, NU);
		}

		for (int i = 0; i < ITERATIONS; i++) {
			System.err.println("Running " + (i + 1) + " out of " + ITERATIONS);
			// int next = rnd.nextInt(r.train.length);
			int next = i % r.train.length;
			for (int j = 0; j < regressions.length; j++) {
				regressions[j].gradient(r.train_label[next] == j ? 1 : -1,
						r.train[next].vector);
			}
		}

		try {
			PrintWriter out = new PrintWriter("learn");
			for (int i = 0; i < regressions.length; i++) {
				for (int j = 0; j < regressions[i].theta.length; j++) {
					if (j % r.train[0].m == 0) {
						out.println();
					}
					out.print(regressions[i].theta[j] + " ");
				}
				out.println();
			}
			out.println();
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
				for (int j = 0; j < regressions.length; j++) {
					int y = r.check_label[i] == j ? 1 : -1;
					if (regressions[j].get(r.check[i].vector) != y) {
						error += 1;
						out.print("-1 ");
					} else {
						out.print("1 ");
					}

					if (regressions[j].get(r.check[i].vector) != 1) {
						continue;
					}

					double z = regressions[j].getBetter(r.check[i].vector);
					if (max < z) {
						max = z;
						mj = j;
					}
				}

				if (r.check_label[i] != mj) {
					better_error++;
				}

				for (int j = 0; j < regressions.length; j++) {
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
