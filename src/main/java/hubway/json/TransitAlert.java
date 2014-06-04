package hubway.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.javadocmd.simplelatlng.LatLng;

public class TransitAlert {

	public HashMap <String,LinkedList<String>> linesAndStations;
	
	public TransitAlert()
	{
		linesAndStations = new HashMap<>();		
	}
	

}
