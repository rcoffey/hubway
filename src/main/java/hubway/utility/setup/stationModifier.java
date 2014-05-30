package hubway.utility.setup;

import hubway.galaway;
import hubway.utility.HubwayQueryBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class stationModifier {
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
		
		DBCollection stations = galawayDb.getCollection("Stations");
		DBObject query = new BasicDBObject(); // select * query
		
		// remove all existing stations from Stations collection
		stations.remove(query);
		
		// get station info from hubway api
		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");
		JSONObject stationData = hubwayQuerier.query("station", "");
		// happen to know there are <100 stations visible in api, so don't need meta.next
		JSONArray jsonStations = stationData.getJSONArray("objects");
		DBObject stationObj;
		double lat, lng;
		JSONObject station;
		for (int i=0; i<jsonStations.length(); i++){
			station = jsonStations.getJSONObject(i);
			lat = station.getJSONObject("point").getJSONArray("coordinates").getDouble(0);
			lng = station.getJSONObject("point").getJSONArray("coordinates").getDouble(1);
			stationObj = BasicDBObjectBuilder.start()
					.add("_id", station.getInt("id"))
							.add("station", station.getString("name"))
							.add("lat", lat).add("lng", lng).get();
			stations.insert(stationObj);
		}
		
	}
}