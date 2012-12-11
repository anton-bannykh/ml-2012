package ru.ifmo.ctd.eremeev.ml.util;

public class Digit {
	
	private int label;
	private byte[] image;
	
	public Digit(int n, byte[] im) {
		if (n < 0 || n > 9) {
			throw new IllegalArgumentException("n less than 0 or more than 9 : " + n);
		}
		if (im == null) {
			throw new NullPointerException();
		}
		label = n;
		image = im;
	}
	
	public int get(int i) {
		return image[i] >= 0 ? image[i] : 256 + image[i];
	}
	
	public int getLabel() {
		return label;
	}
	
	public double getDouble(int i) {
		return ((double)get(i) - 128) / 1000;
	}
	
	public int getN() {
		return image.length;
	}
	
	public double[] imageToDoubleArray() {
		double[] ans = new double[image.length];
		for (int i = 0; i < ans.length; ++i) {
			ans[i] = ((double)get(i) - 128) / 1000;
		}
		return ans;
	}
	
	public int[] getImage() {
		int[] ans = new int[image.length];
		for (int i = 0; i < ans.length; ++i) {
			ans[i] = get(i);
		}
		return ans;
	}
}
