package hubway.utility;

import hubway.json.TransitAlert;

import org.json.JSONObject;

import com.javadocmd.simplelatlng.LatLng;

public class TransitAlertQueryBuilder extends AQueryBuilder {

	public TransitAlertQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public TransitAlert queryTransitAlerts() {
		String url = _url + _credentials;
		return (TransitAlert) super.queryAndDeserialize(url, TransitAlert.class);
	}

}
