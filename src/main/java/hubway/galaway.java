package hubway;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.ObjectIterator;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class galaway {
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(galaway.class);
		// Get the Beans
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring/spring.galaway.beans.xml");

		// Connect to Mongo
		MongoTemplate client = (MongoTemplate) context.getBean("mongoTemplate");
		DB galawayDb = client.getDb();
		if (!galawayDb.isAuthenticated()) {
			logger.error("Authentication failed for mongoDb :"
					+ client.toString());
		}

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
		List<Station> stationList = dao.findObjects("Stations",
				new BasicDBObject(), Station.class).readAll();

		System.out.println("There are " + stationList.size() + " stations");

		printMinMaxStations(stationList);
	}

	private static void printMinMaxStations(List<Station> stationList) {
		Double minDist = Double.MAX_VALUE;
		Double maxDist = Double.MIN_VALUE;
		Station maxStationStart = null, maxStationDest = null, minStationStart = null, minStationDest = null;

		for (Iterator<Station> itStart = stationList.iterator(); itStart
				.hasNext();) {
			Station start = (Station) itStart.next();

			for (Iterator<Station> itDest = stationList.iterator(); itDest
					.hasNext();) {
				Station dest = (Station) itDest.next();

				if (start.station.equalsIgnoreCase(dest.station))
					continue;

				Double dist = distFrom(dest.lat, dest.lng, start.lat, start.lng);

				if (dist < minDist) {
					minDist = dist;
					minStationStart = start;
					minStationDest = dest;
				}

				if (dist > maxDist) {
					maxDist = dist;
					maxStationStart = start;
					maxStationDest = dest;
				}
			}
		}

		System.out
				.println("minStationStart.station() = "
						+ minStationStart.station + ", "
						+ minStationStart.municipality);
		System.out.println("minStationDest.station() = "
				+ minStationDest.station + ", " + minStationDest.municipality);
		System.out.println("minDist() = " + minDist);

		System.out
				.println("maxStationStart.station() = "
						+ maxStationStart.station + ", "
						+ maxStationStart.municipality);
		System.out.println("maxStationDest.station() = "
				+ maxStationDest.station + ", " + maxStationDest.municipality);
		System.out.println("maxDist() = " + maxDist);
	}

	// stolen from the internets.
	public static double distFrom(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 3958.75; // units?
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (double) (dist * meterConversion);
	}

}
