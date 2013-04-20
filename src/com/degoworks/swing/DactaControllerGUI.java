/*
 * Created on 18-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

import javax.swing.JInternalFrame;
import dacta.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import com.degoworks.interfaces.ControllerListener;
import com.degoworks.interfaces.OutputConstants;
import com.degoworks.util.*;



/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

//import java.awt.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/* Used by InternalFrameDemo.java. */
public class DactaControllerGUI extends JInternalFrame implements ActionListener, ControllerListener, OutputConstants {
	private static final long serialVersionUID = -6354758846823194333L;
	private int id;
	protected boolean readOnly = false;
	private DACTA70909Controller theController;
	private DegoGUI parentFrame;
	private DactaConnectionDialog dcDialog;
	private JMenuItem connectMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem powerOnMenuItem;
	private JMenuItem powerOffMenuItem;
	private JCheckBoxMenuItem cbReadOnlyMenuItem;
	protected OutputPanelOverride outputOverride;	
	protected SensorPanel[] sensorPanels = new SensorPanel[8];
	protected OutputPanel[] outputPanels = new OutputPanel[8];
	
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;


	public DactaControllerGUI(DACTA70909Controller theController, DegoGUI parentFrame) {
		super(theController.getName() + " -- Port: " + theController.getPort(), 
			  true, //resizable
			  true, //closable
			  false, //maximizable
			  true);//iconifiable
		++openFrameCount;
		this.id = theController.getId();
		this.theController = theController;
		this.theController.addControllerListener(this);
		this.parentFrame = parentFrame;	
		
//		...Create the GUI and put it in the window...
		JPanel inputsPanel = new JPanel(new GridLayout(2,4));
		inputsPanel.setBorder(BorderFactory.createCompoundBorder(
			  BorderFactory.createTitledBorder("Sensors"), 
			  BorderFactory.createEmptyBorder(1,1,0,1)));
			  
		JPanel outputsPanel = new JPanel(new GridLayout(2,4));
		outputsPanel.setBorder(BorderFactory.createCompoundBorder(
					  BorderFactory.createTitledBorder("Outputs"), 
					  BorderFactory.createEmptyBorder(1,1,0,1)));
	
		Dimension size = new Dimension(120,100);
		inputsPanel.setSize(size);
		inputsPanel.setMaximumSize(size);
		
		//now create all the input and outputs
		for(int i=0; i<8; i++){
			sensorPanels[i] = new SensorPanel(i, theController.getSensor(i));
			inputsPanel.add(sensorPanels[i]);
			outputPanels[i] = new OutputPanel(i, theController);
			outputsPanel.add(outputPanels[i]);
		}
		
		

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createEmptyBorder(2,1,0,1));
		leftPanel.add(inputsPanel, BorderLayout.NORTH);
		
