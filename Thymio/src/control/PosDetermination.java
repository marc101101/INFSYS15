package control;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PosDetermination {
	
	private double[] probPos;
	private int[] states = {1,2,3,4,5,6};
	private int[] actions = {1,2,3,4,5,6};
	private ArrayList<Rule> rulesArray;
	private WorkingMemory wm;
	private int preRuleFired = -1;
	private FileWriter writer;

	public PosDetermination(double[] probsPos) {
		this.probPos = probPos;
		rulesArray = new ArrayList<Rule>();
		wm = new WorkingMemory();
		String timeStamp = new SimpleDateFormat("SSSSSSSS").format(System.currentTimeMillis());
		try {
			writer = new FileWriter("/home/pi/evalRules" + timeStamp + ".csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		setRules();
	}

	/**
	 * 	Erstellt alle möglichen vorstellbaren Regeln 6 x 6 x 5 = 180 Regelsätze
	 *  und fügt diese in das rulesArray ein
	 */
	private void setRules() { 
		Rule rule1 = new Rule (6,6,5);
		rulesArray.add(rule1);
		for(int stateOne : states){
			for(int stateTwo : states){
				for(int action : actions){
					if((stateOne != 6) && (stateTwo != 6)){
						Rule rule = new Rule(stateOne,stateTwo,action);
						rulesArray.add(rule);
					}
				}
			}
		}
	}

	/**
	 * 	Dient als input Methode - belommt von außen Wahrscheinlichkeiten für Hindernissarten
	 *  Setzt im Working Memory das letzte Hinderniss und das aktuell wahrscheinlichste 
	 *  Hinderniss neu.
	 *  Zudem wird hier die evaulate() Methode aufgerufen.
	 *  Zudem wird hier die momentan beste Regel bestimmt - durch den Aufruf der der Methode
	 *  chechRules();
	 *  Aufgrund dem Rückgabewert der checkRules wird dann entschieden welche Action im 
	 *  Working Memory gesetzt wird.
	 */
	public void updatePos(double[] probs) {
		wm.setLastObst(wm.getCurrObst());
		wm.setCurrObst(getValues(probs));
		
		printRules();
		
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
	
	public void printRules(){
		try {
			writer.append("EMPTY LINE");
			int index = 0;
			for(Rule rule : rulesArray){
				writer.append("RULE " + index + " :" + rule.lastObst + "," + rule.currObst + "," + rule.getWeight());
				index++;
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 	Evaluate Rule bestimmt wie sinnvoll die Anwendung der Regel war und 
	 *  gewichtet diese entsprechend hoch oder runter.
	 *  Sollte eine Regel abgewertet werden, werden gleichzeit alle anderen
	 *  Regeln zusätzlich höher gewichtet.
	 */
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
	
	/**
	 * 	Hier wird die Beste Regel ermittelt und deren Index in der rulesArray Liste
	 *  zurückgegeben.
	 *  Dazu wird aus dem rulesArray die ruleToFire gesucht, die die höchste Gewichtung 
	 *  besitzt.
	 */
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

	/**
	 * 	Match Rules bestimmt welche Regel am besten passt, indem das letzte und das aktuelle
	 *  Objekt aus dem Working Memory mit dem der Regel verglichen werden.
	 */
	private void matchRules() { 
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

	/**
	 * 	Dient als Output der Klasse und gibt den aktuellen Befehl aus dem Working Memory zurück.
	 */
	public String getResult() {
		return wm.getActionString();
	}
	
	/**
	 * 	Gibt den Index und somit das Objekt aus dem probs Array zurück, das die höchste Wahrscheinlichkeit
	 *  besitzt.
	 */
	private int getValues(double[] probs) {
		int index = 0;
		double currentMax = -1;
		for (int i = 0; i<probs.length;i++){
			if(currentMax < probs[i]){
				currentMax = probs[i];
				index = i;
			}
		}
		System.out.println("HighestValue: " + index+1);
		return index+1;
	}

}
