/*
 * Created on 2013-04-16
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

import javax.swing.SwingWorker;

import com.degoworks.ProjectManager;
import com.degoworks.project.Application;

public class LoadProjectManagerWorker extends SwingWorker<ProjectManager, DegoGUI> {

	private DegoGUI degoGui;
	private Application app;
	
	protected LoadProjectManagerWorker(DegoGUI degoGui){
		this.degoGui = degoGui;
		this.app = degoGui.getApplication();
	}
	
	@Override
	protected ProjectManager doInBackground() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance(app);
		
		return projectManager;
	}
	
	protected void done() {
		try { 			
        	 ProjectManager projectManager = get();
        	 //projectManager.projectStatusEvent(
        	 degoGui.projectLoaded(projectManager);
        } catch (Exception ignore) {
        }
    }

}
