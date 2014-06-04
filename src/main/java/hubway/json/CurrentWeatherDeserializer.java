package hubway.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javadocmd.simplelatlng.LatLng;

public class CurrentWeatherDeserializer implements JsonDeserializer<Weather> {

	public Weather deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		final JsonArray results = element.getAsJsonObject().getAsJsonArray("result");
		
		if (results != null && results.size() > 0) {

			final JsonObject obsv = element.getAsJsonObject().getAsJsonObject("current_observation");
			final String weather = obsv.get("weather").getAsString();
			final String feelslike = obsv.get("feelslike_string").getAsString();
			
			final float wind = obsv.get("wind_mph").getAsFloat();
			final float temp = obsv.get("temp_f").getAsFloat();

			return new Weather(weather, feelslike, temp,wind);
		}
		return null;
	}

}
