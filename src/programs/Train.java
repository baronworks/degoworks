/*
 * Created on Mar 30, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package programs;

import com.degoworks.ProjectManager;
import com.degoworks.project.ProgramImpl;

import dacta.DACTA70909Controller;

public class Train extends ProgramImpl{
	
	private DACTA70909Controller[] dcControllers = ProjectManager.getControllers();
	
	public void turnTrainOn(){
		if(ProjectManager.stdOut)
			System.out.println("turnTrainOn");
		if(!dcControllers[2].getOutput(7).isDisabled()){
			if(dcControllers[2].getOutput(7).getPower()!=5)
				dcControllers[2].setPower(7, 5);
			dcControllers[2].setOn(7);
		}
	}
	
	public void turnTrainOff(){
		if(ProjectManager.stdOut)
			System.out.println("turnTrainOff");
		if(!dcControllers[2].getOutput(7).isDisabled()){
			dcControllers[2].setOff(7);
		}
	}
	
	public synchronized void stop(){
		super.stop();
		dcControllers = null;
	}
	
}
