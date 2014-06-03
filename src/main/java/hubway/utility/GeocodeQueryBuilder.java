package hubway.utility;

import org.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;


public class GeocodeQueryBuilder extends AQueryBuilder {

	public GeocodeQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryByAddress(String address) {
		String url = _url + "address=" + address.replace(' ', '+') + "&sensor=false&key=" + _credentials;
		return super.query(url);
	}

}
