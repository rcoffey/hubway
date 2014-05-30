package hubway.utility;

import org.json.JSONObject;

public class HubwayQueryBuilder extends AQueryBuilder {
	public HubwayQueryBuilder(String url_, String credentials_) {
		super(url_, credentials_);
	}

	public JSONObject query(String queryType_, String queryString_) {
		String url = _url + queryType_ + _credentials + queryString_;
		return super.query(url);
	}
}
