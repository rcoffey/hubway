package hubway;

import hubway.json.Route;
import hubway.json.Weather;
import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.GeocodeQueryBuilder;
import hubway.utility.HubwayQueryBuilder;
import hubway.utility.IntegerConverter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.googlecode.mjorm.query.DaoQuery;
import com.javadocmd.simplelatlng.LatLng;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class galaway {

	static Logger logger = LoggerFactory.getLogger(galaway.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// Get the Beans
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring.galaway.beans.xml");

		// Print interesting general station info. (Farthest points, most used
		// stations...)
		// This could include the station ids for now so we have points to
		// search between.

		// Ask for an Address

		// Use GeoCode to get coordinates of the address

		// Use Mongo near query to get nearest hubway station

		// Suggest most common destinations and average trip durations

		// Ask for destination

		// Look up route options between the 2

		// Connect to Mongo
		MongoTemplate client = (MongoTemplate) context.getBean("mongoTemplate");
		DB galawayDb = client.getDb();
		if (!galawayDb.isAuthenticated()) {
			logger.error("Authentication failed for mongoDb :" + client.toString());
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
		List<Station> stationList = dao.findObjects("Stations", new BasicDBObject(), Station.class).readAll();
		
		System.out.println("There are " + stationList.size() + " stations");
		System.out.println(stationList.toString());

		StationPair test = Calculator.printMinMaxStations(stationList);
		
		// !CL query for lat long using string
		GeocodeQueryBuilder geocodeQueryBuilder = (GeocodeQueryBuilder) context.getBean("geocodeQueryBuilder");
		LatLng davis = geocodeQueryBuilder.queryLatLng("40 Holland St, Somerville MA");
		double[] loc = {davis.getLongitude(), davis.getLatitude()};
		
		DBObject nearQuery = BasicDBObjectBuilder.start().add("geometry.coordinates", 
				BasicDBObjectBuilder.start().add("$near", loc).get()).get();
		Station nearStation = dao.findObject("Stations", nearQuery, Station.class);
		System.out.println(nearStation);
		
		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");

		// does it make sense to print the list of station name / station id
		// pairs here?

		System.out.println("Please enter your start station id:");
		Scanner input = new Scanner(System.in);
		int startStationId = input.nextInt();
		System.out.println("Please enter your destination station id.  If you want advice on your destination"
				+ " enter 0:");
		int destStationId = input.nextInt();
		input.close();

		DaoQuery query = dao.createQuery();
		query.eq("_id", startStationId);
		query.setCollection("Stations");
		Station startStation = query.findObject(Station.class);
		if (startStation == null) {
			System.out.println("No such station as " + startStationId + "!");
			return;
		}
		query.clear();

		boolean advice = false;
		if (destStationId == 0) {
			destStationId = startStation.maxDest;
			advice = true;
		}

		query.eq("_id", destStationId);
		query.setCollection("Stations");
		Station destStation = query.findObject(Station.class);
		if (destStation == null) {
			System.out.println("No such station as " + destStationId + "!");
			return;
		}
		StationPair stationsOfInterest = new StationPair(startStation, destStation);

		if (advice) {
			System.out.println("Perhaps you would like to go to " + destStation.station
					+ ", the most popular trip from " + startStation.station);
			produceOutput(stationsOfInterest, context, hubwayQuerier);
			// can we pause here to allow user to read output?
			query.clear();
			query.eq("_id", startStation.penMaxDest);
			query.setCollection("Stations");
			destStation = query.findObject(Station.class);
			if (destStation != null) {
				System.out.println("");
				System.out.println("Or maybe you'd prefer " + destStation.station
						+ ", the second most popular trip from " + startStation.station);
				stationsOfInterest = new StationPair(startStation, destStation);
				produceOutput(stationsOfInterest, context, hubwayQuerier);
			}
		} else {
			produceOutput(stationsOfInterest, context, hubwayQuerier);
		}

	}

	private static void produceOutput(StationPair stationsOfInterest, ApplicationContext context,
			HubwayQueryBuilder hubwayQuerier) {
		stationsOfInterest.addTrips(hubwayQuerier);

		stationsOfInterest.info();

		LocationDataEnricher locationData = (LocationDataEnricher) context.getBean("locationEnricher");
		
		
		JSONObject hst = locationData.getHistoricalWeather("20130821", "MA/Boston");
		Weather cur = locationData.getCurrentWeather("MA/Boston");
				
		
		
		Map<String, Route> locationDataMap = locationData.getRoutes(stationsOfInterest.station1.getLatLng(),
				stationsOfInterest.station2.getLatLng());

		Map<String, JSONObject> hubways = locationData.getHubways(stationsOfInterest.station1.getLatLng(),
				stationsOfInterest.station2.getLatLng(), 500);

		compareRoutes(locationDataMap);
		Route bike = (Route) locationDataMap.get("bicycling");
		Route transit = (Route) locationDataMap.get("transit");

		long bikeDist = (long) (bike.getTotalDistance() * 0.000621371);
		double bikeDur = bike.getTotalDuration() / 60;

		long transitDist = (long) (transit.getTotalDistance() * 0.000621371);
		double transitDur = transit.getTotalDuration() / 60;

	}

	private static void compareRoutes(Map<String, Route> routeMap_) {
		logger.info("Comparing " + routeMap_.size() + " routes for travel types : " + routeMap_.keySet().toString());
		Entry<String, Route> quickest = null;
		Entry<String, Route> mostEfficient = null;
		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (quickest == null || entry.getValue().getTotalDuration() < quickest.getValue().getTotalDuration()) {
				quickest = entry;
			}

			if (mostEfficient == null
					|| entry.getValue().getNumberOfLegs() < mostEfficient.getValue().getNumberOfLegs()) {
				mostEfficient = entry;
			}
		}
		String results = "The quickest form of travel is " + quickest.getKey() + ", with a duration of "
				+ quickest.getValue().getTotalDuration() + " minutes to travel "
				+ quickest.getValue().getTotalDistance() + " miles.";
		// if (!quickest.getKey().equals(mostEfficient.getKey())) {
		// results = results + "\n However, " + mostEfficient.getKey()
		// + " has fewer legs to the trip and should take " +
		// mostEfficient.getValue().getTotalDuration()
		// + " minutes to travel " + mostEfficient.getValue().getTotalDistance()
		// + " miles.";
		// }
		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (!entry.getKey().equals(quickest.getKey())) {
				Route route = entry.getValue();
				results += "\n Option : " + entry.getKey() + " would take " + route.getTotalDuration()
						+ " minutes to travel " + route.getTotalDistance() + " miles over " + route.getNumberOfLegs()
						+ " legs.";
			}
		}
		System.out.println(results);

	}
}
