package hubway;

import org.json.JSONArray;
import org.json.JSONObject;

import hubway.utility.Calculator;
import hubway.utility.HubwayQuery;

public class MongoStationPair {
	public Station station1, station2;
	public double geoDist; // distance as the crow flies, ie geodesic
	public int tripCount; // both directions
	public int trips12, trips21; // trips start to destination
	public double avgTime; // how long it takes on average to make a trip (in seconds)
	
	
	public MongoStationPair(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		
		geoDist = Calculator.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
	}
	
	public int addTrips(HubwayQuery querier){
		// if we only care about trip count, we can get that from meta.total_count
		// and skip the repeat querying.
		String queryString = "&start_station=" + station1.id + "&end_station=" + station2.id;
		JSONObject tripData = querier.query("trip", queryString);
		// actual trips are under "objects"
		JSONArray trips = tripData.getJSONArray("objects");
		tripCount += trips.length();
		int totalTime = computeTime(trips);
		
		// if there are >100 such trips, there is a "next" entry under "meta"
		// and we need to repeat the query with an offset
		JSONObject meta = tripData.getJSONObject("meta");
		int offset = 100;
		while (meta.has("next") && !meta.isNull("next")) { 
			tripData = querier.query("trip", queryString + "&offset="+offset);
			trips = tripData.getJSONArray("objects");
			tripCount += trips.length();
			totalTime += computeTime(trips);
			meta = tripData.getJSONObject("meta");
			//System.out.println("Offset is " + offset + tripData.toString(2));
			offset +=100;
		} 

		System.out.println("There are " + tripCount + " trips between " + station1.station + 
				" and " + station2.station + ".");
		
		avgTime = totalTime/tripCount; // in seconds, so don't worry about int division
		System.out.println("These trips took on average " + avgTime + " seconds.");
		return tripCount; 
	}
	
	private int computeTime(JSONArray trips) {
		int time = 0;
		for (int i=0; i<trips.length(); i++) {
			time += trips.getJSONObject(i).getInt("duration");
		}
		return time;
	}
}