package hubway;

import hubway.json.Route;
import hubway.json.Weather;
import hubway.utility.Calculator;
import hubway.utility.DateConverter;
import hubway.utility.GeocodeQueryBuilder;
import hubway.utility.HubwayQueryBuilder;
import hubway.utility.IntegerConverter;

import java.util.ArrayList;
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
	protected List<StationPair> _stationPairs;

	Logger logger = LoggerFactory.getLogger(GalawayService.class);

	protected MongoDao _dao = null;

	public GalawayService(final MongoTemplate mongoTemplate_, final HubwayQueryBuilder hubwayQuerier_,
			final LocationDataEnricher locationEnricher_, final GeocodeQueryBuilder geocodeQueryBuilder_) {
		_mongoTemplate = mongoTemplate_;
		_hubwayQuerier = hubwayQuerier_;
		_locationEnricher = locationEnricher_;
		_geocodeQueryBuilder = geocodeQueryBuilder_;
		_stationPairs = new ArrayList<StationPair>();
	}

	public void runGalaway() {
		initializeMongo();

		System.out.println("\nDisclaimer: Not all Hubway stations are included in the historical data"
				+ " on which we've based our results and suggestions.  Some answers may therefore be unexpected.\n");
		List<Station> stationList = _dao.findObjects("Stations", new BasicDBObject(), Station.class).readAll();
		_stationPairs = Calculator.createStationPairs(stationList);

	}

	/**
	 * Get mongo database connection, register custom type converters
	 */
	protected void initializeMongo() {
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

	/**
	 * Create travel options based on route duration and distance as well as
	 * current weather conditions.
	 * 
	 * @param weather
	 * @param routeMap_
	 */
	public void compareRoutes(Weather weather, Map<String, Route> routeMap_) {
		logger.debug("Comparing " + routeMap_.size() + " routes for travel types : " + routeMap_.keySet().toString());
		Entry<String, Route> quickest = null;
		Entry<String, Route> mostEfficient = null;
		Entry<String, Route> recommended = null;
		Entry<String, Route> badweatherOption = null;

		Boolean weatherIsGood = weather.tempf > 55 && weather.tempf < 85 && weather.windmph < 10
				&& !weather.weather.contains("rain") && !weather.weather.contains("snow");

		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (quickest == null || entry.getValue().getTotalDuration() < quickest.getValue().getTotalDuration()) {
				quickest = entry;
			}

			if (mostEfficient == null
					|| entry.getValue().getNumberOfLegs() < mostEfficient.getValue().getNumberOfLegs()) {
				mostEfficient = entry;
			}

			// !CL we are trying to sell bicycles , so lets take precedence if
			// it is nice out.
			if (weatherIsGood && (entry.getKey().contains("walking") || entry.getKey().contains("bicycling"))) {
				if (recommended != null && !recommended.getKey().contains("walking"))
					logger.info("recommended is not null and we are currently bicycling");
				else
					recommended = entry;
			}

			if (!(entry.getKey().contains("walking") || entry.getKey().contains("bicycling"))) {
				if (recommended != null && !recommended.getKey().contains("driving"))
					logger.info("recommended is not null and we are currently taking transit");
				else
					badweatherOption = entry;
			}
		}

		if (recommended == null)
			recommended = badweatherOption;
		System.out.println("\n***** Route Options *****");

		String results = "The quickest form of travel is " + quickest.getKey() + ", with a duration of "
				+ quickest.getValue().getTotalDuration() + " minutes to travel "
				+ quickest.getValue().getTotalDistance() + " miles.";

		if (recommended != null)
			results += "\n Recommended Option : " + recommended.getKey() + " would take "
					+ recommended.getValue().getTotalDuration() + " minutes to travel "
					+ recommended.getValue().getTotalDistance() + " miles. " + " The weather is " + weather.weather
					+ " and it feels like " + weather.feelslike + ".";

		for (Entry<String, Route> entry : routeMap_.entrySet()) {
			if (!entry.getKey().equals(quickest.getKey())) {
				Route route = entry.getValue();
				results += "\n Option : " + entry.getKey() + " would take " + route.getTotalDuration()
						+ " minutes to travel " + route.getTotalDistance() + " miles.";
			}
		}
		System.out.println(results);

	}

	/**
	 * Get station data from mongo for a station id
	 * 
	 * @param startStationId_
	 * @return
	 */
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

	/**
	 * Get a Station by translating address to a lat, lng via the Google
	 * Geocoding API then use this location in a mongo near query.
	 * 
	 * @param address
	 * @return
	 */
	public Station processAddress(String address) {
		LatLng coords = _geocodeQueryBuilder.queryLatLng(address);
		if (coords == null) {
			System.out.println("Are you sure you entered an address?  Check your syntax and try again.");
			return null;
		}
		double[] loc = { coords.getLongitude(), coords.getLatitude() };

		DBObject nearQuery = BasicDBObjectBuilder.start()
				.add("geometry.coordinates", BasicDBObjectBuilder.start().add("$near", loc).get()).get();
		Station nearStation = _dao.findObject("Stations", nearQuery, Station.class);
		if (nearStation == null) {
			System.out.println("Alas, no nearby stations.  Try again with another address.");
			return null;
		}
		System.out.println("Your closest station is " + nearStation.station + "\n");
		return nearStation;
	}

	/**
	 * Advises a the most popular destination from the start, or the second most
	 * popular if the first returns to the the start.
	 * 
	 * @param startStation_
	 */
	public void adviseDestination(Station startStation_) {
		Station destStation;
		if (startStation_.maxDest != Integer.parseInt(startStation_.id)) {
			destStation = processStation(startStation_.maxDest);
			if (destStation == null) {
				logger.warn("No historical data was found to make a prediction for start station "
						+ startStation_.getStation());
				return;
			}
			System.out.println("Perhaps you would like to go to " + destStation.station
					+ ", the most popular trip from " + startStation_.station + "\n");
		} else {
			System.out.println("Perhaps you would like to go for a joyride, starting and ending at "
					+ startStation_.station + "\n This is the most popular trip from " + startStation_.station + "\n");
			destStation = processStation(startStation_.penMaxDest);
			System.out.println("Alternatively, you could go to " + destStation.station
					+ ", the second most popular trip from " + startStation_.station + "\n");
		}

		produceOutput(startStation_, destStation);
	}

	/**
	 * Compare routes between two stations.
	 * 
	 * @param origin_
	 * @param destination_
	 */
	public void produceOutput(Station origin_, Station destination_) {
		Map<String, Route> locationDataMap = _locationEnricher.getRoutes(origin_.getLatLng(), destination_.getLatLng());

		Weather cur = _locationEnricher.getCurrentWeather("MA/Boston");

		compareRoutes(cur, locationDataMap);

	}
}
