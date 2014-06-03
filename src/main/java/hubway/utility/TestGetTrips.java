package hubway.utility;

import hubway.models.TripInput;
import java.util.ArrayList;

public class TestGetTrips {

	public TestGetTrips() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		System.out.println("Testing parsing hubway trip data from csv");
		String trips = "C:\\Users\\Clem\\Dev\\hubway\\src\\main\\resources\\hubwaydata_10_12_to_11_13.csv";

		// !CL get the list of trips
		ArrayList<TripInput> tripList = TripDataReader
				.extractStationCSV(trips);

		// !CL for loop to cycle through things
		for (TripInput tripInput : tripList) {
			System.out.println("Id : " + tripInput.Id);
		}
	}

}