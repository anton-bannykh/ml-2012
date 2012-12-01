package logit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogitRegr {

	public static final int N = 28, M = 28, COUNT = 60000;
	public static final String IM = "train.imgs", LAB = "labels.imgs";
	public static final double MULT = 1000, SHIFT = 128;

	public static void main(String[] args) throws Exception {
		double[][] x = new double[COUNT][N * M + 1];
		int[] y = new int[COUNT];

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
				x[w][N * M] = 1;
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
			tpe.execute(new Runner(i, x, y, lock));
		}
		lock.await();

		tpe.shutdownNow();
		PrintWriter pw = new PrintWriter("out.txt");
		for (int i = 0; i < 10; ++i) {
			Scanner sc = new Scanner(new File("out/" + i + ".txt"));
			pw.println(sc.nextLine());
		}
		pw.close();
	}
}
