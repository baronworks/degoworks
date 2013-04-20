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


import com.degoworks.interfaces.OutputConstants;

public class OutputConfig implements OutputConstants {
	protected int id;
	protected int controllerId;
	protected int type;
	protected boolean disabled = false;
	protected String tooltip;
	protected String description;

	protected OutputConfig(int controllerId, int id, String type){
		this.controllerId = controllerId;
		this.id = id;
		setType(type);
	}
	
	public int getId(){
		return id;
	}
	
	public int getType(){
		return type;
	}
	
	public String getTooltip(){
		return tooltip;
	}
	
	public boolean getDisabled(){
		return disabled;
	}

	/**
	 * 	public static final int TYPE_LIGHT_SIDE = 0;
	 *	public static final int TYPE_LIGHT_TOP_SINGLE = 1;
	 *	public static final int TYPE_LIGHT_TOP_DUAL = 2;
	 *	public static final int TYPE_MOTOR_MICRO = 3;
	 *	public static final int TYPE_MOTOR_9V = 4;
	 *	public static final int TYPE_MOTOR_9V_GEARED = 5;//
	 *	public static final int TYPE_MOTOR_45V = 6;
	 *
	 *	public static final int TYPE_SIREN = 7;
	 *	public static final int TYPE_TRAIN = 8;
	 *	public static final int TYPE_EMPTY = 9;
	 *
	 *	public static final int TYPE_MOTOR_NAV_LIMIT = 10;//used for motors with limit/rotation range
	 *	public static final int TYPE_MULTIPLE = 11;//used when more then one hooked up to single output
	 * @param type
	 */
	private void setType(String type){
		if(type.equals("TYPE_LIGHT_SIDE")){
			this.type = TYPE_LIGHT_SIDE;
		}
		else if(type.equals("TYPE_LIGHT_TOP_SINGLE")){
			this.type = TYPE_LIGHT_TOP_SINGLE;
		}
		else if(type.equals("TYPE_LIGHT_TOP_DUAL")){
			this.type = TYPE_LIGHT_TOP_DUAL;
		}
		else if(type.equals("TYPE_MOTOR_MICRO")){
			this.type = TYPE_MOTOR_MICRO;
		}
		else if(type.equals("TYPE_MOTOR_9V")){
			this.type = TYPE_MOTOR_9V;
		} 
		else if(type.equals("TYPE_MOTOR_9V_GEARED")){
			this.type = TYPE_MOTOR_9V_GEARED;
		}
		else if(type.equals("TYPE_MOTOR_45V")){
			this.type = TYPE_MOTOR_45V;
		}
		else if(type.equals("TYPE_SIREN")){
			this.type = TYPE_SIREN;
		}
		else if(type.equals("TYPE_TRAIN")){
			this.type = TYPE_TRAIN;
		}
		else if(type.equals("TYPE_EMPTY")){
			this.type = TYPE_EMPTY;
		}
		else if(type.equals("TYPE_MOTOR_NAV_LIMIT")){
			this.type = TYPE_MOTOR_NAV_LIMIT;
		}
		else if(type.equals("TYPE_MULTIPLE")){
			this.type = TYPE_MULTIPLE;
		}
		else {
			this.type = TYPE_EMPTY;
		}
	}
}
