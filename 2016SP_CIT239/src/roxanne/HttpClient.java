package roxanne;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import roxanne.WitResult;
import roxanne.WitResult.Entities.Color;
import roxanne.WitResult.Entities.Number;

import roxanne.ColorMap;
import roxanne.ExecuteShellCommand;

public class HttpClient {
	private final static String AUTH = "cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d";
	private final static String BASEURL = "https://api.lifx.com/v1/lights/all/";

	/*
	 *
curl -X PUT "https://api.lifx.com/v1/lights/all/state" \
     -H "Authorization: Bearer cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d" \
     -d "power=on"
	 * 
	 */


	public static void main(String[] args) throws Exception{
		String input3 = "{ \"msg_id\": \"f2550366-dd2e-4c83-9b28-8baf68ff9d10\", \"_text\": \"Turn on the red light\", \"outcomes\": [ { \"_text\": \"Turn on the red light\", \"confidence\": 0.993, \"intent\": \"bulb_turnon\", \"entities\": { \"color\": [ { \"type\": \"value\", \"value\": \"red\" } ] } } ] }";
		String input2 = "{ \"msg_id\": \"f2550366-dd2e-4c83-9b28-8baf68ff9d10\", \"_text\": \"Turn on the red light\", \"outcomes\": [ { \"_text\": \"Turn on the red light\", \"confidence\": 0.993, \"intent\": \"bulb_turnon\", \"entities\": { \"color\": [ { \"type\": \"value\", \"value\": \"red\" }, { \"type\": \"value\", \"value\": \"blue\", \"suggested\": \"true\"} ] } } ] }";
		String input = "{ \"msg_id\": \"f2550366-dd2e-4c83-9b28-8baf68ff9d10\", \"_text\": \"Turn on the red light\", \"outcomes\": [ { \"_text\": \"Turn on the red light\", \"confidence\": 0.993, \"intent\": \"bulb_turnon\", \"entities\": { \"color\": [ { \"type\": \"value\", \"value\": \"blue\", \"suggested\": \"true\"}, { \"type\": \"value\", \"value\": \"red\" } ] } } ] }";
		String input4 = "{ \"msg_id\": \"dd0b76c8-5563-49c3-b604-2a50aa8d6093\", \"_text\": \"Set the light to 25 %\", \"outcomes\": [ { \"_text\": \"Set the light to 25 %\", \"confidence\": 0.971, \"intent\": \"light_set\", \"entities\": { \"number\": [ { \"type\": \"value\", \"value\": 251234 } ] } } ]}";
		String input5 = "{ \"msg_id\": \"9d5048f5-0c4b-4ee0-8de6-7208eda52eb8\", \"_text\": \"Set the light from 23 to 54\", \"outcomes\": [ { \"_text\": \"Set the light from 23 to 54\", \"confidence\": 0.924, \"intent\": \"light_set\", \"entities\": { \"number\": [ { \"type\": \"value\", \"value\": 23 }, { \"type\": \"value\", \"value\": 54 } ] } } ]}";
		String input6 = "{ \"msg_id\": \"97989356-3987-4dce-8de6-77ce9fa02048\", \"_text\": \"Turn on the red light to 26%\", \"outcomes\": [ { \"_text\": \"Turn on the red light to 26%\", \"confidence\": 0.844, \"intent\": \"bulb_turnon\", \"entities\": { \"color\": [ { \"type\": \"value\", \"value\": \"red\" }, { \"type\": \"value\", \"value\": \"blue\", \"suggested\": true } ], \"number\": [ { \"type\": \"value\", \"value\": 26 } ] } } ]}";

		WitResult test = JsonParser.parse(input6);
		System.out.println(test.toString());

		String result = sendlifx(test);
		System.out.println(result);
	}

