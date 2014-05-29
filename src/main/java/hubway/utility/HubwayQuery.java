package hubway.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HubwayQuery {
	protected String _hubwayUrl;
	protected String _hubwayCredentials;
	final private Logger logger = LoggerFactory.getLogger(HubwayQuery.class);

	public HubwayQuery(final String hubwayUrl_, final String hubwayCredentials_) {
		_hubwayUrl = hubwayUrl_;
		_hubwayCredentials = hubwayCredentials_;
	}

	public JSONObject query(String queryType_, String queryString_) {
		String url = _hubwayUrl + queryType_ + _hubwayCredentials
				+ queryString_;
		try {
			URL query = new URL(url);
			BufferedReader hubwayReader = new BufferedReader(
					new InputStreamReader(query.openStream()));

			String jsonString = hubwayReader.readLine(); // it's only one line
			return new JSONObject(jsonString);
		} catch (MalformedURLException e) {
			logger.error("Unable to query hubway using url " + url, e);
		} catch (IOException e) {
			logger.error("Unable to read result of query " + url, e);
		}
		return null;
	}
}
