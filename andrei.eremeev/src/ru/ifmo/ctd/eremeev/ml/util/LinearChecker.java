package ru.ifmo.ctd.eremeev.ml.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;

public class LinearChecker {
	
	public static void main(String[] args) {
		DigitInputStream in = null;
		BufferedReader bf = null;
		PrintWriter out = null;
		int err = 0;
		try {
			in = new DigitInputStream(FileNames.TEST_LABELS, FileNames.TEST_IMAGES);
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(FileNames.RESULT), "cp1251"));
			out = new PrintWriter(FileNames.RIGHT_NUMBERS);
			Digit[] ds = new Digit[in.getNumberOfImages()];
			for (int i = 0; i < ds.length; ++i) {
				if ((i + 1) % 1000 == 0) {
					System.out.println("Test read : " + (i + 1));
				}
				ds[i] = in.readDigit();
			}
			int rows = in.getNumberOfRows();
			int columns = in.getNumberOfColumns();
			int[][][] teta = new int[10][rows][columns];
			for (int k = 0; k < 10; ++k) {
				StringTokenizer st = new StringTokenizer(bf.readLine());
				for (int i = 0; i < rows; ++i) {
					for (int j = 0; j < columns; ++j) {
						teta[k][i][j] = Integer.parseInt(st.nextToken());
					}
				}
			}
			int[] sum = new int[10];
			for (int x = 0; x < ds.length; ++x) {
				Arrays.fill(sum, 0);
				int kmax = 0;
				for (int k = 0; k < sum.length; ++k) {
					for (int i = 0; i < rows; ++i) {
						for (int j = 0; j < columns; ++j) {
							sum[k] += teta[k][i][j] * ds[x].get(i, j);
						}
					}
					if (sum[k] > sum[kmax]) {
						kmax = k;
					}
				}
				if (ds[x].getLabel() != kmax) {
					++err;
				} else {
					out.println(x);
				}
			}
			System.out.println("[ERRORS] : " + err + " / " + ds.length);
			System.out.println("[ACCEPTED] : " + (ds.length - err) + " / " + ds.length);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
