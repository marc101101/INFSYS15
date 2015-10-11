package control;

public class WorkingMemory {
	
	int lastObst = 6;
	int currentObst = 6;
	int action;
	ValueParser parser = new ValueParser();

	public WorkingMemory() {
	}

	public int getLastObst() {
		return lastObst;
	}

	public String getLastObstString() {
		return parser.getObstacle(lastObst); //FINISHED
	}

	public String getActionString() {
		return parser.getAction(action); //FINISHED
	}

	/**private String getClassFromID(int id) { //BRAUCH MA NED HAM DEN VALUEPARSER
		return "";
	}*/

	public String getCurrObstString() {
		return parser.getObstacle(currentObst); //FINISHED
	}

	public void setLastObst(int lastObst) {
		this.lastObst = lastObst;
	}

	public int getCurrObst() {
		return currentObst;
	}

	public void setCurrObst(int currObst) {
		this.currentObst = currObst;
	}

	public int getAction() { 
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

}
