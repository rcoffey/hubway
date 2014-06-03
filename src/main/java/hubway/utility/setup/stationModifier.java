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
		
		/* This block is to insert stations */
		  
		/* // remove all existing stations from Stations collection
		//stations.remove(query);
		
		// get station info from hubway api
		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");
		JSONObject stationData = hubwayQuerier.query("station", "&id__gt=75&id__lte=100");
		// happen to know there are <100 stations visible in api, so don't need meta.next
		JSONArray jsonStations = stationData.getJSONArray("objects");
		System.out.println("There are " + jsonStations.length() + " stations available.");
		DBObject stationObj;
		double lat, lng;
		JSONObject station;
		for (int i=0; i<jsonStations.length(); i++){
			station = jsonStations.getJSONObject(i);
			lat = station.getJSONObject("point").getJSONArray("coordinates").getDouble(1);
			lng = station.getJSONObject("point").getJSONArray("coordinates").getDouble(0);
			stationObj = BasicDBObjectBuilder.start()
					.add("_id", station.getInt("id"))
							.add("station", station.getString("name"))
							.add("lat", lat).add("lng", lng).get();
			stations.insert(stationObj);
			System.out.println("Inserted " + station.getString("name"));
		}*/
		
		/* this block is to add trip data to stations */
		/*
		// use skip and limit to run on only part of the collection at a given time
		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");
		String queryString;
		JSONObject tripData;
		int count;
		for (DBObject station : stations.find()) {
			// look up trips with this _id as &start_station=
			queryString = "&start_station=" + station.get("_id");
			tripData = hubwayQuerier.query("trip", queryString).getJSONObject("meta");
			count = tripData.getInt("total_count");
			// set count as tripsFrom
			station.put("tripsFrom", count);
			
			// look up trips with this _id as &end_station=
			queryString = "&end_station=" + station.get("_id");
			tripData = hubwayQuerier.query("trip", queryString).getJSONObject("meta");
			count = tripData.getInt("total_count");
			// set count as tripsTo
			station.put("tripsTo", count);
			stations.save(station);
		}*/
		
		// use trips to calculate most popular destination from given station
		// store the station pair (or just store the station id if there are space constraints)
		// also calc second most popular in hopes of more interesting results
		HubwayQueryBuilder hubwayQuerier = (HubwayQueryBuilder) context.getBean("hubwayQuerier");
		String queryString;
		JSONObject tripData;
		int count;
		int newCount;
		DBObject maxStation;
		for (DBObject station : stations.find()) {
			count = 0;
			System.out.println("Calculating most popular destinations from " + station.get("station"));
			maxStation = station;
			queryString = "&start_station=" + station.get("_id");
			for (DBObject dest : stations.find()) {
				if (dest.get("_id") == station.get("maxDest")) {
					continue;
				}
				queryString += "&end_station=" + dest.get("_id");
				tripData = hubwayQuerier.query("trip", queryString).getJSONObject("meta");
				newCount = tripData.getInt("total_count");
				if (count < newCount) {
					maxStation = dest;
					count = newCount;
				}
			}
			station.put("penMaxDest", maxStation.get("_id"));
			stations.save(station);
		}
	}
}