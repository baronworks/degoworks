/*
 * Created on 3-Jan-2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks;

/**
 * @author doc
 * Singleton design pattern
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import dacta.*;
import com.degoworks.rxtx.PortDetection;
import com.degoworks.swing.DactaControllerGUI;
//import com.degoworks.comm.RemoteControl;
//import com.degoworks.comm.RemoteControlListener;
//import com.degoworks.comm.TowerPort;
import com.degoworks.interfaces.ControllerListener;

import com.degoworks.project.Action;
import com.degoworks.project.Application;
import com.degoworks.project.ControllerConfig;
import com.degoworks.project.DomProjectParser;
import com.degoworks.project.EventTrigger;
import com.degoworks.project.InvalidProjectConfigException;
import com.degoworks.project.ProgramConfig;
import com.degoworks.project.Program;
import com.degoworks.project.ProgramFactory;
import com.degoworks.project.ProgramImpl;
import com.degoworks.project.Project;
import com.degoworks.project.SensorConfig;

import com.degoworks.util.EventsFilter;
import com.degoworks.util.KeyValue;

//import java.util.ArrayList;
//import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;

//public class DegoManager implements ControllerListener, RemoteControlListener {
public class ProjectManager implements ControllerListener {	
	private static ProjectManager _instance;
	
	private static boolean pauseEvents = false;
	private static boolean allConnectionsGo = false;

	private static int iNumControllers = 0;

	private static DACTA70909Controller[] dcControllers;
	private static ControllerConfig[] controllerConfigs;
	//order in which to access controllers if sequencing
	private static int[] dcOrder;
	
	//private static RemoteControl remote;
	//private static TowerPort towerPort;
	
	
	
	//sytem.out and system.err slow things down a shit load
	public static boolean stdOut = false;
	public static boolean stdErr = true;
	public static EventsFilter filterOut;
	public static EventsFilter filterErr;
	
	private static Project project;
	private static Application app;
	private static HashMap<String, Program> programs = null;
	private static boolean allSetUp = false;
	
	//should be read from projects.xml
	protected String projectFile = "projects/light_me_up.project.xml";
	
	//private Program program;
	private ProjectManager(Application app)
	{
		ProjectManager.app = app;
		this.projectFile = app.getDefaultProjectFile();
		System.out.println("ProjectManager load " + projectFile);
		DomProjectParser dpp = new DomProjectParser();
		project = dpp.parseProject(projectFile);	

		try{
			HashMap<String, ProgramConfig> programConfigs = project.getProgramConfigs();
			
			programs = new HashMap<String, Program>();
			Iterator it = programConfigs.keySet().iterator();
			while (it.hasNext()) {
				String key = (String)it.next();
				ProgramConfig programConfig = programConfigs.get(key);
				Program program = ProgramFactory.getProgram(programConfig);					
				program.init();
				programs.put(key, program);
			}

			setupControllers(project);
			it = programConfigs.keySet().iterator();
			while (it.hasNext()) {
				String key = (String)it.next();
				programs.get(key).setDACTA70909Controllers(dcControllers);
			}
			/*
			if(project.getRemoteControl()!=null){
				System.out.println("Tower ");
				remote = project.getRemoteControl();
				//give RemoteControlListener reference as well
				towerPort = new TowerPort("USB", this);
			}
			*/
			allSetUp = true;
		}
		catch(InvalidProjectConfigException ipce){
			System.err.println("DegoManager - Unable to set up project\n error: " + ipce.getMessage());
		}
		/*
		catch(IOException ioe){
			System.err.println("DegoManager - unable to open tower: " + ioe.getMessage());
		}*/		
	}
	

	
	public void addListenerToControllers(ControllerListener dcListener){
		for(int i=0; i<dcControllers.length; i++){
			dcControllers[i].addControllerListener(dcListener);
		}
	}
	public static void main(String[] args) {
		String projectFile = "projects/light_me_up.project.xml";
		//getInstance(projectFile);		
	}

	public static ProjectManager getInstance(Application app)
	{
		if(_instance == null)
			synchronized(ProjectManager.class) {				
				_instance = new ProjectManager(app);	
				//		it's ok, we can call this constructor
			}			
				
		return _instance;	
	}	
	
	public static DACTA70909Controller[] getControllers(){
		return dcControllers;
	}
	
	protected static synchronized void destroyInstance()
	{
		_instance = null;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
		//that'll teach 'em
  	}
	

	/**
	* will populate arrays controllers and controllerGUIs using controllerNames and controllerPorts
	* also populate initMessages to populate eventViewer with initialization status of each controller
	* 
	* also set boolean allConnectionsGo = true if all connections were made and everything is initialized.
	*/	
	private synchronized void setupControllers(Project project){		
		PortDetection portDetective = new PortDetection(app.getIgnoreSerialPorts());
		portDetective.listSerialPorts();
		int errorCheck = 0;				
		controllerConfigs = project.getControllerConfigs();
		iNumControllers = controllerConfigs.length;
		dcControllers = new DACTA70909Controller[iNumControllers];
		for(int i=0; i<iNumControllers; i++){
			DACTA70909Controller controller = new DACTA70909Controller(controllerConfigs[i]);
			controller.init(controllerConfigs[i].getDefaultPort());
			controller.addControllerListener(this);
			dcControllers[i] = controller;
			errorCheck += controller.getStatusCode();
		}		
		if(errorCheck==0){
			allConnectionsGo = true;
		}
	}
	
	
	public KeyValue[] controllerConfigOptions(){
		KeyValue[] options = new KeyValue[iNumControllers];
		for(int i=0; i<iNumControllers; i++){
			
			options[i] = new KeyValue(controllerConfigs[i].getName(), new Integer(controllerConfigs[i].getId()));
		}
		return options;
	}
	
	/**
	 * reinitializes theController connection after creating a new DACTA70909Controller object
	 * 
	 * @param Stringport
	 * @return int status
	 */
	public int reconnect(DactaControllerGUI controllerGUI, String port){
		int controllerId = controllerGUI.getId();
		System.out.println("Reinit controller as " + dcControllers[controllerId].getName() + " on port: " + port);
		int status = -10;
		// see about changing the config at the same time		
		ControllerConfig controllerConfig = project.getControllerConfig(controllerId);	
		DACTA70909Controller controller = new DACTA70909Controller(controllerConfig);
		status = controller.init(port);
		controllerGUI.setController(controller);
		controller.addControllerListener(this);
		dcControllers[controllerId] = controller;		
		//System.out.println("Reinit status: " + status + " with errorString: " + DACTA70909Controller.errorString(status));
		return status;
	}
	
	public void sensorEvent(SensorEvent sensorEvent){
		//see if we need to trigger event
		//System.out.println("DegoManager sensorEvent: " + sensorEvent.getEventTypeAsString());
		ControllerConfig cf = project.getControllerConfig(sensorEvent.getSourceId());
		SensorConfig sf = cf.getSensorConfig(sensorEvent.getSensorId());	
		EventTrigger et = sf.getEventTrigger(sensorEvent.getEventTypeAsString());
		if(et != null && allSetUp){
			Action[] actions = et.getActions();
			for(int i=0; i<actions.length; i++){
				String[] event = getProgramAndMethod(actions[i].getProgramAndMethod());
				//System.out.println("DegoManager sensorEvent: " + actions[i].getProgramAndMethod());
				if(event!=null){
					if(programs.get(event[0])!=null){
						programs.get(event[0]).invokeMethod(event[1]);
					}
					else {
						System.err.println("Program " + programs.get(event[0]).getName() + " not loaded");
					}
					
				}			
			}
		}
	}
	
	public void outputChanged(DACTA70909Controller controller, DACTAOutput output, int aOldValue, int errorCode){
		//String eventString = controller.getName() + " - Output #: " + output.getId();
		//String error = 	DACTA70909Controller.errorString(errorCode);
	}
	
	
    public void setPower(boolean on){
    	for(int i=0; i<iNumControllers; i++){
			DACTA70909Controller controller = dcControllers[i];
			// do not override the leftRightState
			boolean leftRightState = controller.getOutput(i).getLeftRightState();
			controller.setPower(on);
		}	
    }

	
	public String getName(){
		return app.getName();
	}
	
	public synchronized void changeOutput()
	{
		
	}
	
	public synchronized void closeProject(){
		System.out.println("stopping programs and closing connections");
		Iterator values = programs.values().iterator();
		while(values.hasNext()){
			ProgramImpl program = (ProgramImpl)values.next();
			program.stop();
		}

		for(int i=0; i<iNumControllers; i++){
			if(dcControllers[i] != null){				
				dcControllers[i].closeConnection();
			}				
		}
		destroyInstance();
	}
	
	public void setApplication(Application app){
		ProjectManager.app = app;
	}
	
	public Application getApplication(){
		return ProjectManager.app;
	}
	
	/**
	* @param int controllerId
	* @param int status
	*/
	public void controllerStatus(int controllerId, int status){
		//TODO create panel to show message
//		String message = controllers[controllerId].getName() + " on port " + 
//		controllers[controllerId].getPort() + " status updated: " + DACTA70909Controller.errorString(status);
//	  	System.out.println(message);
//	  	//if(status==-5){
//	  	if(status!=0){
//	  		final int thisControllerId = controllerId;
//		  	SwingWorker worker = new SwingWorker() {
//			  	public Object construct() {
//				//new thread to allow closeConnection to do its thing on the controller
//				  	return delayDialog(thisControllerId);
//				}
//			 };
//			 worker.start();
//		}
	}
	
	private Object delayDialog(int controllerId){
	try {		  
		Thread.sleep(10000);			
		//controllers[controllerId].showDactaConnectionDialog();
	}
	catch (InterruptedException e) {
		return "Interrupted"; 
	}
	return "All Done"; 
}
	
	
	
	
	/* #### RemoteControlListener implementation #### */
	/*
	public void remoteButtonOnPress(int buttonId){		
		String[] event = getProgramAndMethod(remote.getOnPressEvent(buttonId));
		//System.out.println("DegoManager remoteButtonOnPress: " + remote.getName(buttonId) + " event->" + remote.getOnPressEvent(buttonId));
		if(event!=null && allSetUp){
			programs.get(event[0]).invokeMethod(event[1]);
		}
	}
	
	
	public void remoteButtonPressed(int buttonId){
		//System.out.println("DegoManager remoteButtonPressed: " + remote.getName(buttonId) + " event->" + remote.getPressedEvent(buttonId));
		String[] event = getProgramAndMethod(remote.getPressedEvent(buttonId));
		if(event!=null && allSetUp){
			programs.get(event[0]).invokeMethod(event[1]);
		}
	}
	
	public void remoteButtonReleased(int buttonId){		
		//program.invokeMethod(remote.getOnReleaseEvent(buttonId));
		//System.out.println("DegoManager remoteButtonReleased: " + remote.getName(buttonId) + " event->" + remote.getOnReleaseEvent(buttonId));
		String[] event = getProgramAndMethod(remote.getOnReleaseEvent(buttonId));
		if(event!=null && allSetUp){
			programs.get(event[0]).invokeMethod(event[1]);
		}
	}
	*/
	
	/**
	 * 
	 * @param breakMeUp in the form Program.method 
	 * will split breakMeUp using .  (dot syntax)
	 * @return String[2] programAndMethod
	 * will return null if breakMeUp can not be split into two
	 * programAndMethod[0] = String program
	 * programAndMethod[1] = String method
	 */
	public static String[] getProgramAndMethod(String breakMeUp){
		String[] programAndMethod = null;
		String[] brokenUp = breakMeUp.split("\\.");
		if(brokenUp.length==2)
			programAndMethod = brokenUp;
		return programAndMethod;
	}
}