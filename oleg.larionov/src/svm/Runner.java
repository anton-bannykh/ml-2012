package svm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public class Runner implements Runnable {

	public static final double REG_CONST = 2, EPSILON = 0.1, GAMMA = 0.0078125,
			TOL = 1e-3, ZERO = 1e-6;

	private String out;
	private int myNum;
	private double x[][], trY[], alpha[], b, e[];
	private CountDownLatch lock;
	private Kernel k;
	private RandomGenerator rnd;

	public Runner(int myNum, double[][] x, int[] y, Kernel k,
			CountDownLatch lock) {
		this.myNum = myNum;
		this.x = x;
		out = "out/" + myNum + ".txt";
		this.lock = lock;
		this.k = k;
		b = 0;
		trY = new double[x.length];
		alpha = new double[x.length];
		e = new double[x.length];
		for (int i = 0; i < x.length; ++i) {
			trY[i] = y[i] == myNum ? 1.0 : -1.0;
			e[i] = -trY[i];
		}
		rnd = new MersenneTwister(System.nanoTime());
	}

	@Override
	public void run() {
		System.out.println(myNum + " begin calculating");

		int numChanged = 0;
		boolean examineAll = true;
		while (numChanged > 0 || examineAll) {
			numChanged = 0;
			if (examineAll) {
				for (int i = 0; i < x.length; ++i) {
					numChanged += examine(i);
				}
			} else {
				List<Integer> nonBound = new ArrayList<>();
				for (int i = 0; i < x.length; ++i) {
					if (alpha[i] > 0 && alpha[i] < REG_CONST) {
						nonBound.add(i);
					}
				}
				for (int i = 0; i < nonBound.size(); ++i) {
					numChanged += examine(nonBound.get(i));
				}
			}
			if (examineAll) {
				examineAll = false;
			} else if (numChanged == 0) {
				examineAll = true;
			}
			// System.err.println("current err value " + value(alpha));
		}

		try (PrintWriter pw = new PrintWriter(out)) {
			for (int i = 0; i < alpha.length; ++i) {
				if (FastMath.abs(alpha[i]) > ZERO) {
					pw.print(i + " " + alpha[i] + " ");
				}
			}
			pw.print("60000 " + b);
		} catch (FileNotFoundException e) {
			System.err.println("cannot write to file " + out + " "
					+ e.getMessage());
		}
		lock.countDown();
		System.err.println("num " + myNum + " calculated.");
	}

	private int examine(int num) {
		double e2 = e[num], r2 = e2 * trY[num];
		List<Integer> nonBound = new ArrayList<>();
		for (int i = 0; i < alpha.length; ++i) {
			if (alpha[i] > 0 && alpha[i] < REG_CONST) {
				nonBound.add(i);
			}
		}
		if ((r2 < -TOL && alpha[num] < REG_CONST)
				|| (r2 > TOL && alpha[num] > 0)) {
			if (nonBound.size() > 1) {
				if (e2 > 0) {
					int mink = 0;
					double min = e[nonBound.get(mink)];
					for (int i = 1; i < nonBound.size(); ++i) {
						if (e[nonBound.get(i)] < min) {
							mink = i;
							min = e[nonBound.get(mink)];
						}
					}
					if (takeStep(nonBound.get(mink), num, e2) == 1) {
						return 1;
					}
				} else {
					int maxk = 0;
					double max = e[nonBound.get(maxk)];
					for (int i = 1; i < nonBound.size(); ++i) {
						if (e[nonBound.get(i)] > max) {
							maxk = i;
							max = e[nonBound.get(maxk)];
						}
					}
					if (takeStep(nonBound.get(maxk), num, e2) == 1) {
						return 1;
					}
				}
			}

			int from;
			if (nonBound.size() > 0) {
				from = rnd.nextInt(nonBound.size());
				for (int i = from; i < nonBound.size(); ++i) {
					if (num != nonBound.get(i)) {
						if (takeStep(nonBound.get(i), num, e2) == 1) {
							return 1;
						}
					}
				}
				for (int i = 0; i < from; ++i) {
					if (num != nonBound.get(i)) {
						if (takeStep(nonBound.get(i), num, e2) == 1) {
							return 1;
						}
					}
				}
			}

			from = rnd.nextInt(x.length);
			for (int i = from; i < x.length; ++i) {
				if (num != i) {
					if (takeStep(i, num, e2) == 1) {
						return 1;
					}
				}
			}
			for (int i = 0; i < from; ++i) {
				if (num != i) {
					if (takeStep(i, num, e2) == 1) {
						return 1;
					}
				}
			}
		}

		return 0;
	}

	private int takeStep(int num1, int num2, double e2) {
		if (num1 == num2) {
			return 0;
		}
		double e1 = e[num1], s = trY[num1] * trY[num2], l, h;
		if (s > 0) {
			l = FastMath.max(0, alpha[num1] + alpha[num2] - REG_CONST);
			h = FastMath.min(REG_CONST, alpha[num1] + alpha[num2]);
		} else {
			l = FastMath.max(0, alpha[num2] - alpha[num1]);
			h = FastMath.min(REG_CONST, REG_CONST + alpha[num2] - alpha[num1]);
		}
		if (FastMath.abs(l - h) < ZERO) {
			return 0;
		}
		double k11 = k.mult(x[num1], x[num1]), k22 = k.mult(x[num2], x[num2]), k12 = k
				.mult(x[num1], x[num2]);
		double eta = k11 + k22 - 2 * k12;
		double a1, a2;
		if (eta > 0) {
			a2 = alpha[num2] + trY[num2] * (e1 - e2) / eta;
			if (FastMath.abs(a2) < ZERO) {
				a2 = 0;
			} else if (FastMath.abs(a2 - REG_CONST) < ZERO) {
				a2 = REG_CONST;
			}
			if (a2 < l) {
				a2 = l;
			} else if (a2 > h) {
				a2 = h;
			}
		} else {
			double tmpAlpha[] = Arrays.copyOf(alpha, alpha.length);
			tmpAlpha[num2] = l;
			double lObj = value(tmpAlpha);
			tmpAlpha[num2] = h;
			double hObj = value(tmpAlpha);
			if (lObj < hObj - ZERO) {
				a2 = l;
			} else if (lObj > hObj + ZERO) {
				a2 = h;
			} else {
				a2 = alpha[num2];
			}
		}
		if (FastMath.abs(a2 - alpha[num2]) < ZERO * (a2 + alpha[num2] + ZERO)) {
			return 0;
		}
		a1 = alpha[num1] + s * (alpha[num2] - a2);
		if (FastMath.abs(a1) < ZERO) {
			a1 = 0;
		} else if (FastMath.abs(a1 - REG_CONST) < ZERO) {
			a1 = REG_CONST;
		}
		double b1 = e1 + trY[num1] * (a1 - alpha[num1]) * k11 + trY[num2]
				* (a2 - alpha[num2]) * k12 + b;
		double b2 = e2 + trY[num1] * (a1 - alpha[num1]) * k12 + trY[num2]
				* (a2 - alpha[num2]) * k22 + b;
		double bn = (b1 + b2) / 2;
		recalc(num1, num2, a1, a2, bn);

		// System.out.println("--");
		System.out.println("num " + myNum + " alpha " + num1 + " " + num2
				+ " changed");
		// System.out.println("num " + myNum + " alpha " + num2 +
		// " changed from "
		// + alpha[num2] + " to " + a2);
		// System.out.println("--");

		alpha[num1] = a1;
		alpha[num2] = a2;
		b = bn;
		return 1;
	}

	private void recalc(int num1, int num2, double a1, double a2, double bn) {
		for (int i = 0; i < e.length; ++i) {
			double k1 = k.mult(x[i], x[num1]), k2 = k.mult(x[i], x[num2]);
			e[i] = e[i] + b - trY[num1] * alpha[num1] * k1 - trY[num2]
					* alpha[num2] * k2;
			e[i] = e[i] - bn + trY[num1] * a1 * k1 + trY[num2] * a2 * k2;
		}
	}

	public double value(double[] point) {
		double sum = 0, sum2 = 0;
		for (int i = 0; i < point.length; ++i) {
			for (int j = 0; j < point.length; ++j) {
				sum += trY[i] * trY[j] * point[i] * point[j]
						* k.mult(x[i], x[j]);
			}
			sum2 += point[i];
		}
		sum = (sum / 2) - sum2;

		return sum;
	}
}
