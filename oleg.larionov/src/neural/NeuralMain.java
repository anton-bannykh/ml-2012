package neural;

import java.io.DataInputStream;
import java.io.FileInputStream;

public class NeuralMain {

	public static final int N = 28, M = 28, COUNT = 50000, CHECK = 10000;
	public static final String IM = "train.imgs", LAB = "labels.imgs";
	private static final double MULT = 1000, SHIFT = 127.5;

	public static void main(String[] args) {
		double[][] x = new double[COUNT][N * M + 1], xCheck = new double[CHECK][N
				* M + 1];
		int[] y = new int[COUNT], yCheck = new int[CHECK];

		try (DataInputStream imgs = new DataInputStream(new FileInputStream(IM));
				DataInputStream labels = new DataInputStream(
						new FileInputStream(LAB));) {
			imgs.skip(16);
			labels.skip(8);

			System.out.println("reading numbers");
			for (int w = 0; w < COUNT; ++w) {
				if ((w + 1) % 10000 == 0) {
					System.out.println("reading train " + (w + 1));
				}

				y[w] = labels.read();
				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < M; ++j) {
						x[w][i * N + j] = (imgs.read() - SHIFT) / MULT;
					}
				}
				x[w][N * M] = 1.0;
			}

			for (int w = 0; w < CHECK; ++w) {
				if ((w + 1) % 10000 == 0) {
					System.out.println("reading check " + (w + 1));
				}

				yCheck[w] = labels.read();
				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < M; ++j) {
						xCheck[w][i * N + j] = (imgs.read() - SHIFT) / MULT;
					}
				}
				x[w][N * M] = 1.0;
			}
		} catch (Exception e) {
			System.err.println("fail reading images " + e.getMessage());
			return;
		}

		Network net = new Network(x, y, 300, 1, xCheck, yCheck);
		net.teach(100);
	}
}
