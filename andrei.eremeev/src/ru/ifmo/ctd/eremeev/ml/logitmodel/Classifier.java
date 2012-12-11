package ru.ifmo.ctd.eremeev.ml.logitmodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;
import ru.ifmo.ctd.eremeev.ml.util.Digit;
import ru.ifmo.ctd.eremeev.ml.util.Utils;
public class Classifier {

	private static final int ITERATIONS = 10000;
	private static final double LAMBDA = 0;
	private static final double ETA = -1;
	private static final double EPS = 0.01;
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		DigitInputStream is = null;
		PrintWriter out = null;
		Locale.setDefault(new Locale("uk", "us"));
		try {
			is = new DigitInputStream(Utils.TRAIN_LABELS, Utils.TRAIN_IMAGES);
			Digit[] ds = new Digit[is.getNumberOfImages()];
			for (int i = 0; i < ds.length; ++i) {
				if ((i + 1) % 10000 == 0) {
					System.out.println("Test read : " + (i + 1));
				}
				ds[i] = is.readDigit();
			}
			
			int n = is.getNumberOfColumns() * is.getNumberOfRows();
			double[][] teta = new double[10][n];
			
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			CountDownLatch count = new CountDownLatch(10);
			for (int k = 0; k < 10; ++k) {
				executor.execute(new Teacher(ds, k, teta[k], ITERATIONS, count, LAMBDA, ETA, EPS));
			}
			count.await();
			
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(Utils.RESULT_LOGIT_MODEL), "cp1251"));
			for (int k = 0; k < 10; ++k) {
				for (int i = 0; i < n; ++i) {
					out.print(teta[k][i] + " ");
				}
				out.println();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
			if (out != null) {
				out.close();
			}
		}
		System.out.println(Utils.time(start));
	}
}
