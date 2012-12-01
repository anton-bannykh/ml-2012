package logit;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.util.FastMath;

public class Runner implements Runnable {

	public static final int MAX_EVAL = 30000;
	public static final double INIT = 0;

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
		double w[] = new double[LogitRegr.N * LogitRegr.M + 1];
		Arrays.fill(w, INIT);

		DifferentiableMultivariateFunction f = new Logit(myNum, x, y);
		NonLinearConjugateGradientOptimizer opt = new NonLinearConjugateGradientOptimizer(
				ConjugateGradientFormula.POLAK_RIBIERE, new SimpleValueChecker(0, 1));
		PointValuePair p = opt.optimize(MAX_EVAL, f, GoalType.MINIMIZE, w);

		try {
			PrintWriter pw = new PrintWriter(out);
			for (int i = 0; i < p.getPointRef().length; ++i) {
				pw.print(p.getPointRef()[i] + " ");
			}
			pw.println();
			pw.close();
		} catch (Exception e) {
			lock.countDown();
			System.err.println(myNum + " fail printing results "
					+ e.getMessage());
			return;
		}

		lock.countDown();
		System.err.println("num " + myNum + " calculated. log value "
				+ p.getValue() + " lock " + lock.getCount());
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
