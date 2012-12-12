package svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CheckSVM {

	public static final String IM = "cimgs", LAB = "clabels",
			INPUT = "out.txt", TRAIN_IMGS = "train.imgs",
			TRAIN_LABELS = "labels.imgs";
	public static final int TRAIN_COUNT = 60000, CHECK_COUNT = 10000;

	public static void main(String[] args) {
		Kernel k = null;
		double mult, shift;
		List<List<Pair>> alpha = new ArrayList<List<Pair>>();
		double b[] = new double[10];
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(INPUT)))) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			shift = Double.parseDouble(st.nextToken());
			mult = Double.parseDouble(st.nextToken());
			Double.parseDouble(st.nextToken()); // reg_const
			switch (Kernels.valueOf(st.nextToken())) {
			case GAUSSIAN:
				k = new Gaussian(Double.parseDouble(st.nextToken()));
				break;

			case SCALAR:
				k = new Scalar();
				break;
			}

			for (int i = 0; i < 10; ++i) {
				st = new StringTokenizer(br.readLine() + " ");
				alpha.add(new ArrayList<Pair>());
				int num;
				double cur;
				while (st.hasMoreTokens()) {
					num = Integer.parseInt(st.nextToken());
					cur = Double.parseDouble(st.nextToken());
					if (num == TRAIN_COUNT) {
						b[i] = cur;
					} else {
						alpha.get(i).add(new Pair(cur, num));
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Cannot read svm from file. " + e.getMessage());
			return;
		}

		double x[][] = new double[TRAIN_COUNT][SVM.N * SVM.M], trY[][] = new double[10][TRAIN_COUNT], y;
		try (DataInputStream trainImgs = new DataInputStream(
				new FileInputStream(TRAIN_IMGS));
				DataInputStream trainLabels = new DataInputStream(
						new FileInputStream(TRAIN_LABELS))) {
			trainImgs.skip(16);
			trainLabels.skip(8);
			for (int i = 0; i < TRAIN_COUNT; ++i) {
				for (int j = 0; j < SVM.N * SVM.M; ++j) {
					x[i][j] = (trainImgs.read() - shift) / mult;
				}
				y = trainLabels.read();
				for (int j = 0; j < 10; ++j) {
					trY[j][i] = y == j ? 1.0 : -1.0;
				}
				if ((i + 1) % 10000 == 0) {
					System.out.println("train " + (i + 1));
				}
			}
		} catch (IOException e) {
			System.err.println("Cannot read train data. " + e.getMessage());
			return;
		}

		int err = 0;
		try (DataInputStream checkImgs = new DataInputStream(
				new FileInputStream(IM));
				DataInputStream checkLabels = new DataInputStream(
						new FileInputStream(LAB))) {
			checkImgs.skip(16);
			checkLabels.skip(8);
			double cur[] = new double[SVM.N * SVM.M], val[] = new double[10];
			for (int i = 0; i < CHECK_COUNT; ++i) {
				for (int j = 0; j < cur.length; ++j) {
					cur[j] = (checkImgs.read() - shift) / mult;
				}

				for (int j = 0; j < 10; ++j) {
					val[j] = calc(cur, alpha.get(j), x, trY[j], b[j], k);
				}

				int maxk = 0;
				double max = val[maxk];
				for (int j = 1; j < 10; ++j) {
					if (val[j] > max) {
						maxk = j;
						max = val[maxk];
					}
				}

				if (maxk != checkLabels.read()) {
					++err;
				}
				if ((i + 1) % 1000 == 0) {
					System.out.println(i + 1 + " " + err);
				}
			}
			System.out.println("total " + err + " errors");
		} catch (IOException e) {
			System.err.println("Cannot read check data. " + e.getMessage());
			return;
		}
	}

	public static double calc(double cur[], List<Pair> alpha, double x[][],
			double trY[], double b, Kernel k) {
		double sum = -b;
		for (Pair p : alpha) {
			sum += trY[p.num] * p.alpha * k.mult(cur, x[p.num]);
		}
		return sum;
	}

	public static class Pair {
		private double alpha;
		private int num;

		public Pair(double alpha, int num) {
			this.alpha = alpha;
			this.num = num;
		}
	}
}
