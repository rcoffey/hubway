package hubway.utility.setup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hubway.Station;
import hubway.StationPair;
import hubway.galaway;
import hubway.models.TripInput;
import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.IntegerConverter;
import hubway.utility.TripDataReader;

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
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class stationPairsToDB {
	public static void main(String[] args){
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

		// get all station pairs
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
		List<StationPair> test = Calculator.createStationPairs(stationList);
		
		// get all trips 
		String trips = "C:\\Users\\cbaltera\\Downloads\\hubway-updated-26-feb-2014\\hubwaydata_10_12_to_11_13.csv";
		// !CL get the list of trips
		LinkedList<TripInput> tripList = TripDataReader.extractStationCSV(trips);

		// apportion trips to station pairs by time (keep total)
		// loop through trips, put in {start : { dest : trips}}
		Map<Integer, Map<Integer, List<TripInput>>> tripMap = new HashMap<Integer, Map<Integer, List<TripInput>>>(); 
		for (TripInput trip : tripList) {
			logger.info("Working on " + trip.Id);
			if (tripMap.containsKey(trip.Station_Start)){
				if (tripMap.get(trip.Station_Start).containsKey(trip.Station_End)){
					tripMap.get(trip.Station_Start).get(trip.Station_End).add(trip);
				} else {
					List<TripInput> rides = new ArrayList<TripInput>();
					rides.add(trip);
					tripMap.get(trip.Station_Start).put(trip.Station_End, rides);
				}
			} else {
				List<TripInput> rides = new ArrayList<TripInput>();
				rides.add(trip);
				Map<Integer, List<TripInput>> destMap = new HashMap<Integer, List<TripInput>>();
				destMap.put(trip.Station_End, rides);
				tripMap.put(trip.Station_Start, destMap);
			}
		}// end loop through trips
		
		// loop through station pairs, look up trips with station1:station2
		// add to appropriate day/time categories
		for (StationPair pair : test){
			int startId = Integer.parseInt(pair.station1);
			if (startId == 13)
				startId = 102;
			else if (startId==23)
				startId = 99;
			else if (startId==35)
				startId = 112;
			else if (startId==37)
				startId = 110;
			else if (startId==38)
				startId = 111;
			else if (startId==56)
				startId = 100;
			else if (startId==60)
				startId = 113;
			else if (startId==61)
				startId = 103;
			else if (startId==82)
				startId = 126;
			else if (startId==85)
				startId = 104;
			else if (startId==97)
				startId = 129;
			int endId = Integer.parseInt(pair.station2);
			if (endId == 13)
				endId = 102;
			else if (endId==23)
				endId = 99;
			else if (endId==35)
				endId = 112;
			else if (endId==37)
				endId = 110;
			else if (endId==38)
				endId = 111;
			else if (endId==56)
				endId = 100;
			else if (endId==60)
				endId = 113;
			else if (endId==61)
				endId = 103;
			else if (endId==82)
				endId = 126;
			else if (endId==85)
				endId = 104;
			else if (endId==97)
				endId = 129;
			
			HashMap<String, Integer> tripsByTime = new HashMap<String, Integer>(11);
			int total = 0;
			if (tripMap.containsKey(startId) && tripMap.get(startId).containsKey(endId)){
				for (TripInput mappedTrip : tripMap.get(startId).get(endId)) {
					total ++;
					String time = computeTime(mappedTrip.Date_Start);
					String day = computeDay(mappedTrip.Date_Start);
					if (tripsByTime.containsKey(time)){
						int k = tripsByTime.get(time);
						tripsByTime.put(time, k+1);
					} else {
						tripsByTime.put(time, 1);
					}
					if (tripsByTime.containsKey(day)){
						int k = tripsByTime.get(day);
						tripsByTime.put(day, k+1);
					} else {
						tripsByTime.put(day, 1);
					}
				}
			}
			tripsByTime.put("total", total);
			pair.tripsByTime = tripsByTime;
		}
		
		// add updated station pair objects (ie with trips) to DB
		DBCollection pairs = galawayDb.getCollection("Station Pairs");
		DBObject pairObj, mapObj;
		JSONObject jMap;
		for (StationPair p : test) {
			jMap = new JSONObject(p.tripsByTime);
			mapObj = (DBObject) JSON.parse(jMap.toString());
			pairObj = BasicDBObjectBuilder.start()
					.add("station1", p.station1).add("station2", p.station2)
					.add("tripCount", p.tripsByTime.get("total")).add("tripsByTime", mapObj).get();	
			pairs.insert(pairObj);
			System.out.println("Inserted " + p.station1 + p.station2);
		}
		
	}
	// for now just using start time to set time
		private static String computeTime(Date time){
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			String t;
			if (hour < 4 || 20 <= hour){
				t = "NIGHT";
			} else if (4 <= hour && hour < 12) {
				t = "MORNING";
			} else {
				t = "AFTERNOON";
			}
			return t;
		}
		private static String computeDay(Date date){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String day;
			switch(cal.get(Calendar.DAY_OF_WEEK)){
			case 1:
				day = "SUNDAY";
				break;
			case 2:
				day = "MONDAY";
				break;
			case 3:
				day = "TUESDAY";
				break;
			case 4:
				day = "WEDNESDAY";
				break;
			case 5:
				day = "THURSDAY";
				break;
			case 6:
				day = "FRIDAY";
				break;
			case 7:
				day = "SATURDAY";
				break;
			default:
				day = ""; // should never happen
				break;
			}
			return day;
		}

	}