package thymio.devicecontrol;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import classification.ObstacleClassifier;
import thymio.model.Thymio;
import dataexchange.TCPConnection;

public class ThymioMonitor extends Thread {
	private TCPConnection myConnection;
	private Thymio myThymio;
	private ObstacleClassifier myAlerter;

	public ThymioMonitor(TCPConnection c, Thymio t) {
		myConnection = c;
		myThymio = t;
		myAlerter = new ObstacleClassifier("/home/pi/Thymio/data.arff");
	}
	
	public void run() {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder result;
		
		while (true) {
			double [] probs = myAlerter.classify(myThymio.getVariable("prox.horizontal"));
			JsonArrayBuilder values = factory.createArrayBuilder();

			result = factory.createArrayBuilder();
			result.add(factory.createObjectBuilder().add("status", "ok").build());
		
			// build JSON string for detected obstacles and it's classification
			
			for (int i = 0; i < probs.length; i++) {
				JsonObject o = factory.createObjectBuilder().add("class_" + i, Double.toString(probs[i])).build();
				values.add(o);
			}
			
			result.add(factory.createObjectBuilder().add("obstacles", values.build()));
			
			// build JSON string for estimated position
			
			values = factory.createArrayBuilder();
			values.add(factory.createObjectBuilder().add("pos_x", Double.toString(myThymio.getPosX())).build());
			values.add(factory.createObjectBuilder().add("pos_y", Double.toString(myThymio.getPosY())).build());
			
			result.add(factory.createObjectBuilder().add("position", values.build()));
			
			myConnection.sendMessage(factory.createObjectBuilder().add("values", result).build().toString());
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
