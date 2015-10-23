package thymio.devicecontrol;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import classification.ObstacleClassifier;
import control.PosDetermination;
import thymio.model.Thymio;
import dataexchange.TCPConnection;

public class ThymioMonitor extends Thread {
	private TCPConnection myConnection;
	private Thymio myThymio;
	private ObstacleClassifier myAlerter;
	private double[] startProb = { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
	private PosDetermination posDet;

	public ThymioMonitor(TCPConnection c, Thymio t) {
		myConnection = c;
		myThymio = t;
		myAlerter = new ObstacleClassifier("/home/pi/Thymio/data.arff");
		posDet = new PosDetermination(startProb);
	}
	
	public void run() {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder result;
		
		while (true) {
			List<Short> sensorRaw = myThymio.getVariable("prox.horizontal");
			double [] probs = myAlerter.classify(sensorRaw);
			JsonArrayBuilder values;
			
			posDet.updatePos(probs);
			String detResult = posDet.getResult();
			if(!detResult.equals("set speed 0 0")){
				myConnection.getUSB().process(detResult);
			}
			String histCurrObst = posDet.getCurrObsWM();
			String histPrevObst = posDet.getLastObsWM();
			if(!(histPrevObst.equals("FREI")) && (histCurrObst.equals("FREI"))){
				myConnection.getUSB().process("set speed 0 0");
			}

			result = factory.createArrayBuilder();
			result.add(factory.createObjectBuilder().add("status", "ok").build());
		
			// build JSON string for infrared raw values

			values = factory.createArrayBuilder();
			
			for (int i = 0; i < sensorRaw.size(); i++) {
				JsonObject o = factory.createObjectBuilder().add("sensor_" + i, Short.toString(sensorRaw.get(i))).build();
				values.add(o);
			}
			
			result.add(factory.createObjectBuilder().add("sensor_raw", values.build()));
			
			// build JSON string for detected obstacles and it's classification
			
			values = factory.createArrayBuilder();
			
			for (int i = 0; i < probs.length; i++) {
				JsonObject o = factory.createObjectBuilder().add("class_" + i, Double.toString(probs[i])).build();
				values.add(o);
			}
			
			result.add(factory.createObjectBuilder().add("obstacles", values.build()));
			
			// build JSON string for estimated position
			
			values = factory.createArrayBuilder();
			values.add(factory.createObjectBuilder().add("pos_x", Double.toString(myThymio.getPosX())).build());
			values.add(factory.createObjectBuilder().add("pos_y", Double.toString(myThymio.getPosY())).build());
			values.add(factory.createObjectBuilder().add("orientation", Double.toString(myThymio.getOrientation())).build());

			result.add(factory.createObjectBuilder().add("position", values.build()));

			try {
				synchronized (myConnection) {
					while (myConnection.isSending()) {
						System.out.println(this.getClass().getName() + ": waiting for TCP Connection");
						wait();
					}
				}

				myConnection.sendMessage(factory.createObjectBuilder().add("values", result).build().toString());

				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
