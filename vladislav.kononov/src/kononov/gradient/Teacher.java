package kononov.gradient;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;

import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 31.01.13
 * Time: 0:41
 * To change this template use File | Settings | File Templates.
 */
public class Teacher implements Runnable {

    private final double[][] data;
    private final int[] labels;
    private final int curLabel;
    private final double[][] theta;
    private final CountDownLatch lock;
    private static final int MAX_STEPS = 10000;

    public Teacher(double[][] data, int[] labels, int curLabel, double[][] theta, CountDownLatch lock) {
        this.data = data;
        this.labels = labels;
        this.curLabel = curLabel;
        this.theta = theta;
        this.lock = lock;
    }

    @Override
    public void run() {
        System.out.println("Theta for " + curLabel);

        DifferentiableMultivariateFunction f = new WeightFunction(data, labels, curLabel);
        NonLinearConjugateGradientOptimizer opt = new NonLinearConjugateGradientOptimizer(
                ConjugateGradientFormula.FLETCHER_REEVES, new SimpleValueChecker(0, 0.1));
        PointValuePair p = opt.optimize(MAX_STEPS, f, GoalType.MINIMIZE, theta[curLabel]);
        theta[curLabel] = p.getPointRef();
        lock.countDown();

        System.out.println("Found theta for" + curLabel);
    }
}
