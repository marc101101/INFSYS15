package main;

import gui.ThymioInterface;

public class MainController {
	
	private ThymioInterface gui;

	public MainController() {
			gui = new ThymioInterface();		
	}

	public static void main(String[] args) {
		MainController mc = new MainController();
	}
	
}
