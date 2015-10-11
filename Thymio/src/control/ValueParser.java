package control;

public class ValueParser {
	
	static String getAction (int action){
		String actState = "";
		switch(action){
			case 1: actState =  "VOR"; break;
			case 2: actState =  "ZURÜCK"; break;
			case 3: actState =  "LINKS"; break;
			case 4: actState =  "RECHTS"; break;
			case 5: actState =  "STOP"; break;
			case -1: actState =  "unKnown"; break;
		}
		return actState;
	}
	
	static String getObstacle (int obst){
		String actState = "";
		switch(obst){
			case 1: actState =  "FRONTAL"; break;
			case 2: actState =  "ECKE"; break;
			case 3: actState =  "KANTE"; break;
			case 4: actState =  "LINKS"; break;
			case 5: actState =  "RECHTS"; break;
			case 6: actState =  "FREI"; break;
		}
		return actState;
	}

}
