package hubway;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.DBObject;

public class Station {
	private static DateFormat stationDF = new SimpleDateFormat("M/d/yyyy");
	
	public int id;
	public String terminal;
	public String station,municipality;
	public int nb_docks;
	public Double lat,lng;
	public Date install_date,last_day;
	
	public Station() {
		// TODO Auto-generated constructor stub
	}
	public Station(DBObject stationObj){
		// This does not handle missing fields well.  We seem to have "" 
		// when there are things missing.
		id = (Integer) stationObj.get("id");
		terminal = (String) stationObj.get("terminal");
		station = (String) stationObj.get("station");
		municipality = (String) stationObj.get("municipality");
		if (!"".equals(stationObj.get("nb_docks"))){
			nb_docks = (Integer) stationObj.get("nb_docks");
		} else {
			nb_docks = 0;
		}
		lat = (Double) stationObj.get("lat");
		lng = (Double) stationObj.get("lng");
		try {
		install_date = stationDF.parse((String) stationObj.get("install_date"));
		last_day = stationDF.parse((String)stationObj.get("last_day"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
        	// e.printStackTrace();
			System.out.println("Missing date(s) for " + station);
		}
	}

}
