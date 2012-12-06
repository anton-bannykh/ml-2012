package svm;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.direct.CMAESOptimizer;
import org.apache.commons.math3.random.MersenneTwister;

public class Runner implements Runnable {

	public static final double INIT = 0, REG_CONST = 1, EPSILON = 2;

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
		double init[] = new double[SVM.M * SVM.N + 1];
		Arrays.fill(init, INIT);

		Func f = new Func(myNum, x, y, REG_CONST);

		CMAESOptimizer opt = new CMAESOptimizer(50, null,
				CMAESOptimizer.DEFAULT_MAXITERATIONS,
				CMAESOptimizer.DEFAULT_STOPFITNESS,
				CMAESOptimizer.DEFAULT_ISACTIVECMA,
				CMAESOptimizer.DEFAULT_DIAGONALONLY,
				CMAESOptimizer.DEFAULT_CHECKFEASABLECOUNT, new MersenneTwister(
						System.nanoTime()), false, new SimpleValueChecker(0,
						EPSILON));
		PointValuePair p = opt.optimize(CMAESOptimizer.DEFAULT_MAXITERATIONS,
				f, GoalType.MINIMIZE, init);

		double w[] = p.getPointRef();
		try {
			PrintWriter pw = new PrintWriter(out);
			for (int i = 0; i < w.length; ++i) {
				pw.print(w[i] + " ");
			}
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
}
