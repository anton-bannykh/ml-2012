package ru.ifmo.ctd.eremeev.ml.io;

import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import ru.ifmo.ctd.eremeev.ml.util.Digit;
import ru.ifmo.ctd.eremeev.ml.util.FileNames;

public class ImagePainter {

	private static DigitInputStream in;
	
	private static void write(BufferedImage image, String name) throws IOException {
		ImageIO.write(image, "png", new File("image/im/" + name + ".png"));
	}
	
	private static BufferedImage convert(Digit d) {
		int[][] b = d.getImage();
		BufferedImage bi = new BufferedImage(d.getNumberOfRows(), d.getNumberOfColumns(), BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster wr = bi.getRaster();
		for (int i = 0; i < b.length; ++i) {
			for (int j = 0; j < b[i].length; ++j) {
				wr.setPixel(j, i, new int[]{b[i][j]});
			}
		}
		return bi;
	}
	
	private static int[] readFile(String name) throws IOException {
		BufferedReader bf = null;
		List<Integer> l = new ArrayList<Integer>();
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(name), "cp1251"));
			String s = null;
			while ((s = bf.readLine()) != null) {
				if (s.isEmpty()) {
					continue;
				}
				l.add(Integer.parseInt(s));
			}
		} finally {
			if (bf != null) {
				bf.close();
			}
		}
		int[] ans = new int[l.size()];
		for (int i = 0; i < l.size(); ++i) {
			ans[i] = l.get(i);
		}
		return ans;
	}
	
	private static String toString(int k) {
		int len = 4;
		StringBuilder sb = new StringBuilder();
		while (len-- > 0) {
			sb.append(k % 10);
			k /= 10;
		}
		return sb.reverse().toString();
	}
	
	public static void main(String[] args) {
		try {
			in = new DigitInputStream(FileNames.TEST_LABELS, FileNames.TEST_IMAGES);
			Digit[] ds = new Digit[in.getNumberOfImages()];
			int[] write = readFile(FileNames.RIGHT_NUMBERS);
			for (int k = 0; k < ds.length; ++k) {
				ds[k] = in.readDigit();
			}
			for (int k : write) {
				write(convert(ds[k]), toString(k) + "_" + ds[k].getLabel());
				System.out.println(k + " : " + ds[k].getLabel());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
