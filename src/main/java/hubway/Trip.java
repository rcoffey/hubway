package hubway;

import java.util.Date;

public class Trip {
	// data to store:
	// date (no time stamp)
	public Date start, end;
	// time of day (morning, afternoon, evening) 

	// start/end station
	public int start_station, end_station;
	// duration
	public int duration;
	
	
	// get from Clem:
	// Date start_date, end_date
	// int start_station, end_station
	// int duration
	public Trip(Date start_date, Date end_date, int start_station, int end_station, int duration){
		this.start_station = start_station;
		this.end_station = end_station;
		this.duration = duration;
		
		// remainder needs calculation
	}
}
