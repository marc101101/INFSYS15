package gui;

import java.awt.event.ActionEvent;
import javax.json.JsonArray;
import map.Map;
import javax.swing.JFrame;

import client.ThymioConnector;

public class ThymioInterface {
	
	private static final int FREI = 5;

	//window dimensions
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 768;
	
	private ThymioConnector myConnector;
	private PanelFrame window;
	private Map m;

	public ThymioInterface(){
		initComponents();
		initWindow();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		window.repaint();
	}
	
	/**
	 * Inits the general window in which the UI will be displazed with basic parameters
	 */
	private void initWindow(){
		window = new PanelFrame(this);
		window.setTitle("ThymioGUI");
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initComponents() {
		myConnector = new ThymioConnector(this);
		m = new Map(8, 20, 1);
	}

	public void thymioEvent(JsonArray data) {
		String status;
		JsonArray obstacles;
		JsonArray position;
		int bestClass = FREI;
		double bestProb;

		status = data.getJsonObject(0).getString("status");
		if (status.equals("ok")) {
			obstacles = data.getJsonObject(1).getJsonArray("obstacles");

			//gets highest probability
			bestProb = Double.MIN_VALUE;
			for (int i = 0; i < 6; i++) {
				double p = Double.parseDouble(obstacles.getJsonObject(i)
						.getString("class_" + i));

				if (p > bestProb) {
					bestProb = p;
					bestClass = i;
				}
			}
			position = data.getJsonObject(2).getJsonArray("position");
			
			if((position.getJsonObject(0).getString("pos_x") != null) && (position.getJsonObject(0).getString("pos_y") != null)){
				window.updatePosition(
						Double.parseDouble(position.getJsonObject(0).getString("pos_x")),
						Double.parseDouble(position.getJsonObject(1).getString("pos_y")));
			}
			window.updateObstacle(bestClass);
			window.repaint();
		} else {
			System.out.println("ERROR: " + status);
		}
	}

	protected void performAction(ActionEvent e) {
		 if (e.getSource() == window.getFwButton()) {
			 window.appendLine("FORWARD!");
			 myConnector.sendMessage("set speed 50 50");
		 } 
		 else if (e.getSource() == window.getBwButton()) {
			 window.appendLine("BACKWARD!");
			 myConnector.sendMessage("set speed -50 -50");
		 } 
		 else if (e.getSource() == window.getLeftButton()) {
			 window.appendLine("LEFT!");
			 myConnector.sendMessage("set speed -50 50");
		 } 
		 else if (e.getSource() == window.getRightButton()) {
			 window.appendLine("RIGHT!");
			 myConnector.sendMessage("set speed 50 -50");
		 } 
		 else if (e.getSource() == window.getStopButton()) {
			 window.appendLine("STOP!");
			 myConnector.sendMessage("set speed 0 0");
		 } 
		 else {
			 return;
		 }
	}

	public Map getMap(){
		return m;
	}
	
	public double getTheta(){
		return m.getThymioOrientation();
	}
}
