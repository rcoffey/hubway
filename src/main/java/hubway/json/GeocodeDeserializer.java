package hubway.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javadocmd.simplelatlng.LatLng;

public class GeocodeDeserializer implements JsonDeserializer<LatLng> {

	public LatLng deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {

		final JsonObject result = element.getAsJsonObject();
		
		final JsonObject loc = result.getAsJsonObject("geometry").getAsJsonObject("location");
		final long lon = loc.get("lon").getAsLong();
		final long lat = loc.get("lat").getAsLong();

		LatLng ll = new LatLng(lat,lon);

		return ll;
	}

}