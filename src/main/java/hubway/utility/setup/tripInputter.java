package hubway.utility.setup;

import hubway.Trip;
import hubway.galaway;
import hubway.utility.IntegerConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.DB;


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
				
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(Trip.class);
		mapper.registerTypeConverter(new IntegerConverter()); // do we need others?
		MongoDao dao = new MongoDaoImpl(galawayDb, mapper);
		
		// get array or whatever of tripInputs from clem
		/* for (TripInput input : array) {
		Trip trip = new Trip(input);
		dao.createObject("trips", trip);
		
		}
		*/
		
	}
}