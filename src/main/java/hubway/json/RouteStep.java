package hubway.json;

public class RouteStep {

	protected String departureStop;
	protected String lineName;
	protected String arrivalStop;
	protected String transitType;
	private long duration;
	private long distance;

	private String instructions;

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

	public String getFormattedStep() {
		return this.instructions + " (Distance: " + this.distance + "; Duration: " + this.duration + ")";
	}

	/**
	 * @return the departureStop
	 */
	public String getDepartureStop() {
		return departureStop;
	}

	/**
	 * @param departureStop
	 *            the departureStop to set
	 */
	public void setDepartureStop(String departureStop) {
		this.departureStop = departureStop;
	}

	/**
	 * @return the lineName
	 */
	public String getLineName() {
		return lineName;
	}

	/**
	 * @param lineName
	 *            the lineName to set
	 */
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	/**
	 * @return the arrivalStop
	 */
	public String getArrivalStop() {
		return arrivalStop;
	}

	/**
	 * @param arrivalStop
	 *            the arrivalStop to set
	 */
	public void setArrivalStop(String arrivalStop) {
		this.arrivalStop = arrivalStop;
	}

	/**
	 * @return the transitType
	 */
	public String getTransitType() {
		return transitType;
	}

	/**
	 * @param transitType
	 *            the transitType to set
	 */
	public void setTransitType(String transitType) {
		this.transitType = transitType;
	}

}
