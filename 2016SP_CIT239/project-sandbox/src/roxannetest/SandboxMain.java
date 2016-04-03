package roxannetest;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import roxannetest.JsonParser;
import roxannetest.WitResult;

public class SandboxMain {

	public static void main(String[] args) throws IOException {
		/* Loading file... */
		String strInput = null;
		File file = new File("raw.txt");
		
		// Check if given file exists
		if(!file.exists()){
			System.out.println("No such filie exist.");
			file.createNewFile();
			return;
		}
		try {
			// Treat whole file as one string.
			Scanner scan = new Scanner(file);
			scan.useDelimiter("\\Z");  
			strInput = scan.next(); 
			scan.close();
		} catch (IOException e) {
			System.out.println(e + ": File does not exist!");
		}

		strInput = strInput.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ");

		
		// Convert json to WitResult object
		WitResult result = JsonParser.parse(strInput);

		// Show the result file of the result
		System.out.println(HttpClient.getdata(result));

	}
}
