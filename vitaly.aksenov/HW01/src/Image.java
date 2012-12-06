import java.io.DataInputStream;
import java.io.IOException;

public class Image {
	int n, m;
	byte[][] b;
	byte[] vector;

	public Image(int n, int m) {
		this.n = n;
		this.m = m;
		b = new byte[n][m];
		vector = new byte[n * m];
	}

	public void read(DataInputStream in) throws IOException {
		in.read(vector);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				b[i][j] = vector[i * m + j];
			}
		}
	}

	public int length() {
		return n * m;
	}
}
