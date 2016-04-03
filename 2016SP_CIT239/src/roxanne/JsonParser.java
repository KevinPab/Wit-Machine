package roxanne;

import roxanne.WitResult;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonParser {
	public static WitResult parse(String input) throws IOException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		try {
		result = "{" + input.substring(input.indexOf("[") + 2, input.lastIndexOf("]"));
		} catch (StringIndexOutOfBoundsException e){
			System.out.println("[Error]: Empty output: " + e);
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(result);
		WitResult wit = mapper.readValue(result, WitResult.class);
		return wit; 
	}
}