package hubway.utility;

import org.json.JSONObject;

public class WundergroundQueryBuilder extends AQueryBuilder {

	protected String HISTORY = "/history_";

	public WundergroundQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryHistorical(String date_, String stateCity_) {
		String url = _url + _credentials + HISTORY + date_ + "/q/" + stateCity_ + ".json";
		return super.query(url);
	}
}
