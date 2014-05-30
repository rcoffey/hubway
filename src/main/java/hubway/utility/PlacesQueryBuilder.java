package hubway.utility;

import org.json.JSONObject;

public class PlacesQueryBuilder extends AQueryBuilder {

	public PlacesQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryMbtaNear(Double lat_, Double lng_, int radius_) {
		String url = _url + "location=" + lat_ + "," + lng_ + "&radius=" + radius_
				+ "&types=bus_station|subway_station&sensor=false&key=" + _credentials;
		return super.query(url);
	}

}
