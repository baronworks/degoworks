/*
 * Created on Mar 9, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.interfaces;

public interface EventConstants {
	public static final int i_SOURCE_DC = 0;//DactaController
	public static final int i_SOURCE_RCX = 1;//mindstorms RCX
	public static final int i_SOURCE_NXT = 2;//mindstorms NXT
	public static final int i_SOURCE_PROGRAM = 3;//program generated event	
	public static final int i_SOURCE_PROJECT = 4;//project generated event
	public static final int i_SOURCE_USER = 5;//user generated event
	
	public static final String s_SOURCE_DC = "DC";//DactaController
	public static final String s_SOURCE_RCX = "RCS";//mindstorms RCX
	public static final String s_SOURCE_NXT = "NXT";//mindstorms NXT
	public static final String s_SOURCE_PROGRAM = "Program";//program generated event
	public static final String s_SOURCE_PROJECT = "Project";//project generated event
	public static final String s_SOURCE_USER = "User";//user generated event
	
	public static final int i_SOURCE_EVENT = 0;
	public static final int i_SENSOR_EVENT = 1;
	public static final int i_OUTPUT_EVENT = 2;
	
	public static final String s_SOURCE_EVENT = "Source";
	public static final String s_SENSOR_EVENT = "Sensor";
	public static final String s_OUTPUT_EVENT = "Output";
	
	public static final int PROJECT_LOAD_EVENT = 0;
	public static final int PROJECT_LOADED_EVENT = 1;
	public static final int PROJECT_CLOSING_EVENT = 2;
	public static final int PROJECT_CLOSED_EVENT = 3;
	public static final int PROJECT_RUNNING_EVENT = 4;
	public static final int PROJECT_PAUSED_EVENT = 5;	
	
}
