package hubway.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RouteStepDeserializer implements JsonDeserializer<RouteStep> {

	public RouteStep deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {

		final JsonObject stepObject = element.getAsJsonObject();

		final long duration = stepObject.getAsJsonObject("duration").get("value").getAsLong();
		final long distance = stepObject.getAsJsonObject("distance").get("value").getAsLong();
		final String travel_mode = stepObject.get("travel_mode").getAsString();

		final RouteStep rs = new RouteStep();
		rs.setDistance(distance);
		rs.setDuration(duration);
		rs.setTravelMode(travel_mode);

		return rs;
	}

}