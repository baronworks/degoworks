package dacta;

import com.degoworks.ProjectManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.lang.String;

import java.util.Timer;
import java.util.TimerTask;


//import javax.comm.*;
import gnu.io.*;

import com.degoworks.interfaces.Controller;
import com.degoworks.interfaces.ControllerListener;
import com.degoworks.interfaces.EventConstants;
import com.degoworks.project.ControllerConfig;
import com.degoworks.project.OutputConfig;
import com.degoworks.project.SensorConfig;

/**
* @author Timo Dinnesen
*
* A class that can establish connection to a DACTA 70909 box, read sensor values and set outputs.
/*
 * Created on May 6, 2005
 *
 * Updated March 8, 2006
 * V1.01
 * NOTE: cannot onOffState and leftRightState can be set at the same time -
 * ->setOutput(outputId, onOffState, leftRightState);
 * setPower has to be done separately and will not send the info to the 
 * controller if setPower is done with anything else
 */
public class DACTA70909Controller extends TimerTask implements Controller, SerialPortEventListener, EventConstants {
	private int statusCode = -10;//
	private int sourceType = i_SOURCE_DC;
	private int id;
	private String port;
	private String name = "Sweet Dacta";
	//private Enumeration portList;
	private SerialPort theSerialPort;
	private CommPortIdentifier cpi;
	private InputStream is;
	private DataInputStream dis;
	private OutputStream os;
	private DACTA70909KeepAliveThread kat;
	private boolean seenCorrectReply = false;
	private DACTASensor[] sensors; 
	private DACTAOutput[] outputs;
	private int iNumListeners = 0;
	public static final int iMaxListeners = 8;
	private ControllerListener[] dcListeners;
	private Timer timer;
	
	
	
	public DACTA70909Controller(int id, DACTASensor[] sensors, DACTAOutput[] outputs) {
		this.id = id;
		this.sensors = sensors;
		this.outputs = outputs;
		//Use init to start the controller.
	};
	
	public DACTA70909Controller(int id) {
		this.id = id;
		// Empty constructor.. Use init to start the controller.
	};
	
	public DACTA70909Controller(ControllerConfig controllerConfig) {
		this.id = controllerConfig.getId();
		this.name = controllerConfig.getName();
		initSensors(controllerConfig.getSensorConfigs());
		initOutputs(controllerConfig.getOutputConfigs());
	};
	
	public void initSensors(SensorConfig[] sensorConfigs){
		sensors = new DACTASensor[sensorConfigs.length];
		for(int i=0; i<sensorConfigs.length; i++){
			sensors[i] = new DACTASensor(sensorConfigs[i]);
		}
	}
	
	public void initOutputs(OutputConfig[] outputConfigs){
		outputs = new DACTAOutput[outputConfigs.length];
		for(int i=0; i<outputConfigs.length; i++){
			outputs[i] = new DACTAOutput(outputConfigs[i]);
		}
	}
	public int getId(){
		return id;
	}
	
