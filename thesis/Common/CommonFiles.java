package thesis.Common;

import java.io.FileWriter;
import java.io.IOException;


public class CommonFiles {
	public static void writeFile(String path, String dataToWrite) {
		try {
			FileWriter myWriter = new FileWriter(path);
			myWriter.write(dataToWrite);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
