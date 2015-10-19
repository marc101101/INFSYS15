package dataexchange;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.json.JsonObject;

import control.PosDetermination;
import thymio.devicecontrol.USBConnection;

public class TCPThread extends Thread {
	private TCPConnection myConn;
	private USBConnection myThymio;

	public TCPThread(TCPConnection conn, USBConnection t) {
		this.myConn = conn;
		myThymio = t;
        myConn.setStopped(false);
	}

	public void closeConnection() {
		myConn.setConnectionClosed(true);
	}
	
	public void run() {
		String data, answer;

		while (!myConn.isStopped()) {
			try {
				Socket server = myConn.getMe().accept();
				myConn.setConnectionClosed(false);
				
				while (!myConn.isConnectionClosed()) {
					 System.out.println("waiting for request");
					 myConn.setIn(new BufferedReader(new InputStreamReader(server.getInputStream())));
					 myConn.setOut(new DataOutputStream(server.getOutputStream()));
					 
					 data = myConn.getIn().readLine();
					 if (data != null) {
						 JsonObject result = myThymio.process(data);
						 answer = result.toString();
						 myConn.sendMessage(answer);
					 }
					 else myConn.setConnectionClosed(true);
				}

				server.close();
			}
			catch (IOException e) {
				if (!myConn.isStopped()) {
					System.err.println("Error while communicating via TCP: " + e);
					e.printStackTrace();
				}
				else System.out.println("Terminating TCPThread after shutdown was requested.");
			}
		}
	}
}
