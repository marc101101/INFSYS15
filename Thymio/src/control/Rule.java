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
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	public void setMatched(boolean matched){
		this.matched = matched;
	}
	
	public void setLastObst(int lastObst){
		this.lastObst = lastObst;
	}
	
	public void setCurrObst(int givenCurrObst){
		givenCurrObst = currObst;
	}
	
	public void setAction(int action){
		this.action = action;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public boolean getMatched(){
		return matched;
	}
	
	public int getLastObst(){
		return lastObst;
	}
	
	public int getCurrObst(){
		return currObst;
	}
	
	public int getAction(){
		return action;
	}
	
	public void upperWeight(){
		weight++;
	}
	
	public void lowerWeight(){
		weight--;
	}
	
}
