package hubway;

import hubway.utility.Calculator;
import hubway.utility.DistanceQueryBuilder;
import hubway.utility.HubwayQueryBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javadocmd.simplelatlng.LatLng;

public class StationPair {
	public Station station1, station2;
	public double geoDist; // distance as the crow flies, ie geodesic, in miles
	public double navDist; // navigable distance
	public int tripCount; // station1 to station2
	//public int trips12, trips21; // trips start to destination
	public double avgTime; // how long it takes on average to make a trip (in
							// seconds)
	public double minTime, maxTime; // how long the shortest/longest trip took
									// (in seconds)
	public String minDay, maxDay; // day of the week on which shortest/long trip
									// took place
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public StationPair(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;

		geoDist = Calculator.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
		minTime = -1; // impossible default
		navDist = -1.0; // impossible default
	}
	
	public double setNavDist(DistanceQueryBuilder distance){
		// should probably come up with better error-prediction here
		try{
		LatLng origin = new LatLng(station1.getLat(), station1.getLng()); 
		LatLng destination = new LatLng(station2.getLat(), station2.getLng()); 
		JSONObject distanceBike =
				  distance.queryDistanceBetween(origin, destination, "bicycling");
		String ans = distanceBike.getJSONArray("rows").getJSONObject(0).getJSONArray("elements")
				.getJSONObject(0).getJSONObject("distance").getString("text");
		navDist = Double.parseDouble(ans.substring(0,ans.length()-3));
		} catch(Exception e){
			navDist = -1.0;
			logger.warn("Cannot set navigable distance for " + station1.station + " and "
					+ station2.station + ". Error: " + e);
		}
		return navDist;
	}

	public int addTrips(HubwayQueryBuilder querier) {
		String queryString = "&start_station=" + station1.id + "&end_station=" + station2.id;
		// if we want to specify registered vs. casual, add
		// "&subscription_type=Registered" or "&subscription_type=Casual"
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
			tripData = querier.query("trip", queryString + "&offset=" + offset);
			trips = tripData.getJSONArray("objects");
			tripCount += trips.length();
			totalTime += computeTime(trips);
			meta = tripData.getJSONObject("meta");
			offset += 100;
		}

		if (totalTime > 0) {
			avgTime = totalTime / tripCount; // in seconds, so don't worry about int division
		}
		return tripCount;

	}
	
	public void info() {
		logger.info("Your start station is " + station1.station);
		logger.info("Your end station is " + station2.station);
		logger.info("They are " + geoDist + " miles apart as the crow flies.");
		logger.info("But you will have to travel at least " + navDist + " miles "
				+ "to complete the trip.");
		logger.info("There are " + tripCount + " trips between " + station1.station + 
				" and " + station2.station + ".");
		if (station1.tripsFrom != 0 && station2.tripsTo != 0){
			logger.info("That is " + tripCount / (double)station1.tripsFrom * 100 + " percent "
				+ "of the trips from " + station1.station);
			logger.info(" and " + tripCount/ (double)station2.tripsTo * 100 + " percent "
				+ "of the trips to " + station2.station + ".");
		}
		logger.info("These trips took on average " + avgTime / 60 + " minutes.");
		logger.info("The longest took " + maxTime / 60 + " minutes, and the shortest " + minTime / 60
				+ " minutes.");
		if (minTime != 0) {
			double dist = (navDist == -1.0 ? geoDist : navDist);
			logger.info("The maximum speed was " + dist / (((double) minTime) / 3600) + " mph.");
		}
	}

	private int computeTime(JSONArray trips) {
		int time = 0;
		int longest, shortest, tripTime;

		// handle empty array case
		if (trips.length() == 0)
			return 0;

		JSONObject trip = trips.getJSONObject(0);
		longest = trip.getInt("duration");
		shortest = trip.getInt("duration");
		for (int i = 0; i < trips.length(); i++) {
			trip = trips.getJSONObject(i);
			tripTime = trip.getInt("duration");
			time += tripTime;

			if (tripTime < shortest) {
				shortest = tripTime;
			}
			if (tripTime > longest) {
				longest = tripTime;
			}
		}

		if (shortest < minTime || minTime == -1) {
			minTime = shortest;
		}
		if (longest > maxTime) {
			maxTime = longest;
		}

		return time;
	}
}