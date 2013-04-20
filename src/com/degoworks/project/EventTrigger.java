/*
 * Created on Mar 9, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

import com.degoworks.interfaces.SensorConstants;

public class EventTrigger implements SensorConstants {
	
	private int iEventType;
	private String sEventType;
	private Action[] actions;
	
	public EventTrigger(int type, String eventType){
		this.iEventType = type;
		this.sEventType = eventType;
	}
	
	public void setActions(Action[] actions){
		this.actions = actions;
	}
	
	public Action[] getActions(){
		return this.actions;
	}
}
