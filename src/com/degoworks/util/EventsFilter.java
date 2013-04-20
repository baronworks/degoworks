/*
 * Created on Mar 8, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.util;

import com.degoworks.interfaces.EventConstants;

public class EventsFilter implements EventConstants{

	protected boolean on;
	protected boolean project;	
	protected boolean gui;
	
	protected boolean[] notices;
	//for i number of programs
	protected boolean []programs;
	//for i number of dc controllers
	protected boolean[] dc;//new boolean[i];
	protected boolean[][] dc_sensors;//new boolean[i][8];
	protected boolean[][] dc_outputs;//new boolean[i][8];
	//for i number of rcx controllers
	protected boolean[] rcx;//new boolean[i];
	protected boolean[][] rcx_sensors;//new boolean[i][3];
	protected boolean[][] rcx_outputs;//new boolean[i][3];
	//for i number of nxt controllers
	protected boolean[] nxt;//new boolean[i];
	protected boolean[][] nxt_sensors;//new boolean[i][4];
	protected boolean[][] nxt_outputs;//new boolean[i][3];
	
	
	public EventsFilter(){
		
	}
	
	/**
	 * String s is message sent to System.out or System.err 
	 * which has been redirected using JTextAreaOutStream (private class in EventsPanel)
	 *  
	 * @param s
	 * @return true if message should be filtered out
	 */
	public boolean filterEventMessage(String s){
		return false;
	}
	
	/**
	 * String s is message sent to System.out or System.err 
	 * which has been redirected using JTextAreaOutStream (private class in EventsPanel)
	 *
	 * @param s 
	 * s = EVENT_SOURCE | SOURCE_ID | EVENT_TYPE | EVENT_ID | message
	 * styleClass = EVENT_SOURCE + "_" + SOURCE_ID + "_" + EVENT_TYPE
	 * or
	 * s = EVENT_SOURCE | SOURCE_ID | message
	 * styleClass = EVENT_SOURCE + "_" + SOURCE_ID
	 * or
	 * s = EVENT_SOURCE | message
	 * styleClass = EVENT_SOURCE
	 * or
	 * s = message
	 * styleClass = "out"
	 * 
	 * @return NULL if message should be filtered out
	 * @return String[] messageAndStyle if should display eventMessage
	 * messageAndStyle[0] = message 
	 * messageAndStyle[1] = styleClass
	 * 
	 */
	public String[] filterEventMessageAndStyle(String s){
		boolean filteredOut = false;
		if(filteredOut){
			return null;
		}
		//setup default
		String[] messageAndStyle = new String[2];		
		String[] allSplitUp = s.split("\\|");
		switch(allSplitUp.length){
			case(5):
				messageAndStyle[0] = allSplitUp[4];
				messageAndStyle[1] = allSplitUp[0] + "_" + allSplitUp[1] + "_" + allSplitUp[2];
				break;
			case(3):
				messageAndStyle[0] = allSplitUp[2];
				messageAndStyle[1] = allSplitUp[0] + "_" + allSplitUp[1];
				break;
			case(2):
				messageAndStyle[0] = allSplitUp[1];
				messageAndStyle[1] = allSplitUp[0];
				break;
			default:
				messageAndStyle[0] = s;
				messageAndStyle[1] = "out";
				break;
		}
		return messageAndStyle;
	}
	
	/**
	 * EVENT_SOURCE | SOURCE_TYPE | SOURCE_ID | 
	 */
	public void filterSensorEvent(){
		
	}
}
