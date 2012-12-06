package ru.ifmo.ctddev.baidarov.logisticregression;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;
import ru.ifmo.ctddev.baidarov.util.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: Baidarov Andrew
 * Date: 05.12.12
 * Time: 4:01
 */
public class Teacher implements Runnable {
    private final Test[] trainingSet;
    private final int curLabel;
    private final double[][] theta;
    private final CountDownLatch lock;
    private static final int STEPS = 10000;

    public Teacher(Test[] trainingSet, int curLabel, double[][] theta, CountDownLatch lock) {
        this.trainingSet = trainingSet;
        this.curLabel = curLabel;
        this.theta = theta;
        this.lock = lock;
    }

    @Override
    public void run() {
        System.out.println("learn " + curLabel);

        DifferentiableMultivariateFunction f = new CostFunction(trainingSet, curLabel);
        NonLinearConjugateGradientOptimizer opt = new NonLinearConjugateGradientOptimizer(
                ConjugateGradientFormula.FLETCHER_REEVES, new SimpleValueChecker(0, 0.1));
        PointValuePair p = opt.optimize(STEPS, f, GoalType.MINIMIZE, theta[curLabel]);
        theta[curLabel] = p.getPointRef();
        lock.countDown();

        System.out.println("learn finished" + curLabel);
    }
}
