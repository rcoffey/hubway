package hubway.utility;

import hubway.galaway;
import hubway.models.TripInput;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class TestGetTrips {

	public TestGetTrips() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		System.out.println("Testing parsing hubway trip data from csv");
		String trips = "C:\\Users\\cbaltera\\Downloads\\hubway-updated-26-feb-2014\\hubwaydata_10_12_to_11_13.csv";

		// !CL get the list of trips
		LinkedList<TripInput> tripList = TripDataReader.extractStationCSV(trips);

		
		Map<Integer, List<TripInput>> joyrides = new HashMap<Integer, List<TripInput>>();
		Map<Integer, List<TripInput>> startMap = new HashMap<Integer, List<TripInput>>();
		Map<Integer, List<TripInput>> destMap = new HashMap<Integer, List<TripInput>>();
		for (TripInput trip : tripList) {
			if (trip.Station_Start==trip.Station_End){
				if (joyrides.containsKey(trip.Station_Start)){
					joyrides.get(trip.Station_Start).add(trip);
				} else {
					List<TripInput> rides = new ArrayList<TripInput>();
					joyrides.put(trip.Station_Start, rides);
				}		
			} else { // if not a joyride, include in aggregate data
				if (startMap.containsKey(trip.Station_Start)){
					startMap.get(trip.Station_Start).add(trip);
				} else {
					List<TripInput> tripsTo = new ArrayList<TripInput>();
					tripsTo.add(trip);
					startMap.put(trip.Station_Start, tripsTo);
				}
				if (destMap.containsKey(trip.Station_End)){
					destMap.get(trip.Station_End).add(trip);
				} else {
					List<TripInput> tripsFrom = new ArrayList<TripInput>();
					tripsFrom.add(trip);
					destMap.put(trip.Station_End, tripsFrom);
				}	
			}			
		} // end loop through trips
		
		//get stations
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
		Map<String, Integer> tripsToByTime;
		Map<String, Integer> tripsFromByTime;
		Map<String, Integer> joyTrips;
		DBCursor cursor = stations.find();
		for (DBObject station : cursor){
			logger.info("Beginning aggregation for station " + station.get("station"));
			int stationId = (Integer) station.get("_id");
			// joyrides
			if (joyrides.containsKey(stationId)) {
				joyTrips = new HashMap<String, Integer>(11);
				int total = 0;
				for (TripInput trip : joyrides.get(stationId)){
					total ++;
					String time = computeTime(trip.Date_Start);
					String day = computeDay(trip.Date_Start);
					if (joyTrips.containsKey(time)){
						int k = joyTrips.get(time);
						joyTrips.put(time, k+1);
					} else {
						joyTrips.put(time, 1);
					}
					if (joyTrips.containsKey(day)){
						int k = joyTrips.get(day);
						joyTrips.put(day, k+1);
					} else {
						joyTrips.put(day, 1);
					}
				}
				joyTrips.put("total", total);
				station.put("joyrides", joyTrips);
			}
			else
				station.put("joyrides", new HashMap<String, Integer>());
			// tripsTo
			// we want to aggregate by day of week and time of day
			int totalTo = 0;
			tripsToByTime = new HashMap<String, Integer>(11);
			if (destMap.containsKey(stationId)){
				for (TripInput trip : destMap.get(stationId)){
					totalTo++;
					String time = computeTime(trip.Date_Start);
					String day = computeDay(trip.Date_Start);
					if (tripsToByTime.containsKey(time)){
						int k = tripsToByTime.get(time);
						tripsToByTime.put(time, k+1);
					} else {
						tripsToByTime.put(time, 1);
					}
					if (tripsToByTime.containsKey(day)){
						int k = tripsToByTime.get(day);
						tripsToByTime.put(day, k+1);
					} else {
						tripsToByTime.put(day, 1);
					}
				}
			}
			// add total to tripsToByTime
			if ("java.lang.Integer".equals(station.get("tripsTo").getClass().getName())){
				tripsToByTime.put("total", totalTo);
			} else {
				tripsToByTime.put("total", totalTo);
			}
			// put tripsToByTime into station
			JSONObject jTrips = new JSONObject(tripsToByTime);
			String sTrips = jTrips.toString();
			DBObject dTrips = (DBObject) JSON.parse(sTrips);
			station.put("tripsTo", dTrips);
			
			// tripsFrom
			// we want to aggregate by day of week and time of day
			tripsFromByTime = new HashMap<String, Integer>(11);
			int totalFrom = 0;
			if (startMap.containsKey(stationId)){
				for (TripInput trip : startMap.get(stationId)){
					totalFrom++;
					String time = computeTime(trip.Date_Start);
					String day = computeDay(trip.Date_Start);
					if (tripsFromByTime.containsKey(time)){
						int k = tripsFromByTime.get(time);
						tripsFromByTime.put(time, k+1);
					} else {
						tripsFromByTime.put(time, 1);
					}
					if (tripsFromByTime.containsKey(day)){
						int k = tripsFromByTime.get(day);
						tripsFromByTime.put(day, k+1);
					} else {
						tripsFromByTime.put(day, 1);
					}
				}
			}
			// add total to tripsFromByTime
			if ("java.lang.Integer".equals(station.get("tripsFrom").getClass().getName())){
				tripsFromByTime.put("total", totalFrom);
			} else {
				tripsFromByTime.put("total", totalFrom);
			}
			// put tripsFromByTime into station
			jTrips = new JSONObject(tripsFromByTime);
			sTrips = jTrips.toString();
			dTrips = (DBObject) JSON.parse(sTrips);
			station.put("tripsFrom", dTrips);
			
			stations.save(station);
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