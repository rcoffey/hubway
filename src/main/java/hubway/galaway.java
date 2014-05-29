package hubway;

import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.HubwayQuery;
import hubway.utility.IntegerConverter;

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

		Calculator.printMinMaxStations(stationList);

		HubwayQuery hubwayQuerier = (HubwayQuery) context
				.getBean("hubwayQuerier");
		JSONObject bostonStations = hubwayQuerier.query("station",
				"&name__icontains=Boston");
		System.out.println(bostonStations.toString(2));

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
