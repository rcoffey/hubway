package hubway.utility;

import hubway.MongoStationPair;
import hubway.Station;
import hubway.StationPair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Calculator {
	public static MongoStationPair printMinMaxStations(List<Station> stations) {
		Double minDist = Double.MAX_VALUE;
		Double maxDist = Double.MIN_VALUE;            
		Station maxStationStart = null, maxStationDest = null, minStationStart = null, minStationDest = null; 
				
		for (Iterator<Station> itStart = stations.iterator(); itStart.hasNext();) {
			Station start = (Station) itStart.next();


			for (Iterator<Station> itDest = stations.iterator(); itDest.hasNext();) {
				Station dest = (Station) itDest.next();

				if (start.station.equalsIgnoreCase(dest.station))
					continue;
						
				Double dist = distFrom(dest.lat, dest.lng, start.lat, start.lng);
														
				if (dist < minDist)
				{
					minDist = dist;
					minStationStart = start;
					minStationDest = dest;
				}
				
				if (dist > maxDist)
				{
					maxDist = dist;
					maxStationStart = start;
					maxStationDest = dest;
				}
			}
		}            

		System.out.println("minStationStart.station() = " + minStationStart.station + ", " + minStationStart.municipality);
		System.out.println("minStationDest.station() = " + minStationDest.station + ", " + minStationDest.municipality);
		System.out.println("minDist() = " + minDist);
		
		System.out.println("maxStationStart.station() = " + maxStationStart.station + ", " + maxStationStart.municipality);
		System.out.println("maxStationDest.station() = " + maxStationDest.station + ", " + maxStationDest.municipality);
		System.out.println("maxDist() = " + maxDist);
		
		return new MongoStationPair(maxStationStart, maxStationStart);
	}

	// stolen from the internets.
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 3958.75; //units?
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return (double) (dist * meterConversion);
	    }
}