	public static String sendlifx(WitResult result) throws Exception {
		// Basic curl setting --Don't touch.
		String option = result.intent.contains("toggle") ? "toggle" : "state";
		if (result.intent.contains("dim") || result.intent.contains("bright")) {
			ExecuteShellCommand bash = new ExecuteShellCommand();
			String state = bash.execute("./state.sh");
			int index = state.indexOf("brightness") + 13;
			double brightness = Double.valueOf(state.substring(index, state.indexOf(",", index)));
			result.brightness = brightness;
		}
		URL url = new URL (BASEURL + option);

		// Create connection object
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(result.intent.contains("toggle") ? "POST" : "PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Authorization", "Bearer " + AUTH);

		// Create string from WitResult data
		byte[] data= getdata(result).getBytes("UTF-8");

		// Now create the curl command from connection object and WitResult data
		String curlcommand = toCurlRequest(connection, data) + " -H \"Authorization: Bearer cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d\"";
		return curlcommand;
	}



	public static String getdata(WitResult data){
		//String power,color,brightness;
		String result = null;
		List<String> variables = new ArrayList<String>();

		String intent[] = data.intent.split("_");


		// Power on/off setting
		if (intent[1].matches("turnoff")){
			variables.add("power=off");
		} else {
			variables.add("power=on");
		}
		
		/*
		if(intent[0].matches("bulb")){
			if(intent[1].matches("turnon")){
				variables.add("power=on");
			}
			else if(intent[1].matches("turnoff")){
				variables.add("power=off");
			}
		}
		*/


		// Color setting
		if(data.entities.color != null){
			String tempcolor = null;
			for(Color color: data.entities.color){
				if(color.suggested){
					tempcolor = color.value;
					break;
				}
				else if (color.value != null && tempcolor == null){
					tempcolor = color.value;
				}
			}
			if (tempcolor != null) {
				// Color validation
				ArrayList<String> colorlist = new ArrayList<String>();
				colorlist.addAll(Arrays.asList("white", "red", "orange", "yellow", "cyan", "green", "blue", "purple", "pink", "random"));
				// Check if color is supported by Lifx
				if(!colorlist.contains(tempcolor)){
					// If not, see if it is supported by regular color list
					TreeMap<String,String> colormap = ColorMap.colormap;
					if(colormap.containsKey(tempcolor)){
						tempcolor = colormap.get(tempcolor);
					}
					else {
						tempcolor = "white";	
					}
				}
			}
			variables.add("color=" + tempcolor);
		}

		// Number setting
		// If number contains something (user uttered a number)
		if(data.entities.number != null){
			double tempnumber = Double.NaN;
			// Let the most reliable number to be tempnumber
			for(Number number: data.entities.number){
				if(number.suggested){
					tempnumber = number.value;
					break;
				}
				else if (!Double.isNaN(number.value) && Double.isNaN(tempnumber)){
					tempnumber = number.value;
				}
			}
			// If given number is valid
			if (!Double.isNaN(tempnumber)) {
				// Number normalization
				if (tempnumber > 0 && tempnumber < 100){
					tempnumber = tempnumber / 100;
				}
				else if (tempnumber <= 0) {
					tempnumber = 0;
				}
				else if (tempnumber >= 100){
					int length = (int)(Math.log10(tempnumber) + 1);
					tempnumber = tempnumber/Math.pow(10, length);
				}
				variables.add("brightness=" + Double.toString(tempnumber));
			}
		} else if (!Double.isNaN(data.brightness)){
			double sign = (intent[1].equals("bright")) ? 1.0 : 0.0; 
			double tempnumber = (data.brightness + sign) / 2; 
			//System.out.println("sign: " + sign + "\nintent[1]: " + intent[1]);
			variables.add("brightness=" + Double.toString(tempnumber));
		}

		result = Arrays.toString(variables.toArray()).replaceAll(", ", "&").replace("[","").replace("]","");
		return result;
	}


	// The method which creates curl command from the connection object and data.
	public static String toCurlRequest(HttpURLConnection connection, byte[] body) {
		StringBuilder builder = new StringBuilder("curl -v ");
		// Method
		builder.append("-X ").append(connection.getRequestMethod()).append(" ");
		// Headers
		for (Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
			builder.append("-H '").append(entry.getKey()).append(":");
			for (String value : entry.getValue())
				builder.append(" ").append(value);
			builder.append("' ");
		}
		// Body
		if (body != null)
			builder.append("-d '").append(new String(body)).append("' ");
		// URL
		builder.append("'").append(connection.getURL()).append("'");
		return builder.toString();
	}


}

