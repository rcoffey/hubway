package hubway.json;

import java.util.HashMap;
import java.util.LinkedList;

public class TransitAlert {

	public HashMap<String, LinkedList<String>> linesAndStations;

	public TransitAlert() {
		linesAndStations = new HashMap<String, LinkedList<String>>();
	}

}