		JLabel statusLabel = new JLabel("TODO");
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Status"), 
					BorderFactory.createEmptyBorder(2,1,0,1)));
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		
		outputOverride = new OutputPanelOverride(theController, outputPanels);
		leftPanel.add(outputOverride, BorderLayout.SOUTH);
		
		
		JPanel controllerPane = new JPanel();
		controllerPane.add(leftPanel, BorderLayout.WEST);
		controllerPane.add(outputsPanel, BorderLayout.EAST);
		
		this.getContentPane().add(controllerPane);
		this.setJMenuBar(createMenuBar());
		this.pack();
		this.setVisible(true);
		//...Then set the window size or call pack...
		int width = 630;
		int height = 200;
		this.setSize(width, height);		
		//Set the window's location.
		int x = xOffset + 500;// put it to the right of the console
		int y = yOffset;
		if(openFrameCount > 1){
			if(openFrameCount % 2 != 0){
				// up the y by height * openFrameCount/2
				y +=  height * openFrameCount/2;
			}
			else {
				// up the x by width
				x += width;
			}
			
		}
		setLocation(x, y);
	}
	
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu connectionMenu = new JMenu("Connection");	
		
		connectMenuItem = new JMenuItem("Connect");
		connectMenuItem.setActionCommand("connectDialog");
		connectMenuItem.addActionListener(this);
		connectMenuItem.setEnabled(false);
		connectionMenu.add(connectMenuItem);
	  	
	  	disconnectMenuItem = new JMenuItem("Disconnect");	
	  	disconnectMenuItem.setActionCommand("disconnect");
	  	disconnectMenuItem.addActionListener(this);
	  	connectionMenu.add(disconnectMenuItem);
	  	
	  	menuBar.add(connectionMenu);
	  	
	  	cbReadOnlyMenuItem = new JCheckBoxMenuItem("Read Only");
	  	cbReadOnlyMenuItem.setActionCommand("read_only");
	  	cbReadOnlyMenuItem.addActionListener(this);
	  	menuBar.add(cbReadOnlyMenuItem);	  

		
		return menuBar;
	}
	
	/*
	 * used when reconnecting to the com port
	 * need to reset the controller, sensors and outputs
	 * 
	 */
	public void setController(DACTA70909Controller controller){
		this.theController = controller;
		this.theController.addControllerListener(this);
		for(int i=0; i<8; i++){
			sensorPanels[i].setSensor(theController.getSensor(i));
			outputPanels[i].setOutput(i, theController);
		}
		outputOverride.reset(controller, outputPanels);
		setTitle(theController.getName() + " -- Port: " + controller.getPort());
	}
	
	public int getId(){
		return id;
	}
	
	
	public void sensorEvent(SensorEvent sensorEvent){
		if(sensorPanels[sensorEvent.getSensorId()]!=null){
			sensorPanels[sensorEvent.getSensorId()].updateValue();
		}		
	}
	
	public void outputChanged(DACTA70909Controller controller, DACTAOutput output, int aOldValue, int errorCode){
		if(outputPanels[output.getId()]!=null){
			outputPanels[output.getId()].updatePanel();
		}
	}
	
	
	/*
	 * 
	 * enableAll sets all buttons including the outputOverride.onOffButton
	 */
	protected void enableAll(boolean enable){
		outputOverride.onOffButton.setEnabled(enable);
		outputOverride.leftRightButton.setEnabled(enable);
		outputOverride.powerslider.setEnabled(enable);
		for(int i=0; i<8; i++){
			outputPanels[i].onOffButton.setEnabled(enable);
			outputPanels[i].leftRightButton.setEnabled(enable);
			outputPanels[i].powerslider.setEnabled(enable);
		}
	}
	
	/*
	 * 
	 * readOnly sets all buttons except the outputOverride.onOffButton
	 */
	protected void readOnly(boolean readOnly){
		// leave the onOff on always
		outputOverride.onOffButton.setEnabled(true);
		outputOverride.leftRightButton.setEnabled(!readOnly);
		outputOverride.powerslider.setEnabled(!readOnly);
		for(int i=0; i<8; i++){
			outputPanels[i].onOffButton.setEnabled(!readOnly);
			outputPanels[i].leftRightButton.setEnabled(!readOnly);
			outputPanels[i].powerslider.setEnabled(!readOnly);
		}
	}
	
	//React to menu selections.
	public void actionPerformed(ActionEvent e) {
		if ("connectDialog".equals(e.getActionCommand())) {
			showDactaConnectionDialog();
		} else if ("disconnect".equals(e.getActionCommand())) {			
			// set status to anything but 0
			// -11 is disabled
			controllerStatus(theController.getId(), -11);
			setTitle(theController.getName() + " -- Port: disconnected");
			connectMenuItem.setEnabled(true);
			disconnectMenuItem.setEnabled(false);
		} else if ("connect".equals(e.getActionCommand())) {
			controllerStatus(theController.getId(), -11);
			setTitle(theController.getName() + " -- Port: " + theController.getPort());
			connectMenuItem.setEnabled(false);
			disconnectMenuItem.setEnabled(true);
		} else if ("read_only".equals(e.getActionCommand())) {
			boolean on = ( (JCheckBoxMenuItem) e.getSource() ).getState();
			readOnly(on);
			//disableControlsMenuItem.setEnabled(false);				
		}
	}
		
	/**
	* @param int controllerId
	* @param int status
	* if status !=0 will disable all buttons, setVisible(true), setSelected(true)
	*/
	public void controllerStatus(int controllerId, int status){
		//TODO create panel to show message
		String message = theController.getName() + " on port " + 
			theController.getPort() + " status updated: " + DACTA70909Controller.errorString(status);
		System.out.println(message);
		//if(status==-5){
		if(status!=0){
			enableAll(false);

			this.setVisible(true);	
			try {
				this.setSelected(true);
			} catch (java.beans.PropertyVetoException e) {}	
			
			theController.closeConnection();
			// - 11 is disabled which is a user event
			if(status !=- 11){
				String errorMessage = theController.getName() + " lost connection on port " + theController.getPort();
				JOptionPane.showMessageDialog(this,
					errorMessage,
					"Lost Connection!",
					JOptionPane.ERROR_MESSAGE);
			}			
		}
	}
	
	private Object delayConnectionDialog(){
		try {	
			Thread.sleep(5000);			
			showDactaConnectionDialog();
		}
		catch (InterruptedException e) {
			return "Interrupted"; 
		}
		return "All Done"; 
	}
	
		
	protected void showDactaConnectionDialog(){
		System.out.println("showDactaConnectionDialog");
		dcDialog = new DactaConnectionDialog(parentFrame, this);
		dcDialog.pack();
		dcDialog.setLocationRelativeTo(this);
		dcDialog.setVisible(true);		
	}
	
	/* Wrapper methods for the DACTA70909Controller */
	public String getName(){
		return theController.getName();
	}
	
	public String getPort(){
		return theController.getPort();
	}	
	
	
	
}