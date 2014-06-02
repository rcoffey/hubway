package hubway.json;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RouteLegDeserializer implements JsonDeserializer<RouteLeg> {

	public RouteLeg deserialize(JsonElement element, Type arg1, JsonDeserializationContext context)
			throws JsonParseException {
		// TODO Auto-generated method stub
		final JsonObject routeLeg = element.getAsJsonObject();
		final long duration = routeLeg.getAsJsonObject("duration").get("value").getAsLong();
		final long distance = routeLeg.getAsJsonObject("distance").get("value").getAsLong();

		RouteStep[] steps = context.deserialize(routeLeg.getAsJsonArray("steps"), RouteStep[].class);
		List<RouteStep> routeSteps = Arrays.asList(steps);

		final RouteLeg leg = new RouteLeg();
		leg.setDistance(distance);
		leg.setDuration(duration);
		leg.setSteps(routeSteps);

		return leg;
	}

}
