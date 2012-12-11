package ru.ifmo.ctd.eremeev.ml.logitmodel;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;

import ru.ifmo.ctd.eremeev.ml.util.Digit;

public class Teacher implements Runnable {

	private Digit[] ds;
	private int label;
	private double[] teta;
	private int iterations;
	private CountDownLatch count;
	
	public Teacher(Digit[] ds, int label, double[] teta, int iterations, CountDownLatch cnt) {
		this.ds = ds;
		this.label = label;
		this.teta = teta;
		this.iterations = iterations;
		this.count = cnt;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Label : " + label + " starts");
			DifferentiableMultivariateFunction ff = new FitnessFunction(ds, label);
			NonLinearConjugateGradientOptimizer opt = 
					new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE, new SimpleValueChecker(0.05, -1));
			PointValuePair pv = opt.optimize(iterations, ff, GoalType.MINIMIZE, teta);
			System.arraycopy(pv.getPointRef(), 0, teta, 0, teta.length);
			System.out.println("Label : " + label + " is complete");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			count.countDown();
		}
	}

}
