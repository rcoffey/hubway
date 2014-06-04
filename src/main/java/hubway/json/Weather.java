package hubway.json;

public class Weather {

	public Weather(String weather, String feelslike, float tempf, float windmph) {

		this.weather = weather;
		this.feelslike = feelslike;
		this.tempf = tempf;
		this.windmph = windmph;
		
	}	
	
	public final String weather;
	public final String feelslike;

	public final float tempf;
	public final float windmph;
}
