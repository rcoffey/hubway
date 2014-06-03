package hubway.utility;

import hubway.json.Route;

import org.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;

public class DirectionsQueryBuilder extends AQueryBuilder {

	public DirectionsQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryDirections(LatLng origin_, LatLng dest_, String mode_) {
		String url = _url + latLngToString(origin_) + "&destination=" + latLngToString(dest_) + "&mode=" + mode_
				+ "&departure_time=" + System.currentTimeMillis() / 1000 + "&sensor=false";
		return super.query(url);
	}

	public Route queryString(LatLng origin_, LatLng dest_, String mode_) {
		String url = _url + latLngToString(origin_) + "&destination=" + latLngToString(dest_) + "&mode=" + mode_
				+ "&departure_time=" + System.currentTimeMillis() / 1000 + "&sensor=false";
		return super.queryString(url);
	}

}
