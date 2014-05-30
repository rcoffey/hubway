package hubway.utility.setup;

import hubway.galaway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

// hopefully this is one-use and doesn't need to be run again
public class rename {
	
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
		
		BasicDBObject query = new BasicDBObject(); // select * query
		BasicDBObject updates = new BasicDBObject(); // rename thing
		updates.put("$rename", new BasicDBObject("id", "hubwayId"));
		
		
		stations.update(query, updates, false, true); 
		
	}
	
}