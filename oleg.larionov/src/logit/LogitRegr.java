package logit;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogitRegr {

	public static void main(String[] args) throws Exception {
		int proc = Runtime.getRuntime().availableProcessors();
		CountDownLatch lock = new CountDownLatch(10);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(proc, 10, 1,
				TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
		for (int i = 0; i < 10; ++i) {
			tpe.execute(new Runner(i, 5500, 6000, lock));
		}
		lock.await();
		tpe.shutdownNow();
		PrintWriter pw = new PrintWriter("out.txt");
		for (int i = 0; i < 10; ++i) {
			Scanner sc = new Scanner(new File("out/" + i + ".txt"));
			pw.println(sc.nextLine());
		}
		pw.close();
		CheckLogit.main(null);
	}
}
