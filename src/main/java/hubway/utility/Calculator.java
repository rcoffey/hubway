package hubway.utility;

import hubway.Station;
import hubway.StationPair;

import java.util.Iterator;
import java.util.List;

public class Calculator {
	static String[] _days = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
	static String[] _times = { "MORNING", "AFTERNOON", "NIGHT" };

	public static StationPair printMinMaxStations(List<Station> stations) {
		Double minDist = Double.MAX_VALUE;
		Double maxDist = Double.MIN_VALUE;
		Station mostTotal = null, leastTotal = null, mostJoyRides = null, leastJoyRides = null;
		StationPair mostTripsBetween, leastTripsBetween;
		Station maxStationStart = null, maxStationDest = null, minStationStart = null, minStationDest = null;

		for (Iterator<Station> itStart = stations.iterator(); itStart.hasNext();) {
			Station start = (Station) itStart.next();
			if (mostTotal == null && leastTotal == null && mostJoyRides == null && leastJoyRides == null) {
				mostTotal = start;
				leastTotal = start;
				mostJoyRides = start;
				leastJoyRides = start;
			} else {
				if (mostTotal.totalTrips() < start.totalTrips()) {
					mostTotal = start;
				} else if (leastTotal.totalTrips() == 0 || leastTotal.totalTrips() > start.totalTrips()) {
					leastTotal = start;
				}
				if (mostJoyRides.getJoyrides().get("total") < start.getJoyrides().get("total")) {
					mostJoyRides = start;
				} else if (leastJoyRides.getJoyrides().get("total") == 0
						|| leastJoyRides.getJoyrides().get("total") > start.getJoyrides().get("total")) {
					leastJoyRides = start;
				}

			}

			for (Iterator<Station> itDest = stations.iterator(); itDest.hasNext();) {
				Station dest = (Station) itDest.next();

				if (start.station.equalsIgnoreCase(dest.station))
					continue;

				Double dist = distFrom(dest.lat, dest.lng, start.lat, start.lng);

				if (dist < minDist) {
					minDist = dist;
					minStationStart = start;
					minStationDest = dest;
				}

				if (dist > maxDist) {
					maxDist = dist;
					maxStationStart = start;
					maxStationDest = dest;
				}
			}
		}

		System.out.println("************************ OVERALL SUMMARY OF HUBWAY USE ************************");
		System.out.println("There are " + stations.size() + " stations according to historical data.");
		System.out.println("The most frequented hubway station is " + mostTotal.getStation() + " with "
				+ mostTotal.totalTrips() + " total trips. TripsTo: " + mostTotal.tripsTo.get("total") + " TripsFrom: "
				+ mostTotal.tripsFrom.get("total") + " JoyRides: " + mostTotal.getJoyrides().get("total"));
		printBreakdown(mostTotal);
		System.out.println("The least frequented hubway station is " + leastTotal.getStation() + " with "
				+ leastTotal.totalTrips() + " total trips. TripsTo: " + leastTotal.tripsTo.get("total")
				+ " TripsFrom: " + leastTotal.tripsFrom.get("total") + " JoyRides: "
				+ leastTotal.getJoyrides().get("total"));
		printBreakdown(leastTotal);
		System.out.println(mostJoyRides.getStation() + " has the most joy rides with a total of "
				+ mostJoyRides.getJoyrides().get("total"));
		printJoyRideBreakdown(mostJoyRides);
		System.out.println("The least joyful station (with more than 0 joyrides) is " + leastJoyRides.getStation()
				+ " with only " + leastJoyRides.getJoyrides().get("total"));
		printJoyRideBreakdown(leastJoyRides);

		System.out.println("minStationStart.station() = " + minStationStart.station);
		System.out.println("minStationDest.station() = " + minStationDest.station);
		System.out.println("minDist() = " + minDist);

		System.out.println("maxStationStart.station() = " + maxStationStart.station);
		System.out.println("maxStationDest.station() = " + maxStationDest.station);

		System.out.println("maxDist() = " + maxDist);

		return new StationPair(maxStationStart, maxStationDest);
	}

	protected static void printBreakdown(Station station_) {
		System.out.println("Daily trip breakdown for Station " + station_.getStation());
		for (String day : _days) {
			System.out.println("\t" + day + ":: TripsTo: " + station_.getTripsTo().get(day) + " TripsFrom: "
					+ station_.getTripsFrom().get(day));
		}
		System.out.println("Time of day breakdown for Station " + station_.getStation());
		for (String time : _times) {
			System.out.println("\t" + time + ":: TripsTo: " + station_.getTripsTo().get(time) + " TripsFrom: "
					+ station_.getTripsFrom().get(time));
		}
		System.out.println();
	}

	protected static void printJoyRideBreakdown(Station station_) {
		System.out.println("Daily trip breakdown for Station " + station_.getStation());
		for (String day : _days) {
			System.out.println("\t" + day + ":: JoyRides: " + station_.getJoyrides().get(day));
		}
		System.out.println("Time of day breakdown for Station " + station_.getStation());
		for (String time : _times) {
			System.out.println("\t" + time + ":: JoyRides: " + station_.getJoyrides().get(time));
		}
		System.out.println();

	}

	// stolen from the internets.
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75; // miles
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		// int meterConversion = 1609;

		return (double) (dist);// * meterConversion);
	}
}