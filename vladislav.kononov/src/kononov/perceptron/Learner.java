package kononov.perceptron;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 04.12.12
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class Learner {
    
    private String dataPath;
    private String labelsPath;
    
    private int dataLength;
    private int dataSize;
    private int rounds = 0;

    private int labels[];
    private int data[][];
    private int values[][];

    public Learner(String data, String labels){
        dataPath = data;
        labelsPath = labels;
        rounds = 1;
    }

    public void init() throws Exception{
        DataInputStream dReader = new DataInputStream(new FileInputStream(dataPath));
        DataInputStream lReader = new DataInputStream(new FileInputStream(labelsPath));
        dReader.skip(4);
        dataLength = dReader.readInt();
        dataSize = dReader.readInt();
        dataSize = dataSize * dReader.readInt();
        lReader.skip(8);
        labels = new int[dataLength];
        data = new int[dataLength][dataSize];
        values = new int[10][dataSize];
        for (int i = 0; i < dataLength; ++i){
            labels[i] = lReader.read();
            for (int k = 0; k < dataSize; ++k){
                data[i][k] = dReader.read();
            }
            if (i % 1000 == 0)
                System.out.println("learning: " + i);
        }
    }
    
    public int propose(int[] pic){
        long sum[] = new long[10];
        for (int i = 0; i < 10; ++i){
            for (int k = 0; k < dataSize; ++k){
                sum[i] = sum[i] + values[i][k] * pic[k];
            }
        }
        long limit = sum[0];
        int pos = 0;
        for (int i = 1; i < 10; ++i)
            if (limit < sum[i]){
                limit = sum[i];
                pos = i;
            }
        return pos;
    }

    public void arithm(int[] pic, int l){
        int z = propose(pic);
        if (z != l)
            for (int i = 0; i < dataSize; ++i){
                values[l][i] = values[l][i] + pic[i];
            }
    }
    
    public void teach(){
        for (int i = 0; i < rounds; ++i){
            for (int k = 0; k < dataLength; ++k){
                arithm(data[k], labels[k]);
                if (k % 1000 == 0)
                    System.out.println("Rounds: " + i + " | Pic: " + k);
            }
        }
    }
    
    public void setRounds(int r){
        rounds = r;
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
            int[] toCheck = new int[dataSize];
            for (int k = 0; k < dataSize; ++k)
                toCheck[k] = dReader.read();
            int label = lReader.read();
            if (label != propose(toCheck))
                ++errors;
            if (i % 1000 == 0)
                System.out.println("Test: " + i);
        }
        System.out.println("Number of errors: " + errors + " out of " + count);
    }
    
    
}
