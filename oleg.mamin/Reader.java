import java.io.*;

public class Reader {
	
	private String fileName, fileNameLabels;
	private DataInputStream dis, disLabels;
	private int setSize, dimSize1, dimSize2;
	private int test[][];
	private byte label[];
	
	Reader(String idxFileName, String idxFileNameLabels) {
		try {
			fileName = idxFileName;
			dis = new DataInputStream(new FileInputStream(new File(idxFileName)));
			disLabels = new DataInputStream(new FileInputStream(idxFileNameLabels));
			dis.skip(4);
			disLabels.skip(8);
			setSize = dis.readInt();
			dimSize1 = dis.readInt();
			dimSize2 = dis.readInt();
			test = new int[setSize][dimSize1 * dimSize2];
			label = new byte[setSize];
			
		} catch (IOException e) {
			System.err.println("Error on initializing \"Reader\"");
			e.printStackTrace();
		}
	}
	
	public void readTests() {
		for (int i = 0; i < setSize; ++i) {
			try {
				byte tmp[] = new byte[dimSize1 * dimSize2];
				dis.read(tmp);
				for (int j = 0; j < dimSize1 * dimSize2; j++) {
					test[i][j] = tmp[j] < 0 ? 256 + tmp[j] : tmp[j];
				}
				label[i] = disLabels.readByte();
			} catch (IOException e) {
				System.err.println("Error on reading test or label " + i);
				e.printStackTrace();
			}
		}
	}
	
	public int[] getTest(int i) {
		return test[i];
	}
	
	public byte getLabel(int i) {
		return label[i];
	}

}
