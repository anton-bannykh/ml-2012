package kononov.gradient;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 30.01.13
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    private final static String LEARNING_LABELS = "C:/Kononov/learn/train-labels.idx1-ubyte";
    private final static String LEARNING_SET = "C:/Kononov/learn/train-images.idx3-ubyte";
    private final static String TESTING_LABELS = "C:/Kononov/learn/t10k-labels.idx1-ubyte";
    private final static String TESTING_SET = "C:/Kononov/learn/t10k-images.idx3-ubyte";
    
    public static void main(String[] args) throws Exception {
        Gradient gradient = new Gradient(LEARNING_SET, LEARNING_LABELS);
        gradient.init();
        gradient.teach();
        gradient.checkTest(TESTING_SET, TESTING_LABELS);
    }
    
}
