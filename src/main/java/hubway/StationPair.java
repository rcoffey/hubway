package hubway;

import java.util.ArrayList;


public class StationPair {
	public Station station1;
	public Station station2;
	public double geoDist; // distance as the crow flies, ie geodesic
	// some representation of trips from station1 to station2 and vice-versa
	// probably want both the number and a collection of trip objects
	public int tripCount; // which direction?  Do we care?
	// for now let tripCount be the total of both directions
	public ArrayList<Trip> trips12; // from station1 to station2
	public ArrayList<Trip> trips21; // from station2 to station1
	
	
	
	
	public StationPair(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		
		//geoDist = mongo.distFrom(station1.lat, station1.lng, station2.lat, station2.lng);
		tripCount = 0;
	}
	
	public void addTrip(Trip trip) { 
		// making the possibly fool-hardy assumption that this will only be called for 
		// trips between the two stations in question.  We should perhaps amend that later.
		tripCount++;
		
		if (trip.startStationId == station1.id) {
			trips12.add(trip);
		} else {
			trips21.add(trip);
		}
	}
}