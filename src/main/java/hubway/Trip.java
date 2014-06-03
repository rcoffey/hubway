package hubway;

import hubway.models.Time;
import hubway.models.TripInput;

import java.util.Calendar;
import java.util.Date;

import com.googlecode.mjorm.annotations.Entity;
import com.googlecode.mjorm.annotations.Property;

@Entity
public class Trip {
	@Override
	public String toString() {
		return "Trip [_id=" + _id +", startDay=" + startDay + ", endDay=" + endDay
				+ ", time=" + time + ", start_station=" + start_station
				+ ", end_station=" + end_station + ", duration=" + duration + "]";
	}
	// data to store:
	// day of week 
	public int startDay, endDay;
	// time of day (morning, afternoon, evening) 
	public Time time;
	// start/end station
	public int start_station, end_station;
	// duration
	public int duration;
	
	public int _id;
	
	public Trip() {
		
	}
	
	public Trip(TripInput input){
		new Trip(input.Id, input.Date_Start, input.Date_End, input.Station_Start, input.Station_End, input.Duration);
	}
	private Trip(int id, Date start_date, Date end_date, int start_station, int end_station, int duration){
		this.start_station = start_station;
		this.end_station = end_station;
		this.duration = duration;
		this._id = id;
		
		// remainder needs calculation
		computeTime(start_date, end_date);
		computeDate(start_date, end_date);
	}
	
	// for now just using start time to set time
	private void computeTime(Date start, Date end){
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour < 4 || 20 <= hour){
			time = Time.EVENING;
		} else if (4 <= hour && hour < 12) {
			time = Time.MORNING;
		} else {
			time = Time.AFTERNOON;
		}
	}
	private void computeDate(Date start, Date end){
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		startDay = cal.get(Calendar.DAY_OF_WEEK);
		cal.setTime(end);
		endDay = cal.get(Calendar.DAY_OF_WEEK);
	}

	@Property
	public int getStartDay(){
		return startDay;
	}
	public void setStartDay(int startDay){
		this.startDay = startDay;
	}
	
	@Property
	public int getEndDay(){
		return endDay;
	}
	public void setEndDay(int endDay){
		this.endDay = endDay;
	}
	
	@Property
	public Time getTime(){
		return time;
	}
	public void setTime(Time time){
		this.time = time;
	}
	
	@Property
	public int getStart_station(){
		return start_station;
	}
	public void setStart_station(int start_station){
		this.start_station = start_station;
	}
	
	@Property
	public int getEnd_station(){
		return end_station;
	}
	public void setEnd_station(int end_station){
		this.end_station = end_station;
	}
	
	@Property
	public int getDuration(){
		return duration;
	}
	public void setDuration(int duration){
		this.duration = duration;
	}
}
