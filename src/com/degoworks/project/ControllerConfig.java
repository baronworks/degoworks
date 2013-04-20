/*
 * Created on 24-Feb-2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ControllerConfig {
	protected int id;
	protected String name;
	protected String defaultPort;
	
	protected SensorConfig[] sensorConfigs;
	protected OutputConfig[] outputConfigs;
	
	public ControllerConfig(int id, String name, String defaultPort){
		this.id = id;
		this.name = name;
		this.defaultPort = defaultPort;
	}
	
	public int getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public String getDefaultPort(){
		return defaultPort;
	}
	
	public SensorConfig[] getSensorConfigs(){
		return sensorConfigs;
	}
	
	public SensorConfig getSensorConfig(int sensorId){
		return sensorConfigs[sensorId];
	}
	
	public OutputConfig[] getOutputConfigs(){
		return outputConfigs;
	}
	
	public OutputConfig getOutputConfig(int outputId){
		return outputConfigs[outputId];
	}

}
