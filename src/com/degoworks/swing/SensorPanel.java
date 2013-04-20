/*
 * Created on 19-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.degoworks.interfaces.SensorConstants;
import dacta.DACTASensor;

/**
 * @author doc
 *
 * A panel for sensor output, used in the DACTA Example
 */
public class SensorPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private int sensorId;
	private JLabel valuedisplay;
	private DACTASensor sensor;	
	ImageIcon upIcon = makeIcon("Up16.gif");
	ImageIcon downIcon = makeIcon("Down16.gif");
	Font font = new Font("Arial", Font.BOLD, 10);
	
	
	public SensorPanel(int sensorId, DACTASensor sensor){
		this.sensorId = sensorId;	
		valuedisplay = new JLabel("Wait..");		
		Dimension size = new Dimension(42,25);
		this.setPreferredSize(size);		
		setSensor(sensor);	
	}
		
	public void setSensor(DACTASensor sensor){
		this.sensor = sensor;
		
		if(sensor != null){
			//updateValue(1023);
			//set up the layout based on the type
			EmptyBorder emptyBorder = null;
			valuedisplay.setToolTipText(sensor.getTooltip());
			switch(sensor.getType()) {
				case SensorConstants.SENSOR_TYPE_RAW: 
					emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder(5,1,0,1);
					//this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
					valuedisplay.setFont(font);
					this.add(valuedisplay);
					break;
				case SensorConstants.SENSOR_TYPE_TOUCH: 
					//emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder(1,1,1,1);
					//this.setLayout(new BorderLayout());
					valuedisplay.setIcon(upIcon);
					this.add(valuedisplay, BorderLayout.PAGE_START);
					break; 
				case SensorConstants.SENSOR_TYPE_TEMP:
					emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder(5,1,0,1);
					//this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
					valuedisplay.setFont(font);
					this.add(valuedisplay);
					break;
				case SensorConstants.SENSOR_TYPE_LIGHT: 
					emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder(5,1,0,1);
					//this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
					valuedisplay.setFont(font);
					this.add(valuedisplay);
					break;
				case SensorConstants.SENSOR_TYPE_ROT: 
					emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder(5,1,0,1);
					valuedisplay.setFont(font);
					this.add(valuedisplay);
					break;
			}
			//this.setBorder(BorderFactory.createCompoundBorder(
			//	BorderFactory.createTitledBorder("" + (sensorId+1)), 
			//	emptyBorder));
			this.setBorder(BorderFactory.createRaisedBevelBorder());
		}
		//update with whatever is available to remove the wait..
		updateValue();
	}
	
	public void updateValue(){
		if(sensor!=null){
			switch(sensor.getType()) {
			case SensorConstants.SENSOR_TYPE_RAW: 
				valuedisplay.setText("" + sensor.getRawValue());
				break;
			case SensorConstants.SENSOR_TYPE_TOUCH: 
				if(sensor.getB1()!=-1){//
					valuedisplay.setIcon(downIcon);								
 				}
 				else {
					valuedisplay.setIcon(upIcon);
 				}
				valuedisplay.setText("");
				break; 
			case SensorConstants.SENSOR_TYPE_TEMP:
				valuedisplay.setText(getTemp());
				break;
			case SensorConstants.SENSOR_TYPE_LIGHT: 
				valuedisplay.setText("L-" + sensor.getValue());
				break;
			case SensorConstants.SENSOR_TYPE_ROT: 
				//DecimalFormat myFormatter = new DecimalFormat("#0.0");
				//valuedisplay.setText(myFormatter.format(sensor.getValue()));
				valuedisplay.setText("" + sensor.getValue()/16);
				break;
			}
		}
		else {
			valuedisplay.setText("na");
		}		
	}
	
	protected String getTemp(){
		DecimalFormat myFormatter = new DecimalFormat("#0.0C");
		//System.out.println("rawTempValue: " + sensor.getRawValueWithThreshold());
		double tempF = (760 - sensor.getSensorValue()) / 4.4 + 32;
		double tempC = (tempF-32)*5/9;
		//System.out.println("tempC: " + tempC);
		return myFormatter.format(tempC);
	}
	
	protected ImageIcon makeIcon(String imageName) {
			
		//Look for the image.
		String imgLocation = "/toolbarButtonGraphics/navigation/" + imageName;
		URL imageURL = SensorPanel.class.getResource(imgLocation);
		ImageIcon icon = null;
		//Create and initialize the icon.
		if (imageURL != null) {                      //image found
			icon = new ImageIcon(imageURL);
		} else {                                     //no image found
			System.err.println("Resource not found: "
							   + imgLocation);
		}
		return icon;
	}
}
