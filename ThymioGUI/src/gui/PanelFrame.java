package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dataanalysis.InfraRed;


public class PanelFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private ThymioInterface iface;
	private Container pane;
	private JTextArea console;
	private JPanel bottom, controlbar, buttons;
	private JLabel sign;
	private MapPanel mp;
	private JButton fwButton, bwButton, leftButton, rightButton, stopButton;
	
	public PanelFrame(ThymioInterface iface){
		this.iface = iface;
		
		pane = getContentPane();
		pane.setBackground(Color.BLACK);
		pane.setLayout(new BorderLayout(5,5));
		mp = new MapPanel(iface.getMap(), new InfraRed(), this);
		pane.add(mp, BorderLayout.LINE_START);
		
		initConsole();
		initControls();
		addListeners();
	}
	
	/**
	 * used for loggig actions on the screen
	 */
	private void initConsole(){
		console = new JTextArea(30, 10);
		console.setLineWrap(true);
		console.setEditable(false);
		console.setBackground(Color.DARK_GRAY);
		console.setForeground(Color.WHITE);
		console.setFont(new Font("Verdana", Font.BOLD, 12));

		JScrollPane scrollPane = new JScrollPane(console);	
		pane.add(scrollPane, BorderLayout.EAST);
	}

	/**
	 * Adds the different buttons in a new layout
	 */
	private void initControls(){
		bottom = new JPanel();
		bottom.setBackground(Color.LIGHT_GRAY);
		bottom.setLayout(new BorderLayout(10, 10));
		controlbar = new JPanel();
		controlbar.setBackground(Color.LIGHT_GRAY);
		
		buttons = new JPanel();
		buttons.setLayout(new BorderLayout(10, 10));
		buttons.setBackground(Color.LIGHT_GRAY);
		buttons.add(fwButton = new JButton(new ImageIcon("resources/button_fw.png")), BorderLayout.PAGE_START);
		buttons.add(bwButton = new JButton(new ImageIcon("resources/button_bw.png")), BorderLayout.PAGE_END);
		buttons.add(leftButton = new JButton(new ImageIcon("resources/button_left.png")), BorderLayout.LINE_START);
		buttons.add(rightButton = new JButton(new ImageIcon("resources/button_right.png")), BorderLayout.LINE_END);
		buttons.add(stopButton = new JButton(new ImageIcon("resources/button_stop.png")), BorderLayout.CENTER);
		controlbar.add(buttons);
		
		sign = new JLabel();
		bottom.add(controlbar, BorderLayout.WEST);
		bottom.add(sign, BorderLayout.EAST);
		pane.add(bottom, BorderLayout.PAGE_END);
	}
	
	/**
	 * Addes ActionListeners to all buttons, all calling the same method actionPerformed
	 */
	private void addListeners(){
		fwButton.addActionListener(this);
		bwButton.addActionListener(this);
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		stopButton.addActionListener(this);	
	}
	
	/**
	 * Adds line to the console
	 * @param line text to be added
	 */
	public void appendLine(String line){
		console.append(line + "\n");
	}
	
	public JButton getFwButton(){
		return this.fwButton;
	}
	
	public JButton getBwButton(){
		return this.bwButton;
	}
	
	public JButton getLeftButton(){
		return this.leftButton;
	}
	
	public JButton getRightButton(){
		return this.rightButton;
	}
	
	public JButton getStopButton(){
		return this.stopButton;
	}
	
	/**
	 * Updates the position of the Thymio on the map
	 */
	public void updatePosition(double posXmm, double posYmm, double theta) {
		mp.setPose(posXmm/10, posYmm/10, theta);
	}

	/**
	 * Updates the obstacle currently "seen" by the Thymio
	 * @param obstClass Class determined by thymioEvent of ThymioInterface.java
	 */
	public void updateObstacle(int obstClass) {
		mp.updateObstacle(obstClass);
	}

	/**
	 * Notifies ThymioInterface of occurred event
	 */
	public void actionPerformed(ActionEvent e) {
		iface.performAction(e);		
	}
	
	/**
	 * Draws the sign of the obstacle in the bottom left corner
	 * @param i - Icon to be drawn
	 */
	public void drawSign(ImageIcon i){
		sign.setIcon(i);
	}
	
}
