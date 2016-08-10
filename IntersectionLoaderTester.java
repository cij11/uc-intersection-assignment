package traffic.diy;

import static org.junit.Assert.*;

import org.junit.Test;

import traffic.diy.MyIntersectionLoader.IntersectionTextParser;
import traffic.load.TrafficSyntaxException;
import traffic.signal.SignalFace;
import traffic.util.State;

public class IntersectionLoaderTester {

	IntersectionTextParser myLoader = new IntersectionTextParser();
	
	@Test
	public void testReadFaceStandard(){
		try {
			assertEquals(IntersectionTextParser.StringToFaceType("STANDARD"), SignalFace.STANDARD);
		} catch (TrafficSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadFaceLeft(){
		try {
			assertEquals(IntersectionTextParser.StringToFaceType("LEFT_ARROW"), SignalFace.LEFT_ARROW);
		} catch (TrafficSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadFaceRight(){
		try {
			assertEquals(IntersectionTextParser.StringToFaceType("RIGHT_ARROW"), SignalFace.RIGHT_ARROW);
		} catch (TrafficSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelayReadSuccess(){
		try{
		assertEquals(IntersectionTextParser.StringToTime("1"), 1);
		}
		catch (TrafficSyntaxException e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadColorState(){
		try {
			assertEquals(IntersectionTextParser.CharToState('G'), State.GREEN);
		} catch (TrafficSyntaxException e) {
			e.printStackTrace();
		}
	}
}