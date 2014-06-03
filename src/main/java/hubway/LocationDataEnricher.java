package hubway;

import hubway.json.Route;
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

	public Map<String, Object> getLocationData(LatLng origin_, LatLng destination_, int radius_) {
		HashMap<String, Object> results = new HashMap<String, Object>(4);
		JSONObject mbtaOrigin = _placesQueryBuilder.queryMbtaNear(origin_, radius_);
		JSONObject mbtaDest = _placesQueryBuilder.queryMbtaNear(destination_, radius_);
		Route directionsBike = _directionsQueryBuilder.queryString(origin_, destination_, "bicycling");
		Route directionsTransit = _directionsQueryBuilder.queryString(origin_, destination_, "transit");

		results.put("mbtaOrigin", mbtaOrigin);
		results.put("mbtaDestination", mbtaDest);
		results.put("bikeDirections", directionsBike);
		results.put("transitDirections", directionsTransit);

		return results;

	}
}
