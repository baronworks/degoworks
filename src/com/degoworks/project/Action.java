/*
 * Created on Mar 9, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

public class Action {
	protected String programAndMethod;
	
	public Action(String programAndMethod){
		this.programAndMethod = programAndMethod;
	}
	
	public String getProgramAndMethod(){
		return this.programAndMethod;
	}
}
