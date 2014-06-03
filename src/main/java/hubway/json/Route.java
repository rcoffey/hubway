package hubway.json;

import java.text.DecimalFormat;
import java.util.List;

public class Route {
	private String summary;
	private List<RouteLeg> legs;

	private DecimalFormat df2 = new DecimalFormat("###.##");

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

	/**
	 * Sum of duration of every leg in minutes
	 * 
	 * @return
	 */
	public Double getTotalDuration() {
		Double total = 0.0;
		for (RouteLeg leg : legs) {
			total += leg.getDuration();
		}
		return Double.valueOf(df2.format(total / 60));
	}

	/**
	 * Sum of all distances converted to miles.
	 * 
	 * @return
	 */
	public Double getTotalDistance() {
		Double total = 0.0;
		for (RouteLeg leg : legs) {
			total += leg.getDistance();
		}
		return Double.valueOf(df2.format(total * 0.000621371));
	}

	public String toString() {
		String summary = "Summary: " + this.summary + "\n";
		for (RouteLeg leg : this.legs) {
			summary += leg.toString() + "\n";
		}
		return summary;
	}

	public int getNumberOfLegs() {
		return legs.size();
	}
}