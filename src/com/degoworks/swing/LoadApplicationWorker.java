/*
 * Created on 2013-04-16
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

import javax.swing.SwingWorker;

import com.degoworks.project.Application;

public class LoadApplicationWorker extends SwingWorker<Application, DegoGUI> {

	private DegoGUI degoGui;
	
	protected LoadApplicationWorker(DegoGUI degoGui){
		this.degoGui = degoGui;
	}

	@Override
	protected Application doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
