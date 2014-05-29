package hubway;

import java.util.Date;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;

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

	public String id;
	public String _mongoId;
	public String terminal;
	public String station, municipality;
	public Integer nb_docks;
	public Double lat, lng;
	public Date install_date, last_day;

	public Station() {
		// TODO Auto-generated constructor stub
	}

	@Id
	@Property(field = "_id")
	public String getMongoId() {
		return _mongoId;
	}

	public void setMongoId(String mongoId_) {
		_mongoId = mongoId_;
	}

	@Property(field = "id")
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

	@Property()
	public Date getInstall_date() {
		return install_date;
	}

	public void setInstall_date(Date install_date) {
		this.install_date = install_date;
	}

}
