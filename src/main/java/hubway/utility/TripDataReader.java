package hubway.utility;

import hubway.models.TripInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class TripDataReader {

	public TripDataReader() {
		// TODO Auto-generated constructor stub
	}

	// /!CL reads from the file and parses it into an array of trips.
	public static ArrayList<TripInput> extractStationCSV(String stations) {
		ArrayList<TripInput> tripList = new ArrayList<TripInput>();
		//int k = 0;
		File file = new File(stations);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Scanner inputStream = new Scanner(file);
			String data = inputStream.nextLine();

			// hashNext() loops line-by-line
			while (inputStream.hasNextLine()) {
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

				// parsing ints with some logic because there are quotes there,
				// could probably be done better
//				if (csvLine[4] == null || csvLine[4].trim().isEmpty())
					trip.Station_Start = csvLine[4];
//							Integer
//							.parseInt(csvLine[4].subSequence(1,
//									csvLine[4].length() - 1).toString());
//				if (csvLine[6] == null || csvLine[6].trim().isEmpty())
					trip.Station_End = csvLine[6];
//				Integer.parseInt(csvLine[6].subSequence(
//							1, csvLine[6].length() - 1).toString());
//				if (csvLine[9] == null || csvLine[9].trim().isEmpty())
					trip.Zip =csvLine[9]; 
//					Integer.parseInt(csvLine[9].subSequence(1,
//							csvLine[9].length() - 1).toString());

				trip.bike_nr = (csvLine[7]);
				trip.subscription = (csvLine[8].compareTo("Registered") == 0);//

				tripList.add(trip);

				// !CL load just 500 for now to test
				// k++; if(k>500) break;
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
