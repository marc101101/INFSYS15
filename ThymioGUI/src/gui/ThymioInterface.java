package gui;

import java.awt.event.ActionEvent;

import javax.json.JsonArray;

import client.ThymioConnector;
import dataanalysis.InfraRed;
import map.Map;

import javax.swing.*;

public class ThymioInterface {

	private static final int FREI = 0;
	
	//private ThymioConnector myConnector;
	private PanelFrame window;
	private Map m;
	private MapPanel mp;

	public ThymioInterface() {
		initWindow();
		initComponents();
	}
	
	private void initWindow(){
		window = new PanelFrame(this);
		window.setTitle("ThymioGUI");
		window.setSize(800, 800);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initComponents() {
		//myConnector = new ThymioConnector(this);
		m = new Map(15, 15, 1);
		mp = new MapPanel(m, new InfraRed(), window);
		window.setMap(mp);
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

			updatePosition(
					Double.parseDouble(position.getJsonObject(0).getString(
							"pos_x")),
					Double.parseDouble(position.getJsonObject(1).getString(
							"pos_y")));
			updateObstacle(bestClass);
			//window.repaint();
		} else {
			System.out.println("ERROR: " + status);
		}
	}

	protected void performAction(ActionEvent e) {
		 if (e.getSource() == window.getFwButton()) {
			 window.appendLine("FORWARD!");
			 //myConnector.sendMessage("set speed 50 50");
		 } 
		 else if (e.getSource() == window.getBwButton()) {
			 window.appendLine("BACKWARD!");
			 //myConnector.sendMessage("set speed -50 -50");
		 } 
		 else if (e.getSource() == window.getLeftButton()) {
			 window.appendLine("LEFT!");
			 //myConnector.sendMessage("set speed -50 50");
		 } 
		 else if (e.getSource() == window.getRightButton()) {
			 window.appendLine("RIGHT!");
			 //myConnector.sendMessage("set speed 50 -50");
		 } 
		 else if (e.getSource() == window.getStopButton()) {
			 window.appendLine("STOP!");
			 //myConnector.sendMessage("set speed 0 0");
		 } 
		 else {
			 return;
		 }
	}

	private void updatePosition(double posXmm, double posYmm) {
		//myMapDisplay.setPose(posXmm/10, posYmm/10, theta);
		mp.setPose(posXmm/10, posYmm/10, m.getThymioOrientation());
	}

	private void updateObstacle(int obstClass) {
	}

}
