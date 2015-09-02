package control;

public class Rule {
	
	public boolean matched;
	public int weight;
	public int lastObst, currObst, action;
	
	public Rule(int lastObst, int currObst, int action){
		this.lastObst = lastObst;
		this.currObst = currObst;
		this.action = action;
	}
	
	public void setWeight(int givenWeight){
		givenWeight = weight;
	}
	
	public void setMatched(boolean givenMatch){
		givenMatch = matched;
	}
	
	public void setLastObst(int givenLastObst){
		givenLastObst = lastObst;
	}
	
	public void setCurrObst(int givenCurrObst){
		givenCurrObst = currObst;
	}
}
