package hubway.json;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javadocmd.simplelatlng.LatLng;

public class TransitAlertDeserializer implements JsonDeserializer<TransitAlert> {

	public TransitAlert deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		final JsonArray alerts = element.getAsJsonObject().getAsJsonArray("alerts");
		
		TransitAlert ta = new TransitAlert();
		
		if (alerts != null) {
			
			for (JsonElement alert : alerts) {
				final JsonArray services = alert.getAsJsonObject().getAsJsonObject("affected_services").getAsJsonArray("services");
			
				for (JsonElement svc : services) {
					
					try {
					final String route_name = svc.getAsJsonObject().get("route_name").getAsString(); // orange line
					final String stop_name = svc.getAsJsonObject().get("stop_name").getAsString(); // orange line	
					
					if (!ta.linesAndStations.containsKey(route_name))
						ta.linesAndStations.put(route_name, new LinkedList<String>());
					
					if (stop_name != null)
						ta.linesAndStations.get(route_name).add(stop_name);
					}catch(Exception e)
					{
						// meh 
					}
					
				}				
			}
			return ta;
		}
		return null;
	}

}
