package navigation;

import java.util.ArrayList;
import java.util.List;

import thymio.devicecontrol.USBConnection;
import thymio.model.MapElement;
import thymio.model.Thymio;


public class RouteNavigator {
	private static final String groundDelta = "prox.ground.delta";
	private static final String groundAmbiant = "prox.ground.ambiant";
	private static final String groundReflected = "prox.ground.reflected";
	private static final String distSensors = "prox.horizontal";
	private final int THRESHOLD_BLACK = 500;
	
	private USBConnection myDevice;
	
	public RouteNavigator(USBConnection thymio) {
//			logger = new FileLogger();
		myDevice = thymio;
	}

	public void crossFieldStraight() {
		final int HORIZON = 5;
		boolean initLeftBlack, initRightBlack;
		boolean currLeftBlack, currRightBlack;
		boolean stopped = false;
		List<Short> values;
		ArrayList<String> history = new ArrayList<String>();
		
		values = myDevice.getVariable(groundReflected);
	
		initLeftBlack = (values.get(0).intValue() < THRESHOLD_BLACK);
		initRightBlack = (values.get(1).intValue() < THRESHOLD_BLACK);
		
		System.out.println("init left: " + initLeftBlack + " init right: " + initRightBlack);
		history.add(Boolean.toString(initLeftBlack) + Boolean.toString(initRightBlack));
		
		myDevice.ahead();
		
		while (!stopped) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			values = myDevice.getVariable(groundReflected);
			
			currLeftBlack = (values.get(0).intValue() < THRESHOLD_BLACK);
			currRightBlack = (values.get(1).intValue() < THRESHOLD_BLACK);

			if (history.size() == HORIZON) history.remove(HORIZON - 1);
			history.add(0, Boolean.toString(currLeftBlack == initLeftBlack) + Boolean.toString(currRightBlack == initRightBlack));
			
			if ((currLeftBlack != initLeftBlack) && (currRightBlack != initRightBlack)) {
				// both sensors observe color change: goal field reached
				
				System.out.println("curr left: " + currLeftBlack + " curr right: " + currRightBlack);
				myDevice.halt();
				stopped = true;
			}
			else if ((currLeftBlack != initLeftBlack) && (currRightBlack == initRightBlack)) {
				// (a) Thymio turned slightly to the left and is entering the field left to it.
				// (b) Thymio turned a lot to the right and is entering the field ahead of the 
				//     current field, but not straight (this is the less probable hypothesis)
				
				System.out.println("LEFT: curr left: " + currLeftBlack + " curr right: " + currRightBlack);

				myDevice.halt();
				myDevice.rotateRight();

				do {
					values = myDevice.getVariable(groundReflected);
				
					currLeftBlack = (values.get(0).intValue() < THRESHOLD_BLACK);
					currRightBlack = (values.get(1).intValue() < THRESHOLD_BLACK);
				}
				while (currLeftBlack != currRightBlack);
				System.out.println("RECOVERED: curr left: " + currLeftBlack + " curr right: " + currRightBlack);

				myDevice.ahead();
			}
			else if ((currLeftBlack == initLeftBlack) && (currRightBlack != initRightBlack)) {
				// (a) Thymio turned slightly to the right and is entering the field right to it.
				// (b) Thymio turned a lot to the left and is entering the field ahead of the 
				//     current field, but not straight (this is the less probable hypothesis)
				
				System.out.println("RIGHT: curr left: " + currLeftBlack + " curr right: " + currRightBlack);

				myDevice.halt();
				
				// (a) can be tested if after a slight turn to the left, 
				//     (currLeftBlack == initLeftBlack) && (currRightBlack == initRightBlack)
				//     is true again.
				
				myDevice.rotateLeft();

				do {
					values = myDevice.getVariable(groundReflected);
				
					currLeftBlack = (values.get(0).intValue() < THRESHOLD_BLACK);
					currRightBlack = (values.get(1).intValue() < THRESHOLD_BLACK);
				}
				while (currLeftBlack != currRightBlack);
				System.out.println("RECOVERED: curr left: " + currLeftBlack + " curr right: " + currRightBlack);
				
				myDevice.ahead();
			}
			// else  (currLeftBlack == initLeftBlack) && (currRightBlack == initRightBlack) -> go ahead!
		}
	}
}
