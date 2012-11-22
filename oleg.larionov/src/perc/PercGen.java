package perc;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class PercGen {

	public static final String TRAIN_NAME = "train.imgs",
			LABELS_NAME = "labels.imgs", RESULT = "out.txt";

	public static void main(String[] args) throws Exception {
		DataInputStream imgs = new DataInputStream(new FileInputStream(
				TRAIN_NAME)), labels = new DataInputStream(new FileInputStream(
				LABELS_NAME));
		imgs.skip(4);
		labels.skip(8);
		int count = imgs.readInt(), n = imgs.readInt(), m = imgs.readInt(), num;
		// n x m
		int[][][] omega = new int[10][n][m];
		int[] sum = new int[10];
		int[][] cur;
		for (int r = 0; r < 2; ++r) {
			imgs = new DataInputStream(new FileInputStream(TRAIN_NAME));
			labels = new DataInputStream(new FileInputStream(LABELS_NAME));
			imgs.skip(16);
			labels.skip(8);
			for (int c = 0; c < count; ++c) {
				if (c % 1000 == 0) {
					System.out.println(r + " " + c);
				}
				num = labels.read();
				Arrays.fill(sum, 0);
				cur = new int[n][m];
				for (int i = 0; i < n; ++i) {
					for (int j = 0; j < m; ++j) {
						cur[i][j] = imgs.read();
						for (int k = 0; k < 10; ++k) {
							sum[k] += omega[k][i][j] * cur[i][j];
						}
					}
				}
				int max = sum[0], maxk = 0;
				for (int k = 0; k < 10; ++k) {
					if (max < sum[k]) {
						max = sum[k];
						maxk = k;
					}
				}
				if (num != maxk) {
					for (int i = 0; i < n; ++i) {
						for (int j = 0; j < m; ++j) {
							omega[num][i][j] += cur[i][j];
							omega[maxk][i][j] -= cur[i][j];
						}
					}
				}
			}
		}
		PrintWriter pw = new PrintWriter(RESULT);
		for (int k = 0; k < 10; ++k) {
			for (int i = 0; i < n; ++i) {
				for (int j = 0; j < m; ++j) {
					pw.print(omega[k][i][j] + " ");
				}
			}
			pw.println();
		}
		pw.close();
	}
}
