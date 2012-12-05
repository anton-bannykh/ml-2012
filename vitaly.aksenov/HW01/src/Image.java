import java.io.DataInputStream;
import java.io.IOException;

public class Image {
	int n, m;
	int[][] b;
	int[] vector;

	static byte[] buffer = new byte[28 * 28];

	public Image(int n, int m) {
		this.n = n;
		this.m = m;
		b = new int[n][m];
		vector = new int[n * m];
	}

	public void read(DataInputStream in) throws IOException {
		in.read(buffer);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				b[i][j] = vector[i * m + j] = (buffer[i * m + j] < 0 ? 256 : 0)
						+ buffer[i * m + j];
			}
		}
	}

	public int length() {
		return n * m;
	}
}
