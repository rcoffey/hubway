package hubway.utility;

import org.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;

public class DistanceQueryBuilder extends AQueryBuilder {

	public DistanceQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryDistanceBetween(LatLng origin_, LatLng dest_, String mode_) {
		String url = _url + latLngToString(origin_) + "&destinations=" + latLngToString(dest_) + "&mode=" + mode_
				+ "&sensor=false&key=" + _credentials;
		return super.query(url);
	}

}
