package hubway;

import hubway.json.Route;
import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.GeocodeQueryBuilder;
import hubway.utility.HubwayQueryBuilder;
import hubway.utility.IntegerConverter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.MongoDaoImpl;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.googlecode.mjorm.query.DaoQuery;
import com.javadocmd.simplelatlng.LatLng;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class GalawayService {

	final MongoTemplate _mongoTemplate;
	final HubwayQueryBuilder _hubwayQuerier;
	final LocationDataEnricher _locationEnricher;
	final GeocodeQueryBuilder _geocodeQueryBuilder; 

	Logger logger = LoggerFactory.getLogger(GalawayService.class);

	protected MongoDao _dao = null;

	public GalawayService(final MongoTemplate mongoTemplate_, final HubwayQueryBuilder hubwayQuerier_,
			final LocationDataEnricher locationEnricher_, final GeocodeQueryBuilder geocodeQueryBuilder_) {
		_mongoTemplate = mongoTemplate_;
		_hubwayQuerier = hubwayQuerier_;
		_locationEnricher = locationEnricher_;
		_geocodeQueryBuilder = geocodeQueryBuilder_;
	}

	public void runGalaway() {
		initialize();

		List<Station> stationList = _dao.findObjects("Stations", new BasicDBObject(), Station.class).readAll();
		Calculator.printMinMaxStations(stationList);

	}

	protected void initialize() {
		DB galawayDb = _mongoTemplate.getDb();
		if (!galawayDb.isAuthenticated()) {
			logger.error("Authentication failed for mongoDb :" + _mongoTemplate.toString());
		}

		// Use mjorm to map our results to our java Stations.
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(Station.class);
		// Need these custom converters because they didn't think it was
		// important to go from String -> Common freaking types.
		mapper.registerTypeConverter(new DateConverter());
		mapper.registerTypeConverter(new IntegerConverter());
		_dao = new MongoDaoImpl(galawayDb, mapper);
	}

	public void compareRoutes(Map<String, Route> routeMap_) {
		logger.info("Comparing " + routeMap_.size() + " routes for travel types : " + routeMap_.keySet().toString());
		Entry<String, Route> quickest = null;
		Entry<String, Route> mostEfficient = null;
		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (quickest == null || entry.getValue().getTotalDuration() < quickest.getValue().getTotalDuration()) {
				quickest = entry;
			}

			if (mostEfficient == null
					|| entry.getValue().getNumberOfLegs() < mostEfficient.getValue().getNumberOfLegs()) {
				mostEfficient = entry;
			}
		}
		String results = "The quickest form of travel is " + quickest.getKey() + ", with a duration of "
				+ quickest.getValue().getTotalDuration() + " minutes to travel "
				+ quickest.getValue().getTotalDistance() + " miles.";
		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (!entry.getKey().equals(quickest.getKey())) {
				Route route = entry.getValue();
				results += "\n Option : " + entry.getKey() + " would take " + route.getTotalDuration()
						+ " minutes to travel " + route.getTotalDistance() + " miles over " + route.getNumberOfLegs()
						+ " legs.";
			}
		}
		System.out.println(results);

	}

	public Station processStation(int startStationId_) {
		DaoQuery query = _dao.createQuery();
		query.eq("_id", startStationId_);
		query.setCollection("Stations");
		Station startStation = query.findObject(Station.class);
		if (startStation == null) {
			System.out.println("No such station as " + startStationId_ + "!");
			return null;
		}
		return startStation;
	}
	
	public Station processAddress(String address) {
		LatLng coords = _geocodeQueryBuilder.queryLatLng(address);
		double[] loc = {coords.getLongitude(), coords.getLatitude()};
			
		DBObject nearQuery = BasicDBObjectBuilder.start().add("geometry.coordinates", 
		BasicDBObjectBuilder.start().add("$near", loc).get()).get();
		Station nearStation = _dao.findObject("Stations", nearQuery, Station.class);
		if (nearStation == null) {
			System.out.println("Alas, no nearby stations.  Try again with another address.");
			return null;
		}
		System.out.println("Your closest station is " + nearStation.station + "\n");
		return nearStation;
	}

	public void adviseDestination(Station startStation_) {
		Station destStation = processStation(startStation_.maxDest);
		StationPair stationsOfInterest = new StationPair(startStation_, destStation);

		System.out.println("Perhaps you would like to go to " + destStation.station + ", the most popular trip from "
				+ startStation_.station + "\n");
		produceOutput(stationsOfInterest);
	}

	public void produceOutput(StationPair stationsOfInterest) {
		stationsOfInterest.addTrips(_hubwayQuerier);

		stationsOfInterest.info();

		Map<String, Route> locationDataMap = _locationEnricher.getRoutes(stationsOfInterest.station1.getLatLng(),
				stationsOfInterest.station2.getLatLng());
		compareRoutes(locationDataMap);

	}
}
