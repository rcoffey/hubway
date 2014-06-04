package hubway.json;

import java.util.List;

/**
 * This class represents a single leg between two addresses
 * 
 * @author Pranav Angara
 * 
 */
public class RouteLeg {

	/**
	 * @return the routeSteps
	 */
	public List<RouteStep> getRouteSteps() {
		return routeSteps;
	}

	/**
	 * @param routeSteps
	 *            the routeSteps to set
	 */
	public void setRouteSteps(List<RouteStep> routeSteps) {
		this.routeSteps = routeSteps;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @return the distance
	 */
	public long getDistance() {
		return distance;
	}

	private long duration;
	private long distance;

	private List<RouteStep> routeSteps;

	/**
	 * Updates the collection of steps within this leg
	 * 
	 * @param routeSteps
	 *            - A List containing the directional steps for this leg
	 */
	public void setSteps(List<RouteStep> routeSteps) {
		this.routeSteps = routeSteps;
	}

	/**
	 * Sets the value of entire duration for this leg
	 * 
	 * @param duration
	 *            - A number representing the duration in seconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Sets the value of the entire distance for this leg
	 * 
	 * @param distance
	 *            - A number representing the distance in meters
	 */
	public void setDistance(long distance) {
		this.distance = distance;
	}

	/**
	 * Returns the directional steps for this leg
	 * 
	 * @return A List containing the directional steps
	 */
	public List<RouteStep> getSteps() {
		return this.routeSteps;
	}

	public String toString() {
		String summary = "Distance: " + this.distance + "; Duration: " + this.duration + "\n";
		int stepNumber = 1;
		for (RouteStep step : this.routeSteps) {
			summary += stepNumber + ": " + step.getFormattedStep() + "\n";
			stepNumber++;
		}

		return summary;
	}
}