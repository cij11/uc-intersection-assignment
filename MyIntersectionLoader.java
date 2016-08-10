package traffic.diy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
//import javax.swing.JPanel;

import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
import traffic.phaseplan.FullyActuatedPhasePlan;
import traffic.phaseplan.PhasePlan;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.signal.SignalFace;
import traffic.util.State;
import traffic.util.TrafficDirection;
//import traffic.load.TrafficException;
import traffic.load.TrafficSyntaxException;
import traffic.misc.RandomDetector;
//import traffic.load.TrafficIOException;;

/**
 * Read an intersection description file and build an intersection from the data
 * it contains.
 *
 */
public class MyIntersectionLoader {
	
	/**
	 * Convert string data to other data types required by intersection.
	 * 
	 * @author cijolly
	 */
	public static class IntersectionTextParser{
		/**
		 * Convert a string from the intersection file into a usable direction enum
		 * 
		 * @param directionString	String containing a word indicating a direction
		 * @return	Enum indicating the parsed direction
		 */
		public static TrafficDirection StringToDirection(String directionString) throws TrafficSyntaxException{
			switch(directionString){
				case "NE":
				{
					return TrafficDirection.NORTHEAST;
				}
				case "NW":
				{
					return TrafficDirection.NORTHWEST;
				}
				case "SE":
				{
					return TrafficDirection.SOUTHEAST;
				}
				case "SW":
				{
					return TrafficDirection.SOUTHWEST;
				}
				case "N":
				{
					return TrafficDirection.NORTH;
				}
				case "S":
				{
					return TrafficDirection.SOUTH;
				}
				case "E":
				{
					return TrafficDirection.EAST;
				}
				case "W":
				{
					return TrafficDirection.WEST;
				}
				default:{
					throw new TrafficSyntaxException("Invalid direction");
				}
			}
		}
		
		/**
		 * Convert a string from the intersection file into a SignalFace type
		 * 
		 * @param signalFaceString	String containing the word of the SignalFace line which says the shape of that face.
		 * @return	Int indicating the shape of the signal face.
		 */
		public static int StringToFaceType(String signalFaceString) throws TrafficSyntaxException{
			switch(signalFaceString){
				case "STANDARD":
				{
					return SignalFace.STANDARD;
				}
				case "LEFT_ARROW":
				{
					return SignalFace.LEFT_ARROW;
				}
				case "RIGHT_ARROW":
				{
					return SignalFace.RIGHT_ARROW;
				}
				default:
				{
					throw new TrafficSyntaxException("Invalid shape");
				}
			}
		}
			
		public static State CharToState(char stateChar) throws TrafficSyntaxException{
			State streamState = State.RED;
			switch(stateChar)
			{
				case 'G':
				{
					streamState = State.GREEN;
					break;
				}
				case 'Y':
				{
					streamState = State.YELLOW;
					break;
				}
				case 'R':
				{
					streamState = State.RED;
					break;
				}
				case 'X':
				{
					streamState = State.OFF;
					break;
				}
				default:
				{
					throw new TrafficSyntaxException("Invalid signal colour token");		//Throw exception if invalid character
				}
			}
			return streamState;
		}
		
		/**
		 * Convert a string from the intersection file into an integer indicating delay time.
		 * 
		 * @param phaseTimeString	String containing the word of the SignalFace line which says the delay time of that face.
		 * @return	Int indicating the time delay of the phase.
		 */
		public static int StringToTime(String phaseTimeString) throws TrafficSyntaxException{
			 if (!phaseTimeString.matches("[0-9]*")) throw new TrafficSyntaxException("Delay time must be numeric"); //Throw exception if not a number
			return Integer.parseInt(phaseTimeString);
		}	
	}

	private BufferedReader br;
	private ArrayList<TrafficStream> trafficStreamList = new ArrayList<TrafficStream>();
	private HashMap<String, TrafficStream> trafficStreamMap = new HashMap<String, TrafficStream>();
	private boolean preTimedIntersection = true;
	
