import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*Reader r = new Reader("C:\\MachineLearning\\Digits\\TrainingSet\\data\\train-images.idx3-ubyte", "C:\\MachineLearning\\Digits\\TrainingSet\\labels\\train-labels.idx1-ubyte");
		r.readTests();
		LinearPerceptron lp = new LinearPerceptron(r, 28 * 28, false, 2);
		lp.calculate();*/
		/*Reader re = new Reader("C:\\MachineLearning\\Digits\\TestSet\\data\\t10k-images.idx3-ubyte", "C:\\MachineLearning\\Digits\\TestSet\\labels\\t10k-labels.idx1-ubyte");
		re.readTests();
		Tester t = new Tester(re);
		t.test();*/
		Reader r = new Reader("C:\\MachineLearning\\Digits\\TrainingSet\\data\\train-images.idx3-ubyte", "C:\\MachineLearning\\Digits\\TrainingSet\\labels\\train-labels.idx1-ubyte");
		r.readTests();
		LogisticRegression lr = new LogisticRegression(r, 28 * 28, false, 1000000);
		lr.calculate();
		Reader re = new Reader("C:\\MachineLearning\\Digits\\TestSet\\data\\t10k-images.idx3-ubyte", "C:\\MachineLearning\\Digits\\TestSet\\labels\\t10k-labels.idx1-ubyte");
		re.readTests();
		LogisticTester t = new LogisticTester(re);
		t.test();
		/*
		/*BufferedImage bi = new BufferedImage(28, 28, 10);
		DataBuffer db = bi.getRaster().getDataBuffer();
		for (int i = 0; i < 28; ++i) {
			for (int j = 0; j < 28; ++j) {
				db.setElem(i * 28 + j, buf[i * 28 + j]);
			}
		}
		ImageIO.write(bi, "png", new FileImageOutputStream(new File("D:\\images\\out.png")));*/
	}      
	
}
