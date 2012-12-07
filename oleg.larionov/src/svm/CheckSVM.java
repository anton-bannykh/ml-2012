package svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class CheckSVM {

	public static final String IM = "cimgs", LAB = "clabels",
			INPUT = "out.txt";
	public static final int COUNT = 10000;

	public static void main(String[] args) throws Exception {
		DataInputStream imgs = new DataInputStream(new FileInputStream(IM)), labels = new DataInputStream(
				new FileInputStream(LAB));
		imgs.skip(16);
		labels.skip(8);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(INPUT)));

		double w[][] = new double[10][SVM.N * SVM.M], w0[] = new double[10];
		int err = 0;
		for (int k = 0; k < 10; ++k) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for (int i = 0; i < SVM.N; ++i) {
				for (int j = 0; j < SVM.M; ++j) {
					w[k][i * SVM.N + j] = Double.parseDouble(st.nextToken());
				}
			}
			w0[k] = Double.parseDouble(st.nextToken());
		}

		double cur[] = new double[SVM.N * SVM.M], val[] = new double[10], max;
		int num, maxn;
		for (int c = 0; c < COUNT; ++c) {
			if ((c + 1) % 1000 == 0) {
				System.out.println(c + 1);
			}
			num = labels.read();
			for (int i = 0; i < SVM.N; ++i) {
				for (int j = 0; j < SVM.M; ++j) {
					cur[i * SVM.N + j] = (imgs.read() - SVM.SHIFT) / SVM.MULT;
				}
			}

			for (int i = 0; i < 10; ++i) {
				val[i] = SVM.k.mult(w[i], cur) - w0[i];
			}

			maxn = 0;
			max = val[maxn];
			for (int i = 1; i < 10; ++i) {
				if (max < val[i]) {
					maxn = i;
					max = val[maxn];
				}
			}

			if (num != maxn) {
				++err;
			}
		}
		System.out.println("total " + err + " errors");
	}
}
