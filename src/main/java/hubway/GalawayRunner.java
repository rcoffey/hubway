package hubway;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GalawayRunner {

	public GalawayRunner() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// Get the Beans
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring.galaway.beans.xml");
		GalawayService service = (GalawayService) context.getBean("galawayService");
		// Print interesting general station info. (Farthest points, most used
		// stations...)
		service.runGalaway();
		// This could include the station ids for now so we have points to
		// search between.

		// Ask for an Address/Id
		System.out.println("Please enter your start address: ");
		Scanner input = new Scanner(System.in);
		String address = input.nextLine();
		
		// Use GeoCode to get coordinates of the address
		// Use Mongo near query to get nearest hubway station
		Station startStation = service.processAddress(address);

		// TODO let's just get the advised station for now
		service.adviseDestination(startStation);

		// Suggest most common destinations and average trip durations

		// Ask for destination

		// Look up route options between the 2
	}

}
