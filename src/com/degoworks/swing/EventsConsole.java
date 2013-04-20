/*
 * Created on Mar 8, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;

//import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class EventsConsole extends JPanel{
	
	/**
     * Singleton implementation.
     */
	protected static EventsConsole s_eventsConsole = null;
    

	public EventsConsole getEventsConsole(){
		return s_eventsConsole;
	}
	
}
