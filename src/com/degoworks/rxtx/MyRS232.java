/*
 * Created on 16-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
 
package com.degoworks.rxtx;
 

/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.ArrayList;



public class MyRS232 {

	
	private static ArrayList<String> ignoreList = new ArrayList<String>();
	
	
	public static void main(String[] args) {
		//ignoreList.add("COM1");
		//ignoreList.add("COM2");
		
		PortDetection detective = new PortDetection(ignoreList);
		String[] portNames = detective.getAvailableSerialPortNames();
		for(int i=0; i<portNames.length; i++){
			System.out.println("Available Port: " + portNames[i]);
		}

	}
}