	/**
	 * Used to tell the controller which port to use and start the controller.
	 * @param port Port name. Use "COM1" or "COM2" on most windows machines.
	 * @return Errorcode if something went wrong, else 0
	 */
	public int init(String port){
		// Search for port
		System.out.println("trying init on port: " + port);
		this.port = port;
		boolean portFound = false;
		int status = -10;
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			cpi = (CommPortIdentifier) portList.nextElement();
			//Only check serial ports
			if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println("init checking port: " + cpi.getName());
				// Check for right name
				if (cpi.getName().equals(port)) {
					System.out.println("init found port: " + port);
					portFound = true;
					//  Open port 
					try{
						String owner = "DC." + System.currentTimeMillis();
						theSerialPort = (SerialPort)cpi.open("DACTA Controller", 2000);
						// Set parameters
						theSerialPort.setSerialPortParams(9600,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
						
						// Start the show :-)
						os = theSerialPort.getOutputStream();
						is = theSerialPort.getInputStream();
						dis = new DataInputStream(is);
						
						// Register this as an eventlistener
						theSerialPort.addEventListener(this);
						theSerialPort.notifyOnDataAvailable(true);
						kat = new DACTA70909KeepAliveThread(os);
						kat.start();
						startCommunication();
						
						/* Wait one sec for reply */
						long startTime = new Date().getTime();
						long now = startTime; 
						while (now < (startTime+1000)){
							now = new Date().getTime();
						};
						if(!seenCorrectReply){							
							os.close();
							is.close();
							dis.close();
							theSerialPort.close();
							kat.signalClose();
							status = -9;
						}
						status = 0;
					} catch (UnsupportedCommOperationException e2) {
						status = -3;
					} catch (PortInUseException e) {
						status = -2;
					} catch (IOException e) {
						System.err.println("IOException " + e.getMessage());
						e.printStackTrace();
						status = -4;
					} catch (TooManyListenersException e1) {
						status = -8;
					} 
					break;
				}
			}
		}
		if(portFound && status==0){			
			timer = new Timer();
			timer.scheduleAtFixedRate(this, 10, 10);
		}
		updateStatus(status); 
		return status;		
	}
	
	
	/*
	 * 	Needed to send the required string to wake up the DACTA box
	 * @throws IOException
	 */
	private void startCommunication() throws IOException{
		String message = "p\0###Do you byte, when I knock?$$$";
		os.write(message.getBytes());
	}
	
	public void setSensors(DACTASensor[] sensors){
		this.sensors = sensors;
	}
	
	/**
	 * Returns the raw value of a sensor output.
	 * @param sensorId The sensor (0-7)
	 * @return The value (0-1023)
	 */
	public int getRawValue(int sensorId) {
		return sensors[sensorId].getRawValue();
	}
	
	/**
	 * Returns the canical value of a sensor output.
	 * @param sensorId The sensor (0-7)
	 * @return 
	 */
	public int getSensorValue(int sensorId) {
		return sensors[sensorId].getSensorValue();
	}
	
	/**
	 * Returns the 6 bit status value of a sensor output.
	 * @param sensorId
	 * @return The value (0-63)
	 */
	public byte getStatusValue(int sensorId) {
		return sensors[sensorId].getByteStatus();
	}
	
	/**
	 * To turn a port on/off etc.
	 * @param port (outputId) (0-7) to be set.
	 * TODO implement ways to prevent invalid on/right values based on DACTAOutput.type and mode
	 * @param on on/off (true = on, false = off)
	 * @param right (true = set port to right, false = set port to left.)
	 * @return Error code if something went wrong, else 0
	 */
	public int setOutput(int outputId, boolean on, boolean right){			
		int previousPowerValue = outputs[outputId].getPower();
		int errorCode = 0;
		if(outputs[outputId].isDisabled()){
			errorCode = -11;
		}
		else {
			byte theonoffbyte = 0x28; 	// Command to turn port on "00101ppp"
			byte theleftrightbyte = 0x40; 	// Command to turn port left "01000ppp"
			
			if (outputId>=0 && outputId<=7){
				theonoffbyte = (byte)(theonoffbyte + (byte)outputId); // Insert port number into command.
				theleftrightbyte = (byte)(theleftrightbyte + (byte)outputId);
				if(!on){
					theonoffbyte = (byte)(theonoffbyte + 16); ; // Turn bit 5 on if we're turning port off.
				}
				
				if(right){
					theleftrightbyte += 8; // Set bit 4 if we want to go right.
				}
				try {
					os.write(theonoffbyte);
					os.write(theleftrightbyte);					
					//record the state
					outputs[outputId].setOnOffState(on);
					outputs[outputId].setLeftRightState(right);
					if(previousPowerValue==0){
						outputs[outputId].setPower(DACTAOutput.maxPower);
					}
					errorCode = 0;
				} catch (IOException e) {
					errorCode = -5;
				}
				
			} else {
				errorCode = -6; // Output out of range
			}	
		}
		//System.err.println("output " + outputId + "; errorCode: " + errorCode);
		callOutputListeners(outputId, previousPowerValue, errorCode);
		return errorCode;
	}
	
	public void setPower(boolean on) {	
		for(int i=0; i<8; i++){
			setOutput(i, on, outputs[i].getLeftRightState());
		}
	}
	
	/**
	 * Sets the power of an ouput. Setting power turns the output off.
	 * This is due to the way the DACTA70909 works.
	 * TODO implement ways to prevent invalid power levels based on DACTAOutput.type and mode
	 * @param outputnr The output to be set 0-7
	 * @param value The power to set 0-8
	 * @return Error code if something went wrong, else 0
	 */
	public int setPower(int outputId, int value) {		
		int previousPowerValue = outputs[outputId].getPower();		
		int errorCode = 0;
		if(outputs[outputId].isDisabled()){
			errorCode = -11;
		}
		else {
			byte setpower = (byte)0xB0; 	// Command to set power "10110ppp"
			byte outputbit = (byte)0x01; 	// set the bit to point at output 0
			if(outputId>=0 && outputId<=7){ // Check for right output port
				outputbit = (byte)(outputbit << outputId); 
				
				// Use setpower command if value is 1-8
				if ((value>0) && (value<=8)){
					setpower += (value-1);	
					try {
						os.write(setpower);
						os.write(outputbit);
					} catch (IOException e) {
						errorCode = -5;
					}
					errorCode = 0;
				}
				else if(value==0){
					setpower = (byte)0x92; 	// Command to set power to zero "10010010"		
					try {
						os.write(setpower);
						os.write(outputbit);
					} catch (IOException e) {
						errorCode = -5;
					}
					errorCode = 0;
				}
				else {
					errorCode = -7; // Value out of range
				}
			} else {
				errorCode = -6; // Output out of range
			}		
			if(errorCode==0)
				outputs[outputId].setPower(value);
		}
		callOutputListeners(outputId, previousPowerValue, errorCode);
		return errorCode;
	}
	
	/**
	 * To turn a port on/off etc.
	 * @param port (outputId) (0-7) to be set.
	 * @param on on/off (true = on, false = off)
	 * @return Error code if something went wrong, else 0
	 */
	public int setOnOff(int outputId, boolean on){			
		return setOutput(outputId, on, outputs[outputId].getLeftRightState());
	}
	
	public int setOn(int outputId){			
		return setOutput(outputId, true, outputs[outputId].getLeftRightState());
	}
	
	public int setOff(int outputId){			
		return setOutput(outputId, false, outputs[outputId].getLeftRightState());
	}
	
	public int setPowerMax(int outputId){
		return setPower(outputId, DACTAOutput.maxPower);
	}
	public int setOnRightPowerMax(int outputId){
		int errorCode =	setPowerMax(outputId);
		if(errorCode==0){
			errorCode = setOutput(outputId, true, true);
		}
		return errorCode;
	}
	
	public int setOnLeftPowerMax(int outputId){
		int errorCode =	setPowerMax(outputId);
		if(errorCode==0){
			errorCode = setOutput(outputId, true, false);
		}
		return errorCode;
	}
	/**
	 * To turn a port on/off etc.
	 * @param port (outputId) (0-7) to be set.
	 * @param right (true = set port to right, false = set port to left.)
	 * @return Error code if something went wrong, else 0
	 */
	public int setLeftRight(int outputId, boolean right){			
		return setOutput(outputId, outputs[outputId].getOnOffState(), right);
	}
	
	public int reverseLeftRight(int outputId){
		return setLeftRight(outputId, !outputs[outputId].getLeftRightState());
	}
	
	
	
	/*
	 *  -1: Port not found.
	 *  -2: Port already in use.
	 *  -3: Port does not support the right parameters.
	 *  -4: Could not get Input- or Output-stream (Never happens?)
	 *  -5: IO-error
	 *  -6: Wrong output selected, must be 0-7.
	 *  -7: Wrong powerlevel, must be 0-8
	 *  -8: Could not register Eventlistener on serial port
	 *  -9: No reply from DACTA box. Not connected?
	 */		
	
	/**
	 * Used to convert an error code into a description.
	 * used for both controller status and output errors
	 * @param errorcode
	 * @return A string describing the meaning of the errorcode.
	 */
	public String errorString(){
		return errorString(this.statusCode);
	}
	
	/**
	 * 
	 * @param int errorCode
	 * @return String errorMessage
	 * errorCode are for both the controller statusCode
	 * as well as the errorCode for setPower and setOutput
	 */
	public static String errorString(int errorCode){
		switch(errorCode) {
			case -0: return "Connected.";
			case -1: return "Port not found.";
			case -2: return "Port already in use.";
			case -3: return "Port does not support the right parameters.";
			case -4: return "Could not get Input- or Output-stream.";
			case -5: return "IO-Error";
			case -6: return "Wrong output selected. Must be 0-7.";
			case -7: return "Wrong powerlevel. Must be 0-8.";
			case -8: return "Could not register Eventlistener on serial port";
			case -9: return "No reply from DACTA Box. Not connected?";
			case -10: return "Not Initialized";
			case -11: return "Disabled";
			default: return "Unkown errorcode";
		}
	}
	
	/* (non-Javadoc)
	 * Needed to get input from the serialport.
	 */
	public void serialEvent(SerialPortEvent event) {
		switch(event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			
		byte[] packetBuffer = new byte[19];
		boolean correctData = true; // True if the data can be "trusted"
		try {
			// Only do something if theres enough data for a whole packet.
			if (dis.available()>19){
				for(int i=0;i<19;i++){
					packetBuffer[i] = dis.readByte();
				}
				
				if(!seenCorrectReply){ // Simple check for "just a bit of the block..." stuff.
					for (int i = 0; i < packetBuffer.length; i++) {
						if(packetBuffer[i] == (byte)0x24){
							seenCorrectReply = true;
						}
					}
				}
				
				// check sync. (Is first two bytes = 0?)
				// If not "eat" 1 byte, to slowly sync. A full sync will take about a � sec.
				if(packetBuffer[0] != 0 || packetBuffer[1] !=0) {
					if (dis.available()>1){
						dis.readByte();
					}
					correctData = false;
				}
				// Decode the packet
				int chksum=0;
				for (int i = 0; i < packetBuffer.length; i++) {
					int unsignedbyte = ((int)packetBuffer[i] & 0xff);
					chksum = chksum + unsignedbyte;
				}
				if ((chksum&0xff) != 0xff){
					correctData = false;
				}
				// If we can trust the data, set the sensorvalue array.
				if (correctData) {	
					sensors[0].setRawValues( packetBuffer[14],packetBuffer[15] );
					sensors[1].setRawValues( packetBuffer[10],packetBuffer[11] );
					sensors[2].setRawValues( packetBuffer[6],packetBuffer[7] );
					sensors[3].setRawValues( packetBuffer[2],packetBuffer[3] );
					sensors[4].setRawValues( packetBuffer[16],packetBuffer[17] );
					sensors[5].setRawValues( packetBuffer[12],packetBuffer[13] );
					sensors[6].setRawValues( packetBuffer[8],packetBuffer[9] );
					sensors[7].setRawValues( packetBuffer[4],packetBuffer[5] );

				}	
			}		
		} catch (IOException e) {
			//e.printStackTrace();			
			updateStatus(-5);//"IO-Error";
			closeConnection();							
		}
		break;
		}
	}	
	
	/**
	* Implements TimerTask's abstract run method.
	* this will check for state change as the serialEvents need to be slowed down for gui interfaces
	* 
	*/
	public void run(){
		callSensorListeners();
	}
	  
	/**
	 * 
	 * 
	 */
	private void callSensorListeners() {
		//loop thru the sensorvalues and look for any change in value
		
		for(int j=0; j<8; j++){
			//attempt to update the sensor value (threshold values may prevent value change)
			if(sensors[j].updateValue()){				
				//notify all listeners about the statechange			
				SensorEvent sensorEvent = new SensorEvent(this, sensors[j]);
				for (int i=0; i<iNumListeners; i++) {				
					dcListeners[i].sensorEvent(sensorEvent);										
				}
			}
		}
	}
	
	
	private void callOutputListeners(int outputId, int previousPowerValue, int errorCode) {				
		//notify all listeners about the statechange	
		if(ProjectManager.stdOut|| ProjectManager.stdErr){
			String message = s_SOURCE_DC + "|" + this.id + "|"  
				+ s_OUTPUT_EVENT + "|" + outputId + "|";
			if(errorCode==0 && ProjectManager.stdOut){
				//String leftRightState = outputs[outputId].getLeftRightState() ? "+" : "-";
				message += "old: " + previousPowerValue + " -> new: " + outputs[outputId].getPower();
				System.out.println(message);
			}
			else if (ProjectManager.stdOut){
				message += "errorCode: " + errorCode;
				System.err.println(message);
			}
		}
		for (int i=0; i<iNumListeners; i++) {				
			dcListeners[i].outputChanged(this, outputs[outputId], previousPowerValue, errorCode);										
		}		
	}
	
	/**
	 * 
	 * @param int status
	 * will update the controller statusCode 
	 * and if new value will notify all listeners
	 */
	private void updateStatus(int status) {	
		
		if(this.statusCode != status){	
			this.statusCode = status;			
			//notify all listeners about the state change	
			for (int i=0; i<iNumListeners; i++) {				
				dcListeners[i].controllerStatus(this.id, status);										
			}		
		}			
	}
	
	/**
   	* Adds a DACTAControllerListener
   	* <p>
   	* <b>
   	* NOTE 1: You can add at most iMaxListeners listeners.<br>
   	* NOTE 2: Synchronizing inside listener methods could result
   	* in a deadlock.
   	* </b>
   	* @see josx.platform.rcx.SensorListener
   	*/
  	public synchronized void addControllerListener (ControllerListener dcListener)
  	{
  		if (dcListeners == null)
		{
			dcListeners = new ControllerListener[iMaxListeners];
		}
		dcListeners[iNumListeners++] = dcListener;
  	}
  	
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;	
	}
		
	public String getPort(){
		return this.port;	
	}
	
	public int getStatusCode(){
		return this.statusCode;
	}
	
	public int getSourceType(){
		return this.sourceType;
	}
	
	public DACTASensor getSensor(int sensorId){
		if(sensors!=null){
			return sensors[sensorId];
		}
		return null;
	}
	
	public DACTAOutput getOutput(int outputId){
		if(outputs!=null){
			return outputs[outputId];
		}
		return null;
	}
	
	/**will kill the DACTA70909KeepAliveThread thread 
	 * also closes:
	 * 	OutputStream os
	 *  InputStream is
	 *  DataInputStream dis;
	 *  SerialPort theSerialPort
	 *  NOTE:  need to kill entire DACTA70909Controller thread after trying to close
	 * 		the SerialPort as it will hang!!!
	 *
	 */
	public synchronized void closeConnection(){
		System.out.println("\tclosing port connection: " + this.port);		
		if(kat!=null && kat.isAlive()){
			kat.signalClose();
			kat = null;
		}
		
		try{
			if(os != null){
				os.close();
			}
				
			if(is != null){
				is.close();
			}
				
			if(dis != null){
				dis.close();
			}
				
			if(theSerialPort != null){
				try{
					theSerialPort.removeEventListener();
					theSerialPort.close();
				}
				catch(Exception e)
				{
					System.out.println("error closing theSerialPort connection: " + e.getMessage());
				}
				
			}
			
		} catch (IOException e) {
		   // Do nothing... :-/		   
	   	}
		timer.cancel();
	}
	
	/**
	 * Overwrites the clone method
	 * returning a new DACTA70909Controller with the same id, name, sensors, outputs and dcListeners
	 * used when needing to reinitialize the connection
	 * @deprecated
	 */	
	public Object clone(){
		DACTA70909Controller newController = new DACTA70909Controller(this.id, this.sensors, this.outputs);
		newController.name = this.name;
		newController.dcListeners = this.dcListeners;		
		return newController;					
	}
	
}
