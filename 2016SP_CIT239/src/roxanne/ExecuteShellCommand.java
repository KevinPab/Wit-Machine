// 2016/03/14
// Written by Shunsuke Haga
// 
// This class handles all the given commands.

package roxanne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteShellCommand{
	public String execute(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try{
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while((line = reader.readLine()) != null){
				output.append(line+"\n");
				System.out.println(line);
			}
		}catch (Exception e){
			System.out.println("[ERROR]: " + e);
			e.printStackTrace();
		}
		return output.toString();
	}

	public void executealt(String command) throws IOException{
		Runtime.getRuntime().exec(command);
	}
}
