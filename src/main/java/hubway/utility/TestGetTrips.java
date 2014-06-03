package hubway.utility;

import hubway.models.TripInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TestGetTrips {

	public TestGetTrips() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		System.out.println("Testing parsing hubway trip data from csv");
		String trips = "C:\\Users\\Clem\\Dev\\hubway\\src\\main\\resources\\hubwaydata_10_12_to_11_13.csv";

		// !CL get the list of trips
		LinkedList<TripInput> tripList = TripDataReader
				.extractStationCSV(trips);

		HashMap<String, Integer> startStationUsage = new 		HashMap<String, Integer> ();
		HashMap<String, Integer> endStationUsage = new 		HashMap<String, Integer> ();
		HashMap<String, Integer> totalStationUsage = new 		HashMap<String, Integer> ();

		System.out.println("printHighestStartUsage");
		printHighestStartUsage(tripList, startStationUsage);
		System.out.println("endStationUsage");
		printHighestEndUsage(tripList, endStationUsage);
		System.out.println("totalStationUsage");
		printHighestTotalUsage(tripList, totalStationUsage);
		
	}


	private static void printHighestTotalUsage(LinkedList<TripInput> tripList,
			HashMap<String, Integer> startStationUsage) {
		// !CL for loop to cycle through things
		for (TripInput tripInput : tripList) {			
			if (startStationUsage.containsKey(tripInput.Station_End))
			{
				int k = startStationUsage.get(tripInput.Station_End);
				startStationUsage.put(tripInput.Station_End, k+1);
			} else 
			{
				startStationUsage.put(tripInput.Station_End, 1);				
			}			
			
			if (startStationUsage.containsKey(tripInput.Station_Start))
			{
				int k = startStationUsage.get(tripInput.Station_Start);
				startStationUsage.put(tripInput.Station_Start, k+1);
			} else 
			{
				startStationUsage.put(tripInput.Station_Start, 1);				
			}	
		}
		
		
		int max =0;
		int min = Integer.MAX_VALUE;

		String maxStation = null;
		String minStation = null;
		
		for (Map.Entry<String, Integer> entry : startStationUsage.entrySet())
		{
			if (entry.getValue().compareTo(max) > 0)
			{
				max = entry.getValue(); 
				maxStation = entry.getKey();
			}
			
			if (entry.getValue().compareTo(min) < 0)
			{
				min = entry.getValue(); 
				minStation = entry.getKey();
			}			
		}
		
		System.out.println("Highest total point usage Station : " + maxStation);
		System.out.println("Highest total point usage Station usage : " + max);		
		System.out.println("Lowest total point usage Station : " + minStation);
		System.out.println("Lowest total point usage Station usage : " + min);
	}
	
	private static void printHighestEndUsage(LinkedList<TripInput> tripList,
			HashMap<String, Integer> startStationUsage) {
		// !CL for loop to cycle through things
		for (TripInput tripInput : tripList) {			
			if (startStationUsage.containsKey(tripInput.Station_End))
			{
				int k = startStationUsage.get(tripInput.Station_End);
				startStationUsage.put(tripInput.Station_End, k+1);
			} else 
			{
				startStationUsage.put(tripInput.Station_End, 1);				
			}			
		}
		
		int max =0;
		int min = Integer.MAX_VALUE;

		String maxStation = null;
		String minStation = null;
		
		for (Map.Entry<String, Integer> entry : startStationUsage.entrySet())
		{
			if (entry.getValue().compareTo(max) > 0)
			{
				max = entry.getValue(); 
				maxStation = entry.getKey();
			}
			
			if (entry.getValue().compareTo(min) < 0)
			{
				min = entry.getValue(); 
				minStation = entry.getKey();
			}			
		}
		
		System.out.println("Highest end point usage Station : " + maxStation);
		System.out.println("Highest end point usage Station usage : " + max);
		
		System.out.println("Lowest end point usage Station : " + minStation);
		System.out.println("Lowest end point usage Station usage : " + min);
	}
	
	private static void printHighestStartUsage(LinkedList<TripInput> tripList,
			HashMap<String, Integer> startStationUsage) {
		// !CL for loop to cycle through things
		for (TripInput tripInput : tripList) {			
			if (startStationUsage.containsKey(tripInput.Station_Start))
			{
				int k = startStationUsage.get(tripInput.Station_Start);
				startStationUsage.put(tripInput.Station_Start, k+1);
			} else 
			{
				startStationUsage.put(tripInput.Station_Start, 1);				
			}			
		}
		
		int max =0;
		int min = Integer.MAX_VALUE;

		String maxStation = null;
		String minStation = null;
		
		for (Map.Entry<String, Integer> entry : startStationUsage.entrySet())
		{
			if (entry.getValue().compareTo(max) > 0)
			{
				max = entry.getValue(); 
				maxStation = entry.getKey();
			}
			
			if (entry.getValue().compareTo(min) < 0)
			{
				min = entry.getValue(); 
				minStation = entry.getKey();
			}			
		}
		
		System.out.println("Highest start point usage Station : " + maxStation);
		System.out.println("Highest start point usage Station usage : " + max);
		
		System.out.println("Lowest start point usage Station : " + minStation);
		System.out.println("Lowest start point usage Station usage : " + min);
	}

}