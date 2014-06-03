package hubway.utility;

import org.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;

public class PlacesQueryBuilder extends AQueryBuilder {

	public PlacesQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryMbtaNear(LatLng location_, int radius_) {
		String url = _url + "location=" + latLngToString(location_) + "&radius=" + radius_
				+ "&types=bus_station|subway_station&sensor=false&key=" + _credentials;
		return super.query(url);
	}


	public JSONObject queryHubwayNear(LatLng location_, int radius_) {
		String url = _url + "location=" + latLngToString(location_) + "&radius=" + radius_
				+ "&name=hubway&sensor=false&key=" + _credentials;
		return super.query(url);
	}
	

}
