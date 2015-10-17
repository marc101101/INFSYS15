package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dataanalysis.InfraRed;
import map.Map;

public class MapPanel extends JPanel {
	
	//obstacle classes
	private static final int FRONTAL = 1;
	private static final int ECKE = 2;
	private static final int KANTE = 3;
	private static final int LINKS = 4;
	private static final int RECHTS = 5;
	private static final int FREI = 6;
		
	//images
	private Image imgFree, imgFrontal, imgKante, imgLinks, imgRechts, imgEcke;
	
	private static final long serialVersionUID = 1L;
	private Map myMap;
	public static final int LENGTHSCALE = 40;
	private double lengthX;
	private InfraRed myIRData;

	public MapPanel(Map m, InfraRed ir, JFrame f) {
		myMap = m;
		myIRData = ir;
		//lengthX = myMap.getEdgeLengthX();
		lengthX = myMap.getEdgeLength();
		myMap.setPose(0, 0, 0);

		this.setPreferredSize(new Dimension(myMap.getSizeX() * LENGTHSCALE + (int) f.getBounds().getWidth(),
				myMap.getSizeY() * LENGTHSCALE + (int) f.getBounds().getHeight()));
		this.setMaximumSize(new Dimension(myMap.getSizeX() * LENGTHSCALE, myMap.getSizeY() * LENGTHSCALE));
		this.setMinimumSize(new Dimension(myMap.getSizeX() * LENGTHSCALE, myMap.getSizeY() * LENGTHSCALE));		
		
		loadImages();
	}
	
	private void loadImages(){
		imgEcke = new ImageIcon("resources/ecke.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
		imgFree = new ImageIcon("resources/free.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
		imgFrontal = new ImageIcon("resources/frontal.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
		imgKante = new ImageIcon("resources/kante.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
		imgLinks = new ImageIcon("resources/left.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
		imgRechts = new ImageIcon("resources/right.png").getImage().getScaledInstance(LENGTHSCALE, LENGTHSCALE, 0);
	}
	

	public void setPose(double x, double y, double theta) {
		myMap.setPose(x, y, theta);
		this.repaint();
	}
	
	private void drawObstacles(Graphics g){
		for(int y = 0; y < myMap.getSizeY(); y++){
			for(int x = 0; x < myMap.getSizeX(); x++){
				if(myMap.isOccupied(x, y)){
					g.drawImage(imgFree, x * LENGTHSCALE, y * LENGTHSCALE, null);
				}
			}
		}
	}

	public void paint(Graphics g) {
		double angle = myMap.getThymioOrientation();
		double dx;
		double dy;
		double diffSensor = 20.0 * Math.PI / 180.0;
		double sensorVal;
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.BLACK);

		for (int i = 1; i < myMap.getSizeX(); i++){
			g.drawLine(LENGTHSCALE * i - 1, 0, LENGTHSCALE * i - 1, this.getHeight());
		}
		for (int i = 1; i < myMap.getSizeY(); i++){
			g.drawLine(0, LENGTHSCALE * i - 1, this.getWidth(), LENGTHSCALE * i - 1);
		}
		
		drawObstacles(g);
		
		dx = (myMap.getPosX() + 5.5 * Math.cos(angle)) / lengthX * LENGTHSCALE;
		dy = this.getHeight() - (myMap.getPosY() + 5.5 * Math.sin(angle)) / lengthX * LENGTHSCALE;		

		/*
		g.fillRect((int)(myMap.getPosX()/lengthX*LENGTHSCALE),
				this.getHeight() - 5 - (int)(myMap.getPosY()/lengthX*LENGTHSCALE), 5, 5);//was commented out
		*/
		g.setColor(Color.BLUE);

		g.drawLine((int) ((myMap.getPosX() - 5.5 * Math.cos(angle)) / lengthX * LENGTHSCALE),
				(int) (this.getHeight() - (myMap.getPosY() - 5.5 * Math.sin(angle)) / lengthX * LENGTHSCALE), (int) dx,
				(int) dy);

		g.setColor(Color.RED);

		for (int i = 0; i < 5; i++) {
			sensorVal = myIRData.getValue(i);
			if (sensorVal != Double.POSITIVE_INFINITY) {
				dx = (myMap.getPosX() + sensorVal * Math.cos(angle - (i - 2) * diffSensor)) / lengthX * LENGTHSCALE;
				dy = this.getHeight() - (myMap.getPosY() + sensorVal * Math.sin((angle - (i - 2) * diffSensor)))
						/ lengthX * LENGTHSCALE;
				g.drawLine((int) (myMap.getPosX() / lengthX * LENGTHSCALE),
						(int) (this.getHeight() - myMap.getPosY() / lengthX * LENGTHSCALE), (int) dx, (int) dy);
			}
		}
	}

	public double getPosX() {
		return myMap.getPosX();
	}

	public double getPosY() {
		return myMap.getPosY();
	}
	
	public void updateObstacle(int obstClass){
		//TODO: change image of obstacle currently seen
		switch(obstClass){
			case FRONTAL:  break;
			case ECKE: break;
			case KANTE: break;
			case LINKS: break;
			case RECHTS: break;
			case FREI: break;
		};
	}
}
