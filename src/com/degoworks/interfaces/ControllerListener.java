/*
 * Created on 20-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.interfaces;

import java.util.EventListener;

import dacta.DACTA70909Controller;
import dacta.DACTAOutput;
import dacta.SensorEvent;

/**
 * @author doc
 *
 * not sure at this time if implementing classes will need to synchronize the methods 
 * if we get some deadlocks then this is the place to start?
 */
public interface ControllerListener extends EventListener {
	/**
	   * Called when the canonical value of the sensor changes.
	   * @param aSource The sensor that generated the event.
	   */
	public void sensorEvent(SensorEvent sensorEvent);
	  
	  /**
	   * 
	   * @param controller
	   * @param output
	   * @param aOldValue 0 -> 8
	   * @param errorCode 0 is connection made
	   * 
	   */
	public void outputChanged(DACTA70909Controller controller, DACTAOutput output, int aOldPowerValue, int errorCode);

	  
	  /**
	   * @param int controllerId
	   * @param int status
	   */
	public void controllerStatus(int controllerId, int status);
	  
	  /**
	   * @return String name
	   */
	public String getName();

}
