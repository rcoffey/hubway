package hubway.json;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RouteDeserializer implements JsonDeserializer<Route> {

	public Route deserialize(JsonElement element, Type arg1, JsonDeserializationContext context)
			throws JsonParseException {
		// TODO Auto-generated method stub
		final JsonObject object = element.getAsJsonObject();
		final JsonArray routes = object.getAsJsonArray("routes");
		final JsonObject route = routes.get(0).getAsJsonObject();

		final String summary = route.get("summary").getAsString();

		RouteLeg[] routeLegs = context.deserialize(route.getAsJsonArray("legs"), RouteLeg[].class);
		List<RouteLeg> listLegs = Arrays.asList(routeLegs);

		final Route rt = new Route();
		rt.setSummary(summary);
		rt.setLegs(listLegs);

		return rt;
	}

}