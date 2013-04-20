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
 */
public interface ProjectListener extends EventListener {
	
	public void projectEvent(int event);

	

}
