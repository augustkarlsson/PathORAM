package pathOramHw;
import java.io.IOException;
import java.io.FileWriter;

public class Pair {
	public int x;
	public int y;

	Pair(int x,int y) {
		this.x = x;
		this.y = y;
	}

	public void printPair(FileWriter file) {
		try {
			file.write(x+", "+y+"\n");
		} catch (IOException e) {
			System.out.println("Error in writing to file: "+ e);
		}
	}
}