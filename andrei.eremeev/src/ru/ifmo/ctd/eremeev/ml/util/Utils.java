package ru.ifmo.ctd.eremeev.ml.util;

public class Utils {
	public static final String TRAIN_LABELS = "image/train-labels.idx1-ubyte";
	public static final String TRAIN_IMAGES = "image/train-images.idx3-ubyte";
	public static final String TEST_LABELS = "image/t10k-labels.idx1-ubyte";
	public static final String TEST_IMAGES = "image/t10k-images.idx3-ubyte";
	public static final String RESULT_PERCEPTRON = "result/perceptron.txt";
	public static final String RESULT_LOGIT_MODEL = "result/logitmodel.txt";
	
	public static String time(long start) {
		long time = (System.currentTimeMillis() - start) / 1000;
		long seconds = time % 60;
		long minutes = (time / 60) % 60;
		long hours = (time / 3600) % 24;
		return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}
}
