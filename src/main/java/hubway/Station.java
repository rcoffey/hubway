package hubway;

import java.util.HashMap;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Id;
import com.googlecode.mjorm.annotations.Property;
import com.javadocmd.simplelatlng.LatLng;

@Entity
public class Station {
	@Override
	public String toString() {
		return "Station [id=" + id + ", station=" + station + ", penMaxDest=" + penMaxDest + ", nb_docks=" + nb_docks
				+ ", lat=" + lat + ", lng=" + lng + ", maxDest=" + maxDest + ", tripsFrom=" + tripsFrom + ", tripsTo="
				+ tripsTo + "]";

	}

	public String id;
	public String station;
	public Integer nb_docks;
	public Double lat, lng;
	public HashMap<String, Integer> tripsFrom, tripsTo, joyrides;
	public HashMap<String, String> maxDest, penMaxDest;

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
	public HashMap<String, String> getMaxDest() {
		return maxDest;
	}

	public void setMaxDest(HashMap<String, String> dest) {
		this.maxDest = dest;
	}

	@Property
	public HashMap<String, String> getPenMaxDest() {
		return penMaxDest;
	}

	public void setPenMaxDest(HashMap<String, String> dest) {
		this.penMaxDest = dest;
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
	public HashMap<String, Integer> getTripsFrom() {
		return tripsFrom;
	}

	public void setTripsFrom(HashMap<String, Integer> tripsFrom) {
		this.tripsFrom = tripsFrom;
	}

	@Property
	public HashMap<String, Integer> getTripsTo() {
		return tripsTo;
	}

	public void setTripsTo(HashMap<String, Integer> tripsTo) {
		this.tripsTo = tripsTo;
	}

	public LatLng getLatLng() {
		return new LatLng(lat, lng);
	}

	/**
	 * @return the _joyrides
	 */
	@Property
	public HashMap<String, Integer> getJoyrides() {
		return this.joyrides;
	}

	/**
	 * @param joyrides
	 *            the _joyrides to set
	 */
	public void setJoyrides(HashMap<String, Integer> joyrides) {
		this.joyrides = joyrides;
	}

	/**
	 * Sum of trips to, from and joyrides
	 * 
	 * @return
	 */
	public int totalTrips() {
		if (tripsFrom != null && tripsTo != null && joyrides != null && joyrides.size() > 0) {
			return tripsFrom.get("total") + tripsTo.get("total") + joyrides.get("total");
		}
		return 0;
	}
}
