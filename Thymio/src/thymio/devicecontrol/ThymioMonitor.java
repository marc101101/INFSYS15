package thymio.devicecontrol;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import classification.ObstacleClassifier;
import control.PosDetermination;
import thymio.model.Thymio;
import dataexchange.TCPConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ThymioMonitor extends Thread {
	private TCPConnection myConnection;
	private Thymio myThymio;
	private ObstacleClassifier myAlerter;
	private double[] startProb = { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
	PosDetermination posDet;
	FileWriter writer;

	public ThymioMonitor(TCPConnection c, Thymio t) {
		myConnection = c;
		myThymio = t;
		myAlerter = new ObstacleClassifier("/home/pi/Thymio/data.arff");
		posDet = new PosDetermination(startProb);
		String timeStamp = new SimpleDateFormat("SSSSSSSS").format(System.currentTimeMillis());
		try{
			writer = new FileWriter("/home/pi/dataCollection" + timeStamp + ".csv");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void run() {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder result;
		
		while (true) {
			double [] probs = myAlerter.classify(myThymio.getVariable("prox.horizontal"));
			try{
				writer.append("Array: " + probs[0] + " "+ probs[1]+ " "+ probs[2]+ " "+ probs[3]+ " "+ probs[4]+ " "+ probs[5]+"\n");
				writer.flush();
			}
			catch(IOException e){
				System.out.println(e);
			} 
			posDet.updatePos(probs);
			String detResult = posDet.getResult();
			if(!detResult.equals("set speed 0 0")){
				myConnection.getUSB().process(detResult);
			}
			System.out.println(detResult);
			
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