/*
 * Created on Mar 30, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

public class ProjectInfo {
	protected String projectFile;
	protected String name;
	protected String description;
	protected Boolean runOnLoad;
	
	public ProjectInfo(String name, String projectFile, Boolean runOnLoad){		
		this.name = name;
		this.projectFile = projectFile;
		this.runOnLoad = runOnLoad;
	}	
	
}
