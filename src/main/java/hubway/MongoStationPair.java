package hubway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import hubway.utility.Calculator;

public class MongoStationPair {
	public Station station1, station2;
	public double geoDist; // distance as the crow flies, ie geodesic
	public int tripCount; // both directions
	public int trips12, trips21; // trips start to destination
	public double avgTime; // how long it takes on average to make a trip
	
	
	public MongoStationPair(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		
		geoDist = Calculator.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
	}
	
	public int addTrips(String urlToQuery){
		// probably will want to call this by dates, because we're limited
		// to 100 results per query
		// or allow for repeat querying the "next" URL
		try {
			URL query = new URL(urlToQuery + "&start_station=" + station1.id + "&end_station=" + station2.id);
			BufferedReader reader = new BufferedReader(new InputStreamReader(query.openStream()));
        
			String json = reader.readLine(); // it's only one line
			JSONObject trips = new JSONObject(json);
			
			System.out.println(trips.get("meta"));
        
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 2; // really return the number of trips added.
	}
}