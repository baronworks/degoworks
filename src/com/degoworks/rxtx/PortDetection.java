/*
 * Created on 16-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.rxtx;


//import javax.comm.* ;
import gnu.io.*;

import java.util.* ;
/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PortDetection {
	

	private static Enumeration<CommPortIdentifier> portList;
	/** A mapping from names to CommPortIdentifiers. */
  	protected HashMap<String, CommPortIdentifier> cpiMap = new HashMap<String, CommPortIdentifier>();

	private ArrayList<String> ignoreList = new ArrayList<String>();
	private String[] serialPortNames;
	private String[] availableSerialPortNames;
	private String[] currentlyOwnedSerialPortNames;
	
	
	public PortDetection(){			
		detectSerialPortNames();	
	}
	
	public PortDetection(ArrayList<String> ignoreSerialPorts){	
		if(ignoreSerialPorts!=null){
			for(String port : ignoreSerialPorts){
				System.out.println("please ignore " + port);
				ignoreList.add(port);
			}
		}		
		detectSerialPortNames();	
	}

	
	public void detectSerialPortNames(){
		portList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> serialPortNames = new ArrayList<String>();
		ArrayList<String> availableSerialPortNames = new ArrayList<String>();
		ArrayList<String> currentlyOwnedSerialPortNames = new ArrayList<String>();
		while (portList.hasMoreElements()) // hasMoreElements()
		{
			CommPortIdentifier cpi = portList.nextElement();
			cpiMap.put(cpi.getName(), cpi);
			System.err.println("looking for " + cpi.getName() + " - " + !ignoreList.contains(cpi.getName()));
			if(!ignoreList.contains(cpi.getName())){
				if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					serialPortNames.add(cpi.getName());
					System.out.println("something getCurrentOwner: " + cpi.getCurrentOwner());
					if(cpi.isCurrentlyOwned()){
						currentlyOwnedSerialPortNames.add(cpi.getName());
					}
					else{
						//System.out.println("apparently all are available");
						availableSerialPortNames.add(cpi.getName());
					}
						
				}
			}
		}	
		this.serialPortNames = serialPortNames.toArray(
									new String[serialPortNames.size()]);
		this.availableSerialPortNames = availableSerialPortNames.toArray(
									new String[availableSerialPortNames.size()]);
		this.currentlyOwnedSerialPortNames = currentlyOwnedSerialPortNames.toArray(
									new String[currentlyOwnedSerialPortNames.size()]);
		
	}
	
	public ArrayList<String> getIgnoreList(){
		return ignoreList;
	}
	
	public String[] getSerialPortNames(){
		return serialPortNames;
	}
	
	public String[] getAvailableSerialPortNames(){
		return availableSerialPortNames;
	}
		
	public String[] getCurrentlyOwnedSerialPortNames(){
		return currentlyOwnedSerialPortNames;
	}
	
	
	public void listSerialPorts(){		
		System.out.println("Listing available serial ports");
		for(int i=0; i<serialPortNames.length; i++){
			System.out.println("\tPort: " + serialPortNames[i]);
		}
	}
	
	public void listAllPorts(){
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) // hasMoreElements()
		{
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			System.out.println("Port: "+ portId.getName() + "; type: " + portId.getPortType()+ " isCurrentlyOwned: " + portId.isCurrentlyOwned()); //COM1,COM2
			if(portId.isCurrentlyOwned()){
				System.out.println("\towner: " + portId.getCurrentOwner());
			}
		}
	}
	
	
	
	
}
