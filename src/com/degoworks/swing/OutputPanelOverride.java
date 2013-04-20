/*
 * Created on 19-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import dacta.*;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.degoworks.interfaces.OutputConstants;

import java.net.URL;

/*
 * Created on May 6, 2005
 *
 * A panel with buttons for use in the DACTAExample.
 * 
 */

public class OutputPanelOverride extends JPanel implements OutputConstants {

	private static final long serialVersionUID = 1L;
	DACTA70909Controller thecontroller;
	protected boolean onOffState = false;
	protected boolean leftRightState = true; // left = false;
	protected int powerValue = 0;//0 is off
	protected JButton onOffButton;
	protected JButton leftRightButton;
	private OutputPanel[] outputPanels;
	JSlider powerslider;
	JPanel buttonPanel;
	
	ImageIcon forwardIcon = makeIcon("/toolbarButtonGraphics/navigation/Forward16.gif");
	ImageIcon backIcon = makeIcon("/toolbarButtonGraphics/navigation/Back16.gif");
	ImageIcon onIcon = makeIcon("/toolbarButtonGraphics/general/TipOfTheDay16.gif");
	ImageIcon offIcon = makeIcon("/toolbarButtonGraphics/general/Stop16.gif");
	
	Font font = new Font("Arial", Font.BOLD, 11);
	
	public OutputPanelOverride(DACTA70909Controller thecontroller, OutputPanel[] outputPanels){
		this.thecontroller = thecontroller;
		this.outputPanels = outputPanels;
		addWidgets();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createCompoundBorder(
				  BorderFactory.createTitledBorder("Output Override"), 
				  BorderFactory.createEmptyBorder(2,1,0,1)));
		//Add the button and slider panels to the main panel.
		
		this.add(buttonPanel);
		this.add(powerslider);
		Dimension size = new Dimension(120,100);
		this.setSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);

	}
	
	public void reset(DACTA70909Controller thecontroller, OutputPanel[] outputPanels){
		this.thecontroller = thecontroller;
		this.outputPanels = outputPanels;
	}
	
	
	/*
	* set up the widgets.
	*/
	private void addWidgets() {
		buttonPanel = new JPanel(new GridLayout(1, 2));
		Dimension size = new Dimension(20,05);
		onOffButton = new JButton("");
		onOffButton.setIcon(offIcon);
		onOffButton.setSize(size);
		onOffButton.setToolTipText("Feel the power");
		//onOffButton.setMaximumSize(size);
		//onOffButton.setMinimumSize(size);
		onOffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				turnMeOnOff();
			}
		});
		leftRightButton = new JButton("");
		leftRightButton.setIcon(forwardIcon);
		leftRightButton.setSize(size);
		leftRightButton.setToolTipText(REVERSE_TOOLTIP);
		//leftRightButton.setMaximumSize(size);
		//leftRightButton.setMinimumSize(size);
		leftRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				turnMeLeftRight();
			}
		});
		
		buttonPanel.add(onOffButton);
		buttonPanel.add(leftRightButton);
		
		powerslider = new JSlider(0,8,8);
		powerslider.setPreferredSize(new Dimension(60,35));
		powerslider.setMinorTickSpacing(1);
		powerslider.setPaintTicks(true);
		powerslider.setSnapToTicks(true);
		//powerslider.setLabelTable(powerslider.createStandardLabels(1));
		//powerslider.setPaintLabels(true);
		powerslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				sliderchanged();
			}
		});
		
	}
	/**
	 * 
	 */
	protected void sliderchanged() {
		int value = powerslider.getValue();
		if (value==0){
			onOffButton.setIcon(offIcon);
			onOffState = false;
			powerValue = value;
		} else {
			//if it was off we 
			if(powerValue==0 && !onOffState){
				powerValue = value;
				turnMeOnOff();
			}
			onOffButton.setIcon(onIcon);
			onOffState = true;			
		}
		for(int i=0; i<8; i++){
			thecontroller.setPower(i, value);
		}
	}
	private void turnMeOnOff(){
		onOffState = !onOffState;
		for(int i=0; i<8; i++){
			// do not override the leftRightState
			boolean previousRightState = thecontroller.getOutput(i).getLeftRightState();
			thecontroller.setOutput(i, onOffState, previousRightState);
		}	
		if(onOffState){
			onOffButton.setIcon(onIcon);
		} else {
			onOffButton.setIcon(offIcon);	
		}
	}
	private void turnMeLeftRight(){
		leftRightState = !leftRightState;
		for(int i=0; i<8; i++){
			// do not override the onOffState
			boolean previousOnOffState = thecontroller.getOutput(i).getOnOffState();
			thecontroller.setOutput(i, previousOnOffState, leftRightState);
		}
		if(leftRightState){
			leftRightButton.setIcon(forwardIcon);
		} else {
			leftRightButton.setIcon(backIcon);
		}
	}
	
		
	protected ImageIcon makeIcon(String imagePath) {			
		//Look for the image.
		URL imageURL = SensorPanel.class.getResource(imagePath);
		ImageIcon icon = null;
		//Create and initialize the icon.
		if (imageURL != null) {                      //image found
			icon = new ImageIcon(imageURL);
		} else {                                     //no image found
			System.err.println("Resource not found: "
							   + imagePath);
		}
		return icon;
	}

}
