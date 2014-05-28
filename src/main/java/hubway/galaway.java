package hubway;

import hubway.utility.Calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;
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
        
        try {
			URL stationQuery = new URL("http://hubwaydatachallenge.org/api/v1/station/?format=json&username=cbaltera&api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6&name__icontains=Boston");
			BufferedReader stationReader = new BufferedReader(new InputStreamReader(stationQuery.openStream()));
        
			String stationJSON = stationReader.readLine(); // hopefully it's one line
			JSONObject bostonStations = new JSONObject(stationJSON);
			
			System.out.println(bostonStations.toString(2));
        
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * hubway api url/queries:
	 * http://hubwaydatachallenge.org/api/v1/station/
	 * ?format=json&username=cbaltera&api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6
	 * &name__icontains=Boston
	 * 
	 * The api doesn't have municipality available for stations. nb_docks is a in a 
	 * separate schema from the basic station info.  nb_bikes and nb_emptyDocks are also
	 * available.
	 * Trip data appears to be the same, so maybe keep stations in mongo and get trips from 
	 * the hubway api.
	 * 
	 * http://hubwaydatachallenge.org/api/v1/trip/
	 * ?format=json&username=cbaltera&api_key=25f3498d4e7f722a0ed6f3757542669b443e21a6
	 * &duration__gt=3600&start_station=33&start_date__gte=2011-08-01&end_date__lte=2011-08-31
	 */
}
