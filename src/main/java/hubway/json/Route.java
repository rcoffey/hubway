package hubway.json;

import java.util.List;

public class Route {
	private String summary;
	private List<RouteLeg> legs;

	/**
	 * Sets the summary of the route
	 * 
	 * @param summary
	 *            - A String representing the route summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setLegs(List<RouteLeg> routeLegs) {
		this.legs = routeLegs;
	}

	public List<RouteLeg> getLegs() {
		return this.legs;
	}

	public String getSummary() {
		return this.summary;
	}

	public long getTotalDuration() {
		long total = 0L;
		for (RouteLeg leg : legs) {
			total += leg.getDuration();
		}
		return total;
	}

	public long getTotalDistance() {
		long total = 0L;
		for (RouteLeg leg : legs) {
			total += leg.getDistance();
		}
		return total;
	}

	public String toString() {
		String summary = "Summary: " + this.summary + "\n";
		for (RouteLeg leg : this.legs) {
			summary += leg.toString() + "\n";
		}
		return summary;
	}
}