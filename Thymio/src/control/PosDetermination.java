package control;

import java.util.ArrayList;

public class PosDetermination {
	
	private double[] probPos;
	private int[] states = {1,2,3,4,5,6};
	private int[] actions = {1,2,3,4,5,6};
	private ArrayList<Rule> rulesArray;
	private WorkingMemory wm;
	private int preRuleFired = -1;

	public PosDetermination(double[] probsPos) {
		this.probPos = probPos;
		rulesArray = new ArrayList<Rule>();
		wm = new WorkingMemory();
		setRules();
	}

	private void setRules() { //ERSTELLT ALLE MÖGLICHEN KOMBINATIONEN AUS REGELN UND BEFEHLEN 6 x 6 x 5 (evtl STOP raus)
		for(int stateOne : states){
			for(int stateTwo : states){
				for(int action : actions){
					Rule rule = new Rule(stateOne,stateTwo,action);
					rulesArray.add(rule);
				}
			}
		}
	}

	public void updatePos(double[] probs) { //INPUT METHOD
		wm.setLastObst(wm.getCurrObst());
		wm.setCurrObst(getValues(probs));
		
		evaluateRule();
		
		int bestRule = checkRules();
		
		if(bestRule != -1){
			wm.setAction(rulesArray.get(bestRule).getAction());
			preRuleFired = bestRule;
		}
		else{
			wm.setAction(-1);
			preRuleFired = -1;
		}
	}
		
	private void evaluateRule(){
		//N++;
		if(wm.getCurrObstString().equals("FREI")){
			//NSuccess++;
			if(preRuleFired != -1){
				rulesArray.get(preRuleFired).upperWeight();
			}
		}
		else{
			if(preRuleFired != -1){
				rulesArray.get(preRuleFired).lowerWeight();
			}
			for(int i = 0;i<rulesArray.size();i++){
				if(rulesArray.get(i).getMatched()){
					rulesArray.get(i).upperWeight();
				}
			}
		}
	}
	
	private int checkRules() {
		int ruleToFire = -1;
		matchRules();
		int index = 0;
		
		for(Rule rule : rulesArray){
			if(rule.getMatched()){
				if(ruleToFire == -1){
					ruleToFire = index;
				}
				else{
					if (rule.getWeight() > rulesArray.get(ruleToFire).getWeight()){
						ruleToFire = index;
					}
				}
			}
			index++;
		}
		return ruleToFire;
	}

	private void matchRules() { //11 Regel_Matching
		for(Rule rule : rulesArray){
			if(rule.getLastObst() == wm.getLastObst()){
				if(rule.getCurrObst() == wm.getCurrObst()){
					rule.setMatched(true);
				}
			}
			else{
				rule.setMatched(false);
			}
		}
	}

	public String getResult() {
		return wm.getActionString();
	}

	private int getValues(double[] probs) {
		int index = 0;
		double currentMax = -1;
		for (int i = 0; i<probs.length;i++){
			if(currentMax < probs[i]){
				currentMax = probs[i];
				index = i;
			}
		}
		return index+1;
	}

}
