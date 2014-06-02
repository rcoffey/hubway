package hubway.json;

public class RouteStep {

	private long duration;
	private long distance;

	private String instructions;

	private String travel_mode;

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDistance() {
		return this.distance;
	}

	public long getDuration() {
		return this.duration;
	}

	public String getInstructions() {
		return this.instructions;
	}

	/**
	 * @return the travel_mode
	 */
	public String getTravel_mode() {
		return travel_mode;
	}

	/**
	 * @param travel_mode
	 *            the travel_mode to set
	 */
	public void setTravel_mode(String travel_mode) {
		this.travel_mode = travel_mode;
	}

	public String getFormattedStep() {
		return this.instructions + " (Distance: " + this.distance + "; Duration: " + this.duration + ")";
	}
}
