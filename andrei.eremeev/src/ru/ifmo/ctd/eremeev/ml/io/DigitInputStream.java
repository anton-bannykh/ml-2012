package ru.ifmo.ctd.eremeev.ml.io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import ru.ifmo.ctd.eremeev.ml.util.Digit;

public class DigitInputStream {
	
	private DataInputStream labelStream;
	private DataInputStream imageStream;
	private int numberOfReadImages;
	private int numberOfImages;
	private int rows;
	private int columns;
	
	public DigitInputStream(String labelsFileName, String imagesFileName) throws IOException {
		labelStream = new DataInputStream(new FileInputStream(labelsFileName));
		imageStream = new DataInputStream(new FileInputStream(imagesFileName));
		labelStream.skip(4);
		imageStream.skip(4);
		numberOfImages = labelStream.readInt();
		if (numberOfImages != imageStream.readInt()) {
			throw new IOException();
		}
		if ((rows = imageStream.readInt()) < 0) {
			throw new IllegalArgumentException("Number of rows is not positive : " + rows);
		}
		if ((columns = imageStream.readInt()) < 0) {
			throw new IllegalArgumentException("Number of columns is not positive : " + columns);
		}
	}
	
	public int getNumberOfRows() {
		return rows;
	}
	
	public int getNumberOfColumns() {
		return columns;
	}
	
	public boolean hasNext() {
		return numberOfReadImages < numberOfImages;
	}
	
	public int getNumberOfImages() {
		return numberOfImages;
	}
	
	public Digit readDigit() throws IOException {
		byte label = labelStream.readByte();
		byte[] b = new byte[rows * columns];
		imageStream.readFully(b);
		++numberOfReadImages;
		return new Digit(label, b);
	}
	
	public void close() throws IOException {
		try {
			labelStream.close();
		} catch (IOException e) {
			imageStream.close();
			throw e;
		}
		imageStream.close();
	}
}
