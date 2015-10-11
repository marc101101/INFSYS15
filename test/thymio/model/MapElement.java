package thymio.model;

public class MapElement {
	private int posX;				// position in the map
	private int posY;
	
	private boolean occupied; 		// element is known in advance to be occupied
	private double probOccupied;	// estimation of the state of this element
									// from observations
		
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	public double getProbOccupied() {
		return probOccupied;
	}
	
	public MapElement(int posX, int posY) {
		super();
		this.posX = posX;
		this.posY = posY;
	}
	
	public MapElement(int posX, int posY, boolean occupied) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.occupied = occupied;
	}
	
	public void setOccupied() {
		occupied = true;
	}
}
