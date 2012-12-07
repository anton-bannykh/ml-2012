package ru.ifmo.ctd.eremeev.ml.util;

public class Digit {
	
	private int label;
	private int[][] image;
	
	public Digit(int n, int[][] im) {
		if (n < 0 || n > 9) {
			throw new IllegalArgumentException("n less than 0 or more than 9 : " + n);
		}
		if (im == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < im.length; ++i) {
			if (im[i] == null) {
				throw new NullPointerException();
			}
		}
		label = n;
		image = im;
	}
	
	public int get(int i, int j) {
		return image[i][j];
	}
	
	public int getNumberOfRows() {
		return image.length;
	}
	
	public int getNumberOfColumns() {
		return image[0].length;
	}
	
	public int getLabel() {
		return label;
	}
	
	public int[][] getImage() {
		return image;
	}
}
