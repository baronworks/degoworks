/*
 * Created on 22-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package dacta;

import com.degoworks.interfaces.OutputConstants;
import com.degoworks.project.OutputConfig;
/**
 * @author doc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DACTAOutput implements OutputConstants {
	private int id;
	private int iType;
	private boolean onOffState = false;
	private boolean leftRightState = true; // left = false;
	private int powerValue = 0;//0-8 	
	private String tooltip;
	
	private boolean disabled = false;
	//TODO read write and lock options, 
	//also implement rules based on output type so that motors, and lights do not get over or under powered 
	public static final int maxPower = 8;
	
	public DACTAOutput(int id, int type){
		this.id = id;
		this.iType = type;
	}
	
	public DACTAOutput(OutputConfig outputConfig){
		this.id = outputConfig.getId();
		this.iType = outputConfig.getType();
		this.disabled = outputConfig.getDisabled();
		this.tooltip = outputConfig.getTooltip();
	}
    	
	/**
	 * Return the ID of the output. One of 0 thru to 7.
	 */
	public final int getId(){
		return id;
	}
	
	public final int getType(){
		return iType;
	}
	
	public boolean getOnOffState(){
		return onOffState;
	}
	/* only DACTA70909Controller should set this */
	protected void setOnOffState(boolean onOffState){
		if(!disabled)
			this.onOffState = onOffState;
	}
	
	public boolean getLeftRightState(){
		return leftRightState;
	}
	/* only DACTA70909Controller should set this */
	protected void setLeftRightState(boolean leftRightState){
		if(!disabled)
			this.leftRightState = leftRightState;
	}

	//values 0-8
	public int getPower(){
		return powerValue;
	}
	/* only DACTA70909Controller should set this */
	protected void setPower(int power){
		if(!disabled)
			this.powerValue = power;
	}
	
	public String getTooltip(){
		if(tooltip == "" || tooltip == null){
			tooltip = getTypeAsString();
		}
		return tooltip;
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public void disable(){
		this.disabled = true;
	}
	
	public boolean isLight(){
		switch(this.iType){
			case TYPE_LIGHT_SIDE: return true;
			case TYPE_LIGHT_TOP_SINGLE: return true;
			case TYPE_LIGHT_TOP_DUAL: return true;
			default: return false;			
		}
	}
	
	public boolean isLightReversible(){
		switch(this.iType){
			case TYPE_LIGHT_TOP_SINGLE: return true;
			case TYPE_LIGHT_TOP_DUAL: return true;
			default: return false;			
		}
	}
	
	public final String getTypeAsString(){
		if(disabled){
			return "Disabled";
		}
		
		switch(this.iType) {
			case TYPE_LIGHT_SIDE: return "Side light";
			case TYPE_LIGHT_TOP_SINGLE: return "Top light";
			case TYPE_LIGHT_TOP_DUAL: return "Dual Top Light";
			case TYPE_MOTOR_MICRO: return "Micro motor";
			case TYPE_MOTOR_9V: return "9v motor";
			case TYPE_MOTOR_9V_GEARED: return "9v geared moter";
			case TYPE_MOTOR_45V: return "4.5v motor";
			case TYPE_SIREN: return "Siren";
			case TYPE_TRAIN: return "Train";
			case TYPE_EMPTY: return "Empty";
			case TYPE_MOTOR_NAV_LIMIT: return "Motor Limited";
			case TYPE_MULTIPLE: return "Multiple";
			default: return "Unknown";
		}
	}
	
	
}
