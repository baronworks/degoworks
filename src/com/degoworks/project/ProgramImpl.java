/*
 * Created on Mar 6, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;


import java.lang.Thread;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import dacta.DACTA70909Controller;


public class ProgramImpl implements Program {
	
	private String id;
	private String name;
	private String description;
	private volatile Thread running;	
	private boolean threadSuspended;
	private static Project project;
	private Map<String, Comparable> globals = Collections.synchronizedMap(new HashMap<String, Comparable>());
	private Field[] fields;
	private Method[] methods;
	private Constructor[] constructors;
	protected DACTA70909Controller[] dcControllers;
	
	private Class<? extends ProgramImpl> programClass;
	
	private void classReflection(){
		Object programObj = this;
		programClass = this.getClass();
		System.out.println(programClass.getName());
		int mods;
		fields = programClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (!Modifier.isPrivate(fields[i].getModifiers())
			 && !Modifier.isProtected(fields[i].getModifiers()))
			{		
				System.out.println("\tF: " + fields[i].getName() + " - " + fields[i].getType());
				try{
					Object value = fields[i].get(programObj);
					System.out.println("\t value: " + value.toString());
				}
				catch(IllegalAccessException iae){
					System.err.println("IllegalAccessException: " + iae.getMessage());
				}
			}
		}
		constructors = programClass.getConstructors();
		for (int j = 0; j < constructors.length; j++) {
			Constructor<?> constructor = constructors[j];
			System.out.println("\tC: " + constructor);
			
		}
		methods = programClass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!Modifier.isPrivate(methods[i].getModifiers())
			 && !Modifier.isProtected(methods[i].getModifiers()))
			{
				System.out.println("\tM: " + methods[i]);
				//System.out.println("\tM: " + methods[i].getParameterTypes().toString());
//				try{
//					Object[] parameters = {};
//					methods[i].invoke(programObj, parameters);
//				}
//				catch(InvocationTargetException ite){
//					System.err.println("InvocationTargetException: " + ite.getMessage());
//				}
//				catch(IllegalAccessException iae){
//					System.err.println("IllegalAccessException: " + iae.getMessage());
//				}
				//invokeMethod(methods[i].getName());
				
			}
		}

	}
	
	public void invokeMethod(String methodName){
		if(methodName!=null && !methodName.equals("")){
			Object programObj = this;
			Class[] argTypes = {};
			Object[] parameters = {};
			//System.err.println("Invoke method: " + methodName + " on program: " + programClass.getName() );
			try{
				Method method = programClass.getMethod(methodName, argTypes);
				method.invoke(programObj, parameters);
			}
			catch(IllegalAccessException iae){
				System.err.println("IllegalAccessException: " + iae.getMessage());
			}
			catch(NoSuchMethodException nsme){
				System.err.println("NoSuchMethodException: " + nsme.getMessage());
			}
			catch(InvocationTargetException ite){
				System.err.println("Invoke method: " + methodName + " on program: " + programClass.getName() );
				System.err.println("InvocationTargetException: " + ite.getMessage());
			}
		}
	}

	/* ############# Program implementation ############# */
	
	public int count(String global){
		int count = 0;
		if(globals.get(global)!=null){			
			count = ((Integer)globals.get(global)).intValue()+1;
			globals.put(global, new Integer(count));
		}
		else{
			globals.put(global, new Integer(1));
		}
		System.out.println(global + "-global count: " + count);
		return count;
	}
	
	public boolean toggle(String global){
		boolean toggleState = false;
		if(globals.get(global)!=null){			
			toggleState = !((Boolean)globals.get(global)).booleanValue();
			globals.put(global, new Boolean(toggleState));
		}
		else{
			globals.put(global, new Boolean(!toggleState));
		}
		System.out.println(global + "-global toggleState: " + toggleState);
		return toggleState;
	}
	
	public void doWhile(String global){
		
	}
	
	public void execute(String routine){
		
	}
	
	public void init()	
	{	
		classReflection();
		threadSuspended=false;
		running=new Thread(this);
		running.start();
		System.out.println("Program started: " + this.getClass().toString());
	}
	
	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		while(running==thisThread)
		{
			try {
				Thread.sleep(1);
				//Thread.currentThread().sleep(10);
				synchronized(this) {
                    while (threadSuspended)
                        wait();
                }

			} catch (InterruptedException e) {}
		}
	}

	public synchronized void pause()
	{
		threadSuspended = true;
		notify();
	}
	
	public synchronized void resume()
	{
		threadSuspended = false;
		notify();
	}
	
	public synchronized void stop()
	{
		running = null;
		notify();
	}

	public void setProject(Project project){
		ProgramImpl.project = project;
	}
	
	/**
	 * fields made available by reflection 
	 * @return Method[]
	 */
	public Field[] getFields(){
		return fields;
	}
	/**
	 * methods made available by reflection 
	 * @return Method[]
	 */
	public Method[] getMethods(){
		return methods;
	}
	/**
	 * Constructors made available by reflection 
	 * @return Method[]
	 */
	public Constructor[] getConstructors(){
		return constructors;
	}
	
	public void setDACTA70909Controllers(DACTA70909Controller[] dcControllers){
		this.dcControllers = dcControllers;
	}
	
	
//	/* ############# DACTAControllerListener implementation ############# */
//	/**
//	   * Called when the canonical value of the sensor changes.
//	   * @param aSource The sensor that generated the event.
//	   */
//	public void sensorEvent(SensorEvent sensorEvent){
//		//see if we need to trigger event
//		ControllerConfig cf = project.controllerConfigs[sensorEvent.getSourceId()];
//		SensorConfig sf = cf.sensorConfigs[sensorEvent.getSensorId()];	
//		if(sf.eventTriggers.containsKey(sensorEvent.getEventTypeAsString())){
//			EventTrigger et = (EventTrigger)sf.eventTriggers.get(sensorEvent.getEventTypeAsString());
//			Action[] actions = et.getActions();
//			for(int i=0; i<actions.length; i++){
//				invokeMethod(actions[i].getMethodName());				
//			}
//		}
//	}
//	  
//	  /**
//	   * 
//	   * @param controller
//	   * @param output
//	   * @param aOldValue 0 -> 8
//	   * @param errorCode 0 is connection made
//	   * 
//	   */
//	public void outputChanged(DACTA70909Controller controller, DACTAOutput output, int aOldPowerValue, int errorCode){
//		//System.out.println("ProgramImpl.outputChanged: " + controller.getName() + " with output no: " + output.getId());
//	}
//
//	  
//	  /**
//	   * @param int controllerId
//	   * @param int status
//	   */
//	public void controllerStatus(int controllerId, int status){
//		
//	}
//	
	
}
