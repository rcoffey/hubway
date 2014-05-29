package hubway;


import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.HubwayQuery;
import hubway.utility.IntegerConverter;

import java.util.ArrayList;
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
		System.out.println(stationList.toString());
		
		List<MongoStationPair> stationPairList = new ArrayList<MongoStationPair>();
		for (Station station1 : stationList) {
			for (Station station2 : stationList) {
				stationPairList.add(new MongoStationPair(station1, station2));
			}
		}

		MongoStationPair test = Calculator.printMinMaxStations(stationList);

		HubwayQuery hubwayQuerier = (HubwayQuery) context
				.getBean("hubwayQuerier");
		JSONObject bostonStations = hubwayQuerier.query("station",
				"&name__icontains=Boston");
		System.out.println(bostonStations.toString(2));
		
		test.addTrips(hubwayQuerier);
		//the below will query for all trips among all stationpairs, so a lot.
		//thus commenting out for checkin
		/**
		for (MongoStationPair stationPair : stationPairList) {
			stationPair.addTrips(hubwayQuerier);
		}*/

	}

}
