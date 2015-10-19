package dataexchange;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import thymio.devicecontrol.USBConnection;

public class TCPConnection {
	private ServerSocket me;
	private USBConnection thisThymio;
	private Socket server;
	private BufferedReader in;
	private DataOutputStream out;
	private TCPThread myReader;
	private boolean stopped;
	private boolean connectionClosed;
	
	public TCPConnection(USBConnection thisThymio) {
		this.thisThymio = thisThymio;
		
		try {
			initTCPIPServer();
			
			System.out.println("TCP connection up.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot establish socket: " + e.toString());
			System.err.println("Thymio will not communicate with other processes.");
			me = null;
		}
	}
	
	public ServerSocket getMe() {
		return me;
	}

	public void setMe(ServerSocket me) {
		this.me = me;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}


	public boolean isStopped() {
		return stopped;
	}


	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}


	public boolean isConnectionClosed() {
		return connectionClosed;
	}


	public void setConnectionClosed(boolean connectionClosed) {
		this.connectionClosed = connectionClosed;
	}


	public boolean isConnected() {
		return (me != null);
	}
	
	private void shutdownTCPIPServer() throws IOException {
		me.close();
	}
	
	private void initTCPIPServer() throws IOException {
         me = new ServerSocket(6789);
         myReader = new TCPThread(this, thisThymio);
         myReader.start();
	}

	public void shutdown() {
		try {
			stopped = true;
			if (!connectionClosed) {
				if (in != null) {
					in.close();
					in = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
				if (server != null) {
					server.close();
					server = null;
				}
				connectionClosed = true;
			}
			
			synchronized (this) {
				myReader.join();
			}
			
			shutdownTCPIPServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Shutdown of communication socket failed: " + e);
			System.err.println("Thymio will quit anyway.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public USBConnection getUSB(){
		return thisThymio;
	}
	
	public void sendMessage(String mess) {
		if (out != null) {
			try {
				out.writeBytes(mess + "\n");
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
