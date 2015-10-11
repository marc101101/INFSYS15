package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private ThymioInterface iface;
	private Container pane;
	private JTextArea console;
	private JPanel controlbar, buttons;
	private MapPanel map;
	private JButton fwButton, bwButton, leftButton, rightButton, stopButton;
	
	public PanelFrame(ThymioInterface iface){
		this.iface = iface;
		pane = getContentPane();
		pane.setBackground(Color.BLACK);
		pane.setLayout(new BorderLayout(5,5));
		
		initConsole();
		initControls();
		addListeners();
	}
	
	private void initConsole(){
		console = new JTextArea(30, 10);
		console.setLineWrap(true);
		console.setEditable(false);
		console.setBackground(Color.DARK_GRAY);
		console.setForeground(Color.WHITE);
		console.setFont(new Font("Verdana", Font.BOLD, 12));

		JScrollPane scrollPane = new JScrollPane(console);	
		pane.add(scrollPane, BorderLayout.LINE_START);
	}

	private void initControls(){
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
		pane.add(controlbar, BorderLayout.PAGE_END);
	}
	
	private void addListeners(){
		fwButton.addActionListener(this);
		bwButton.addActionListener(this);
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		
	}
	
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

	public void actionPerformed(ActionEvent e) {
		iface.performAction(e);		
	}
	
	public void setMap(MapPanel mp){
		map = mp;
		pane.add(mp);
	}

}