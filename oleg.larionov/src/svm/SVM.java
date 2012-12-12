package svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SVM {

	public static final int N = 28, M = 28, COUNT = 60000;
	public static final String IM = "train.imgs", LAB = "labels.imgs";

	private static final double MULT = 127.5, SHIFT = 127.5,
			GAMMA = 1.0 / (28.0 * 28.0), REG_CONST = 2.82842;
	private static final Kernels KERNEL = Kernels.GAUSSIAN;

	public static void main(String[] args) throws InterruptedException {
		double[][] x = new double[COUNT][N * M];
		int[] y = new int[COUNT];
		Kernel k = null;
		switch (KERNEL) {
		case GAUSSIAN:
			k = new Gaussian(GAMMA);
			break;

		case SCALAR:
			k = new Scalar();
			break;
		}
		try {
			DataInputStream imgs = new DataInputStream(new FileInputStream(IM)), labels = new DataInputStream(
					new FileInputStream(LAB));
			imgs.skip(16);
			labels.skip(8);

			System.out.println("reading numbers");
			for (int w = 0; w < COUNT; ++w) {
				if ((w + 1) % 10000 == 0) {
					System.out.println("reading " + (w + 1));
				}

				y[w] = labels.read();
				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < M; ++j) {
						x[w][i * N + j] = (imgs.read() - SHIFT) / MULT;
					}
				}
			}
			imgs.close();
			labels.close();
		} catch (Exception e) {
			System.err.println("fail reading images " + e.getMessage());
			return;
		}

		int proc = Runtime.getRuntime().availableProcessors();
		CountDownLatch lock = new CountDownLatch(10);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(proc, proc, 1,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		for (int i = 0; i < 10; ++i) {
			tpe.execute(new Runner(i, x, y, k, REG_CONST, lock));
		}
		lock.await();

		tpe.shutdownNow();
		try (PrintWriter pw = new PrintWriter("out.txt");) {
			pw.print(MULT + " " + SHIFT + " " + REG_CONST + " " + KERNEL + " ");
			switch (KERNEL) {
			case GAUSSIAN:
				pw.print(GAMMA);
				break;
			default:
				break;
			}
			pw.println();
			for (int i = 0; i < 10; ++i) {
				try (BufferedReader br = new BufferedReader(new FileReader(
						new File("out/" + i + ".txt")))) {
					pw.println(br.readLine());
				}
			}
			System.out.println("out.txt created");
		} catch (IOException e) {
			System.err.println("cannot create out.txt. " + e.getMessage());
			return;
		}
	}
}
