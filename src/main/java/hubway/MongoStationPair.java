package hubway;

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
	
}