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
		// final String transit_details =
		// stepObject.getAsJsonObject("transit_details").getAsJsonObject("line")
		// .getAsJsonObject("vehicle").get("name").getAsString();
		// String transport = "";
		// if (transit_details != null && transit_details.has("line")) {
		// transport =
		// transit_details.getAsJsonObject("line.vehicle").get("Name").getAsString();
		// }
		final RouteStep rs = new RouteStep();
		rs.setDistance(distance);
		rs.setDuration(duration);
		// rs.setTravel_mode(transit_details);

		return rs;
	}

}