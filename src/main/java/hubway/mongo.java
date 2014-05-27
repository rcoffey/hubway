package hubway;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class mongo {

	public static void main(String[] args) {
		try {
			ServerAddress mongoServer = new ServerAddress(
					"ds049868.mongolab.com", 49868);
			List<ServerAddress> serverList = new LinkedList<ServerAddress>();
			serverList.add(mongoServer);

			MongoCredential cred = MongoCredential.createMongoCRCredential(
					"galaway", "galaway", "galaway1".toCharArray());
			List<MongoCredential> credList = new LinkedList<MongoCredential>();
			credList.add(cred);
			MongoClient galawayMongo = new MongoClient(serverList, credList);
			DB galawayDb = galawayMongo.getDB("galaway");

			DBCollection stations = galawayDb.getCollection("Stations"); 

			System.out
					.println("There are " + stations.getCount() + " stations");
			
			ArrayList<Station> stationList = new ArrayList<Station>();
			
			for (DBObject stationObj : stations.find()) {
				stationList.add(new Station(stationObj));
			}
	        printMinMaxStations(stationList);        

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void printMinMaxStations(ArrayList<Station> stationList) {
		Double minDist = Double.MAX_VALUE;
		Double maxDist = Double.MIN_VALUE;            
		Station maxStationStart = null, maxStationDest = null, minStationStart = null, minStationDest = null; 
				
		for (Iterator<Station> itStart = stationList.iterator(); itStart.hasNext();) {
			Station start = (Station) itStart.next();


			for (Iterator<Station> itDest = stationList.iterator(); itDest.hasNext();) {
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
