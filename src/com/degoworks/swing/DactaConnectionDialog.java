/*
 * Created on 17-Feb-2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.beans.*; //property change stuff
import java.util.ArrayList;
import java.awt.event.*;

import dacta.DACTA70909Controller;

import com.degoworks.ProjectManager;
import com.degoworks.rxtx.PortDetection;

/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/* 1.4 example used by DialogDemo.java. */
class DactaConnectionDialog extends JDialog
				   implements ActionListener,
							  PropertyChangeListener {
							  	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ProjectManager projectManager;
	private DactaControllerGUI controllerGUI;

	private JOptionPane optionPane;

	private String btnString1 = "Connect";
	private String btnString2 = "Cancel";
	
	private JComboBox portComboList;

	private String portName;
	private int configId;


	/** Creates the reusable dialog. */
	public DactaConnectionDialog(DegoGUI parentFrame, DactaControllerGUI dcGUI) {
		super(parentFrame, true);
		projectManager = DegoGUI.getProjectManager();
		ArrayList<String> ignoreSerialPorts = projectManager.getApplication().getIgnoreSerialPorts();
		controllerGUI = dcGUI;
		PortDetection portDetective = new PortDetection(ignoreSerialPorts);
		portComboList = new JComboBox(portDetective.getAvailableSerialPortNames() );		
		portComboList.setSelectedItem(dcGUI.getPort());		
		setTitle("DACTA Connection Error");
		
		//Create an array of the text and components to be displayed.
		String msgString1 = dcGUI.getName() + " lost connection on port: " + 
			dcGUI.getPort() + "!\nMake sure red Stop light is on before trying to reconnect.";
		String msgString2 = "Available ports listed:";
		Object[] jItems = {msgString1, msgString2, portComboList };

		//Create an array specifying the number of dialog buttons
		//and their text.
		Object[] options = {btnString1, btnString2};

		//Create the JOptionPane.
		optionPane = new JOptionPane(jItems,
									JOptionPane.ERROR_MESSAGE,
									JOptionPane.YES_NO_OPTION,
									null,
									options,
									options[0]);
	
		//Make this dialog display it.
		setContentPane(optionPane);

		//Handle window closing correctly.
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window,
				 * we're going to change the JOptionPane's
				 * value property.
				 */
				optionPane.setValue(new Integer(
										JOptionPane.CLOSED_OPTION));
			}
		});

		//Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
	}



	/** This method handles events for the portComboList. */
	public void actionPerformed(ActionEvent e) {
		//optionPane.setValue(btnString1);
	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
		 && (e.getSource() == optionPane)
		 && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
			 JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();
			
			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				//ignore reset
				return;
			}
			
			//Reset the JOptionPane's value.
			//If you don't do this, then if the user
			//presses the same button next time, no
			//property change event will be fired.
			optionPane.setValue(
					JOptionPane.UNINITIALIZED_VALUE);

			if (value.equals("Connect")) {				
				portName = (String)portComboList.getSelectedItem();		
				int status = projectManager.reconnect(controllerGUI, portName);
				if(status==0){
					controllerGUI.enableAll(true);
					dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							DactaConnectionDialog.this,
										"Sorry, unable to connect to port: " +portName + ".\n"
										+ "error message: " + DACTA70909Controller.errorString(status),
										"Try again",
										JOptionPane.ERROR_MESSAGE);
				}
				

       
			} else { //user closed dialog or clicked cancel
				dispose();
			}
		}
	}

}