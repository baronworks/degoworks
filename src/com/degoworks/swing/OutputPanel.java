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

public class OutputPanel extends JPanel implements OutputConstants{
	private static final long serialVersionUID = 1L;
	private DACTA70909Controller thecontroller;
	private int outputId;
	private DACTAOutput output;
	private boolean onOffState = false;
	private boolean leftRightState = true; // left = false;
	private int powerValue = 0;//0 is off
	protected JButton onOffButton;
	protected JButton leftRightButton;
	protected JSlider powerslider;
	JPanel buttonPanel;
	Font font = new Font("Arial", Font.BOLD, 11);
	
	ImageIcon forwardIcon = makeIcon("/toolbarButtonGraphics/navigation/Forward16.gif");
	ImageIcon backIcon = makeIcon("/toolbarButtonGraphics/navigation/Back16.gif");
	ImageIcon onIcon = makeIcon("/toolbarButtonGraphics/general/TipOfTheDay16.gif");
	ImageIcon offIcon = makeIcon("/toolbarButtonGraphics/general/Stop16.gif");
	
	public OutputPanel(int outputId, DACTA70909Controller thecontroller){
		this.thecontroller = thecontroller;
		this.outputId = outputId;
		this.output = thecontroller.getOutput(outputId);
		addWidgets();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		

		//Add the button and slider panels to the main panel.
		
		this.add(buttonPanel);
		this.add(powerslider);
		Dimension size = new Dimension(80,80);
		this.setSize(size);
		//this.setMaximumSize(size);
		//this.setMinimumSize(size);

	}
	
	
	/*
	* set up the widgets.
	*/
	private void addWidgets() {
		buttonPanel = new JPanel(new GridLayout(1,2));
		Dimension size = new Dimension(20,20);
		onOffButton = new JButton();
		onOffButton.setIcon(offIcon);
		onOffButton.setText("");
		onOffButton.setToolTipText(this.output.getTooltip());
		onOffButton.setSize(size);
		//onOffButton.setMaximumSize(size);
		//onOffButton.setMinimumSize(size);
		onOffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				turnMeOnOff();
			}
		});
		leftRightButton = new JButton();
		leftRightButton.setIcon(forwardIcon);
		leftRightButton.setText("");
		leftRightButton.setToolTipText(REVERSE_TOOLTIP);
		leftRightButton.setSize(size);
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
		powerslider.setPreferredSize(new Dimension(50,35));
		powerslider.setMinorTickSpacing(1);
		powerslider.setPaintTicks(true);
		powerslider.setSnapToTicks(true);
		//powerslider.setLabelTable(powerslider.createStandardLabels(1));
		//powerslider.setPaintLabels(true);
		powerslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				sliderChanged();
			}
		});
		
	}
	
	
	private void sliderChanged() {
		int value = powerslider.getValue();
		if (value==0){
			onOffButton.setIcon(offIcon);
			onOffState = false;
			powerValue = value;
		} else {
			//if it was off we 
			if(powerValue==0 && !onOffState){
				powerValue = value;
			//	turnMeOnOff();
			}			
		}
		thecontroller.setPower(outputId, value);
	}
	
	private void turnMeOnOff(){
		onOffState = !onOffState;
		//updateOnOff();
		//System.out.println("turnMeOnOff: onOffState: " + onOffState);
		thecontroller.setOutput(outputId, onOffState, leftRightState);		
	}
		
	public void setOutput(int outputId, DACTA70909Controller thecontroller){
		this.thecontroller = thecontroller;
		this.outputId = outputId;
		this.output = thecontroller.getOutput(outputId);
	}
	
	public void updateOnOff(){
		//System.out.println("updateOnOff: onOffState: " + onOffState);
		//System.out.println("updateOnOff: output.getOnOffState(): " + output.getOnOffState());
		//compare button State with output State
		//if(this.onOffState!=output.getOnOffState()){
			this.onOffState = output.getOnOffState();
			if(this.onOffState){
				onOffButton.setIcon(onIcon);
			} else {
				onOffButton.setIcon(offIcon);	
			}	
	}
	
	
	
	private void turnMeLeftRight(){
		leftRightState = !leftRightState;
		//updateLeftRight();
		thecontroller.setOutput(outputId, onOffState, leftRightState);			
	}
	
	public void updateLeftRight(){
		this.leftRightState = output.getLeftRightState();
		if(this.leftRightState){
			leftRightButton.setIcon(forwardIcon);
		} else {
			leftRightButton.setIcon(backIcon);		
		}
	}
	
	public void updatePowerSlider(){
		//compare slider State with output State
		if(this.powerValue!=output.getPower()){
			powerslider.setValue(output.getPower());
		}		
	}
	
	public void updatePanel(){
		updateOnOff();
		updateLeftRight();
		updatePowerSlider();
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
