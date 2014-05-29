package hubway.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AQueryBuilder {

	protected String _url;
	protected String _credentials;
	protected String _mostRecentQuery = "";
	final protected Logger logger = LoggerFactory.getLogger(HubwayQuery.class);

	public AQueryBuilder(final String url_, final String credentials_) {
		_url = url_;
		_credentials = credentials_;
	}

	public String getMostRecentQuery() {
		return _mostRecentQuery;
	}

	protected JSONObject query(String query_) {
		_mostRecentQuery = query_;
		try {
			URL query = new URL(query_);
			BufferedReader reader = new BufferedReader(new InputStreamReader(query.openStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return new JSONObject(sb.toString());
		} catch (MalformedURLException e) {
			logger.error("Unable to query using query " + query_, e);
		} catch (IOException e) {
			logger.error("Unable to read result of query " + query_, e);
		}
		return null;
	}
}
