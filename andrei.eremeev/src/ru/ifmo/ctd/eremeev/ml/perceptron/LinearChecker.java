package ru.ifmo.ctd.eremeev.ml.perceptron;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;
import ru.ifmo.ctd.eremeev.ml.util.Digit;
import ru.ifmo.ctd.eremeev.ml.util.Utils;

public class LinearChecker {
	
	public static void main(String[] args) {
		DigitInputStream in = null;
		BufferedReader bf = null;
		int err = 0;
		try {
			in = new DigitInputStream(Utils.TEST_LABELS, Utils.TEST_IMAGES);
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.RESULT_PERCEPTRON), "cp1251"));
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
			for (int k = 0; k < 10; ++k) {
				StringTokenizer st = new StringTokenizer(bf.readLine());
				for (int i = 0; i < teta[k].length; ++i) {
					teta[k][i] = Integer.parseInt(st.nextToken());
				}
			}
			int[] sum = new int[10];
			for (int x = 0; x < ds.length; ++x) {
				Arrays.fill(sum, 0);
				int kmax = 0;
				for (int k = 0; k < sum.length; ++k) {
					for (int i = 0; i < teta[k].length; ++i) {
						sum[k] += teta[k][i] * ds[x].get(i);
					}
					if (sum[k] > sum[kmax]) {
						kmax = k;
					}
				}
				if (ds[x].getLabel() != kmax) {
					++err;
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
		}
	}
}
