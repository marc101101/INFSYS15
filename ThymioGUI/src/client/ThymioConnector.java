package client;

import gui.ThymioInterface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;

public class ThymioConnector {
	private Socket clientSocket;
	private DataOutputStream toThymio;
	private BufferedReader fromThymio;
	private ThymioReadThread myReadThread;
	
	public ThymioConnector(ThymioInterface mi) {
		init(mi);
	}
	
	public void init(ThymioInterface mi) {
		try {
			// set up

			clientSocket = new Socket("192.168.43.107", 6789);
			toThymio = new DataOutputStream(clientSocket.getOutputStream());
			fromThymio = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myReadThread = new ThymioReadThread(fromThymio, mi);
		myReadThread.start();
	}
	
	public void close() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void sendMessage(String input) {
		try {
			toThymio.writeBytes(input + "\n");
			toThymio.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}