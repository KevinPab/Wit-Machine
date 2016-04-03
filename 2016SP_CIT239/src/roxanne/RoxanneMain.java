package roxanne;

import java.io.PrintWriter;

import roxanne.HttpClient;

public class RoxanneMain {
	public static final String WIT_SH = "./witd.sh" ;
	public static final String PARSE = "tail -1 result.json";

	//String playmusic = "omxplayer ";
	public static final String LIFX_HEADER = 
			"curl -X PUT \"https://api.lifx.com/v1/lights/all/state\" "
			+ "-H \"Authorization: Bearer cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d\" "
			+ "-d \"selector=all\" ";
     

	public static void main(String[] args) throws Exception {
		// Declare bash commandline.
		ExecuteShellCommand bash = new ExecuteShellCommand();

		// Start process
		bash.execute(WIT_SH);
	
		// Parse JSON to WitResult object
		String jsonstring = bash.execute(PARSE);
		WitResult result = JsonParser.parse(jsonstring);

		// Convert JSON to cURL command.
		String url = HttpClient.sendlifx(result);
		System.out.println("Received: " + url);
		
		// Create lifx shell script from cURL
		PrintWriter writer = new PrintWriter("lifx.sh", "UTF-8");
		writer.println("#!/bin/sh");
		writer.println(url);
		writer.close();

		// Execute lifx.
		System.out.println(bash.execute("chmod +x lifx.sh && ./lifx.sh"));
		System.out.println(bash.execute( "./lifx.sh"));
		//bash.executealt("./lifx.sh");		
	}
}
