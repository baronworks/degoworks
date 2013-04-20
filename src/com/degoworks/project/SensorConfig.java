/*
 * Created on 27-Feb-2008
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

//import com.degoworks.interfaces.SensorConstants2;
import com.degoworks.interfaces.SensorConstants;

public class SensorConfig implements SensorConstants {
	protected int id;
	protected int controllerId;
	protected int type;
	protected int mode;
	protected boolean disabled = false;
	protected String tooltip;
	protected String description;
	protected HashMap<String, EventTrigger> eventTriggers;
	
	protected SensorConfig(int id, String type, String mode){
		this.id = id;
		setType(type);
		setMode(mode);
	}
	
	public int getId(){
		return id;
	}
	public int getType(){
		return type;
	}
	public int getMode(){
		return mode;
	}
	
	public String getTooltip(){
		return tooltip;
	}
	
	public EventTrigger getEventTrigger(String eventType){
		EventTrigger eventTrigger = null;
		if(eventTriggers.containsKey(eventType)){
			eventTrigger = eventTriggers.get(eventType);
		}
		return eventTrigger;
	}
	
	/**
	* 	public static final int SENSOR_TYPE_RAW    = 0;
 	*	public static final int SENSOR_TYPE_TOUCH  = 1;
 	*	public static final int SENSOR_TYPE_TEMP   = 2;
 	*	public static final int SENSOR_TYPE_LIGHT  = 3;
 	*	public static final int SENSOR_TYPE_ROT    = 4;
	 * @param String type 
	 * values from file.project.xml are in readable form and need to be converted
	 * 
	 */
	private void setType(String type){
		if(type.equals("SENSOR_TYPE_RAW")){
			this.type = SENSOR_TYPE_RAW;
		}
		else if(type.equals("SENSOR_TYPE_TOUCH")){
			this.type = SENSOR_TYPE_TOUCH;
		}
		else if(type.equals("SENSOR_TYPE_TEMP")){
			this.type = SENSOR_TYPE_TEMP;
		}
		else if(type.equals("SENSOR_TYPE_LIGHT")){
			this.type = SENSOR_TYPE_LIGHT;
		}
		else if(type.equals("SENSOR_TYPE_ROT")){
			this.type = SENSOR_TYPE_ROT;
		} 
		else {
			this.type = SENSOR_TYPE_RAW;
		}
	}
	
	/**
	*	public static final int SENSOR_MODE_RAW    = 0x00;
	*	public static final int SENSOR_MODE_BOOL   = 0x20;
	*	public static final int SENSOR_MODE_EDGE   = 0x40;
	*	public static final int SENSOR_MODE_PULSE  = 0x60;
	*	public static final int SENSOR_MODE_PCT    = 0x80;
	*	public static final int SENSOR_MODE_DEGC   = 0xa0;
	*	public static final int SENSOR_MODE_DEGF   = 0xc0;
	*	public static final int SENSOR_MODE_ANGLE  = 0xe0;
	* @param String mode 
	* values from file.project.xml are in readable form and need to be converted
	*/
	private void setMode(String mode){
		if(mode.equals("SENSOR_MODE_RAW")){
			this.mode = SENSOR_MODE_RAW;
		}
		else if(mode.equals("SENSOR_MODE_BOOL")){
			this.mode = SENSOR_MODE_BOOL;
		}
		else if(mode.equals("SENSOR_MODE_EDGE")){
			this.mode = SENSOR_MODE_EDGE;
		}
		else if(mode.equals("SENSOR_MODE_PULSE")){
			this.mode = SENSOR_MODE_PULSE;
		}
		else if(mode.equals("SENSOR_MODE_PCT")){
			this.mode = SENSOR_MODE_PCT;
		}
		else if(mode.equals("SENSOR_MODE_DEGC")){
			this.mode = SENSOR_MODE_DEGC;
		}
		else if(mode.equals("SENSOR_MODE_DEGF")){
			this.mode = SENSOR_MODE_DEGF;
		}
		else if(mode.equals("SENSOR_MODE_ANGLE")){
			this.mode = SENSOR_MODE_ANGLE;
		} 
		else {
			this.mode = SENSOR_MODE_RAW;
		}
	}
}
