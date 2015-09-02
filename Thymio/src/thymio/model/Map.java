package thymio.model;

import java.util.ArrayList;
import java.util.Random;

import math.KalmanFilter;

import org.ejml.data.DenseMatrix64F;

public class Map {
	private int sizeX, sizeY; // number of elements in each direction 
							  // used to get a discrete model of the environment
	private int thymioX, thymioY; // coordinates of MapElement where Thymio is currently located on.
	private double posX, posY; // current position of Thymio in real units
	private double thymioTheta; // current orientation of Thymio in the global coordinate system

	private double estPosX, estPosY; // estimated current position of Thymio in real units
	private double estTheta; // estimated current orientation of Thymio in the global coordinate system

	private MapElement [][] element; // Array of MapElement representing the environment
	private double edgelength; // each element in this maps covers edgelength^2 square units.
	
	public static final int N = 20; // number of occupied elements
	public static final double LENGTH_EDGE_CM = 3.5;

	private KalmanFilter posEstimate;
	private DenseMatrix64F Q;
	private DenseMatrix64F P;
	
	public Map(int x, int y, double l) {
		edgelength = l;
		sizeX = x;
		sizeY = y;
		
		element = new MapElement[sizeX][sizeY];
		
		initMap();
		initFilter();
		initState();
	}
	
	private void initState() {
		posX = posY = 0.0;
		thymioTheta = 0.0;
		
		estPosX = estPosY = 0.0;
		estTheta = 0.0;
	}
	
	private void initFilter() {
		// process noise
		
		double [][] valQ = {{0.000005, 0, 0}, {0, 0.00001, 0}, {0, 0, 0.00000000034}};
		Q = new DenseMatrix64F(valQ);
		
		// initial state

		double [][] valP = {{0.000001, 0, 0},{0,  0.000001, 0}, {0, 0, 0.0002}};
		P = new DenseMatrix64F(valP);
		
		double [] state = {0, 0, 0};
		
		posEstimate = new KalmanFilter();
		posEstimate.configureProcessNoise(Q);
		posEstimate.setState(DenseMatrix64F.wrap(3, 1, state), P);
	}
	
	public double getEdgeLength() {
		return edgelength;
	}
	
	public void setPose(double x, double y, double theta) {
		posX = x;
		posY = y;
		thymioTheta = theta;
	}
	
	public void updatePose(double dFobs, double dRobs, double dFpred, double dRpred, double dt) {
		double [] delta = new double[3];
		
		// state transition

		double [][] valF = {{1, 0, Math.cos(estTheta)*dt}, {0, 1, Math.sin(estTheta)*dt}, {0, 0, 0}};
		DenseMatrix64F F = new DenseMatrix64F(valF);
		
		posEstimate.configureLinearModel(F);
		delta[0] = 0;
		delta[1] = 0;
		delta[2] = dFpred;
		
		DenseMatrix64F Gu = DenseMatrix64F.wrap(3, 1, delta);
		
		// observation model
		
		double [][] valH = {{0, 0, 1}};
		DenseMatrix64F H = new DenseMatrix64F(valH);
		
		// sensor noise
		
		double [][] valR = {{0.01}};
		DenseMatrix64F R = new DenseMatrix64F(valR);
		
		// sensor values
		
		double [] speed = {dFobs};
		
		posEstimate.predict(Gu);
		posEstimate.update(DenseMatrix64F.wrap(1, 1, speed), H, R);
		
		DenseMatrix64F estimState = posEstimate.getState();
		estPosX = estimState.get(0);
		estPosY = estimState.get(1);
		//estTheta = estimState.get(1);
	}

	public int getThymioX() {
		return thymioX;
	}

	
	public int getThymioY() {
		return thymioY;
	}
	
	public double getEstimPosX() {
		return estPosX;
	}
	
	public double getEstimPosY() {
		return estPosY;
	}
		
	public double getPosX() {
		return posX;
	}
	
	public double getPosY() {
		return posY;
	}
	
	public double getThymioOrientation() {
		return thymioTheta;
	}
	
	private void initMap() {
		Random r = new Random();
		ArrayList<Integer> occupiedElements = new ArrayList<Integer>();
		
		// initialize each element of the map
		
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				element[x][y] = new MapElement(x, y);
			}
		}
		
		/*
		// collect N distinct random numbers between 0 and the max number of MapElements in this Map
		
		while (occupiedElements.size() < N) {
			Integer pos = new Integer(r.nextInt(sizeX*sizeY));
			if (!occupiedElements.contains(pos)) occupiedElements.add(pos);
		}
		
		// find MapElement corresponding to each of the numbers and set its state to occupied
		
		for (int i = 0; i < N; i ++) {
			Integer pos = occupiedElements.get(i);
			int x = pos / sizeY;  // integer division by number of columns
			int y = pos % sizeX;  // rest of integer division by number of rows
			
			element[x][y].setOccupied();
		}
		*/
	}
	
	public void printMap() {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				MapElement e = element[x][y];
				
				System.out.print(e.isOccupied() ? "T" : "F");
				System.out.print("\t");
			}
			
			System.out.print("\n");
		}
	}

	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
}
