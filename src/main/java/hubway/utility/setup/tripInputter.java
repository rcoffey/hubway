package hubway.utility.setup;

import java.util.List;

import hubway.Trip;
import hubway.galaway;
import hubway.models.TripInput;
import hubway.utility.IntegerConverter;
import hubway.utility.TripDataReader;

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


public class tripInputter {
	
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
				
		DBCollection trips = galawayDb.getCollection("trips");
		DBObject query = new BasicDBObject(); // select * query
		trips.remove(query);
		
		/*
		// attempt to use mjorm to insert
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(Trip.class);
		mapper.registerTypeConverter(new IntegerConverter()); // do we need others?
		MongoDao dao = new MongoDaoImpl(galawayDb, mapper);
		*/
		
		// get array or whatever of tripInputs from clem
		String fileName = "C:\\Users\\cbaltera\\Downloads\\hubway-updated-26-feb-2014\\hubwaydata_10_12_to_11_13.csv";
		List<TripInput> tripInputs = TripDataReader.extractStationCSV(fileName);
		DBObject tripObj;
		for (TripInput input : tripInputs) {
			if (input.Station_Start.equals(input.Station_End)){
				continue; // ignore joy-rides
			}
			logger.info("Inserting a trip from " + input.Station_Start + " to " + input.Station_End);
			Trip trip = new Trip(input);
			//dao.createObject("trips", trip);
			tripObj = BasicDBObjectBuilder.start()
					.add("_id", trip._id)
					.add("startDay", trip.startDay)
					.add("endDay", trip.endDay)
					.add("time", trip.time)
					.add("start_station", trip.start_station)
					.add("end_station", trip.end_station)
					.add("duration", trip.duration).get();
			trips.insert(tripObj);
		}
		
		
	}
}