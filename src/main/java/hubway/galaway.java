package hubway;

import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.DistanceQueryBuilder;
import hubway.utility.HubwayQueryBuilder;
import hubway.utility.IntegerConverter;

import java.util.List;
import java.util.Map;
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

		// does it make sense to print the list of station name / station id
		// pairs here?

		System.out.println("Please enter your start station id:");
		Scanner input = new Scanner(System.in);
		int startStationId = input.nextInt();
		System.out.println("Please enter your destination station id:");
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
		query.eq("_id", destStationId);
		query.setCollection("Stations");
		Station destStation = query.findObject(Station.class);
		if (destStation == null) {
			System.out.println("No such station as " + destStationId + "!");
			return;
		}

		MongoStationPair stationsOfInterest = new MongoStationPair(startStation, destStation);
		DistanceQueryBuilder distance = (DistanceQueryBuilder) context.getBean("distanceQueryBuilder");
		stationsOfInterest.setNavDist(distance);
		stationsOfInterest.addTrips(hubwayQuerier);

		stationsOfInterest.info();

		LocationDataEnricher locationData = (LocationDataEnricher) context.getBean("locationEnricher");
		JSONObject weather = locationData.getHistoricalWeather("20130821", "MA/Boston");
		Map<String, JSONObject> locationDataMap = locationData.getLocationData(startStation.getLatLng(),
				destStation.getLatLng(), 500);
		System.out.print("Done");
	}

}
