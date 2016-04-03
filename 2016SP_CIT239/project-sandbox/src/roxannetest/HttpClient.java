package roxannetest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import roxannetest.WitResult;
import roxannetest.WitResult.Entities.Color;
import roxannetest.WitResult.Entities.Number;

import roxannetest.ColorMap;

public class HttpClient {
	private final static String AUTH = "cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d";
	private final static String URL = "https://api.lifx.com/v1/lights/all/state";

	/*
	 *
curl -X PUT "https://api.lifx.com/v1/lights/all/state" \
     -H "Authorization: Bearer cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d" \
     -d "power=on"
	 * 
	 */

	public static String sendlifx(WitResult result) throws Exception {
		// Basic curl setting --Don't touch.
		URL url = new URL(URL);

		// Create connection object
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Authorization", "Bearer " + AUTH);

		// Create string from WitResult data
		byte[] data= getdata(result).getBytes("UTF-8");

		// Now create the curl command from connection object and WitResult data
		String curlcommand = toCurlRequest(connection, data) + " -H \"Authorization: Bearer cb2a7b10a9436125d78879bc9aa6a0e2925fb3e8047a6023dde9ae5a15fa332d\"";
		return curlcommand;
	}


	// --------------------------------------------------
	// 
	// Please edit following section to manipulate result "data"
	// 
	// --------------------------------------------------
	
	public static String getdata(WitResult data){
		//String power,color,brightness;
		String result = null;
		List<String> variables = new ArrayList<String>();

		String intent[] = data.intent.split("_");


		// Power on/off setting
		if(intent[0].matches("bulb")){
			if(intent[1].matches("turnon")){
				variables.add("power=on");
			}
			else if(intent[1].matches("turnoff")){
				variables.add("power=off");
			}
		}


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
				colorlist.addAll(Arrays.asList("white", "red", "orange", "yellow", "cyan", "green", "blue", "purple", "pink"));
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
		if(data.entities.number != null){
			double tempnumber = Double.NaN;
			for(Number number: data.entities.number){
				if(number.suggested){
					tempnumber = number.value;
					break;
				}
				else if (!Double.isNaN(number.value) && Double.isNaN(tempnumber)){
					tempnumber = number.value;
				}
			}
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

