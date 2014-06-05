package hubway.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class TransitAlert {

	public HashMap<String, HashSet<String>> linesAndStations;

	public TransitAlert() {
		linesAndStations = new HashMap<String, HashSet<String>>();
	}

}
