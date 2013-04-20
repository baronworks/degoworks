/*
 * Created on 28-Feb-2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.interfaces;

/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface OutputConstants {
	public static final int TYPE_LIGHT_SIDE = 0;
	public static final int TYPE_LIGHT_TOP_SINGLE = 1;
	public static final int TYPE_LIGHT_TOP_DUAL = 2;
	public static final int TYPE_MOTOR_MICRO = 3;
	public static final int TYPE_MOTOR_9V = 4;
	public static final int TYPE_MOTOR_9V_GEARED = 5;//
	public static final int TYPE_MOTOR_45V = 6;

	public static final int TYPE_SIREN = 7;
	public static final int TYPE_TRAIN = 8;
	public static final int TYPE_EMPTY = 9;

	public static final int TYPE_MOTOR_NAV_LIMIT = 10;//used for motors with limit/rotation range
	public static final int TYPE_MULTIPLE = 11;//used when more then one hooked up to single output

	public static final int MAX_POWER = 8;
	public static final int MAX_45V_MOTOR_POWER = 4;
	public static final int MIN_REVERSE_LIGHT_POWER = 7;//below 7 lights do not flash
	public static final int FLASH_LIGHT_POWER = 8;
	public static final int SPEED_FLASH_LIGHT_POWER = 7;//lights go crazy fast flash
	
	public static final String REVERSE_TOOLTIP = "Reverse";
}
