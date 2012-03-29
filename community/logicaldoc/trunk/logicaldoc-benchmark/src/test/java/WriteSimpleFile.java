import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WriteSimpleFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("U:/writtenByJava.txt");
        FileWriter fw = new FileWriter(file);
        fw.write("Hello i'm here!");
        fw.close();
	}

}
