package hubway;

import hubway.utility.DirectionsQueryBuilder;
import hubway.utility.DistanceQueryBuilder;
import hubway.utility.PlacesQueryBuilder;
import hubway.utility.WundergroundQueryBuilder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javadocmd.simplelatlng.LatLng;

public class LocationDataEnricher {
	protected WundergroundQueryBuilder _weatherQueryBuilder;
	protected PlacesQueryBuilder _placesQueryBuilder;
	protected DirectionsQueryBuilder _directionsQueryBuilder;
	protected DistanceQueryBuilder _distanceQueryBuilder;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public LocationDataEnricher(WundergroundQueryBuilder weatherQueryBuilder_, PlacesQueryBuilder placesQueryBuilder_,
			DirectionsQueryBuilder directionsQueryBuilder_, DistanceQueryBuilder distanceQueryBuilder_) {
		_weatherQueryBuilder = weatherQueryBuilder_;
		_placesQueryBuilder = placesQueryBuilder_;
		_directionsQueryBuilder = directionsQueryBuilder_;
		_distanceQueryBuilder = distanceQueryBuilder_;
	}

	public JSONObject getHistoricalWeather(String date_, String stateCity_) {
		return _weatherQueryBuilder.queryHistorical(date_, stateCity_);
	}

	public Map<String, JSONObject> getLocationData(LatLng origin_, LatLng destination_, int radius_) {
		HashMap<String, JSONObject> results = new HashMap<String, JSONObject>(3);
		JSONObject mbtaOrigin = _placesQueryBuilder.queryMbtaNear(origin_, radius_);
		logger.info("MBTAs near origin " + mbtaOrigin);
		JSONObject mbtaDest = _placesQueryBuilder.queryMbtaNear(destination_, radius_);
		logger.info("MBTAs near destination " + mbtaDest);
		JSONObject directionsBike = _directionsQueryBuilder.queryDirections(origin_, destination_, "bicycling");
		logger.info("Bicycling directions " + directionsBike);
		JSONObject directionsTransit = _directionsQueryBuilder.queryDirections(origin_, destination_, "transit");
		logger.info("Transit directions " + directionsTransit);

		results.put("mbtaOrigin", mbtaOrigin);
		results.put("mbtaDestination", mbtaDest);
		results.put("bikeDirections", directionsBike);
		results.put("transitDirections", directionsTransit);

		return results;

	}

}
