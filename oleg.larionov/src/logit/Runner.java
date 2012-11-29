package logit;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Runner implements Runnable {

	public static final double ETA = 2;
	public static final double INIT = 1;

	private String out;
	private int myNum;
	private double x[][];
	private int y[];
	private CountDownLatch lock;

	public Runner(int myNum, double[][] x, int[] y, CountDownLatch lock) {
		this.myNum = myNum;
		this.x = x;
		this.y = y;
		out = "out/" + myNum + ".txt";
		this.lock = lock;
	}

	@Override
	public void run() {
		System.out.println(myNum + " begin calculating");
		double w[] = new double[LogitRegr.N * LogitRegr.M], w0 = INIT;
		Arrays.fill(w, INIT);
		for (int run = 0; run < 4 * x.length; ++run) {
			double curX[] = x[run % x.length], curY = y[run % x.length];
			double trY = myNum == curY ? 1.0 : -1.0;
			double sigma = sigma(trY * (mult(w, curX) + w0));
			for (int i = 0; i < w.length; ++i) {
				w[i] += trY * curX[i] * (1.0 - sigma) * ETA;
				w0 += trY * (1.0 - sigma) * ETA;
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
			System.err.println(myNum + "fail printing results "
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
}
