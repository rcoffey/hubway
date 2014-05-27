package hubway.hubway;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class galaway {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.galaway.beans.xml");
		MongoTemplate client = (MongoTemplate) context.getBean("mongoTemplate");
		DB galawayDb = client.getDb();

		DBCollection stations = galawayDb.getCollection("Stations");

		System.out
				.println("There are " + stations.getCount() + " stations");
		
	}

}
