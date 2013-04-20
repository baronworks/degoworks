
package com.degoworks.project;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.degoworks.comm.RemoteControl;

public class DomProjectParser implements ErrorHandler {


	protected Document projectDom;
	protected String projectFile;
	
	public DomProjectParser(){

	}

	public Project parseProject(String projectFile) {
		this.projectFile = projectFile;
		//parse the xml file and get the dom object
		parseXmlFile();		
		Project project = parseProjectDocument();
		//Iterate through the list and print the data
		printData();
		return project;
	}
	
	
	private void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);

		try {			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(this);
			//parse using builder to get DOM representation of the XML file
			projectDom = db.parse(projectFile);			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private Project parseProjectDocument(){
		Project project = null;
		//get the root element
		Element projectRoot = projectDom.getDocumentElement();
		String id = projectRoot.getAttribute("id");
		String className = projectRoot.getAttribute("class");
		System.out.println("project id/className: " + id + "/" + className);
		
		project = new Project(id);
		project.description = getTextValue(projectRoot, "description");
		
		
		System.out.println("description: " + project.description);
		
		project.programConfigs = getProgramConfigs(projectRoot);
		//not yet implement place holder for now
		project.globals = getGlobals(projectRoot);
		try{
			//project.remote = getRemoteControl(projectRoot);
			project.controllerConfigs = getControllerConfigs(projectRoot);
		}
		catch(InvalidProjectConfigException ipce){
			System.err.println(ipce.getMessage());
		}
		return project;
	}
	
	private HashMap<String, ProgramConfig> getProgramConfigs(Element projectRoot){
		HashMap<String, ProgramConfig> programConfigs = new HashMap<String, ProgramConfig>();
		NodeList nodeList = projectRoot.getElementsByTagName("program");
		if(nodeList != null && nodeList.getLength() > 0) {
			for(int i = 0 ; i < nodeList.getLength();i++) {		
				//get the controller element
				Element programElement = (Element)nodeList.item(i);
				String id = programElement.getAttribute("id");
				String name = programElement.getAttribute("name");
				String className = programElement.getAttribute("class");
				programConfigs.put(id, new ProgramConfig(id, name, className));
			}
		}
		return programConfigs;
	}
	
	/*
	private RemoteControl getRemoteControl(Element projectRoot) throws InvalidProjectConfigException{
		RemoteControl remote = null;
		NodeList nodeList = projectRoot.getElementsByTagName("remote");	
		if(nodeList!=null && nodeList.getLength()==1){
			remote = new RemoteControl();
			NodeList buttons = projectRoot.getElementsByTagName("button");
			
			if(buttons.getLength() != 16){
				//
				throw new InvalidProjectConfigException("Remote control missing button declarations; " +
					"check file " + projectFile +
					"All 16 buttons are required, found: " + buttons.getLength());
			}
			String[] names = new String[16];
			String[] onPressEvents = new String[16];
			String[] pressedEvents = new String[16];
			String[] onReleaseEvents = new String[16];
			for(int i = 0 ; i < buttons.getLength();i++) {						
				Element button = (Element)buttons.item(i);				
				int id = Integer.parseInt(button.getAttribute("id"));
				if(id != i){
					//
					throw new InvalidProjectConfigException("remote ids invalid; " +
						"check file " + projectFile +
						"\remote ids start at 0, increment by 1 and should be listed from top to bottom.");
				}
				names[i] = button.getAttribute("name");
				onPressEvents[i] = button.getAttribute("onPress");
				pressedEvents[i] = button.getAttribute("pressed");
				//RemoteButton remoteButton = new RemoteButton(id, method);
				onReleaseEvents[i] = button.getAttribute("onRelease");
				//remoteButton.setOnRelease(onRelease.equalsIgnoreCase("true"));
				System.out.println("Remote button " + id + " -> onPressEvents: " + onPressEvents[i]);
				//remote.addButton(id, remoteButton);
			}
			remote.setNames(names);
			remote.setOnPressEvents(onPressEvents);
			remote.setPressedEvents(pressedEvents);
			remote.setOnReleaseEvents(onReleaseEvents);
		}
		return remote;
	}
	*/
	
	private ControllerConfig[] getControllerConfigs(Element projectRoot) throws InvalidProjectConfigException{
		ControllerConfig[] controllerConfigs = null;
		//		get a nodelist of <controller> elements
		NodeList nodeList = projectRoot.getElementsByTagName("controller");
		if(nodeList != null && nodeList.getLength() > 0) {
			controllerConfigs = new ControllerConfig[nodeList.getLength()];
			for(int i = 0 ; i < nodeList.getLength();i++) {		
				//get the controller element
				Element controllerElement = (Element)nodeList.item(i);
				int controllerId = Integer.parseInt(controllerElement.getAttribute("id"));
				
				if(controllerId != i){
					//
					throw new InvalidProjectConfigException("Controller ids invalid; " +						"check file " + projectFile +
						"\nController ids start at 0, increment by 1 and should be listed from top to bottom.");
				}
				String defaultPort = controllerElement.getAttribute("default_port");
				String name = controllerElement.getAttribute("name");
				System.out.println("controllerId: " + controllerId + " for i=" +i);
				controllerConfigs[i] = new ControllerConfig(controllerId, name, defaultPort);
				controllerConfigs[i].sensorConfigs = getSensorConfigs(controllerId, controllerElement);
				controllerConfigs[i].outputConfigs = getOutputConfigs(controllerId, controllerElement);
			}
		}
		return controllerConfigs;
	}
	
	private SensorConfig[] getSensorConfigs(int controllerId, Element controllerElement) throws InvalidProjectConfigException{
		SensorConfig[] sensorConfigs = null;
		NodeList nodeList = controllerElement.getElementsByTagName("sensor");
		if(nodeList != null && nodeList.getLength() > 0) {
			sensorConfigs = new SensorConfig[nodeList.getLength()];
			for(int i = 0 ; i < nodeList.getLength();i++) {		
				//get the controller element
				Element sensorElement = (Element)nodeList.item(i);
				int sensorId = Integer.parseInt(sensorElement.getAttribute("id"));
		
				if(sensorId != i){
					//
					throw new InvalidProjectConfigException("Sensor ids invalid; " +
						"check file " + projectFile +
						"\nSensor ids start at 0, increment by 1 and should be listed from top to bottom.");
				}
				String type = sensorElement.getAttribute("type");
				String mode = sensorElement.getAttribute("mode");
				String tooltip = sensorElement.getAttribute("tooltip");
			
				sensorConfigs[i] = new SensorConfig(sensorId, type, mode);
				sensorConfigs[i].controllerId = controllerId; 
				sensorConfigs[i].tooltip = sensorElement.getAttribute("tooltip");
				sensorConfigs[i].eventTriggers = getEventTriggers(sensorConfigs[i].getType(), sensorElement );
					
			}	
					
		}
		return sensorConfigs;
	}
	
	private HashMap<String, EventTrigger> getEventTriggers(int iEventType, Element sensorElement){
		HashMap<String, EventTrigger> eventTriggers = new HashMap<String, EventTrigger>();
		NodeList nodeList = sensorElement.getElementsByTagName("event");
		for(int i = 0 ; i < nodeList.getLength();i++) {	
			Element eventElement = (Element)nodeList.item(i);
			String eventKey = eventElement.getAttribute("type");
			EventTrigger eventTrigger = new EventTrigger(iEventType, eventKey);
			eventTrigger.setActions(getSensorActions(eventElement));
			eventTriggers.put(eventKey, eventTrigger);
		}
		return eventTriggers;
	}
	
	private Action[] getSensorActions(Element eventElement){
		NodeList nodeList = eventElement.getElementsByTagName("action");
		Action[] actions = new Action[nodeList.getLength()];
		for(int i = 0 ; i < nodeList.getLength();i++) {
			Element actionElement = (Element)nodeList.item(i);
			actions[i] = new Action(actionElement.getAttribute("method"));
			System.out.println("action: " + actions[i].getProgramAndMethod());
		}
		return actions;
	}
	
	private OutputConfig[] getOutputConfigs(int controllerId, Element controllerElement) throws InvalidProjectConfigException{
		OutputConfig[] outputConfigs = null;
		NodeList nodeList = controllerElement.getElementsByTagName("output");
		if(nodeList != null && nodeList.getLength() > 0) {
			outputConfigs = new OutputConfig[nodeList.getLength()];
			for(int i = 0 ; i < nodeList.getLength();i++) {		
				//get the controller element
				Element outputElement = (Element)nodeList.item(i);
				int outputId = Integer.parseInt(outputElement.getAttribute("id"));

				if(outputId != i){
					//
					throw new InvalidProjectConfigException("Output ids invalid; " +
						"check file " + projectFile +
						"\nSensor ids start at 0, increment by 1 and should be listed from top to bottom.");
				}
				
				String type = outputElement.getAttribute("type");
				outputConfigs[i] = new OutputConfig(controllerId, outputId, type);
				outputConfigs[i].tooltip = outputElement.getAttribute("tooltip");		
				String disabled = outputElement.getAttribute("disabled");
				if(disabled.equalsIgnoreCase("true")){
					outputConfigs[i].disabled = true;
					System.err.println("outputId: " + outputConfigs[i].id + " disabled");
				}

				System.out.println("outputId: " + outputConfigs[i].id + " with type=" +type);		
			}			
		}
		return outputConfigs;
	}
	
	private HashMap getGlobals(Element projectRoot){
		HashMap globals = null;
		
		return globals;
	}
	





	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){
		
	}

	
	public static void main(String[] args){
		//create an instance
		DomProjectParser dpp = new DomProjectParser();
		//call run example
		String projectFile = "projects/test_controller.project.xml";
		dpp.parseProject(projectFile);
	}
	
//
	// ErrorHandler methods
	//

	/** Warning. */
	public void warning(SAXParseException ex) throws SAXException {
		printError("Warning", ex);
	} // warning(SAXParseException)

	/** Error. */
	public void error(SAXParseException ex) throws SAXException {
		printError("Error", ex);
	} // error(SAXParseException)

	/** Fatal error. */
	public void fatalError(SAXParseException ex) throws SAXException {
		printError("Fatal Error", ex);
		throw ex;
	} // fatalError(SAXParseException)
	
	/** Prints the error message. */
	protected void printError(String type, SAXParseException ex) {

		System.err.print("[");
		System.err.print(type);
		System.err.print("] ");
		String systemId = ex.getSystemId();
		if (systemId != null) {
			int index = systemId.lastIndexOf('/');
			if (index != -1)
				systemId = systemId.substring(index + 1);
			System.err.print(systemId);
		}
		System.err.print(':');
		System.err.print(ex.getLineNumber());
		System.err.print(':');
		System.err.print(ex.getColumnNumber());
		System.err.print(": ");
		System.err.print(ex.getMessage());
		System.err.println();
		System.err.flush();

	} // printError(String,SAXParseException)

}
