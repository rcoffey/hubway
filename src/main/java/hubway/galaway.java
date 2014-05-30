package hubway;

import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.HubwayQueryBuilder;
import hubway.utility.IntegerConverter;
import hubway.utility.PlacesQueryBuilder;
import hubway.utility.WundergroundQueryBuilder;

import java.util.List;
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
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

public class galaway {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(galaway.class);
		// Get the Beans
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring.galaway.beans.xml");

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

		MongoStationPair test = Calculator.printMinMaxStations(stationList);

		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");
		JSONObject birthdayRides = hubwayQuerier.query("trip", "&start_date__gte=2011-08-01&end_date__lte=2011-08-31");
		System.out.println(birthdayRides.length());

		WundergroundQueryBuilder wunderground = (WundergroundQueryBuilder) context.getBean("wundergroundQueryBuilder");
		JSONObject birthdayWeather = wunderground.queryHistorical("20130821", "MA/Boston");
		System.out.println(wunderground.getMostRecentQuery() + birthdayWeather.toString());

		JSONObject bostonStations = hubwayQuerier.query("station", "&name__icontains=Boston");
		System.out.println(bostonStations.toString(2));

		test.addTrips(hubwayQuerier);
		
		// does it make sense to print the list of station name / station id pairs here?
		
		System.out.println("Please enter your start station id:");
		Scanner input = new Scanner(System.in);
		int startStationId = input.nextInt();
		System.out.println("Please enter your destination station id:");
		int destStationId = input.nextInt();
		input.close();

		DaoQuery query = dao.createQuery();
		query.eq("hubwayId", startStationId);
		query.setCollection("Stations");
		Station startStation = query.findObject(Station.class);
		System.out.println("You are starting at " + startStation.station);
		query.clear();
		query.eq("hubwayId", destStationId);
		query.setCollection("Stations");
		Station destStation = query.findObject(Station.class);
		System.out.println("You are going to " + destStation.station);
		
		MongoStationPair stationsOfInterest = new MongoStationPair(startStation, destStation);
		stationsOfInterest.addTrips(hubwayQuerier);


		PlacesQueryBuilder places = (PlacesQueryBuilder) context.getBean("placesQueryBuilder");
		JSONObject placesResponse = places.queryMbtaNear(42.351313, -71.116174, 500);
		System.out.println(places.getMostRecentQuery() + placesResponse.toString(2));
	}

	/**
	 * hubway api url/queries: http://hubwaydatachallenge.org/api/v1/station/
	 * ?format =json&username=cbaltera&api_key=25f3498d4e7f722
	 * a0ed6f3757542669b443e21a6 &name__icontains=Boston
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
