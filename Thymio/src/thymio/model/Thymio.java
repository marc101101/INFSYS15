package thymio.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import thymio.devicecontrol.USBConnection;


public class Thymio extends Thread {
	private long lastTimeStamp;
	private PrintWriter logData;
	private USBConnection myDevice;
	
	private Map myMap;
	
	public static final double MAXSPEED = 500;
	public static final double SPEEDCOEFF = 2.93;
	public static final double BASE_WIDTH = 95;
	
	boolean stopped;

	public Thymio(USBConnection d) {
		myDevice = d;
		lastTimeStamp = Long.MIN_VALUE;
		stopped = false;
		
		myMap = new Map(10, 20, 17);
		
		/*
		try {
			logData = new PrintWriter(new FileWriter("./logdata.csv"));
			logData.println("motor.left.speed\tmotor.right.speed\tdelta x observed\tdelta x computed\tdelta theta observed\tdelta theta computed\tpos X\tposY\tvertical 0\tvertical 1");
			logData.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public short getVLeft() {
		return myDevice.getVLeft();
	}
	

	public short getVRight() {
		return myDevice.getVRight();
	}

	public synchronized void shutdown() {
		stopped = true;
	}
	
	public void run() {
		long now = lastTimeStamp;
		
		List<Short> sensorData;
		short odomLeft;
		short odomRight;
		double odomForward;
		double odomRotation;
		double expectedForward;
		double expectedRotation;
		double secsElapsed;

		System.out.println("Control Thread up ...");
		while (!stopped) {
			if (lastTimeStamp > Long.MIN_VALUE) {
				now = System.currentTimeMillis();
				secsElapsed = ((double)now - lastTimeStamp)/1000.0;
				lastTimeStamp = now;

				sensorData = myDevice.getVariable("motor.left.speed");
				if (sensorData != null) odomLeft = sensorData.get(0);
				else {
					odomLeft = Short.MIN_VALUE;
					System.out.println("no data for motor.left.speed");
				}

				sensorData = myDevice.getVariable("motor.right.speed");
				if (sensorData != null) odomRight = sensorData.get(0);
				else {
					odomRight = Short.MIN_VALUE;
					System.out.println("no data for motor.right.speed");
				}

				if (odomLeft > Short.MIN_VALUE && odomRight > Short.MIN_VALUE) {
					short vleft = myDevice.getVLeft();
					short vright = myDevice.getVRight();
					
					odomForward = (odomLeft + odomRight)/(2.0*SPEEDCOEFF);
					// observed speeed in mm/s
					odomRotation = Math.atan2(secsElapsed*(odomRight - odomLeft)/SPEEDCOEFF, BASE_WIDTH);
					// observed rotation around Thymio's center in rad

					expectedForward = (vleft + vright)/(2.0*SPEEDCOEFF);
					// expected speed in mm/s (according to speed setting).
					expectedRotation = Math.atan2(secsElapsed*(vright - vleft)/SPEEDCOEFF, BASE_WIDTH);
					// expected rotation around Thymio's center in rad

					//System.out.println(odomForward + " / " + expectedForward);
					myMap.updatePose(odomForward, odomRotation, expectedForward, expectedRotation, secsElapsed);
				}

				// 				sensorData = myDevice.getVariable("prox.ground.delta");

				//logData.print(odomForward + "\t" + distForward + "\t" + odomRotation + "\t" + distRotation + "\t");

				/*
			logData.print(myPanel.getEstimPosX() + "\t" +myPanel.getEstimPosY() + "\t");
			logData.println(sensorData.get(0) + "\t" + sensorData.get(1)); 
			logData.flush();
				 */
			}
			else lastTimeStamp = System.currentTimeMillis();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		synchronized (this) {
			notify();
			System.out.println("Control Thread terminated.");
		}
	}
	
	public List<Short> getVariable(String var) {
		return myDevice.getVariable(var);
	}
	
	public void setVariable(String var, List<Short> data) {
		myDevice.setVariable(var, data);
	}
	
	public void ahead() {
		myDevice.ahead();
	}
	
	
	public void halt() {
		myDevice.halt();
	}
	
	public void rotate(double rad) {
		myDevice.rotate(rad);
	}
	
	public double getPosX() {
		return myMap.getEstimPosX();
	}
	
	public double getPosY() {
		return myMap.getEstimPosY();
	}
	
	public double getOrientation() {
		return myMap.getEstimTheta();
	}
}
