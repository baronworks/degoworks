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
 * lists all ports and their status,
 * is a stand alone class and not used with the degoworks classes
 */
public class SerialPortDetector {
	
	public static void main(String[] args) {
		PortDetection detective = new PortDetection();
		detective.listAllPorts();
	}
}
