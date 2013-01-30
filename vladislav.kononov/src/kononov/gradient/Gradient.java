package kononov.gradient;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 30.01.13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class Gradient {

    private int dataLength;
    private int dataSize;

    private String dataPath;
    private String labelsPath;

    private int[] labels;
    private double[][] data;
    private double[][] values;
    
    private final int MEAN = 128;
    private final int DISP = 1000;
    private final int TETAS = 10;

    public Gradient(String data, String labels){
        dataPath = data;
        labelsPath = labels;
    }

    public void init() throws Exception{
        DataInputStream dReader = new DataInputStream(new FileInputStream(dataPath));
        DataInputStream lReader = new DataInputStream(new FileInputStream(labelsPath));
        dReader.skip(4);
        dataLength = dReader.readInt();
        dataSize = dReader.readInt();
        dataSize = dataSize * dReader.readInt();
        lReader.skip(8);
        dataLength = 2000;
        labels = new int[dataLength];
        data = new double[dataLength][dataSize];
        values = new double[10][dataSize];
        for (int i = 0; i < dataLength; ++i){
            labels[i] = lReader.read();
            for (int k = 0; k < dataSize; ++k){
                data[i][k] = dReader.read();
                data[i][k] = (data[i][k] - MEAN) / DISP;
            }
            if (i % 1000 == 0)
                System.out.println("learning: " + i);
        }
    }

    public void teach(){
        int proc = Runtime.getRuntime().availableProcessors();
        CountDownLatch lock = new CountDownLatch(TETAS);
        ExecutorService executors = Executors.newFixedThreadPool(proc);
        for (int i = 0; i < TETAS; ++i){
            executors.execute(new Teacher(data, labels, i, values, lock));
        }
        try {
            lock.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executors.shutdownNow();
        }
        for (int i = 0; i < TETAS; ++i){
            for (int j = 0; j < values[i].length; ++j){
                System.out.println(values[i][j]);
            }
        }
    }

    public int propose(double[] pic){
        double sum[] = new double[10];
        for (int i = 0; i < 10; ++i){
            for (int k = 0; k < dataSize; ++k){
                sum[i] = sum[i] + values[i][k] * pic[k];
            }
        }
        double limit = sum[0];
        int pos = 0;
        for (int i = 1; i < 10; ++i)
            if (limit < sum[i]){
                limit = sum[i];
                pos = i;
            }
        return pos;
    }

    public void checkTest(String dataT, String labelT) throws Exception{
        DataInputStream dReader = new DataInputStream(new FileInputStream(dataT));
        DataInputStream lReader = new DataInputStream(new FileInputStream(labelT));
        dReader.skip(4);
        int count = dReader.readInt();
        dReader.skip(8);
        lReader.skip(8);
        long errors = 0;
        for (int i = 0; i < count; ++i){
            double[] toCheck = new double[dataSize];
            for (int k = 0; k < dataSize; ++k){
                toCheck[k] = dReader.read();
                toCheck[k] = (toCheck[k] - MEAN) / DISP;
            }
            int label = lReader.read();
            if (label != propose(toCheck))
                ++errors;
            if (i % 1000 == 0)
                System.out.println("Test: " + i);
        }
        System.out.println("Number of errors: " + errors + " out of " + count);
    }
}
