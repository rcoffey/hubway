/**
 * 
 */
package hubway.models;

import java.util.Date;

/**
 * @author Clem
 *
 *	class for modeling trip data from historical prior to aggregation 
 */
public class TripInput {

	/**
	 * 
	 */
	public TripInput() {
		// TODO Auto-generated constructor stub
	}

	public Date Date_Start;
	public Date Date_End;

	public String Station_Start;
	public String Station_End;
	
	public int Duration;
	

	public int Id;
	public String Zip;
	public String Status;

	public String bike_nr;

	public boolean subscription;
	
}

