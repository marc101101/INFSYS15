package map;

import java.util.ArrayList;
import java.util.Random;

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
		
	public Map(int x, int y, double l) {
		edgelength = l;
		sizeX = x;
		sizeY = y;
		
		element = new MapElement[sizeX][sizeY];
		
		initMap();
	}
	
	public double getEdgeLength() {
		return edgelength;
	}
	
	public void setPose(double x, double y, double theta) {
		posX = x;
		posY = y;
		thymioTheta = theta;
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
				System.out.print(e.onBeam() ? "B" : "-");
				System.out.print("\t");
			}
			
			System.out.print("\n");
		}
	}

	public Path followBeam( int x1, int y1, int x2, int y2 ) {
		Path p = new Path();
		int x = x1, y = y1;
		
	    int w = x2 - x;
	    int h = y2 - y;
	    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
	    if (w<0) dx1 = -1; else if (w>0) dx1 = 1;
	    if (h<0) dy1 = -1; else if (h>0) dy1 = 1;
	    if (w<0) dx2 = -1; else if (w>0) dx2 = 1;
	    int longest = Math.abs(w);
	    int shortest = Math.abs(h);
	    if (!(longest>shortest)) {
	        longest = Math.abs(h);
	        shortest = Math.abs(w);
	        if (h<0) dy2 = -1; else if (h>0) dy2 = 1;
	        dx2 = 0;            
	    }
	    int numerator = longest >> 1;
	    for (int i=0;i<=longest;i++) {
			p.add(new Coordinate(x,y));
			numerator += shortest;
	        if (!(numerator<longest)) {
	            numerator -= longest;
	            x += dx1;
	            y += dy1;
	        } else {
	            x += dx2;
	            y += dy2;
	        }
	    }
	    
	    return p;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public boolean isOnBeam(int x, int y) {
		return element[x][y].onBeam();
	}
}
