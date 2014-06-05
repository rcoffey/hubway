package hubway.utility.setup;

import java.util.HashMap;
import java.util.Map;

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
import com.mongodb.util.JSON;

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
		DBObject query;
		
		/* This block is to insert stations */
		/*  
		// remove all existing stations from Stations collection
		// stations.remove(query);
		
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
		}
		*/
		
		// this block is to reformat lat and lng into geojson syntax
		/*double lat, lng;
		String geoString;
		JSONObject geo;
		DBObject geoObj;
		for (DBObject station : stations.find()) {
			logger.info("Reformatting geometry for " + station.get("station"));
		// get lat and lng from the station obj in the db
			lat = (Double) station.get("lat");
			lng = (Double) station.get("lng");
		// construct geoJSON string
			geoString = "{\"coordinates\": [" + lng + "," + lat + "], \"type\": \"Point\"}";
		// turn into JSONObject
			geoObj = (DBObject) JSON.parse(geoString);
		// put into station with key "geometry"
			station.put("geometry", geoObj);
		// save station
			stations.save(station);
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
		}
		*/
		
		// use trips to calculate most popular destination from given station
		// store the station id
		// also calc second most popular in hopes of more interesting results
		Map<String, Integer> count = new HashMap<String, Integer>(11);
		Map<String, String> maxDestByTime;
		for (DBObject station : stations.find()) {
			count.put("total", 0);
			count.put("MONDAY", 0);
			count.put("TUESDAY", 0);
			count.put("WEDNESDAY", 0);
			count.put("THURSDAY", 0);
			count.put("FRIDAY", 0);
			count.put("SATURDAY", 0);
			count.put("SUNDAY", 0);
			count.put("MORNING", 0);
			count.put("AFTERNOON", 0);
			count.put("NIGHT", 0);
			maxDestByTime = new HashMap<String, String>(11);
			DBObject maxDest = (DBObject) station.get("maxDest");
			// get all station pairs starting at this station
			query = BasicDBObjectBuilder.start()
					.add("station1", station.get("_id").toString()).get();
			DBCollection stationPairs = galawayDb.getCollection("Station Pairs");
			
			System.out.println("Calculating most popular destinations from " + station.get("station"));

			for (DBObject pair : stationPairs.find(query)) {
				DBObject trips = (DBObject) pair.get("tripsByTime");
				if ((Integer)trips.get("total") > count.get("total")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("total"))){
					count.put("total", (Integer)trips.get("total"));
					maxDestByTime.put("total", (String)pair.get("station2"));}
				}
				if (trips.containsField("MONDAY")&&(Integer)trips.get("MONDAY") > count.get("MONDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("MONDAY"))){
					count.put("MONDAY", (Integer)trips.get("MONDAY"));
					maxDestByTime.put("MONDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("TUESDAY")&&(Integer)trips.get("TUESDAY") > count.get("TUESDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("TUESDAY"))){
					count.put("TUESDAY", (Integer)trips.get("TUESDAY"));
					maxDestByTime.put("TUESDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("WEDNESDAY")&&(Integer)trips.get("WEDNESDAY") > count.get("WEDNESDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("WEDNESDAY"))){
					count.put("WEDNESDAY", (Integer)trips.get("WEDNESDAY"));
					maxDestByTime.put("WEDNESDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("THURSDAY")&&(Integer)trips.get("THURSDAY") > count.get("THURSDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("THURSDAY"))){
					count.put("THURSDAY", (Integer)trips.get("THURSDAY"));
					maxDestByTime.put("THURSDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("FRIDAY")&&(Integer)trips.get("FRIDAY") > count.get("FRIDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("FRIDAY"))){
					count.put("FRIDAY", (Integer)trips.get("FRIDAY"));
					maxDestByTime.put("FRIDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("SATURDAY")&&(Integer)trips.get("SATURDAY") > count.get("SATURDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("SATURDAY"))){
					count.put("SATURDAY", (Integer)trips.get("SATURDAY"));
					maxDestByTime.put("SATURDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("SUNDAY")&&(Integer)trips.get("SUNDAY") > count.get("SUNDAY")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("SUNDAY"))){
					count.put("SUNDAY", (Integer)trips.get("SUNDAY"));
					maxDestByTime.put("SUNDAY", (String)pair.get("station2"));}
				}
				if (trips.containsField("MORNING")&&(Integer)trips.get("MORNING") > count.get("MORNING")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("MORNING"))){
					count.put("MORNING", (Integer)trips.get("MORNING"));
					maxDestByTime.put("MORNING", (String)pair.get("station2"));}
				}
				if (trips.containsField("AFTERNOON")&&(Integer)trips.get("AFTERNOON") > count.get("AFTERNOON")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("AFTERNOON"))){
					count.put("AFTERNOON", (Integer)trips.get("AFTERNOON"));
					maxDestByTime.put("AFTERNOON", (String)pair.get("station2"));}
				}
				if (trips.containsField("NIGHT")&&(Integer)trips.get("NIGHT") > count.get("NIGHT")){
					if (!((String)pair.get("station2")).equals((String)maxDest.get("NIGHT"))){
					count.put("NIGHT", (Integer)trips.get("NIGHT"));
					maxDestByTime.put("NIGHT", (String)pair.get("station2"));}
				}
			}

			station.put("penMaxDest", maxDestByTime);
			
			stations.save(station);
		}
	}
}