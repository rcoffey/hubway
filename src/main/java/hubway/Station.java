package hubway;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;
import com.mongodb.DBObject;
@Entity
public class Station {
	@Override
	public String toString() {
		return "Station [id=" + id + ", terminal=" + terminal + ", station="
				+ station + ", municipality=" + municipality + ", nb_docks="
				+ nb_docks + ", lat=" + lat + ", lng=" + lng
				+ ", install_date=" + install_date + ", last_day=" + last_day
				+ "]";
	}
	private static DateFormat stationDF = new SimpleDateFormat("M/d/yyyy");
	
	public String id;
	public String terminal;
	public String station,municipality;
	public Integer nb_docks;
	public Double lat,lng;
	public Date install_date,last_day;
	
	@Property()
	public Date getInstall_date() {
		return install_date;
	}
	public void setInstall_date(Date install_date) {
		this.install_date = install_date;
	}
	public Station() {
		// TODO Auto-generated constructor stub
	}
//	public Station(DBObject stationObj){
//		// This does not handle missing fields well.  We seem to have "" 
//		// when there are things missing.
//		id = (Integer) stationObj.get("id");
//		terminal = (String) stationObj.get("terminal");
//		station = (String) stationObj.get("station");
//		municipality = (String) stationObj.get("municipality");
//		if (!"".equals(stationObj.get("nb_docks"))){
//			nb_docks = (Integer) stationObj.get("nb_docks");
//		} else {
//			nb_docks = 0;
//		}
//		lat = (Double) stationObj.get("lat");
//		lng = (Double) stationObj.get("lng");
//		try {
//		install_date = stationDF.parse((String) stationObj.get("install_date"));
//		last_day = stationDF.parse((String)stationObj.get("last_day"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//        	// e.printStackTrace();
//			System.out.println("Missing date(s) for " + station);
//		}
//	}
	@Id
	@Property
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Property
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	
	@Property
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	
	@Property
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	@Property
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	@Property
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	
	@Property
	public Date getLast_day() {
		return last_day;
	}
	public void setLast_day(Date last_day) {
		this.last_day = last_day;
	}
	@Property
	public Integer getNb_docks() {
		return nb_docks;
	}
	public void setNb_docks(Integer nb_docks) {
		this.nb_docks = nb_docks;
	}

}