	/**
	 * Constructor for class.
	 * 
	 * @param br
	 *            where to read data from.
	 */
	public MyIntersectionLoader(BufferedReader br) {
		this.br = br;
	}

	/**
	 * Build intersection from description in file. Read line at a time and
	 * process rather than parse using grammar.
	 * 
	 * @return the intersection, or null if something went wrong
	 */
	public Intersection buildIntersection() {
		//Read the file line by line.
		//If the next line is an open tag, read the expected
		//data until find a close tag.		
		//If get an invalid line, return null. Null is interpreted
		//by the MyIntersectionMonitor as an invalid intersection.
		
		Intersection myIntersection = null;
	
		//Read the intersection description
		try {
			myIntersection = ReadIntersectionDescription();
		} catch (TrafficSyntaxException e) {
			JOptionPane.showMessageDialog(null, "Error in format of intersection description, or invalid intersection data file",
					"Syntax Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
		
		//Read and build the traffic streams
		try{
			ReadTrafficStreams();
		}
		catch (TrafficSyntaxException e){
			JOptionPane.showMessageDialog(null, "Error in format of traffic streams",
					"Syntax Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
		
		//Read the phase plans, which contain phase streams within them
		try{
			ReadPhasePlans(myIntersection);
		}
		catch (TrafficSyntaxException e){
			JOptionPane.showMessageDialog(null, "Error in format of phase plans",
					"Syntax Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
		
		//Read the signal faces
		try{
			ReadSignalFaces(myIntersection);
		}
		catch (TrafficSyntaxException e){
			JOptionPane.showMessageDialog(null, "Error in format of signal faces",
					"Syntax Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}

		//If this interection is fully actuated, add detectors to the traffic streams.
		if (!preTimedIntersection)
			AddDetectors();
		
		return myIntersection;
	}
	
	/**
	 * Read the intersection open tag, the line naming and desctibing the intersection,
	 * and the intersection close tag.
	 * 
	 * @return
	 * @throws TrafficSyntaxException
	 */
	private Intersection ReadIntersectionDescription() throws TrafficSyntaxException{
		//Read the first line
		String intersectionLine = GetNextNonCommentLine(br);
		if (!intersectionLine.equals(traffic.load.Tag.INTERSECTION))
			throw new TrafficSyntaxException("Missing Intersection tag");
		//Read the second line
		String descriptionLine = GetNextNonCommentLine(br);
		String[] descriptionWords = SplitLineAlongTabs(descriptionLine);
		//All that is required for a valid intersection name and description is two strings
		if(descriptionWords.length < 2)
			throw new TrafficSyntaxException("Missing intersection title or description");
		//Read the last line and return
		String endIntersectionLine = GetNextNonCommentLine(br);
		if (!endIntersectionLine.equals(traffic.load.Tag.END_INTERSECTION))
			throw new TrafficSyntaxException("Missing End intersection Tag");
		return new Intersection(descriptionWords[0], descriptionWords[1]);
	}
	
	/**
	 * Read the TrafficStream open tag, the lines defining the streams,
	 * and the TrafficStream close tag.
	 * 
	 * @return
	 * @throws TrafficSyntaxException
	 */
	private void ReadTrafficStreams() throws TrafficSyntaxException{
		String streamLine = GetNextNonCommentLine(br);
		if (!streamLine.equals(traffic.load.Tag.TRAFFIC_STREAMS))
			throw new TrafficSyntaxException("Missing Traffic Streams tag");
		
		String nextLine;
		//Read the file until the end tag is encountered, of the end of the file.
		while(((nextLine = GetNextNonCommentLine(br)) != null) && !nextLine.equals(traffic.load.Tag.END_TRAFFIC_STREAMS)){
			try {
				AddTrafficStream(nextLine);
			}
			catch (TrafficSyntaxException e){
				e.printStackTrace();
				throw new TrafficSyntaxException("Invalid traffic stream description");
			}
		}
		//If the end of the file was reached before the stream was finished, throw an exception
		if (nextLine == null)
			throw new TrafficSyntaxException("End of file before intersection complete");
	}
	
	/**
	 * Read the phase plans from the file. A number of phase plans can be read between the opening and closing 
	 * PhasePlan tags. A number of phases can be added to each phase plan between the opening and closing Phases tags.
	 * @param myIntersection	The intersection to add the PhasePlans to.
	 * @throws TrafficSyntaxException
	 */
	private void ReadPhasePlans(Intersection myIntersection) throws TrafficSyntaxException{
		String planLine = GetNextNonCommentLine(br);

		if (planLine.equals(traffic.load.Tag.PHASEPLAN))
			preTimedIntersection = true;
		else if (planLine.equals("<PhasePlanActuated>"))
			preTimedIntersection = false;
		else
			throw new TrafficSyntaxException("Missing PhasePlan tag");
		
		boolean phasePlanAdded = false;		
		String nextLine;
		//Process the file for phase plans until the PhasePlan ending tag is reached, or the end of the file is reached
		while(((nextLine = GetNextNonCommentLine(br)) != null) && !nextLine.equals(traffic.load.Tag.END_PHASEPLAN)){
			if (nextLine.equals(traffic.load.Tag.PHASES)){
				PhasePlan phasePlan = null;
				if (preTimedIntersection)
					phasePlan = new PretimedPhasePlan(); 
				else
					phasePlan = new FullyActuatedPhasePlan();
				//Process the file for phases until the Phases ending tag is reached, or the end of the file
				while(((nextLine = GetNextNonCommentLine(br)) != null) && !nextLine.equals(traffic.load.Tag.END_PHASES)){
					try{
						AddPhase(nextLine, phasePlan);	
					}
					catch (TrafficSyntaxException e){
						e.printStackTrace();
						throw new TrafficSyntaxException("Invalid phase");
					}
				}
				if (nextLine == null)
					throw new TrafficSyntaxException("End of file before phases complete");
				myIntersection.addPlan(phasePlan);
				phasePlanAdded = true;
			}
		}
		
		if (nextLine == null)
			throw new TrafficSyntaxException("End of file before phases plans complete");
		//Require the inner loop to have been traversed at least once, to have at least one phase plan.
		if (!phasePlanAdded)
			throw new TrafficSyntaxException("Invalid phase plan description");
	}
	
	/**
	 * Read the SignalFaces from the file.
	 * @param myIntersection	The intersection to add the signal faces to.
	 * @throws TrafficSyntaxException
	 */
	private void ReadSignalFaces(Intersection intersection) throws TrafficSyntaxException{
		String faceLine = GetNextNonCommentLine(br);
		if (!faceLine.equals(traffic.load.Tag.SIGNAL_FACES))
			throw new TrafficSyntaxException("Missing SignalFace tag");
		
		String nextLine;
		while(((nextLine = GetNextNonCommentLine(br)) != null) && !nextLine.equals(traffic.load.Tag.END_SIGNAL_FACES)){
			try{
				AddSignalFace(nextLine, intersection);
			}
			catch (TrafficSyntaxException e){
				e.printStackTrace();
				throw new TrafficSyntaxException("Invalid SignalFace");
			}
		}
		if (nextLine == null)
			throw new TrafficSyntaxException("End of file before SignalFaces complete");
	}

	private void AddDetectors(){
		for (TrafficStream stream: trafficStreamList){
			RandomDetector detector = new RandomDetector();
			stream.addDetector(detector);
		}
	}
	
	/**
	 * Return the next line in the intersection file which is not blank or a comment.
	 * 
	 * @param buffRead	A buffered reader
	 * 
	 * @return A string containing a single line from the intersection file, ignoring comments and empty lines.
	 */
	private String GetNextNonCommentLine(BufferedReader buffRead){
		//Ignore strings that begin with  '//' (ignoring quotes) and empty lines
		//Otherwise, return the next string.
		String line = null;
		try {
			while ( (line = buffRead.readLine()) != null){
				//Return lines that aren't comments or empty
				if (!line.matches("//.*") && line.matches("(.)+")){
					return line;
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Take a line from the input file, and split it by the tabs in the line.
	 * 
	 * @param lineToSplit	String containing a line from the intersection file
	 * @return	Array of strings split along tabs in the initial line
	 */
	private String[] SplitLineAlongTabs(String lineToSplit){
		String[] wordArray = lineToSplit.split("\t");
		return wordArray;
	}
	
	/**
	 * Take a string, split it into words, and use those words to build a traffic stream.
	 * Add the traffic stream to the traffic stream list
	 * 
	 * @param trafficStreamString String containing a line from between the traffic stream tags
	 */
	private void AddTrafficStream(String trafficStreamString) throws TrafficSyntaxException{
		try{
			String[] trafficSplit = SplitLineAlongTabs(trafficStreamString);
			TrafficStream newStream = new TrafficStream(trafficSplit[0], trafficSplit[1]);
			trafficStreamList.add(newStream);
			trafficStreamMap.put(trafficSplit[0], newStream);
		}
		catch (ArrayIndexOutOfBoundsException e){
			throw new TrafficSyntaxException();
		}
	}
	
	/**
	 * Instantiate a new phase. Add the name, then the description.
	 * The number of colours should equal the number of streams
	 * 
	 * @param phaseString String containing a line from between the phase tags
	 */
	private void AddPhase(String phaseString, PhasePlan phasePlan) throws TrafficSyntaxException{
		String[] phaseSplit = SplitLineAlongTabs(phaseString);
		Phase newPhase = new Phase(phaseSplit[0], phaseSplit[1]);
		if (phaseSplit.length < 4) throw new TrafficSyntaxException("Insufficient phase word length");	//Throw exception if insufficient words
		if (phaseSplit[2].length() < trafficStreamList.size()) throw new TrafficSyntaxException("Insufficient signal colours"); //Throw exception if insufficient signal colours
		//Loop through all colours in third word
		for (int i = 0; i < trafficStreamList.size(); i++){
			State streamState = IntersectionTextParser.CharToState(phaseSplit[2].charAt(i));
			newPhase.addStream(trafficStreamList.get(i), streamState);	
		}
		int delayTime = IntersectionTextParser.StringToTime(phaseSplit[3]);
		newPhase.setMinGreenInterval(delayTime);
		phasePlan.add(newPhase);
	}
	
	/**
	 * Add a signal face to the intersection.
	 * 
	 * @param signalString	String containing a line from between the SignalFace tags.
	 * @param intersect		The intersection being built.
	 */
	private void AddSignalFace(String signalString, Intersection intersect) throws TrafficSyntaxException{
		String[] signalSplit = SplitLineAlongTabs(signalString);
		TrafficDirection location;
		TrafficDirection facing;
		int faceType;
		
		if (signalSplit.length < 4) throw new TrafficSyntaxException("Insufficient words in SignalFace description");
		
		try{
			 location = IntersectionTextParser.StringToDirection(signalSplit[0]);
		}
		catch (TrafficSyntaxException e){
			throw new TrafficSyntaxException("Invalid location");
		}
		
		try{
			 facing = IntersectionTextParser.StringToDirection(signalSplit[1]);
		}
		catch (TrafficSyntaxException e){
			throw new TrafficSyntaxException("Invalid direction");
		}
		
		try{
			 faceType = IntersectionTextParser.StringToFaceType(signalSplit[2]);
		}
		catch (TrafficSyntaxException e){
			throw new TrafficSyntaxException("Invalid shape");
		}
		
		SignalFace newSignalFace = new SignalFace(location, facing, faceType);
		
		//Get the value of a traffic stream that corresponds to the key of its name.
		TrafficStream streamToObserve = trafficStreamMap.get(signalSplit[3]);
		if (streamToObserve == null)
			throw new TrafficSyntaxException("Stream assigned to face does not exist");
		else
			streamToObserve.addObserver(newSignalFace);
		
		intersect.addSignalFace(newSignalFace);
	}
}
