package hubway.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javadocmd.simplelatlng.LatLng;

public class GeocodeDeserializer implements JsonDeserializer<LatLng> {

	public LatLng deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		final JsonArray results = element.getAsJsonObject().getAsJsonArray("results");
		if (results != null && results.size() > 0) {
			final JsonObject result = results.get(0).getAsJsonObject();

			final JsonObject loc = result.getAsJsonObject("geometry").getAsJsonObject("location");
			final double lon = loc.get("lng").getAsDouble();
			final double lat = loc.get("lat").getAsDouble();

			return new LatLng(lat, lon);
		}
		return null;
	}

}