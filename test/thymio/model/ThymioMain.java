package thymio.model;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.json.JsonObject;

import classification.ObstacleClassifier;
import dataexchange.TCPConnection;
import thymio.devicecontrol.ThymioMonitor;
import thymio.devicecontrol.USBConnection;

public class ThymioMain {
	private USBConnection myDevice;
	private TCPConnection communication;
	private boolean isAlive;
	private ThymioMonitor myMonitor;
	private Thymio myController;
	
	public ThymioMain() {
		System.out.println("Setting up Thymio ...");
		myDevice = new USBConnection();
		if (myDevice.isAlive()) {
			communication = new TCPConnection(myDevice);
			myController = new Thymio(myDevice);
			isAlive = communication.isConnected();
			myMonitor = new ThymioMonitor(communication, myController);
		}
		else isAlive = false;
	}
	
	private void run() {
		String input;
		int b;
		ArrayList<Integer> codes;
		
		myController.start();
		myMonitor.start();
		
		try {
			b = System.in.read();

			while (b != -1) {
				codes = new ArrayList<Integer>();
				
				while (b != 10) {
					if (b == -1) break;
					codes.add(b);
					b = System.in.read();			
				}
				
				if (b == 10) {
					JsonObject result;
					// process input
					byte [] byte_codes = new byte[codes.size()];
					for (int i = 0; i < codes.size(); i++) byte_codes[i] = codes.get(i).byteValue();
					
					input = new String(byte_codes);
					if (input.equals("EXIT")) break;
					
					result = myDevice.process(input);
					System.out.println(result);
					
					b = System.in.read();
				}				
			}
			
			myController.shutdown();
			myDevice.shutdown();
			communication.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public static void main(String [] args) {
		ThymioMain me = new ThymioMain();
		
		if (me.isAlive()) me.run();
		else System.err.println("Could not setup Thmyio ...");
	}
}
