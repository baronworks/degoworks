/*
 * Created on Mar 9, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package dacta;

import com.degoworks.ProjectManager;
import com.degoworks.interfaces.EventConstants;
import com.degoworks.interfaces.SensorConstants;
import com.degoworks.interfaces.Controller;

public class SensorEvent implements EventConstants, SensorConstants{
	
	private int iSourceType;
	private int iSourceId;
	private int iSensorId;
	private int iEventType;
	private int iSensorValue;
	private int iOldSensorValue;	
	private int iRawSensorValue;
	
	public SensorEvent(Controller controller, DACTASensor sensor){
		this.iSourceType = controller.getSourceType();
		this.iSourceId = controller.getId();
		this.iSensorId = sensor.getId();
		this.iEventType = sensor.getEventType();
		this.iSensorValue = sensor.getSensorValue();
		this.iOldSensorValue = sensor.getOldSensorValue();
		this.iRawSensorValue = sensor.getRawValue();
		if(ProjectManager.stdOut)
			printOut();
	}
	
	public int getSourceType(){
		return this.iSourceType;
	}
	
	public int getSourceId(){
		return this.iSourceId;
	}
	
	public int getSensorId(){
		return this.iSensorId;
	}
	
	public int getEventType(){
		return this.iEventType;
	}
	
	public int getSensorValue(){
		return this.iSensorValue;
	}
	
	public int getOldSensorValue(){
		return this.iOldSensorValue;
	}
	
	public int getRawSensorValue(){
		return this.iRawSensorValue;
	}
	
	public String getEventTypeAsString(){
		return SensorEvent.getEventTypeAsString(this.iEventType);
	}
	
	public static String getEventTypeAsString(int iEventType){
		String eventString = "";
		switch(iEventType){
			case NO_CHANGE_EVENT:
				eventString = "NO_CHANGE_EVENT";	
				break;
			case RAW_VALUE_INCREASE_EVENT:
				eventString = "RAW_VALUE_INCREASE_EVENT";	
				break;
			case RAW_VALUE_DECREASE_EVENT:
				eventString = "RAW_VALUE_DECREASE_EVENT";	
				break;
			case TOUCH_ON_PRESS_EVENT:
				eventString = "TOUCH_ON_PRESS_EVENT";	
				break;
			case TOUCH_ON_RELEASE_EVENT:
				eventString = "TOUCH_ON_RELEASE_EVENT";	
				break;
			case TOUCH_PRESS_AND_RELEASE_EVENT:
				eventString = "TOUCH_PRESS_AND_RELEASE_EVENT";	
				break;
			case TEMP_INCREASE_EVENT:
				eventString = "TEMP_INCREASE_EVENT";	
				break;
			case TEMP_DECREASE_EVENT:
				eventString = "TEMP_DECREASE_EVENT";	
				break;
			case ROTATE_CW_EVENT:
				eventString = "ROTATE_CW_EVENT";	
				break;
			case ROTATE_CCW_EVENT:
				eventString = "ROTATE_CCW_EVENT";	
				break;
			case LIGHT_INCREASE_EVENT:
				eventString = "LIGHT_INCREASE_EVENT";	
				break;
			case LIGHT_DECREASE_EVENT:
				eventString = "LIGHT_DECREASE_EVENT";	
				break;
		}
		return eventString;
	}
	
	private void printOut(){
		String message = s_SOURCE_DC + "|" + this.iSourceId + "|"  
			+ s_SENSOR_EVENT + "|" + iSensorId + "|" 
			+ " EventType: " + SensorEvent.getEventTypeAsString(iEventType)
			+ "-- old/new values:" + iOldSensorValue + "/" + iSensorValue;
		System.out.println(message);
	}

}
