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
		final RouteStep rs = new RouteStep();
		final JsonObject transitDetails = stepObject.getAsJsonObject("transit_details");
		if (transitDetails != null) {
			rs.setDepartureStop(transitDetails.getAsJsonObject("departure_stop").get("name").getAsString());
			rs.setArrivalStop(transitDetails.getAsJsonObject("arrival_stop").get("name").getAsString());
			JsonObject line = transitDetails.getAsJsonObject("line");
			if (line != null) {
				if (line.get("name") != null) {
					rs.setLineName(line.get("name").getAsString());
				} else if (line.get("short_name") != null) {
					rs.setLineName(line.get("short_name").getAsString());
				}
				rs.setTransitType(line.getAsJsonObject("vehicle").get("name").getAsString().toLowerCase());
			}
		} else {
			rs.setTransitType(stepObject.get("travel_mode").getAsString().toLowerCase());
		}
		rs.setDistance(distance);
		rs.setDuration(duration);

		return rs;
	}

}