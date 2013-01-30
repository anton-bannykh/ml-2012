package kononov.perceptron;

/**
 * User: Vincent
 * Date: 04.12.12
 * Time: 22:43
 */
public class Perceptron {

    private final static String LEARNING_LABELS = "C:/Kononov/learn/train-labels.idx1-ubyte";
    private final static String LEARNING_SET = "C:/Kononov/learn/train-images.idx3-ubyte";
    private final static String TESTING_LABELS = "C:/Kononov/learn/t10k-labels.idx1-ubyte";
    private final static String TESTING_SET = "C:/Kononov/learn/t10k-images.idx3-ubyte";

    public static void main(String[] args) throws Exception{
        Learner learner = new Learner(LEARNING_SET, LEARNING_LABELS);
        learner.init();
        learner.setRounds(1);
        learner.teach();
        learner.checkTest(TESTING_SET, TESTING_LABELS);
    }
}
