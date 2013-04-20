/*
 * Created on 20-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package dacta;

import com.degoworks.interfaces.SensorConstants;
import com.degoworks.project.SensorConfig;
/**
 * @author doc
 *Before using a sensor, you should set its mode
 * and type with <code>setTypeAndMode</code> using constants defined in <code>josx.platform.rcx.SensorConstants</code>.
 *  
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DACTASensor implements SensorConstants
{
  	private int sensorId;
  	private int aType;
  	private int aMode;
  	private short iNumListeners = 0;
	private int iRawValue;//pure sensor value no delay
	private int iSensorValue = 0;//state pure sensor value based on update threshold
	private int iOldSensorValue = 0;////pure sensor value before last update
	private int iValue = 0;//canonical value for sensor   
	private int iOldValue = -1;//previous canonical value for sensor
	//6 bit status value (0-63)
	private byte bStatus;
	private byte b1;
	private byte b2;
	private int iEventType = 0;
	private int threshold = 0;
	private String tooltip;
	private int rotationCount = 0;//rotation sensor sends info everytime 1/16 of a full rotation is made 
  	
	
	public DACTASensor(int sensorId)
	{
		this.sensorId = sensorId;
		setTypeAndMode (SENSOR_TYPE_RAW, SENSOR_MODE_RAW);
	}
	
	public DACTASensor(int sensorId, int type, int mode){
		this.sensorId = sensorId;
		setTypeAndMode(type, mode);
	}
	
	public DACTASensor(SensorConfig sensorConfig){
		this.sensorId = sensorConfig.getId();
		this.tooltip = sensorConfig.getTooltip();
		setTypeAndMode(sensorConfig.getType(), sensorConfig.getMode());
	}
  
	/**
	 * Return the ID of the sensor. One of 0 thru to 7.
	 */
	public final int getId()
	{
		return sensorId;
	}
	
	public String getTooltip(){
		if(tooltip == "" || tooltip == null){
			tooltip = getTypeAsString();
		}
		return tooltip;
	}
	
	/**
	 * Sets the sensor's mode and type. If this method isn't called,
	 * the default type is 3 (LIGHT) and the default mode is 0x80 (PERCENT).
	 * @param aType 0 = RAW, 1 = TOUCH, 2 = TEMP, 3 = LIGHT, 4 = ROT.
	 * @param aMode 0x00 = RAW, 0x20 = BOOL, 0x40 = EDGE, 0x60 = PULSE, 0x80 = PERCENT,
	 *              0xA0 = DEGC,
	 *              0xC0 = DEGF, 
	 * 				0xE0 = ANGLE. Also, mode can be OR'd with slope (0..31).
	 * @see josx.platform.rcx.SensorConstants
	 */
	 public final void setTypeAndMode (int aType, int aMode)
	 {
		this.aType = aType;
		this.aMode = aMode;   
		switch(this.aType) {
			case SENSOR_TYPE_RAW: 
				threshold = 0; 
				break;
			case SENSOR_TYPE_TOUCH: 
				threshold = THRESHOLD_TOUCH; 
				break; 
			case SENSOR_TYPE_TEMP:
				threshold = THRESHOLD_DEGC; 
				break;
			case SENSOR_TYPE_LIGHT: 
				threshold = THRESHOLD_LIGHT; 
				break;
			case SENSOR_TYPE_ROT: 
				threshold = THRESHOLD_ROT; 
				break;
		}  
	 }
	
	/**
	*	SENSOR_TYPE_RAW    = 0; 
	*	SENSOR_TYPE_TOUCH  = 1;
	*	SENSOR_TYPE_TEMP   = 2;
	*	SENSOR_TYPE_LIGHT  = 3;
	*	SENSOR_TYPE_ROT    = 4;
	*/
	public final int getType(){
		return this.aType;
	}
	
	public final String getTypeAsString(){
		switch(this.aType) {
			case SENSOR_TYPE_RAW: return "Raw";
			case SENSOR_TYPE_TOUCH: return "Touch";
			case SENSOR_TYPE_TEMP: return "Temperature";
			case SENSOR_TYPE_LIGHT: return "Light";
			case SENSOR_TYPE_ROT: return "Rotation";
			default: return "Unknown";
		}
	}
  
  	/**
  	 *  SENSOR_MODE_RAW    = 0x00;
	 *  SENSOR_MODE_BOOL   = 0x20;
	 *  SENSOR_MODE_EDGE   = 0x40;
	 *  SENSOR_MODE_PULSE  = 0x60;
	 *  SENSOR_MODE_PCT    = 0x80;
	 *  SENSOR_MODE_DEGC   = 0xa0;
	 *  SENSOR_MODE_DEGF   = 0xc0;
	 *  SENSOR_MODE_ANGLE  = 0xe0;
  	 */
  	public final int getMode(){
  		return this.aMode;
  	}
  		
	public byte getB1(){
		return this.b1;
	}
	
	public byte getB2(){
		return this.b2;
	}
	
	
		
	/**
	 * Reads the canonical value of the sensor.
	 */
	public final int getValue()
	{
	  return this.iValue;
	}
		
	/**
	 * state pure sensor value based on update threshold
	 */
	public final int getSensorValue()
	{
	  	return this.iSensorValue;
	}

	/**
	 * Reads the old raw value of the sensor before last update.
	 */
	public final int getOldSensorValue()
	{
		return this.iOldSensorValue;
	}

	/**
	 * Reads the raw value of the sensor.
	 */
	public final int getRawValue()
	{
	  return iRawValue;
	}
	
	/**
	 * Reads the raw value of the sensor.
	 */
	public final byte getByteStatus()
	{
		return bStatus;
	}
	
	/**
	 * 
	 * @param b1
	 * @param b2
	 * takes the packet values b1 and b2 from the controller
	 * Needed to extract bits from 2 bytes and make them into an int.
	 * decodes b1 and b2 into iRawValue
	 * decodes b2 into bStatus 
	 */
	public final void setRawValues(byte b1, byte b2){
		this.b1 = b1;
		this.b2 = b2;
		int highbits = (b1 & 0xff)<<2;
		int lowbits = (b2 & 0xff)>>6;
		this.iRawValue = highbits + lowbits;
		this.bStatus = (byte)(b2 & 0x3f);
	}
	

	/**
	 * if Math.abs(this.iRawValue-this.iSensorValue)>this.threshold
	 * then values are updated and iEventType calculated
	 * sets the iEventType value
	 * 
	 * @return boolen true if iSensorValue was updated
	 */
	public final boolean updateValue(){
		
		boolean updated = Math.abs(this.iRawValue-this.iSensorValue)>this.threshold;
		if(updated){			
			this.iOldSensorValue = this.iSensorValue;
			this.iSensorValue = this.iRawValue;
			boolean increasing = this.iSensorValue > this.iOldSensorValue;
			switch(this.aType) {
				case SENSOR_TYPE_RAW: 
					this.iValue = this.iRawValue;
					this.iEventType = increasing ? RAW_VALUE_INCREASE_EVENT : RAW_VALUE_DECREASE_EVENT;
					return true;
				case SENSOR_TYPE_TOUCH: 
					this.iValue = (this.b1==-1) ? 0 : 1;						
					if(this.iValue==this.iOldValue){
						return false;//state did not change			
					}
					else {
						this.iOldValue = this.iValue;
						this.iEventType = (this.b1==-1) ? TOUCH_ON_RELEASE_EVENT : TOUCH_ON_PRESS_EVENT;
						return true;	
					}
				case SENSOR_TYPE_TEMP:
					this.iValue = this.iRawValue; 
					this.iEventType = increasing ? RAW_VALUE_INCREASE_EVENT : RAW_VALUE_DECREASE_EVENT;
					return true;
				case SENSOR_TYPE_LIGHT: 
					this.iValue = this.iRawValue;
					this.iEventType = increasing ? RAW_VALUE_INCREASE_EVENT : RAW_VALUE_DECREASE_EVENT;
					return true;
				case SENSOR_TYPE_ROT: 					
					//third bit from right on b2 indicates direction
					boolean clockwise = ( ( this.b2 & ( 1<<2 ) ) > 0 );
					this.iEventType = clockwise ? ROTATE_CW_EVENT : ROTATE_CCW_EVENT;
					//last two bits Mode 
					// 00 idle
					// 01 = wheel crossed 1/16 curve  <- normal
					// 10 = wheel crossed 1/8 curve?  <- only when fast
					// 11 = wheel crossed 1/4 curve?  <- only when real fast				
					byte mode = (byte)(b2 & 0x03);
					if(clockwise){
						this.rotationCount += mode;
					}
					else {
						this.rotationCount -= mode;
					}
					this.iValue = this.rotationCount;
					return true;
			} 			
		}
		return false;
	}
	
	public int getEventType(){
		return this.iEventType;
	}

		

	/**
	 * Reads the boolean value of the sensor.
	 */
	public final boolean getBooleanValue()
	{
	  	return this.iRawValue != 1023;//1023 is value with no reading (infinite resistance)
	  //touch sensor when closed will not give 0 value but when open is 1023
	}
	
	

	/**
	 * http://www.k12.nf.ca/sla/is1205/legoactivex.html
	 * Input ports 0 - 3 are simple analog sensors.
	 * Resistance of the input is converted to a 10 bit value
	 * For the touch sensor, if the first byte is 0x2E, then the sensor is pressed in, otherwise it is not. 
	 * 
	 * NOTE can not confirm aboce but do know that b1 is -1 (0xff) when the touch sensor is NOT pressed in
	 *
	 * For the light sensor, the rightmost 12 bits are a value 
	 * 		that describes the intensity of light received by the device. 
	 * For the thermometer, the rightmost 12 bits can be converted into 
	 * 		an approximation of the Fahrenheit temperature by using the formula T = (760 - Value) / 4.4 + 32 
	 * 		temperature from -20 to 50 degrees celsius
	 * 		Tf = (9/5)*Tc+32
	 * 		Tc = (5/9)*(Tf-32);
	 * For the angle sensor, the third bit from the right is a Boolean value specifying the direction of rotation of the sensor. 
	 * 		The rightmost two bytes (bits?) specify the amount of change in the angle since the last frame. 
	 * 		This value, divided by 16, gives the fraction of a circle that the sensor rotated through 
	 * 		since the last update. By remembering the last angle and using the direction and rate of change, 
	 * 		it is possible to track the angle of rotation. 
	 * 	Binary 		01001111  	01011000  
	 *	Hexadecimal 4F  		58  
	 * 	0   0   0   0   0   0   0   0
	 *  128 64  32  16  8   4   2   1
	 * 
	 *  256 512 1024 2056
	 *  http://www.learn-c.com/boolean.htm 
     *  0xC1 & 0xEA = 0xC0 (this says 0xC1 AND 0xEA = 0xC0)
	 *	0xC1 11000001 (the two nibbles are 1100 = 12 = 0xC and 0001 = 1 = 0x1)
	 *	0xEA 11101010
	 *	0xC0 11000000
	 *	     ||||||||_ 1 AND 0 = 0
	 *	     |||||||__ 0 AND 1 = 0
	 *	     ||||||___ 0 AND 0 = 0
	 *	     |||||____ 0 AND 1 = 0
	 *	     ||||_____ 0 AND 0 = 0
	 *	     |||______ 0 AND 1 = 0
	 *	     ||_______ 1 AND 1 = 1
	 *	     |________ 1 AND 1 = 1
     *
	 */

  
}
