package hubway.utility;

import hubway.models.TripInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;

public class TripDataReader {

	public TripDataReader() {
		// TODO Auto-generated constructor stub
	}

	// /!CL reads from the file and parses it into an array of trips.
	public static LinkedList<TripInput> extractStationCSV(String stations) {
		LinkedList<TripInput> tripList = new LinkedList<TripInput>();
		//int k = 0;
		File file = new File(stations);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Scanner inputStream = new Scanner(file);
			String data = inputStream.nextLine();

			// hasNext() loops line-by-line
			int k = 0;
			while (inputStream.hasNextLine()) {
				//k++; if(k>500) break;
				// read single line, put in string
				data = inputStream.nextLine();
				String[] csvLine = data.split(",");

				TripInput trip = new TripInput();

				trip.Id = Integer.parseInt(csvLine[0]);
				trip.Status = (csvLine[1]);
				trip.Duration = Integer.parseInt(csvLine[2]);

				// format of the stupid date string

				try {
					trip.Date_Start = formatter.parse(csvLine[3]);
					trip.Date_End = formatter.parse(csvLine[5]);

				} catch (ParseException e) {
					e.printStackTrace();
				}

					trip.Station_Start = Integer.parseInt(csvLine[4].replaceAll("\"", ""));
					trip.Station_End = Integer.parseInt(csvLine[6].replaceAll("\"", ""));
					trip.Zip =csvLine[9]; 

				trip.bike_nr = (csvLine[7]);
				trip.subscription = (csvLine[8].compareTo("Registered") == 0);

				tripList.add(trip);

				
			}
			inputStream.close();
			System.out.println("stationList.size() = " + tripList.size());

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {

		}

		return tripList;
	}

}
