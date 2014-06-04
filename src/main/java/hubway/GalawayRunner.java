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

		// Ask for an Address/Id
		System.out.println("Please enter your start address: ");
		Scanner input = new Scanner(System.in);
		String address = input.nextLine();
		
		// Use GeoCode to get coordinates of the address
		// Use Mongo near query to get nearest hubway station
		Station startStation = service.processAddress(address);
		
		// ask for destination
		System.out.println("Please enter your destination address, or 0 if you'd like suggestions: ");
		while (!input.hasNextLine()) {
			// wait for input
		} 			
		address = input.nextLine();
		input.close();
		
		// detect and act on advice request
		if ("0".equals(address)) {
			service.adviseDestination(startStation);
		} else {
			// Use GeoCode to get coordinates of the address
			// Use Mongo near query to get nearest hubway station
			Station endStation = service.processAddress(address);
			
			// look up route options between the two
			service.produceOutput(new StationPair(startStation, endStation));
		}
	}

}
