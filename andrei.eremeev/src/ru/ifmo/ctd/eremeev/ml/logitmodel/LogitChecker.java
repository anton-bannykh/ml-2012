package ru.ifmo.ctd.eremeev.ml.logitmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

import ru.ifmo.ctd.eremeev.ml.io.DigitInputStream;
import ru.ifmo.ctd.eremeev.ml.util.Digit;
import ru.ifmo.ctd.eremeev.ml.util.Utils;

public class LogitChecker {

	public static void main(String[] args) {
		DigitInputStream is = null;
		BufferedReader bf = null;
		Locale.setDefault(new Locale("uk", "us"));
		try {
			is = new DigitInputStream(Utils.TEST_LABELS, Utils.TEST_IMAGES);
			Digit[] ds = new Digit[is.getNumberOfImages()];
			for (int i = 0; i < ds.length; ++i) {
				if ((i + 1) % 1000 == 0) {
					System.out.println("Test read : " + (i + 1));
				}
				ds[i] = is.readDigit();
			}
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.RESULT_LOGIT_MODEL), "cp1251"));
			int n = is.getNumberOfRows() * is.getNumberOfColumns();
			double[][] teta = new double[10][n];
			for (int k = 0; k < teta.length; ++k) {
				StringTokenizer st = new StringTokenizer(bf.readLine());
				for (int i = 0; i < n; ++i) {
					teta[k][i] = Double.parseDouble(st.nextToken());
				}
			}
			
			int err = 0;
			double[] sum = new double[10];
			for (Digit d : ds) {
				Arrays.fill(sum, 0);
				double[] x = d.imageToDoubleArray();
				int kmax = 0;
				for (int k = 0; k < 10; ++k) {
					for (int i = 0; i < x.length; ++i) {
						sum[k] += x[i] * teta[k][i];
					}
					if (sum[kmax] < sum[k]) {
						kmax = k; 
					}
				}
				if (kmax != d.getLabel()) {
					++err;
				}
			}
			System.out.println("[ERRORS] : " + err + " / " + ds.length);
			System.out.println("[ACCEPTED] : " + (ds.length - err) + " / " + ds.length);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
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
