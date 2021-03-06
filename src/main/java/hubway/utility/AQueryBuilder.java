package hubway.utility;

import hubway.json.CurrentWeatherDeserializer;
import hubway.json.GeocodeDeserializer;
import hubway.json.Route;
import hubway.json.RouteDeserializer;
import hubway.json.RouteLeg;
import hubway.json.RouteLegDeserializer;
import hubway.json.RouteStep;
import hubway.json.RouteStepDeserializer;
import hubway.json.TransitAlert;
import hubway.json.TransitAlertDeserializer;
import hubway.json.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.javadocmd.simplelatlng.LatLng;

/**
 * Abstract Query Builder provides base methods for returning results from a
 * query url as either a JsonObject or a Java Object. For new classes a
 * deserializer must be registered with the gson builder.
 * 
 * @author Rose Coffey
 * 
 */
public abstract class AQueryBuilder {

	protected String _url;
	protected String _credentials;
	protected String _mostRecentQuery = "";
	protected Gson _gson;
	protected JsonParser _jsonParser;
	final protected Logger logger = LoggerFactory.getLogger(HubwayQueryBuilder.class);

	/**
	 * The different deserializers could be registered by the more specific
	 * queries...
	 * 
	 * @param url_
	 * @param credentials_
	 */
	public AQueryBuilder(final String url_, final String credentials_) {
		_url = url_;
		_credentials = credentials_;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Route.class, new RouteDeserializer());
		gsonBuilder.registerTypeAdapter(RouteLeg.class, new RouteLegDeserializer());
		gsonBuilder.registerTypeAdapter(RouteStep.class, new RouteStepDeserializer());
		gsonBuilder.registerTypeAdapter(LatLng.class, new GeocodeDeserializer());
		gsonBuilder.registerTypeAdapter(Weather.class, new CurrentWeatherDeserializer());
		gsonBuilder.registerTypeAdapter(TransitAlert.class, new TransitAlertDeserializer());
		_gson = gsonBuilder.create();
		_jsonParser = new JsonParser();
	}

	public String getMostRecentQuery() {
		return _mostRecentQuery;
	}

	protected JSONObject query(String query_) {
		_mostRecentQuery = query_;
		try {
			URL query = new URL(query_);
			BufferedReader reader = new BufferedReader(new InputStreamReader(query.openStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return new JSONObject(sb.toString());
		} catch (MalformedURLException e) {
			logger.error("Unable to query using query " + query_, e);
		} catch (IOException e) {
			logger.error("Unable to read result of query " + query_, e);
		}
		return null;
	}

	/**
	 * Query the provided url and attempt to deserialize using registered type
	 * adapters for the given class
	 * 
	 * @param url_
	 * @param class_
	 * @return
	 */
	public <T> Object queryAndDeserialize(String url_, Class<T> class_) {

		URL query;
		try {
			query = new URL(url_);
			JsonReader jreader = new JsonReader(new InputStreamReader(query.openStream()));
			return _gson.fromJson(_jsonParser.parse(jreader), class_);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String latLngToString(LatLng latLng_) {
		return latLng_.getLatitude() + "," + latLng_.getLongitude();
	}
}
