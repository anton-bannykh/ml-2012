package svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import svm.CheckSVM.Pair;

public class GridSearch {

	public static final String IM = "train.imgs", LAB = "labels.imgs",
			INPUT = "out.txt";
	public static final int TRAIN_COUNT = 30000, CHECK_COUNT = 10000, N = 28,
			M = 28;
	private static final double MULT = 6000, SHIFT = 127.5;

	private static double[][] xTrain = new double[TRAIN_COUNT][N * M],
			xCheck = new double[CHECK_COUNT][N * M],
			trYTrain = new double[10][TRAIN_COUNT];
	private static int[] yTrain = new int[TRAIN_COUNT],
			yCheck = new int[CHECK_COUNT];
	private static Kernel k;

	public static void main(String[] args) throws Exception {

		try (DataInputStream imgs = new DataInputStream(new FileInputStream(IM));
				DataInputStream labels = new DataInputStream(
						new FileInputStream(LAB));) {
			imgs.skip(16);
			labels.skip(8);

			System.out.println("reading numbers");
			for (int w = 0; w < TRAIN_COUNT; ++w) {
				if ((w + 1) % 10000 == 0) {
					System.out.println("reading train " + (w + 1));
				}

				yTrain[w] = labels.read();
				for (int i = 0; i < 10; ++i) {
					trYTrain[i][w] = yTrain[w] == i ? 1.0 : -1.0;
				}
				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < M; ++j) {
						xTrain[w][i * N + j] = (imgs.read() - SHIFT) / MULT;
					}
				}
			}
			for (int w = 0; w < CHECK_COUNT; ++w) {
				if ((w + 1) % 10000 == 0) {
					System.out.println("reading cross " + (w + 1));
				}

				yCheck[w] = labels.read();

				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < M; ++j) {
						xCheck[w][i * N + j] = (imgs.read() - SHIFT) / MULT;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("fail reading images " + e.getMessage());
			return;
		}

		searchPoly();
		System.out.println("done");
	}

	private static void searchGauss() throws InterruptedException {
		int proc = Runtime.getRuntime().availableProcessors();
		CountDownLatch lock;
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(proc, proc, 1,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		for (double gamma = 0.004; gamma <= 0.01; gamma += 0.001) {
			for (double reg = 3; reg <= 3.5; reg += 0.5) {
				k = new Gaussian(gamma);
				lock = new CountDownLatch(10);
				System.out.println("begin calc " + gamma + " " + reg);
				for (int i = 0; i < 10; ++i) {
					tpe.execute(new Runner(i, xTrain, yTrain, k, reg, lock));
				}
				lock.await();

				try (PrintWriter pw = new PrintWriter("out.txt");) {
					for (int i = 0; i < 10; ++i) {
						try (BufferedReader br = new BufferedReader(
								new FileReader(new File("out/" + i + ".txt")))) {
							pw.println(br.readLine());
						}
					}
					System.out.println("out.txt created");
				} catch (IOException e) {
					System.err.println("cannot create out.txt. "
							+ e.getMessage());
					return;
				}
				int err = check();
				System.out.println("gamma " + gamma + " reg " + reg + " err "
						+ err);
			}
		}
	}

	private static void searchPoly() throws InterruptedException {
		int proc = Runtime.getRuntime().availableProcessors();
		CountDownLatch lock;
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(proc, proc, 1,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		for (int deg = 4; deg <= 4; ++deg) {
			for (double reg = 7.0; reg <= 20.0; reg += 1.0) {
				k = new InhomoPoly(deg);
				lock = new CountDownLatch(10);
				System.out.println("begin calc " + deg + " " + reg);
				for (int i = 0; i < 10; ++i) {
					tpe.execute(new Runner(i, xTrain, yTrain, k, reg, lock));
				}
				lock.await();

				try (PrintWriter pw = new PrintWriter("out.txt");) {
					for (int i = 0; i < 10; ++i) {
						try (BufferedReader br = new BufferedReader(
								new FileReader(new File("out/" + i + ".txt")))) {
							pw.println(br.readLine());
						}
					}
					System.out.println("out.txt created");
				} catch (IOException e) {
					System.err.println("cannot create out.txt. "
							+ e.getMessage());
					return;
				}
				int err = check();
				System.out.println("degree " + deg + " reg " + reg + " err "
						+ err);
			}
		}
	}

	private static int check() {
		System.out.println("testing...");
		List<List<Pair>> alpha = new ArrayList<List<Pair>>();
		double b[] = new double[10];
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(INPUT)))) {
			for (int i = 0; i < 10; ++i) {
				StringTokenizer st = new StringTokenizer(br.readLine() + " ");
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
			return -1;
		}

		int err = 0;
		double val[] = new double[10];
		for (int i = 0; i < CHECK_COUNT; ++i) {

			for (int j = 0; j < 10; ++j) {
				val[j] = CheckSVM.calc(xCheck[i], alpha.get(j), xTrain,
						trYTrain[j], b[j], k);
			}

			int maxk = 0;
			double max = val[maxk];
			for (int j = 1; j < 10; ++j) {
				if (val[j] > max) {
					maxk = j;
					max = val[maxk];
				}
			}

			if (maxk != yCheck[i]) {
				++err;
			}
			if ((i + 1) % 1000 == 0) {
				System.out.println(i + 1 + " " + err);
			}
		}
		return err;
	}
}