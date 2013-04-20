/*
 * Created on 28-Feb-2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.interfaces;


/**
 * @author doc
 * Constants for Sensor methods.
 * @see josx.platform.rcx.Sensor#setTypeAndMode
 * 
 * based on josx.platform.rcx.SensorConstants
 */


public interface SensorConstants {
	// Begin from josx.platform.rcx.SensorConstants
    public static final int SENSOR_TYPE_RAW    = 0;
	public static final int SENSOR_TYPE_TOUCH  = 1;
	public static final int SENSOR_TYPE_TEMP   = 2;
	public static final int SENSOR_TYPE_LIGHT  = 3;
	public static final int SENSOR_TYPE_ROT    = 4;
	
	public static final int SENSOR_MODE_RAW    = 0x00;
	public static final int SENSOR_MODE_BOOL   = 0x20;
	public static final int SENSOR_MODE_EDGE   = 0x40;
	public static final int SENSOR_MODE_PULSE  = 0x60;
	public static final int SENSOR_MODE_PCT    = 0x80;
	public static final int SENSOR_MODE_DEGC   = 0xa0;
	public static final int SENSOR_MODE_DEGF   = 0xc0;
	public static final int SENSOR_MODE_ANGLE  = 0xe0;
	
	public static final int RAW_VALUE          = 0;
	public static final int CANONICAL_VALUE    = 1;
	public static final int BOOLEAN_VALUE      = 2;
	// End from josx.platform.rcx.SensorConstants

	//	default threshold values to determine when to update raw values
	// will in turn determine when stateChange occurs with sensorType values 
	// reduces computer processing
	public static final int THRESHOLD_TOUCH  	= 0;
	//for displaying degrees in celsius
	public static final int THRESHOLD_DEGC  	= 20;
	//for degrees in fahrenheit
	public static final int THRESHOLD_DEGF  	= 0;
	//light
	public static final int THRESHOLD_LIGHT  	= 20;
	//for rotation
	public static final int THRESHOLD_ROT  		= 30;


	public static final int NO_CHANGE_EVENT = 0;//just in case
	public static final int RAW_VALUE_INCREASE_EVENT = 1;
	public static final int RAW_VALUE_DECREASE_EVENT = 2;
	public static final int TOUCH_ON_PRESS_EVENT = 3;
	public static final int TOUCH_ON_RELEASE_EVENT = 4;
	public static final int TOUCH_PRESS_AND_RELEASE_EVENT = 5;
	public static final int TEMP_INCREASE_EVENT = 6;
	public static final int TEMP_DECREASE_EVENT = 7;
	public static final int ROTATE_CW_EVENT = 8;
	public static final int ROTATE_CCW_EVENT = 9;
	public static final int LIGHT_INCREASE_EVENT = 10;
	public static final int LIGHT_DECREASE_EVENT = 11;
}
