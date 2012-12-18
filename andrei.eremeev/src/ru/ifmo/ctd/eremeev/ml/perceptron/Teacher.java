package ru.ifmo.ctd.eremeev.ml.perceptron;

import java.util.concurrent.CountDownLatch;

import ru.ifmo.ctd.eremeev.ml.util.Digit;

public class Teacher implements Runnable {

	private Digit[] ds;
	private int label;
	private int[] teta;
	private int iterations;
	private CountDownLatch count;
	
	public Teacher(Digit[] ds, int label, int[] teta, int i, CountDownLatch cnt) {
		this.ds = ds;
		this.label = label;
		this.teta = teta;
		this.iterations = i;
		this.count = cnt;
	}
	
	public int y(int n, int m) {
		return n == m ? 1 : -1;
	}
	
	@Override
	public void run() {
		try {
			for (int iteration = 1; iteration <= iterations; ++iteration) {
				for (int x = 0; x < ds.length; ++x) {
					int sum = 0;
					for (int i = 0; i < teta.length; ++i) {
						sum += teta[i] * ds[x].get(i);
					}
					int y = y(label, ds[x].getLabel());
					if (sum * y <= 0) {
						for (int i = 0; i < teta.length; ++i) {
							teta[i] += y * ds[x].get(i);
						}
					}
				}
				if (iteration % 10 == 0) {
					System.out.println("Label : " + label + " Iteration : " + iteration + " complete");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			count.countDown();
		}
	}
}
