package hubway;

import java.util.Date;
import java.util.Map;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class MongoStationPair {
	protected final String[] _days = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
	protected final String[] _times = { "MORNING", "AFTERNOON", "NIGHT" };
	protected String station1, station2;
	protected int tripCount;
	protected Map<String, Integer> tripsByTime;
	protected int minTime;
	protected int maxTime;
	protected int avgTime;
	protected Date minDate;
	protected Date maxDate;

	public MongoStationPair() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the station1
	 */
	@Property
	public String getStation1() {
		return station1;
	}

	/**
	 * @param station1
	 *            the station1 to set
	 */
	public void setStation1(String station1) {
		this.station1 = station1;
	}

	/**
	 * @return the station2
	 */
	@Property
	public String getStation2() {
		return station2;
	}

	/**
	 * @param station2
	 *            the station2 to set
	 */
	public void setStation2(String station2) {
		this.station2 = station2;
	}

	/**
	 * @return the tripCount
	 */
	@Property
	public int getTripCount() {
		return tripCount;
	}

	/**
	 * @param tripCount
	 *            the tripCount to set
	 */
	public void setTripCount(int tripCount) {
		this.tripCount = tripCount;
	}

	/**
	 * @return the tripsByTime
	 */
	@Property
	public Map<String, Integer> getTripsByTime() {
		return tripsByTime;
	}

	/**
	 * @param tripsByTime
	 *            the tripsByTime to set
	 */
	public void setTripsByTime(Map<String, Integer> tripsByTime) {
		this.tripsByTime = tripsByTime;
	}

	/**
	 * @return the minTime
	 */
	@Property
	public int getMinTime() {
		return minTime;
	}

	/**
	 * @param minTime
	 *            the minTime to set
	 */
	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}

	/**
	 * @return the maxTime
	 */
	@Property
	public int getMaxTime() {
		return maxTime;
	}

	/**
	 * @param maxTime
	 *            the maxTime to set
	 */
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	/**
	 * @return the avgTime
	 */
	@Property
	public int getAvgTime() {
		return avgTime;
	}

	/**
	 * @param avgTime
	 *            the avgTime to set
	 */
	public void setAvgTime(int avgTime) {
		this.avgTime = avgTime;
	}

	/**
	 * @return the minDate
	 */
	@Property
	public Date getMinDate() {
		return minDate;
	}

	/**
	 * @param minDate
	 *            the minDate to set
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	/**
	 * @return the maxDate
	 */
	@Property
	public Date getMaxDate() {
		return maxDate;
	}

	/**
	 * @param maxDate
	 *            the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public void info(Station origin, Station destination) {
		System.out.println("************************ YOUR TRIP ************************");
		System.out.println("Start Station: " + origin.getStation());
		System.out.println("End Station: " + destination.getStation());
		System.out.println("\n***** Historical Trip Facts *****");
		if (tripCount > 0) {
			System.out.println("There are " + tripCount + " trips between " + origin.getStation() + " and "
					+ destination.station + ".");
			if (origin.tripsFrom.get("total") != 0 && destination.tripsTo.get("total") != 0) {
				System.out.println("\tThat is " + tripCount / (double) origin.tripsFrom.get("total") * 100
						+ " percent " + "of the trips from " + origin.station);
				System.out.println("\t and " + tripCount / (double) destination.tripsTo.get("total") * 100
						+ " percent " + "of the trips to " + destination.station + ".");
				System.out.println("\nThese trips took on average " + avgTime / 60 + " minutes.");
				if (minTime != 0 && minTime != -1) {
					System.out.println("The longest took " + maxTime / 60 + " minutes on " + maxDate.toString());
					System.out.println("The shortest " + minTime / 60 + " minutes on " + minDate.toString());
				}
			}

			System.out.println();
			printBreakdown();
		} else {
			System.out.println("No one has made this trip on Hubway before!");
		}
	}

	protected void printBreakdown() {
		System.out.println("Daily trip breakdown");
		for (String day : _days) {
			System.out.println("\t" + day + ":: " + tripsByTime.get(day));
		}
		System.out.println("Time of day breakdown");
		for (String time : _times) {
			System.out.println("\t" + time + ":: " + tripsByTime.get(time));
		}
		System.out.println();
	}

}
