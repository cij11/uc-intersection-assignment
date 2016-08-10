package traffic.diy;

//import javax.swing.JOptionPane;

import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
//import traffic.misc.Detector;
import traffic.misc.RandomDetector;
import traffic.phaseplan.FullyActuatedPhasePlan;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.signal.SignalFace;
import traffic.util.State;
import traffic.util.TrafficDirection;

/**
 * "Manually" create an intersection by assembling the various elements. Use the
 * classes provided in the traffic packages to construct an intersection which
 * can be displayed in the monitor.
 *
 */
public class ModelIntersection {

	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more pre-timed phase plans.
	 * @return the intersection I made.
	 */
	public static Intersection preTimedIntersection() {

		//Instantiate the intersection
		Intersection myIntersection = new Intersection("Yaldhurst - Peer street", "Model of a pre-timed Cross Intersection");
		
		//Define the traffic streams
		TrafficStream nInbound = new TrafficStream ("N->S|E|W", "North inbound, 3 outbound");
		TrafficStream sInbound = new TrafficStream ("S->N|E", "South inbound, N E outbound");
		TrafficStream protectedEast = new TrafficStream ("S->W", "South inbound, protected Left turn West");
		TrafficStream eInbound = new TrafficStream ("E->W|N|S", "East inbound, 3 outbound");
		TrafficStream wInbound = new TrafficStream ("W->E|N|S", "West inbound, 3 outbound");
		
		//Instantiate a phase plan
		PretimedPhasePlan phasePlan = new PretimedPhasePlan();
		
		//Add the streams and states to the phase plans
		Phase phase1 = new Phase("East/West Go", "EW streams green, NS streams red/off");
		phase1.addStream(eInbound, State.GREEN);
		phase1.addStream(wInbound, State.GREEN);
		phase1.addStream(nInbound, State.RED);
		phase1.addStream(sInbound, State.RED);
		phase1.addStream(protectedEast, State.OFF);
		phase1.setMinGreenInterval(10);
		phasePlan.add(phase1);
		
		Phase phase2 = new Phase("East/West Warning", "EW streams yellow, NS streams red/off");
		phase2.addStream(eInbound, State.YELLOW);
		phase2.addStream(wInbound, State.YELLOW);
		phase2.addStream(nInbound, State.RED);
		phase2.addStream(sInbound, State.RED);
		phase2.addStream(protectedEast, State.OFF);
		phase2.setMinGreenInterval(3);
		phasePlan.add(phase2);
		
		Phase phase3 = new Phase("All Stopped", "All streams Red/off");
		phase3.addStream(eInbound, State.RED);
		phase3.addStream(wInbound, State.RED);
		phase3.addStream(nInbound, State.RED);
		phase3.addStream(sInbound, State.RED);
		phase3.addStream(protectedEast, State.OFF);
		phase3.setMinGreenInterval(1);
		phasePlan.add(phase3);
		
		Phase phase4 = new Phase("South Go", "S streams green, N|E|W streams red");
		phase4.addStream(eInbound, State.RED);
		phase4.addStream(wInbound, State.RED);
		phase4.addStream(nInbound, State.RED);
		phase4.addStream(sInbound, State.GREEN);
		phase4.addStream(protectedEast, State.GREEN);
		phase4.setMinGreenInterval(8);
		phasePlan.add(phase4);
		
		Phase phase5 = new Phase("North/South Go", "N|S streams green, E|W streams red");
		phase5.addStream(eInbound, State.RED);
		phase5.addStream(wInbound, State.RED);
		phase5.addStream(nInbound, State.GREEN);
		phase5.addStream(sInbound, State.GREEN);
		phase5.addStream(protectedEast, State.OFF);
		phase5.setMinGreenInterval(8);
		phasePlan.add(phase5);
		
		Phase phase6 = new Phase("North/South Warning", "NS streams yellow, EW streams red");
		phase6.addStream(eInbound, State.RED);
		phase6.addStream(wInbound, State.RED);
		phase6.addStream(nInbound, State.YELLOW);
		phase6.addStream(sInbound, State.YELLOW);
		phase6.addStream(protectedEast, State.OFF);
		phase6.setMinGreenInterval(3);
		phasePlan.add(phase6);
		
		Phase phase7 = new Phase("All Stopped", "All streams Red/off");
		phase7.addStream(eInbound, State.RED);
		phase7.addStream(wInbound, State.RED);
		phase7.addStream(nInbound, State.RED);
		phase7.addStream(sInbound, State.RED);
		phase7.addStream(protectedEast, State.OFF);
		phase7.setMinGreenInterval(1);
		phasePlan.add(phase7);
		
		//Define the signal faces
		SignalFace north1 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH , SignalFace.STANDARD);
		SignalFace north2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH , SignalFace.STANDARD);
		SignalFace north3 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH , SignalFace.STANDARD);
		
		SignalFace south1 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		SignalFace south2 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		SignalFace south3 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		
		SignalFace east1 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST , SignalFace.STANDARD);
		SignalFace east2 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.EAST , SignalFace.STANDARD);
		SignalFace east3 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.EAST , SignalFace.STANDARD);
		
		SignalFace west1 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.WEST , SignalFace.STANDARD);
		SignalFace west2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.WEST , SignalFace.STANDARD);
		SignalFace west3 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.WEST , SignalFace.STANDARD);
		
		SignalFace southProtectedRight = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.SOUTH , SignalFace.RIGHT_ARROW);
		
		//Add the signal faces as observers of the streams
		nInbound.addObserver(north1);
		nInbound.addObserver(north2);
		nInbound.addObserver(north3);
		
		sInbound.addObserver(south1);
		sInbound.addObserver(south2);
		sInbound.addObserver(south3);
		
		eInbound.addObserver(east1);
		eInbound.addObserver(east2);
		eInbound.addObserver(east3);
		
		wInbound.addObserver(west1);
		wInbound.addObserver(west2);
		wInbound.addObserver(west3);
		
		protectedEast.addObserver(southProtectedRight);
		
		myIntersection.addPlan(phasePlan);
		
		//Add the signal faces to the intersection
		myIntersection.addSignalFace(east1);
		myIntersection.addSignalFace(east2);
		myIntersection.addSignalFace(east3);
		
		myIntersection.addSignalFace(west1);
		myIntersection.addSignalFace(west2);
		myIntersection.addSignalFace(west3);
		myIntersection.addSignalFace(north1);
		myIntersection.addSignalFace(north2);
		myIntersection.addSignalFace(north3);
		
		myIntersection.addSignalFace(south1);
		myIntersection.addSignalFace(south2);
		myIntersection.addSignalFace(south3);
	
		myIntersection.addSignalFace(southProtectedRight);
		
		return myIntersection;
	}
	
	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more fully-actuated phase plans.
	 * @return the intersection I made.
	 */
	public static Intersection fullyActivatedIntersection() {
		//Instantiate an intersection
		Intersection myIntersection = new Intersection("Fully actuated", "N/S main road with east inbound connecting");
		
		//Define the traffic streams
		TrafficStream nInbound = new TrafficStream ("N->S|E", "North inbound, 2 outbound");
		TrafficStream sInbound = new TrafficStream ("S->N|E", "South inbound, 2 outbound");
		TrafficStream eInbound = new TrafficStream ("E->N|S", "East inbound, 2 outbound");
		
		//Instantiate a phase plan
		FullyActuatedPhasePlan phasePlan = new FullyActuatedPhasePlan();
		
		//Instantiate detectors, and add them to the streams
		RandomDetector detector1 = new RandomDetector();
		RandomDetector detector2 = new RandomDetector();
		RandomDetector detector3 = new RandomDetector();
		
		nInbound.addDetector(detector1);
		sInbound.addDetector(detector2);
		eInbound.addDetector(detector3);
		
		//Add phases to the phase plan
		Phase phase1 = new Phase("East Go", "E streams green, NS streams red");
		phase1.addStream(eInbound, State.GREEN, detector3);
		phase1.addStream(nInbound, State.RED, detector1);
		phase1.addStream(sInbound, State.RED, detector2);
		phase1.setMinGreenInterval(5);
		phasePlan.add(phase1);
		
		Phase phase2 = new Phase("East Warning", "E streams yellow, NS streams red");
		phase2.addStream(eInbound, State.YELLOW, detector3);
		phase2.addStream(nInbound, State.RED, detector1);
		phase2.addStream(sInbound, State.RED, detector2);
		phase2.setMinGreenInterval(3);
		phasePlan.add(phase2);
		
		Phase phase3 = new Phase("All Stopped", "All streams Red");
		phase3.addStream(eInbound, State.RED, detector3);
		phase3.addStream(nInbound, State.RED, detector1);
		phase3.addStream(sInbound, State.RED, detector2);
		phase3.setMinGreenInterval(1);
		phasePlan.add(phase3);
		
		Phase phase4 = new Phase("North/South Go", "NS streams green, E stream red");
		phase4.addStream(eInbound, State.RED, detector3);
		phase4.addStream(nInbound, State.GREEN, detector1);
		phase4.addStream(sInbound, State.GREEN, detector2);
		phase4.setMinGreenInterval(10);
		phasePlan.add(phase4);
		
		Phase phase5 = new Phase("North/South Warning", "NS streams yellow, E stream red");
		phase5.addStream(eInbound, State.RED, detector3);
		phase5.addStream(nInbound, State.YELLOW, detector1);
		phase5.addStream(sInbound, State.YELLOW, detector2);
		phase5.setMinGreenInterval(3);
		phasePlan.add(phase5);
		
		Phase phase6 = new Phase("All Stopped", "All streams Red");
		phase6.addStream(eInbound, State.RED, detector3);
		phase6.addStream(nInbound, State.RED);
		phase6.addStream(sInbound, State.RED);
		phase6.setMinGreenInterval(1);
		phasePlan.add(phase6);
		
		//Define signal faces
		SignalFace north1 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH , SignalFace.STANDARD);
		SignalFace north2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH , SignalFace.STANDARD);
		SignalFace north3 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH , SignalFace.STANDARD);
		
		SignalFace south1 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		SignalFace south2 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		SignalFace south3 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH , SignalFace.STANDARD);
		
		SignalFace east1 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST , SignalFace.STANDARD);
		SignalFace east2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.EAST , SignalFace.STANDARD);
		
		//Add the signal faces as observers of the traffic streams
		nInbound.addObserver(north1);
		nInbound.addObserver(north2);
		nInbound.addObserver(north3);
		
		sInbound.addObserver(south1);
		sInbound.addObserver(south2);
		sInbound.addObserver(south3);
		
		eInbound.addObserver(east1);
		eInbound.addObserver(east2);
		
		myIntersection.addPlan(phasePlan);
		
		//Add the signal faces to the intersection
		myIntersection.addSignalFace(east1);
		myIntersection.addSignalFace(east2);
		
		myIntersection.addSignalFace(north1);
		myIntersection.addSignalFace(north2);
		myIntersection.addSignalFace(north3);
		
		myIntersection.addSignalFace(south1);
		myIntersection.addSignalFace(south2);
		myIntersection.addSignalFace(south3);
	
		return myIntersection;
	}
}
