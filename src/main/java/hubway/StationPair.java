package hubway;

import hubway.utility.Calculator;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StationPair {
	public String station1, station2;
	public double geoDist; // distance as the crow flies, ie geodesic, in miles
	public double navDist; // navigable distance
	public int tripCount; // station1 to station2
	public double avgTime; // how long it takes on average to make a trip (in
							// seconds)
	public double minTime, maxTime; // how long the shortest/longest trip took
									// (in seconds)
	public HashMap<String, Integer> tripsByTime; // split up tripCount by day of
													// week
	// and time of day

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public StationPair(Station station1, Station station2) {
		this.station1 = station1.id;
		this.station2 = station2.id;

		geoDist = Calculator.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
		minTime = -1; // impossible default
		navDist = -1.0; // impossible default
	}

	/*
	 * public void info() { System.out.println(
	 * "************************ YOUR TRIP ************************");
	 * System.out.println("Start Station: " + station1.station);
	 * System.out.println("End Station: " + station2.station);
	 * System.out.println("\n***** Historical Trip Facts *****"); //
	 * System.out.println("They are " + geoDist + //
	 * " miles apart as the crow flies."); if (navDist != -1.0) {
	 * System.out.println("But you will have to travel at least " + navDist +
	 * " miles " + "to complete the trip."); } if (tripCount != 0) {
	 * System.out.println("There are " + tripCount + " trips between " +
	 * station1.station + " and " + station2.station + "."); if
	 * (station1.tripsFrom.get("total") != 0 && station2.tripsTo.get("total") !=
	 * 0) { System.out.println("\tThat is " + tripCount / (double)
	 * station1.tripsFrom.get("total") * 100 + " percent " +
	 * "of the trips from " + station1.station); System.out.println("\t and " +
	 * tripCount / (double) station2.tripsTo.get("total") * 100 + " percent " +
	 * "of the trips to " + station2.station + "."); }
	 * System.out.println("\nThese trips took on average " + avgTime / 60 +
	 * " minutes."); if (minTime != 0 && minTime != -1) {
	 * System.out.println("The longest took " + maxTime / 60 +
	 * " minutes, and the shortest " + minTime / 60 + " minutes."); double dist
	 * = (navDist == -1.0 ? geoDist : navDist); if (dist != 0) {
	 * System.out.println("The maximum speed was about " + dist / (((double)
	 * minTime) / 3600) + " mph."); } } } else {
	 * System.out.println("No one has made this trip on Hubway before!"); } }
	 */
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