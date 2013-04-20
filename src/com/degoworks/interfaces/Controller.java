/*
 * Created on Mar 9, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.interfaces;

import com.degoworks.project.SensorConfig;
import com.degoworks.project.OutputConfig;

public interface Controller {	
	//id is int array location
	public int getId();
	//sourceType denotes DC, RCX, NXT
	public int getSourceType();
	public String getName();
	public int getStatusCode();
	
	public void initSensors(SensorConfig[] sensorConfigs);
	public void initOutputs(OutputConfig[] outputConfigs);
	
}
