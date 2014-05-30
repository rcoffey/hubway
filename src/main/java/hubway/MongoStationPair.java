package hubway;

import hubway.utility.Calculator;
import hubway.utility.HubwayQueryBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class MongoStationPair {
	public Station station1, station2;
	public double geoDist; // distance as the crow flies, ie geodesic, in miles
	public int tripCount; // both directions
	public int trips12, trips21; // trips start to destination
	public double avgTime; // how long it takes on average to make a trip (in
							// seconds)
	public double minTime, maxTime; // how long the shortest/longest trip took
									// (in seconds)
	public String minDay, maxDay; // day of the week on which shortest/long trip
									// took place

	public MongoStationPair(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;

		geoDist = Calculator.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
		minTime = -1; // impossible default
	}

	public int addTrips(HubwayQueryBuilder querier) {
		// if we only care about trip count, we can get that from
		// meta.total_count
		// and skip the repeat querying.
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

		System.out.println("There are " + tripCount + " trips between " + station1.station + " and " + station2.station
				+ ".");
		if (totalTime > 0) {
			avgTime = totalTime / tripCount; // in seconds, so don't worry about
												// int
												// division
			System.out.println("These trips took on average " + avgTime / 60 + " minutes.");
			System.out.println("The longest took " + maxTime / 60 + " minutes, and the shortest " + minTime / 60
					+ " minutes.");
			System.out.println("The maximum speed was " + geoDist / (((double) minTime) / 3600) + " mph.");
		}
		return tripCount;

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