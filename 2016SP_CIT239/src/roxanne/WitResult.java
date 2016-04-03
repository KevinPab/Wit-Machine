package roxanne;


import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class WitResult {
	/* Variable Declaration */
	public String intent;
	public double confidence;
	public String _text;
	public double brightness = Double.NaN;

	// Nested Class
	@JsonProperty("entities")
	public Entities entities;

	/* toString */
	@Override
	public String toString() {
		return "WitResult [intent=" + intent + ", confidence=" + confidence + ", _text=" + _text
				+ ", entities=" + entities.toString() + "]";
	}

	static class Entities{
		@Override
		public String toString() {
			return "Entities [color=" + Arrays.toString(color) + ", number=" + Arrays.toString(number) + "]";
		}
		
		// Nested Class
		@JsonProperty("color")
		public Color[] color;
		@JsonProperty("number")
		public Number[] number;

		@JsonIgnoreProperties(ignoreUnknown=true)
		static class Color {
			@Override
			public String toString() {
				return "Color [type=" + type + ", value=" + value + ", suggested=" + suggested + "]";
			}
			public String type;
			public String value;
			public boolean suggested;

		}
		@JsonIgnoreProperties(ignoreUnknown=true)
		static class Number{
			@Override
			public String toString() {
				return "Number [type=" + type + ", value=" + value + ", suggested=" + suggested + "]";
			}
			public String type;
			public double value = Double.NaN;
			public boolean suggested;
		}
	}
}
