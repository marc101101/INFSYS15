package client;

import gui.ThymioInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

public class ThymioReadThread extends Thread {
	private BufferedReader fromThymio;
	private JsonReaderFactory factory;
	private ThymioInterface myInterface;

	public ThymioReadThread(BufferedReader io, ThymioInterface mi) {
		fromThymio = io;
		myInterface = mi;
	}
	
	public void run() {
		String answer;
		boolean stopped;

		JsonReader reader;
		JsonObject result;
		JsonArray data;
		
		factory = Json.createReaderFactory(null);

		System.out.println("read thread up ...");
		stopped = false;
		
		while (!stopped) {
			try {
				answer = fromThymio.readLine();
				if (answer != null) {
					//System.out.println("received: " + answer);
					reader = factory.createReader(new StringReader(answer));
					result = reader.readObject();
				
					data = result.getJsonArray("result");
					if (data != null) {
						System.out.println(data);
					}
					else {
						data = result.getJsonArray("values");
						if (data != null) myInterface.thymioEvent(data);
						else System.out.println("UNKNOWN event: " + answer);
					}
				}
				else stopped = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}