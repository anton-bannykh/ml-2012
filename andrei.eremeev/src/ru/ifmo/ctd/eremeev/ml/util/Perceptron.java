package ru.ifmo.ctd.eremeev.ml.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;

public class Perceptron {
	
	private static final int ITERATIONS = 3;
	
	private static String time(long start) {
		long time = (System.currentTimeMillis() - start) / 1000;
		return time / 60 + " minutes " + time % 60 + " seconds";
	}
	
	public static void main(String[] args) {
		DigitInputStream in = null;
		PrintWriter out = null;
		long start = System.currentTimeMillis();
		try {
			in = new DigitInputStream(FileNames.TRAIN_LABELS, FileNames.TRAIN_IMAGES);
			Digit[] ds = new Digit[in.getNumberOfImages()];
			for (int i = 0; i < ds.length; ++i) {
				if ((i + 1) % 1000 == 0) {
					System.out.println("Test read : " + (i + 1));
				}
				ds[i] = in.readDigit();
			}
			int[] sum = new int[10];
			int rows = in.getNumberOfRows();
			int columns = in.getNumberOfColumns();
			int[][][] teta = new int[10][rows][columns];
			for (int iteration = 1; iteration <= ITERATIONS; ++iteration) {
				for (int x = 0; x < ds.length; ++x) {
					Arrays.fill(sum, 0);
					for (int i = 0; i < rows; ++i) {
						for (int j = 0; j < columns; ++j) {
							for (int k = 0; k < 10; ++k) {
								sum[k] += teta[k][i][j] * ds[x].get(i, j);
							}
						}
					}
					/*int kmax = 0;
					for (int k = 0; k < 10; ++k) {
						if (sum[k] > sum[kmax]) {
							kmax = k;
						}
					}
					if (kmax != ds[x].getLabel()) {
						for (int i = 0; i < rows; ++i) {
							for (int j = 0; j < columns; ++j) {
								teta[kmax][i][j] -= ds[x].get(i, j);
								teta[ds[x].getLabel()][i][j] += ds[x].get(i, j);
							}
						}
					}*/
					for (int k = 0; k < 10; ++k) {
						int y = k == ds[x].getLabel() ? 1 : -1;
						if (sum[k] * y <= 0) {
							for (int i = 0; i < rows; ++i) {
								for (int j = 0; j < columns; ++j) {
									teta[k][i][j] += y * ds[x].get(i, j);
								}
							}
						}
					}
				}
				System.out.println("Iteration : " + iteration + " complete");
				if (iteration % 10 == 0) {
					System.out.println(time(start));
				}
			}
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileNames.RESULT), "cp1251"));
			for (int k = 0; k < 10; ++k) {
				for (int i = 0; i < rows; ++i) {
					for (int j = 0; j < columns; ++j) {
						out.print(teta[k][i][j] + " ");
					}
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
		System.out.println(time(start));
	}
}
