/*
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

import java.util.ArrayList;

import com.degoworks.util.KeyValue;

/**
 * @author doc
 *
 */


public class Application {
	private String id;
	private String name;
	private String description;
	private String defaultProjectFile;
	
	private ArrayList<String> ignoreSerialPorts;
	private ProjectInfo[] projectInfos;
	
	public Application(String id, String name, String defaultProjectFile){
		this.id = id;
		this.name = name;
		this.defaultProjectFile = defaultProjectFile;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDefaultProjectFile(){
		return defaultProjectFile;
	}
	
	public void setDefaultProjectFile(String defaultProjectFile){
		this.defaultProjectFile = defaultProjectFile;
	}
	
	public ArrayList<String> getIgnoreSerialPorts(){
		return ignoreSerialPorts;
	}
	
	public void setIgnoreSerialPorts(ArrayList<String> ignoreSerialPorts){
		this.ignoreSerialPorts = ignoreSerialPorts;
	}
	
	public void setProjectInfo(ProjectInfo[] projectInfos){
		this.projectInfos = projectInfos;
	}
	
	
	public KeyValue[] getProjectOptions(){
		KeyValue[] options = new KeyValue[projectInfos.length];
		for(int i=0; i<projectInfos.length; i++){			
			options[i] = new KeyValue(projectInfos[i].name, projectInfos[i].projectFile);
		}
		return options;
	}
	
	public String[] getProjectFiles(){
		String[] options = new String[projectInfos.length];
		for(int i=0; i<projectInfos.length; i++){			
			options[i] = projectInfos[i].projectFile;
		}
		return options;
	}
}
