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
import java.util.HashMap;

//import com.degoworks.comm.RemoteControl;

public class Project {
	private String id;
	//private String className;
	protected String description;
	
	protected HashMap globals;
	protected HashMap<String, ProgramConfig> programConfigs;
	//protected RemoteControl remote;
	
	protected ControllerConfig[] controllerConfigs;
	
	public Project(String id){
		this.id = id;
		//this.className = className;
	}
	
	public String getId(){
		return id;
	}
	
	public String getDescription(){
		return description;
	}
	
	public HashMap<String, ProgramConfig> getProgramConfigs(){
		return programConfigs;
	}
	/*
	public RemoteControl getRemoteControl(){
		return remote;
	}
	*/
	public ControllerConfig[] getControllerConfigs(){
		return controllerConfigs;
	}
	
	public ControllerConfig getControllerConfig(int controllerId){
		ControllerConfig cf = null;
		if(controllerConfigs!=null && controllerConfigs[controllerId]!=null){
			cf = controllerConfigs[controllerId];
		}
		return cf;
	}
}
