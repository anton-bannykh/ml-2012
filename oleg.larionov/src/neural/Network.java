package neural;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;

public class Network {

	public static final int N = 28, M = 28, NUM = 10;
	public static final String OUT = "out.txt";
	public static final double EPS = 1e-9;

	private double a[][], b[][], x[][], trY[][], eta, xCheck[][];
	private int neurons, yCheck[];

	public Network(double x[][], int y[], int neurons, double eta,
			double xCheck[][], int yCheck[]) {
		this.x = x;
		this.neurons = neurons;
		this.eta = eta;

		Random rnd = new Random();
		a = new double[neurons][N * M + 1];
		for (int i = 0; i < neurons; ++i) {
			for (int j = 0; j < N * M + 1; ++j) {
				a[i][j] = rnd.nextGaussian() * 0.01;
			}
		}

		b = new double[NUM][neurons + 1];
		for (int i = 0; i < NUM; ++i) {
			for (int j = 0; j < neurons + 1; ++j) {
				b[i][j] = rnd.nextGaussian() * 0.01;
			}
		}

		trY = new double[NUM][y.length];
		for (int i = 0; i < y.length; ++i) {
			for (int j = 0; j < NUM; ++j) {
				trY[j][i] = j == y[i] ? 1.0 : 0;
			}
		}

		this.xCheck = xCheck;
		this.yCheck = yCheck;
	}

	public void teach(int count) {
		System.out.println("teaching...");
		int prevErr = yCheck.length;
		for (int c = 0; c < count; ++c) {
			for (int i = 0; i < x.length; ++i) {
				if ((i + 1) % 10000 == 0) {
					System.out.println(i + 1);
				}
				double z[] = new double[neurons + 1], ans[] = new double[NUM];
				z[z.length - 1] = 1.0;
				for (int j = 0; j < neurons; ++j) {
					z[j] = sigma(mult(x[i], a[j]));
				}
				for (int j = 0; j < NUM; ++j) {
					ans[j] = sigma(mult(z, b[j]));
				}

				double err[] = new double[NUM];
				for (int j = 0; j < NUM; ++j) {
					err[j] = ans[j] * (1 - ans[j]) * (ans[j] - trY[j][i]);
				}

				double errNeur[] = new double[neurons + 1];
				for (int j = 0; j < errNeur.length; ++j) {
					for (int k = 0; k < NUM; ++k) {
						errNeur[j] += err[k] * b[k][j];
					}
					errNeur[j] *= z[j] * (1 - z[j]);
				}

				for (int j = 0; j < neurons; ++j) {
					for (int k = 0; k < N * M + 1; ++k) {
						a[j][k] -= eta * errNeur[j] * x[i][k];
					}
				}

				for (int j = 0; j < NUM; ++j) {
					for (int k = 0; k < neurons + 1; ++k) {
						b[j][k] -= eta * err[j] * z[k];
					}
				}
			}

			int err = check();
			System.out.println("total " + err + " errors after " + (c + 1)
					+ " iterations, eta " + eta);
			if (err >= prevErr) {
				eta /= 3;
			} else {
				eta /= 1.5;
			}
			prevErr = err;
			if (FastMath.abs(eta) < EPS) {
				break;
			}
		}

		try (PrintWriter out = new PrintWriter(OUT)) {
			out.println(neurons + " " + (N * M + 1));
			for (int j = 0; j < neurons; ++j) {
				for (int k = 0; k < N * M + 1; ++k) {
					out.print(a[j][k] + " ");
				}
				out.println();
			}
			out.println(NUM + " " + (neurons + 1));
			for (int j = 0; j < NUM; ++j) {
				for (int k = 0; k < neurons + 1; ++k) {
					out.print(b[j][k] + " ");
				}
				out.println();
			}
		} catch (FileNotFoundException e) {
			System.err.println("cannot print to file. " + e.getMessage());
		}
	}

	public int check() {
		int err = 0;
		for (int i = 0; i < xCheck.length; ++i) {
			double z[] = new double[neurons + 1], ans[] = new double[NUM];
			z[z.length - 1] = -1.0;
			for (int j = 0; j < neurons; ++j) {
				z[j] = sigma(mult(xCheck[i], a[j]));
			}
			for (int j = 0; j < NUM; ++j) {
				ans[j] = sigma(mult(z, b[j]));
			}

			int maxk = 0;
			double max = ans[maxk];
			for (int j = 0; j < NUM; ++j) {
				if (max < ans[j]) {
					maxk = j;
					max = ans[maxk];
				}
			}

			if (maxk != yCheck[i]) {
				++err;
			}
		}

		return err;
	}

	public static double mult(double[] x, double[] y) {
		double ans = 0;
		for (int i = 0; i < x.length; ++i) {
			ans += x[i] * y[i];
		}
		return ans;
	}

	public static double sigma(double x) {
		return 1.0 / (1.0 + FastMath.exp(-x));
	}
}
