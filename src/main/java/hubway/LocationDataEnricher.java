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

	public Map<String, Route> getRoutes(LatLng origin_, LatLng destination_) {
		HashMap<String, Route> results = new HashMap<String, Route>(2);
		Route directionsBike = _directionsQueryBuilder.queryString(origin_, destination_, "bicycling");
		Route directionsTransit = _directionsQueryBuilder.queryString(origin_, destination_, "transit");
		Route directionsWalking = _directionsQueryBuilder.queryString(origin_, destination_, "walking");

		results.put("bicycling", directionsBike);
		results.put("transit", directionsTransit);
		results.put("walking", directionsWalking);

		return results;

	}

	public Map<String, JSONObject> getHubways(LatLng origin_, LatLng destination_, int radius_) {
		HashMap<String, JSONObject> results = new HashMap<String, JSONObject>(2);

		JSONObject hubwayNear = _placesQueryBuilder.queryHubwayNear(origin_, radius_);
		JSONObject hubwayDest = _placesQueryBuilder.queryHubwayNear(destination_, radius_);

		results.put("hubwayNearOrigin", hubwayNear);
		results.put("hubwayNearDestination", hubwayDest);
		return results;
	}

}
