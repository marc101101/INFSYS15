package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.json.JsonArray;

import client.ThymioConnector;

public class ThymioInterface {

	private static final int FREI = 0;

	private ActionListener ae;
	private ThymioConnector myConnector;

	public ThymioInterface() {
		initComponents();
	}

	private void initComponents() {
		myConnector = new ThymioConnector(this);
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
			// window.repaint();
		} else {
			System.out.println("ERROR: " + status);
		}
	}

	protected void performAction(ActionEvent e) {
		// if (e.getSource() == this.fwButton) {
		// System.out.println("FORWARD!");
		// myConnector.sendMessage("set speed 50 50");
		// } else if (e.getSource() == this.bwButton) {
		// System.out.println("BACKWARD!");
		// myConnector.sendMessage("set speed -50 -50");
		// } else if (e.getSource() == this.leftButton) {
		// System.out.println("LEFT!");
		// myConnector.sendMessage("set speed -50 50");
		// } else if (e.getSource() == this.rightButton) {
		// System.out.println("RIGHT!");
		// myConnector.sendMessage("set speed 50 -50");
		// } else if (e.getSource() == this.stopButton) {
		// System.out.println("STOP!");
		// myConnector.sendMessage("set speed 0 0");
		// } else
		// return;
	}

	private void updatePosition(double posXmm, double posYmm) {
	}

	private void updateObstacle(int obstClass) {
	}

}
