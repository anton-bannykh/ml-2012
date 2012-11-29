package logit;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Runner implements Runnable {

	public static final String im = "train.imgs", lab = "labels.imgs";
	public static final int n = 28, m = 28, count = 60000;
	public static final double TAU = 0.5, MULT = 100, SHIFT = 128;
	private String out;
	private int myNum, maxYesCount, maxNoCount;
	private CountDownLatch lock;

	public Runner(int myNum, int maxYesCount, int maxNoCount,
			CountDownLatch lock) {
		this.myNum = myNum;
		this.maxYesCount = maxYesCount;
		this.maxNoCount = maxNoCount;
		out = "out/" + myNum + ".txt";
		this.lock = lock;
	}

	@Override
	public void run() {
		List<Pair> list = new ArrayList<>();
		try {
			DataInputStream imgs = new DataInputStream(new FileInputStream(im)), labels = new DataInputStream(
					new FileInputStream(lab));
			imgs.skip(16);
			labels.skip(8);
			int yes = 0, no = 0, num;

			for (int w = 0; w < count; ++w) {
				if (w % 10000 == 0) {
					System.out.println("num " + myNum + " image " + w);
				}
				if (yes > maxYesCount && no > maxNoCount) {
					System.out.println("num " + myNum + " break on " + w);
					break;
				}

				num = labels.read();
				if (num == myNum) {
					if (yes > maxYesCount) {
						imgs.skip(n * m);
						continue;
					}
					++yes;
				} else {
					if (no > maxNoCount) {
						imgs.skip(n * m);
						continue;
					}
					++no;
				}

				double[] cur = new double[n * m];

				for (int i = 0; i < n; ++i) {
					for (int j = 0; j < m; ++j) {
						cur[i * n + j] = (1.0 * imgs.read() - SHIFT) / MULT;
					}
				}
				list.add(new Pair(cur, num));
			}
			imgs.close();
			labels.close();
		} catch (Exception e) {
			lock.countDown();
			System.err.println("num " + myNum + " fail in first try "
					+ e.getMessage());
			return;
		}

		double w[] = new double[n * m], w0 = 1;
		Arrays.fill(w, 1);
		Random rnd = new Random();
		for (int run = 0; run < 3 * list.size(); ++run) {
			int r = rnd.nextInt(list.size());
			Pair p = list.get(r);
			double y = myNum == p.num ? 1.0 : -1.0;
			double sigma = sigma(y * (mult(w, p.point) + w0));
			for (int i = 0; i < w.length; ++i) {
				w[i] += y * p.point[i] * (1.0 - sigma) * TAU;
				w0 += y * (1.0 - sigma) * TAU;
			}
		}

		try {
			PrintWriter pw = new PrintWriter(out);
			pw.print(w0 + " ");
			for (int i = 0; i < w.length; ++i) {
				pw.print(w[i] + " ");
			}
			pw.println();
			pw.close();
		} catch (Exception e) {
			lock.countDown();
			System.err.println("num " + myNum + "fail in second try "
					+ e.getMessage());
			return;
		}

		lock.countDown();
		System.err.println("num " + myNum + " calculated. lock "
				+ lock.getCount());
	}

	public static double mult(double[] x, double[] y) {
		double ans = 0;
		for (int i = 0; i < x.length; ++i) {
			ans += x[i] * y[i];
		}
		return ans;
	}

	public double sigma(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	private static class Pair {
		public double[] point;
		public int num;

		public Pair(double[] point, int num) {
			this.point = point;
			this.num = num;
		}
	}
}
