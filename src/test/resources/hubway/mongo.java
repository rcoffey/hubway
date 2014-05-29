package hubway;

import hubway.Station;
import hubway.utility.Calculator;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class mongo {

	public static void main(String[] args) {
		try {
			ServerAddress mongoServer = new ServerAddress(
					"ds049868.mongolab.com", 49868);
			List<ServerAddress> serverList = new LinkedList<ServerAddress>();
			serverList.add(mongoServer);

			MongoCredential cred = MongoCredential.createMongoCRCredential(
					"galaway", "galaway", "galaway1".toCharArray());
			List<MongoCredential> credList = new LinkedList<MongoCredential>();
			credList.add(cred);
			MongoClient galawayMongo = new MongoClient(serverList, credList);
			DB galawayDb = galawayMongo.getDB("galaway");

			DBCollection stations = galawayDb.getCollection("Stations"); 

			System.out
					.println("There are " + stations.getCount() + " stations");
			
			ArrayList<Station> stationList = new ArrayList<Station>();
			
			for (DBObject stationObj : stations.find()) {
				stationList.add(new Station(stationObj));
			}
	        Calculator.printMinMaxStations(stationList);        

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
