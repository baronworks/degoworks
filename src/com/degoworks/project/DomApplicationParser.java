
package com.degoworks.project;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class DomApplicationParser implements ErrorHandler {


	protected Document appDom;
	protected String appFile;
	
	public DomApplicationParser(){

	}

	public Application parseApplication(String appFile) {
		this.appFile = appFile;
		//parse the xml file and get the dom object
		parseXmlFile();		
		Application app = parseApplicationDocument();
		return app;
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
			appDom = db.parse(appFile);			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private Application parseApplicationDocument(){
		//get the root element
		Element appRoot = appDom.getDocumentElement();
		String id = appRoot.getAttribute("id");
		String name = appRoot.getAttribute("name");
		String defaultProjectFile = appRoot.getAttribute("default_project");
		System.out.println("application id/name: " + id + "/" + name + "/" + defaultProjectFile);
		
		Application app = new Application(id, name, defaultProjectFile);
		//get ignore serial port list 
		NodeList nodeList = appRoot.getElementsByTagName("serial_port_ignore");
		if(nodeList != null && nodeList.getLength() > 0) {
			ArrayList<String> ignoreSerialPorts = new ArrayList<String>();
			for(int i = 0 ; i < nodeList.getLength();i++) {	
				//System.err.println("ignore " + nodeList.item(i).getTextContent());
				ignoreSerialPorts.add(nodeList.item(i).getTextContent());
			}
			app.setIgnoreSerialPorts(ignoreSerialPorts);
		}
		// get available projects
		nodeList = appRoot.getElementsByTagName("project");
		if(nodeList != null && nodeList.getLength() > 0) {
			ProjectInfo[] projectInfos = new ProjectInfo[nodeList.getLength()];
			for(int i = 0 ; i < nodeList.getLength();i++) {	
				Element projectElement = (Element)nodeList.item(i);
				String projectName = projectElement.getAttribute("name");
				String projectFile = projectElement.getAttribute("src");
				Boolean runOnLoad = new Boolean(projectElement.getAttribute("run_on_load"));
				projectInfos[i] = new ProjectInfo(projectName, projectFile, runOnLoad);
			}
			app.setProjectInfo(projectInfos);
		}
		return app;
	}
	
		
	public static void main(String[] args){
		//create an instance
		DomApplicationParser dpp = new DomApplicationParser();
		//call run example
		String appFile = "application.xml";
		dpp.parseApplication(appFile);
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
