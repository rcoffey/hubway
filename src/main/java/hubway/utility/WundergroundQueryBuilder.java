package hubway.utility;

import hubway.json.Weather;

import org.json.JSONObject;

public class WundergroundQueryBuilder extends AQueryBuilder {

	protected String HISTORY = "/history_";

	protected String CONDITIONS = "/conditions";

	public WundergroundQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject queryHistorical(String date_, String stateCity_) {
		String url = _url + _credentials + HISTORY + date_ + "/q/" + stateCity_ + ".json";
		return super.query(url);
	}
//
//	public JSONObject queryCurrent(String stateCity_) {
//		String url = _url + _credentials + CONDITIONS + "/q/" + stateCity_ + ".json";
//		return super.query(url);
//	}	

	public Weather queryCurrent(String stateCity_) {
		String url = _url + _credentials + CONDITIONS + "/q/" + stateCity_ + ".json";
		return (Weather) super.queryAndDeserialize(url,Weather.class);
	}	


}
