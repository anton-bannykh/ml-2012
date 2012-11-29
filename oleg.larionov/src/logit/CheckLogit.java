package logit;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class CheckLogit {

	public static final String im = "cimgs", lab = "clabels",
			input = "out.txt";
	public static int n = 28, m = 28, count = 10000, myNum = 0;

	public static void main(String[] args) throws Exception {
		DataInputStream imgs = new DataInputStream(new FileInputStream(im)), labels = new DataInputStream(
				new FileInputStream(lab));
		imgs.skip(16);
		labels.skip(8);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(input)));

		double w[][] = new double[10][n * m], w0[] = new double[10];
		int err = 0;
		for (int k = 0; k < 10; ++k) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			w0[k] = Double.parseDouble(st.nextToken());
			for (int i = 0; i < n; ++i) {
				for (int j = 0; j < m; ++j) {
					w[k][i * n + j] = Double.parseDouble(st.nextToken());
				}
			}
		}

		double cur[] = new double[n * m], val[] = new double[10], max;
		int num, maxn;
		for (int c = 0; c < count; ++c) {
			if ((c + 1) % 1000 == 0) {
				System.out.println(c + 1);
			}
			num = labels.read();
			for (int i = 0; i < n; ++i) {
				for (int j = 0; j < m; ++j) {
					cur[i * n + j] = (1.0 * imgs.read() - Runner.SHIFT) / Runner.MULT;
				}
			}

			for (int i = 0; i < 10; ++i) {
				val[i] = Runner.mult(cur, w[i]) + w0[i];
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
