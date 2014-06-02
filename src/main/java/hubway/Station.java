package hubway;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;
import com.javadocmd.simplelatlng.LatLng;

@Entity
public class Station {
	@Override
	public String toString() {
		return "Station [id=" + id + ", station="
				+ station + ", nb_docks="
				+ nb_docks + ", lat=" + lat + ", lng=" + lng
				+ ", " + tripsFrom + " trips from, " + tripsTo + " trips to]";

	}

	public String id;
	public String station;
	public Integer nb_docks;
	public Double lat, lng;
	public int tripsFrom, tripsTo;

	public Station() {
		// TODO Auto-generated constructor stub
	}

	@Id
	@Property(field = "_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Property
	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
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
	public Integer getNb_docks() {
		return nb_docks;
	}

	public void setNb_docks(Integer nb_docks) {
		this.nb_docks = nb_docks;
	}
	
	@Property
	public int getTripsFrom() {
		return tripsFrom;
	}
	
	public void setTripsFrom(int tripsFrom) {
		this.tripsFrom = tripsFrom;
	}
	
	@Property
	public int getTripsTo() {
		return tripsTo;
	}
	
	public void setTripsTo(int tripsTo) {
		this.tripsTo = tripsTo;
	}

	public LatLng getLatLng() {
		return new LatLng(lat, lng);
	}

}
