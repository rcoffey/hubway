package hubway;

import hubway.utility.Calculator;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class galaway {
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(galaway.class);
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring.galaway.beans.xml");
		MongoTemplate client = (MongoTemplate) context.getBean("mongoTemplate");
		DB galawayDb = client.getDb();
		if(!galawayDb.isAuthenticated()){
			logger.error("Authentication failed for mongoDb :" + client.toString() );
		}
		DBCollection stations = galawayDb.getCollection("Stations");

		System.out
				.println("There are " + stations.getCount() + " stations");
		
		ArrayList<Station> stationList = new ArrayList<Station>();
		
		for (DBObject stationObj : stations.find()) {
			stationList.add(new Station(stationObj));
		}
        Calculator.printMinMaxStations(stationList);        

	}


}
