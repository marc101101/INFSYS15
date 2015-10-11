package thymio.devicecontrol;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import navigation.RouteNavigator;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import ch.epfl.mobots.AsebaNetwork;

public class USBConnection {
	private DBusConnection conn;
	private AsebaNetwork dbus;
	private List<String> knownVars;
	private String error;
	private short vleft;
	private short vright;
	private JsonBuilderFactory factory;

	private final double BASE_WIDTH = 95;     // millimeters
	private final double MAX_SPEED = 500;	// units
	private final double SPEED_COEF = 2.93;	// 1mm/sec corresponds to X units of real thymio speed
	
	private final short SPEED_ROTATION = 50;
	private final short SPEED_AHEAD = 100;
	
	private class Timer extends Thread {
		private long delaymillis;
		
		public Timer(long delay) {
			delaymillis = delay;
		}
	
		public void run() {
			try {
				ArrayList<Short> stop = new ArrayList<Short>();
				stop.add((short)0);
				
				Thread.sleep(delaymillis);
				
				vleft = vright = (short)0;
				setVariable("motor.right.target", stop);
				setVariable("motor.left.target", stop);		
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public USBConnection() {
		initDBus();
		
		vleft = vright = 0;
		factory = Json.createBuilderFactory(null);
	}

	public void shutdown() {
		shutdownDBus();
	}

	private void initDBus() {
		try {
			conn = DBusConnection.getConnection(DBusConnection.SESSION);
      		dbus = conn.getRemoteObject("ch.epfl.mobots.Aseba",
										"/",
										AsebaNetwork.class);
			System.out.println("Connected to DBus: " + this.isAlive());
		
			knownVars = dbus.GetVariablesList("thymio-II");
			System.out.println("Variables for thymio-II");
			for (String string : knownVars) {
				System.out.println(string);
			}
		} catch(DBusException e) {
			System.err.println("Error while calling dbus method: " + e.toString());
			dbus = null;
	  }
	}

	public JsonObject process(String input) {
		JsonArrayBuilder result;

		if (input.startsWith("get")) {
			JsonArrayBuilder values = factory.createArrayBuilder();
			String [] command_with_args = input.split(" ");
			List<Short> data = getVariable(command_with_args[1]);

			result = factory.createArrayBuilder();
			if (data == null) {
				return factory.createObjectBuilder().add("result", result.add(factory.createObjectBuilder().add("status", error).build()).build()).build();				
			}
			else {
				JsonObject res;
				
				for (int i = 0; i < data.size(); i++) {
					JsonObject o = factory.createObjectBuilder().add("arg_" + i, data.get(i).shortValue()).build();
					values.add(o);
				}

				res = factory.createObjectBuilder().add("result", result.add(factory.createObjectBuilder().add("status", "ok").build()).add(factory.createObjectBuilder().add("parameter", command_with_args[1]).build()).add(factory.createObjectBuilder().add("values", values.build()).build())).build();
				return res;
			}
		}
		else if (input.startsWith("set")) {
			try {
				String [] command_with_args = input.split(" ");

				if (command_with_args[1].equals("speed")) {
					vleft = Short.parseShort(command_with_args[2]);
					vright = Short.parseShort(command_with_args[3]);

					ArrayList<Short> dataleft = new ArrayList<Short>();
					dataleft.add(vleft);
					ArrayList<Short> dataright = new ArrayList<Short>();
					dataright.add(vright);

					setVariable("motor.left.target", dataleft);
					setVariable("motor.right.target", dataright);

					result = factory.createArrayBuilder();
					return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "ok").build()).build()).build();				
				}
				else {
					ArrayList<Short> data = new ArrayList<Short>();

					for (int i = 2; i < data.size(); i++) data.add(Short.parseShort(command_with_args[i]));
					setVariable(command_with_args[1], data);
					
					result = factory.createArrayBuilder();
					return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "ok").build()).build()).build();
				}
			}
			catch (NumberFormatException e) {
				result = factory.createArrayBuilder();
				return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "At least one argument is not a short value in: " + input).build()).build()).build();
			}
		}
		else if (input.startsWith("rot")) {
			String [] command_with_args = input.split(" ");
			rotate(Double.parseDouble(command_with_args[1])/180*Math.PI);
			
			result = factory.createArrayBuilder();
			return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "ok").build()).build()).build();
		}
		else if (input.startsWith("ahead")) {
			ahead(170);
			
			result = factory.createArrayBuilder();
			return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "ok").build()).build()).build();
		}
		else if (input.startsWith("cross")) {
			RouteNavigator rn = new RouteNavigator(this);
			
			rn.crossFieldStraight();
			
			result = factory.createArrayBuilder();
			return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "ok").build()).build()).build();
		}
		else {
			result = factory.createArrayBuilder();
			return factory.createObjectBuilder().add("result", result.add( factory.createObjectBuilder().add("status", "Unknown command in: " + input).build()).build()).build();
		}
	}

	public void rotateLeft() {
		ArrayList<Short> dataleft = new ArrayList<Short>();
		ArrayList<Short> dataright = new ArrayList<Short>();
		
		vleft = (short)(-SPEED_ROTATION);
		vright = (short)SPEED_ROTATION;

		dataleft.add(vleft);
		dataright.add(vright);

		setVariable("motor.left.target", dataleft);
		setVariable("motor.right.target", dataright);
	}
	
	public void rotateRight() {
		ArrayList<Short> dataleft = new ArrayList<Short>();
		ArrayList<Short> dataright = new ArrayList<Short>();
		
		vleft = (short)SPEED_ROTATION;
		vright = (short)(-SPEED_ROTATION);

		dataleft.add(vleft);
		dataright.add(vright);

		setVariable("motor.left.target", dataleft);
		setVariable("motor.right.target", dataright);
	}
	
	public void rotate(double rad) {
		double dt;
		long milli;
		ArrayList<Short> dataleft = new ArrayList<Short>();
		ArrayList<Short> dataright = new ArrayList<Short>();
		Timer t;

		if (rad < 0) {
			vleft = (short)SPEED_ROTATION;
			vright = (short)(-SPEED_ROTATION);
			
			dataleft.add(vleft);
			dataright.add(vright);
		}
		else {
			vleft = (short)(-SPEED_ROTATION);
			vright = (short)SPEED_ROTATION;
			
			dataleft.add(vleft);
			dataright.add(vright);			
		}
		
		dt = (BASE_WIDTH*SPEED_COEF)/(2*SPEED_ROTATION)*Math.abs(rad);
		milli = (long)(1000*dt);
		

		//t = new Timer(milli);
		
		setVariable("motor.left.target", dataleft);
		setVariable("motor.right.target", dataright);
		
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		halt();
		//t.start();
	}

	public void halt() {
		ArrayList<Short> go = new ArrayList<Short>();

		vleft = vright = (short)0;
		go.add(vleft);
		
		setVariable("motor.left.target", go);
		setVariable("motor.right.target", go);
	}
	
	public void ahead() {
		ArrayList<Short> go = new ArrayList<Short>();

		vleft = vright = (short)SPEED_AHEAD;
		go.add(vleft);
		
		setVariable("motor.left.target", go);
		setVariable("motor.right.target", go);
	}
	
	public void ahead(double mm) {
		double dt;
		long milli;
		Timer t;
		ArrayList<Short> go = new ArrayList<Short>();

		vleft = vright = (short)((mm < 0 ? -1 : 1)*SPEED_AHEAD);
		go.add(vleft);
		
		dt = SPEED_COEF/SPEED_AHEAD*Math.abs(mm);
		milli = (long)(1000*dt);
		
		//t = new Timer(milli);

		setVariable("motor.left.target", go);
		setVariable("motor.right.target", go);
		
		//t.start();
		
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		halt();
	}
	
	public boolean isAlive() {
		return (dbus != null);
	}
	
	public List<Short> getVariable(String varname) {
		if (!knownVars.contains(varname)) {
			error = "variable unknown: " + varname;
			return null;
		}
		else {
			try {
				List<Short> values;

				values = dbus.GetVariable("thymio-II", varname);
				return values;
			} catch(DBusExecutionException e) {
				error = "Error while calling dbus method: " + e.toString();
				return null;
			}
		}
	}

	public void setVariable(String varname, List<Short> data) {
		if (!knownVars.contains(varname)) {
			error = "variable unknown: " + varname;
		}
		else {
			try {
				dbus.SetVariable("thymio-II", varname, data);
			} catch(DBusExecutionException e) {
				error = "Error while calling dbus method: " + e.toString();
				return;
			}
		}
	}
	
	private void shutdownDBus() {
		conn.disconnect();
		System.out.println("Disconnected from DBus");
	}
	
	public short getVLeft() {
		return vleft;
	}
	
	public short getVRight() {
		return vright;
	}
}