package hubway.utility;

import hubway.json.Route;
import hubway.json.RouteDeserializer;
import hubway.json.RouteLeg;
import hubway.json.RouteLegDeserializer;
import hubway.json.RouteStep;
import hubway.json.RouteStepDeserializer;

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
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.javadocmd.simplelatlng.LatLng;

public abstract class AQueryBuilder {

	protected String _url;
	protected String _credentials;
	protected String _mostRecentQuery = "";
	final protected Logger logger = LoggerFactory.getLogger(HubwayQueryBuilder.class);

	public AQueryBuilder(final String url_, final String credentials_) {
		_url = url_;
		_credentials = credentials_;
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

	public Route queryString(String query_) {
		_mostRecentQuery = query_;
		URL query;
		try {
			query = new URL(query_);

			JsonReader jreader = new JsonReader(new InputStreamReader(query.openStream()));
			JsonParser parser = new JsonParser();
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Route.class, new RouteDeserializer());
			gsonBuilder.registerTypeAdapter(RouteLeg.class, new RouteLegDeserializer());
			gsonBuilder.registerTypeAdapter(RouteStep.class, new RouteStepDeserializer());

			Gson gson = gsonBuilder.create();
			return gson.fromJson(parser.parse(jreader), Route.class);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected String latLngToString(LatLng latLng_) {
		return latLng_.getLatitude() + "," + latLng_.getLongitude();
	}
}
