package ru.ifmo.ctd.eremeev.ml.perceptron;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;
import ru.ifmo.ctd.eremeev.ml.util.Digit;
import ru.ifmo.ctd.eremeev.ml.util.Utils;

public class Perceptron {
	
	private static final int ITERATIONS = 2;
	
	
	
	public static void main(String[] args) {
		DigitInputStream in = null;
		PrintWriter out = null;
		long start = System.currentTimeMillis();
		try {
			in = new DigitInputStream(Utils.TRAIN_LABELS, Utils.TRAIN_IMAGES);
			Digit[] ds = new Digit[in.getNumberOfImages()];
			for (int i = 0; i < ds.length; ++i) {
				if ((i + 1) % 1000 == 0) {
					System.out.println("Test read : " + (i + 1));
				}
				ds[i] = in.readDigit();
			}
			
			int rows = in.getNumberOfRows();
			int columns = in.getNumberOfColumns();
			int[][] teta = new int[10][rows * columns];
			
			CountDownLatch count = new CountDownLatch(10);
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for (int k = 0; k < 10; ++k) {
				executor.execute(new Teacher(ds, k, teta[k], ITERATIONS, count));
			}
			count.await();
			
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(Utils.RESULT_PERCEPTRON), "cp1251"));
			for (int k = 0; k < 10; ++k) {
				for (int i = 0; i < teta[k].length; ++i) {
					out.print(teta[k][i] + " ");
				}
				out.println();
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				out.close();
			}
		}
		System.out.println(Utils.time(start));
	}
}
