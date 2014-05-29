package hubway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

public class galaway {
	private static String HubwayURL = "http://hubwaydatachallenge.org/api/v1/";
	private static String HubwayCredentials = "/?format=json&username=cbaltera&api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6";
	private static String HubwayStationURL = HubwayURL + "station"
			+ HubwayCredentials;
	private static String HubwayTripURL = HubwayURL + "trip"
			+ HubwayCredentials;
	private static String HubwayStationCapacityURL = HubwayURL
			+ "stationcapacity" + HubwayCredentials;
	private static String HubwayStationStatusURL = HubwayURL + "stationstatus"
			+ HubwayCredentials;

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(galaway.class);
		// Get the Beans
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring/spring.galaway.beans.xml");

		// Connect to Mongo
		MongoTemplate client = (MongoTemplate) context.getBean("mongoTemplate");
		DB galawayDb = client.getDb();
		if (!galawayDb.isAuthenticated()) {
			logger.error("Authentication failed for mongoDb :"
					+ client.toString());
		}

		// Use mjorm to map our results to our java Stations.
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(Station.class);
		// Need these custom converters because they didn't think it was
		// important to go from String -> Common freaking types.
		mapper.registerTypeConverter(new DateConverter());
		mapper.registerTypeConverter(new IntegerConverter());
		MongoDao dao = new MongoDaoImpl(galawayDb, mapper);
		// Actually go get the stations. The empty BasicDBObject is basically a
		// select * query
		List<Station> stationList = dao.findObjects("Stations",
				new BasicDBObject(), Station.class).readAll();

		System.out.println("There are " + stationList.size() + " stations");

		printMinMaxStations(stationList);

		try {
			URL stationQuery = new URL(HubwayStationURL
					+ "&name__icontains=Boston");
			BufferedReader stationReader = new BufferedReader(
					new InputStreamReader(stationQuery.openStream()));

			String stationJSON = stationReader.readLine(); // it's only one line
			JSONObject bostonStations = new JSONObject(stationJSON);

			System.out.println(bostonStations.toString(2));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printMinMaxStations(List<Station> stationList) {
		Double minDist = Double.MAX_VALUE;
		Double maxDist = Double.MIN_VALUE;
		Station maxStationStart = null, maxStationDest = null, minStationStart = null, minStationDest = null;

		for (Iterator<Station> itStart = stationList.iterator(); itStart
				.hasNext();) {
			Station start = (Station) itStart.next();

			for (Iterator<Station> itDest = stationList.iterator(); itDest
					.hasNext();) {
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

		System.out
				.println("minStationStart.station() = "
						+ minStationStart.station + ", "
						+ minStationStart.municipality);
		System.out.println("minStationDest.station() = "
				+ minStationDest.station + ", " + minStationDest.municipality);
		System.out.println("minDist() = " + minDist);

		System.out
				.println("maxStationStart.station() = "
						+ maxStationStart.station + ", "
						+ maxStationStart.municipality);
		System.out.println("maxStationDest.station() = "
				+ maxStationDest.station + ", " + maxStationDest.municipality);
		System.out.println("maxDist() = " + maxDist);
	}

	// stolen from the internets.
	public static double distFrom(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 3958.75; // units?
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (double) (dist * meterConversion);
	}

	/**
	 * hubway api url/queries: http://hubwaydatachallenge.org/api/v1/station/
	 * ?format
	 * =json&username=cbaltera&api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6
	 * &name__icontains=Boston
	 * 
	 * The api doesn't have municipality available for stations. nb_docks is a
	 * in a separate schema from the basic station info. nb_bikes and
	 * nb_emptyDocks are also available. Trip data appears to be the same, so
	 * maybe keep stations in mongo and get trips from the hubway api.
	 * 
	 * http://hubwaydatachallenge.org/api/v1/trip/
	 * ?format=json&username=cbaltera
	 * &api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6
	 * &duration__gt=3600&start_station
	 * =33&start_date__gte=2011-08-01&end_date__lte=2011-08-31
	 */
}
