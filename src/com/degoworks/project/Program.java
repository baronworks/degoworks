/*
 * Created on Mar 6, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;

import java.lang.Runnable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dacta.DACTA70909Controller;



public interface Program extends Runnable{	
	
	/* create and start the Program thread */
	public void init();
	
	public void setId(String id);
	
	public String getId();
	
	public void setName(String name);
	
	public String getName();
	
	public void setDescription(String description);
	
	public String getDescription();
	
	/* pause the thread, implementing classes should synchronize */
	public void pause();
	
	/* resume the thread, implementing classes should synchronize */
	public void resume();
	
	/* stop and kill the thread, implementing classes should synchronize */
	public void stop();
	
	public void setProject(Project project);
	
	public int count(String global);
	
	public boolean toggle(String global);
	
	public void doWhile(String global);
	
	public void execute(String routine);
	
	/**
	 * fields made available by reflection 
	 * @return Method[]
	 */
	public Field[] getFields();
	/**
	 * methods made available by reflection 
	 * @return Method[]
	 */
	public Method[] getMethods();
	/**
	 * Constructors made available by reflection 
	 * @return Method[]
	 */
	public Constructor[] getConstructors();
	
	public void invokeMethod(String methodName);
	
	public void setDACTA70909Controllers(DACTA70909Controller[] dcControllers);
	
	public void callProgramListeners(String programId, int programEvent);
	
}
