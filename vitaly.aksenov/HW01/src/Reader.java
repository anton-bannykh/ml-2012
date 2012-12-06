import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Reader {
	Image[] train = new Image[0];
	int[] train_label = new int[0];
	Image[] check = new Image[0];
	int[] check_label = new int[0];
	private String dir = "C:\\work\\HomeWorks\\ML\\";

	private Image[] readImages(DataInputStream in) throws IOException {
		in.skip(4);
		Image[] data = new Image[in.readInt()];
		int n = in.readInt();
		int m = in.readInt();

		for (int i = 0; i < data.length; i++) {
			if (i % 1000 == 999) {
				System.err.println("reading " + (i + 1) / 1000 + " out of "
						+ data.length);
			}
			data[i] = new Image(n, m);
			data[i].read(in);
		}

		return data;
	}

	private int[] readLabels(DataInputStream in) throws IOException {
		in.skip(4);
		int[] data = new int[in.readInt()];
		for (int i = 0; i < data.length; i++) {
			data[i] = in.read();
		}
		return data;
	}

	public Reader() {
		try {
			train = readImages(new DataInputStream(new FileInputStream(dir
					+ "train-images.idx3-ubyte")));
			train_label = readLabels(new DataInputStream(new FileInputStream(
					dir + "train-labels.idx1-ubyte")));
			check = readImages(new DataInputStream(new FileInputStream(dir
					+ "t10k-images.idx3-ubyte")));
			check_label = readLabels(new DataInputStream(new FileInputStream(
					dir + "t10k-labels.idx1-ubyte")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
