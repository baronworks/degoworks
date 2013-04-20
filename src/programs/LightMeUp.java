/*
 * Created on Mar 23, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package programs;


import dacta.DACTA70909Controller;

import com.degoworks.ProjectManager;
import com.degoworks.project.*;

public class LightMeUp extends ProgramImpl{
	
	//private DACTA70909Controller[] dcControllers = ProjectManager.getControllers();
	
	public void turnAllLightsOn(){
		if(ProjectManager.stdOut)
			System.out.println("turnAllLightsOn");
		for(int i=0; i<dcControllers.length; i++){
			for(int j=0; j<8; j++){
				if(!dcControllers[i].getOutput(j).isDisabled()
						&& dcControllers[i].getOutput(j).isLight()){
					dcControllers[i].setOnRightPowerMax(j);
				}
			}
		}
	}
	
	public void turnAllLightsOff(){
		if(ProjectManager.stdOut)
			System.out.println("turnAllLightsOff");
		for(int i=0; i<dcControllers.length; i++){
			for(int j=0; j<8; j++){
				if(!dcControllers[i].getOutput(j).isDisabled()
						&& dcControllers[i].getOutput(j).isLight()){
					dcControllers[i].setOff(j);
				}
			}
		}
	}

	public void reverseLights(){
		if(ProjectManager.stdOut)
			System.out.println("reverseLights");
		for(int i=0; i<dcControllers.length; i++){
			for(int j=0; j<8; j++){
				if(!dcControllers[i].getOutput(j).isDisabled()
						&& dcControllers[i].getOutput(j).isLightReversible()){
					dcControllers[i].reverseLeftRight(j);
				}
			}
		}
	}

	public void flashAll(){
		if(ProjectManager.stdOut)
			System.out.println("reverseLights");
		for(int i=0; i<dcControllers.length; i++){
			for(int j=0; j<8; j++){
				if(!dcControllers[i].getOutput(j).isDisabled()
						&& dcControllers[i].getOutput(j).isLightReversible()){
					dcControllers[i].setOnLeftPowerMax(j);
				}
			}
		}
	}
	
	public synchronized void stop(){
		System.err.println("stop in LightMeUp");
		super.stop();
		dcControllers = null;
	}

}
