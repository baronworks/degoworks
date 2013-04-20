/*
 * Created on 18-Apr-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.swing;



import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.*;

import com.degoworks.ProjectManager;
import com.degoworks.interfaces.ControllerListener;
import com.degoworks.project.Application;
import com.degoworks.project.DomApplicationParser;


import java.awt.event.*;
import java.awt.*;

import dacta.*;

/**
 * @author doc
 *
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */
public class DegoGUI extends JFrame implements InternalFrameListener, ActionListener, ControllerListener {

	private static final long serialVersionUID = 5852133545918184754L;
	JDesktopPane desktop;	
	// an internal frame containing the console
    private EventsViewer eventsViewer = new EventsViewer();
    
    private static Application app;
	private static ProjectManager projectManager;
	
	static final String CLEAR = "clear";
	
	private static boolean pauseEvents = false;
	private static boolean allSystemsGo = false;
	private static boolean controlsDisabled = false;
	private static boolean projectLoaded = false;
	
	private static int iNumControllers = 0;
	private static DACTA70909Controller[] controllers;
	private static DactaControllerGUI[] controllerGUIs;
	
	private JMenuBar menuBar;	
	// file menu items
	private JMenu fileMenu;
	private JMenuItem projectCloseMenuItem;
	private JMenuItem projectLoadMenuItem;
	private JMenuItem projectReloadMenuItem;
	private JMenuItem eventsViewMenuItem;
	private JCheckBoxMenuItem systemOutMenuItem;
	private JCheckBoxMenuItem systemErrMenuItem;
	private JMenuItem quitMenuItem;
	
	//programs menu items
	private JMenu programsMenu;
	
	//controllers menu items
	private JMenu controllersMenu;

	
	public DegoGUI() {		
		super(app.getName() + ":  the GUI begins here!");
		//Make the big window be indented 50 pixels from each edge
		//of the screen.
		int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset,
				  screenSize.width  - inset*2,
				  screenSize.height - inset*2);

		//Set up the GUI.
		desktop = new JDesktopPane(); //a specialized layered pane
		//Make dragging a little faster but perhaps uglier.
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);		
		loadProject();
		startEventsViewer();
	}
	
	
	private void startEventsViewer(){
		// same as any other internal frame, add it to the desktop
		eventsViewer.setPreferredSize( new Dimension(400, 400) );
		eventsViewer.setBounds(50, 50, 450, 450);
		eventsViewer.setTitle( "Event Viewer");
		eventsViewer.setVisible( true );
        desktop.add(eventsViewer, JLayeredPane.DEFAULT_LAYER);
        
     // just to demonstrate we are about to redirect the streams...
        System.out.println("goodbye from stdout");
        System.err.println("goodbye from stderr");

        EventsViewer.getConsole().setBackgroundColor( Color.DARK_GRAY );
        EventsViewer.getConsole().setForegroundColor( Color.YELLOW );
        EventsViewer.getConsole().setSelectedTextColor( Color.DARK_GRAY );
        EventsViewer.getConsole().setSelectionColor( Color.YELLOW );
        
        // if you want to modify the text/buttons/title/etc of JConsole,
        // or force it to autosave and clear the console's messages
        // you can do it here ... before starting the console!
        EventsViewer.getConsole().setAppendFirstTime( false );
        EventsViewer.getConsole().setAutoSave( true );
        
        // clear the console messages (only the textarea) when maxchars are
        // reached, because a lot of messages can slow down the textarea panel.
        //JIConsole.getConsole().setMaxChars( 10 * 1024 );
        
        // finally start the console
        EventsViewer.getConsole().startConsole();
        
        // demonstrate that our console is up and running...
        //System.out.println(
        //       "This is the very first message logged to JConsole (stdout)");
        //System.err.println(
        //        "This is the very first message logged to JConsole (stderr)");
	}
	
	private void displayInitReport(){
		System.out.println("Hello " + app.getName() + "!");
		System.out.println("Initialization report...");
		for(int i=0; i<iNumControllers; i++){	
			String name = controllers[i].getName();
			String port = controllers[i].getPort();
			DACTA70909Controller controller = controllers[i];
			System.out.println("Controller: " + name + " on port " + port + "...");	
			if(controller.getStatusCode() == 0){
				System.out.println("\tStatus:" + controller.errorString());
			}
			else {
				System.err.println("\tStatus:" + controller.errorString());				
			}
			
		}
		if(allSystemsGo){
			System.out.println("All Systems Go!");						
		} 
		else {
			System.err.println("Some errors occurred while initializing the DACTA controllers.");
			System.err.println("You may need to restart the application.");
		}
	}

	public String getName(){
		return app.getName();
	}
		
	public Application getApplication(){
		return app;
	}

	public void showEventsWindow(){
		eventsViewer.setVisible( true );
	}
	
	//React to menu selections.
	public void actionPerformed(ActionEvent e) {
		if ("viewAll".equals(e.getActionCommand())) {
			viewAllControllers();
		} else if ("hideAll".equals(e.getActionCommand())) {
			hideAllControllers();
		} else if (e.getActionCommand().startsWith("viewGui")) {			
			viewControllerOnOff(e.getActionCommand().substring(7));
		} else if ("viewEvents".equals(e.getActionCommand())) {
			showEventsWindow();
		} else if ("controls_enable".equals(e.getActionCommand())) {
			enableControls(true);
		} else if ("controls_readonly".equals(e.getActionCommand())) {
			enableControls(false);
		} else if ("controls_readonly_with_power".equals(e.getActionCommand())) {
			readOnly(true);
		} else if ("stdOut".equals(e.getActionCommand())) {
			stdOutOnOff(e);
		} else if ("stdErr".equals(e.getActionCommand())) {
			stdErrOnOff(e);
		} else if ("project_close".equals(e.getActionCommand())) {
			projectCloseDialog();
		} else if ("project_load".equals(e.getActionCommand())) {
			projectLoadDialog();
		} else if ("project_reload".equals(e.getActionCommand())) {
			projectReloadDialog();
		} else if ("output_power_off".equals(e.getActionCommand())) {
			projectManager.setPower(false);
		} else if ("output_power_on".equals(e.getActionCommand())) {			
			projectManager.setPower(false);
		} else {//quit
			quitDialog();
		}
	}
	
	public void stdOutOnOff(ActionEvent e){
		ProjectManager.stdOut = ( (JCheckBoxMenuItem)e.getSource() ).isSelected();
	}
	
	public void stdErrOnOff(ActionEvent e){
		ProjectManager.stdErr = ( (JCheckBoxMenuItem)e.getSource() ).isSelected();
	}
	
	public void enableControls(boolean enable){
		controlsDisabled = !enable;
		for(int i=0; i<iNumControllers; i++){
			controllerGUIs[i].enableAll(enable);			
		}
	}
	
	public void readOnly(boolean readOnly){
		for(int i=0; i<iNumControllers; i++){
			controllerGUIs[i].readOnly(readOnly);			
		}
	}
	
	public void viewControllerOnOff(String stringId){
		int id = Integer.valueOf(stringId).intValue();
		DactaControllerGUI gui = controllerGUIs[id];
		
		gui.setVisible(!gui.isVisible());
		if(gui.isVisible()){
			try {
				gui.setSelected(true);
			} catch (java.beans.PropertyVetoException e) {}
		}
		//update the menubar
		setJMenuBar(createMenuBar());					
	}
	
	public void viewAllControllers(){
		for(int i=0; i<iNumControllers; i++){
			DactaControllerGUI gui = controllerGUIs[i];
//			need to check to see if it already exists
			if(!gui.isVisible()){
				gui.setVisible(true);
			}
		}
		//update the menubar
		setJMenuBar(createMenuBar());		
	}
	
	public void hideAllControllers(){
		for(int i=0; i<iNumControllers; i++){
			DactaControllerGUI gui = controllerGUIs[i];
//			need to check to see if it already exists
			if(gui.isVisible()){
				gui.setVisible(false);
			}
		}
//		update the menubar
		setJMenuBar(createMenuBar());	
	}

	protected void createControllerGUIs(){
		int errorCheck = 0;		
		controllerGUIs = new DactaControllerGUI[iNumControllers];
		for(int i=0; i<iNumControllers; i++){
			if(controllers[i].getStatusCode()==0){//only create Gui if connected
				controllerGUIs[i] = createControllerGUI(i, controllers[i]);
				controllers[i].addControllerListener(this);				
			}
			else {
				controllerGUIs[i] = null;			
			}
			errorCheck += controllers[i].getStatusCode();
		}		
		if(errorCheck==0){
			allSystemsGo = true;
		}
	}
	
	
	protected DactaControllerGUI createControllerGUI(int id, DACTA70909Controller controller){
		//create the internalFrame controllerGUI
		DactaControllerGUI controllerGUI = new DactaControllerGUI(controller, this);
		controllerGUI.setDefaultCloseOperation(
						  WindowConstants.DO_NOTHING_ON_CLOSE );
		controllerGUI.setVisible(true); //necessary as of 1.3
		controllerGUI.addInternalFrameListener(this);
		desktop.add(controllerGUI);
		try {
			controllerGUI.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
		return controllerGUI;
	}
	
	
	
		
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {		
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		//Create and set up the window.
		DegoGUI frame = new DegoGUI();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(WindowEvent winEvt) {				
				quitDialog(); 				
			}
		});
		//Display the window.
		frame.setVisible(true);
		
	}

	public static void main(String[] args) {
		//first load the application
		// is a small parse so have not needed to implement a SwingWorker
		DomApplicationParser dpp = new DomApplicationParser();
		String applicationFile = "projects/application.xml";
		app = dpp.parseApplication(applicationFile);	
		//Schedule a job for the event-dispatching thread:
				//creating and showing this application's GUI.	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	public static ProjectManager getProjectManager(){
		return projectManager;
	}
	
	private void projectLoadDialog(){
		if(projectLoaded){
			JOptionPane.showMessageDialog(this,
				    "Project is already loaded.");
		}
		else {
			String[] options = app.getProjectFiles();
			
			String s = (String)JOptionPane.showInputDialog(this, 
					"\nConfirm all 'Stop' lights on DACTA Controllers are on before loading?\n",
			        "Select Project",
			        JOptionPane.WARNING_MESSAGE, 
			        null, 
			        options, 
			        app.getDefaultProjectFile());
			System.err.println("project " + s);
			if(s != null){	
				app.setDefaultProjectFile(s);				
				loadProject();
				//System.out.println("loadProject call now");
			}
		}		
		
	}
	
	private void projectReloadDialog(){
		//warning icon, custom title
		int n = JOptionPane.showConfirmDialog(
		    this,
		    "Reload Project?",
		    "Confirm",
		    JOptionPane.CANCEL_OPTION,
		    JOptionPane.WARNING_MESSAGE);
		if(n==0){			
			closeProject();
			try {		  
				Thread.sleep(1000);			
				JOptionPane.showMessageDialog(
					    this,
					    "Confirm all 'Stop' lights on DACTA Controllers are on before loading?",
					    "Confirm",
					    JOptionPane.ERROR_MESSAGE);
				loadProject();
			}
			catch (InterruptedException e) {}
		}	
	}
	
	/*
	 * 
	 */
	private void loadProject(){
		System.err.println("defaultProjectFile " + app.getDefaultProjectFile());
		if(projectLoaded){
			System.err.println("Project is already loaded");
		}
		else {
			LoadProjectManagerWorker loadWorker = new LoadProjectManagerWorker(this);
			loadWorker.execute();			
		}
	}
	
	/*
	 * called by the LoadProjectManagerWorker when it is done
	 * 
	 */
	protected void projectLoaded(ProjectManager projectManager){
		DegoGUI.projectManager = projectManager;		
		controllers = ProjectManager.getControllers();
		iNumControllers = controllers.length;
		createControllerGUIs();
		setContentPane(desktop);
		projectLoaded = true;			
		setJMenuBar(createMenuBar());		
		projectCloseMenuItem.setEnabled(true);
		projectReloadMenuItem.setEnabled(true);
		projectLoadMenuItem.setEnabled(false);		
		displayInitReport();		
	}
	
	private void projectCloseDialog(){
		//warning icon, custom title
		int n = JOptionPane.showConfirmDialog(
		    this,
		    "Close Project?",
		    "Confirm",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.WARNING_MESSAGE);
		if(n==0){
			closeProject();
		}
		
	}
	
	private void closeProject(){

		for(int i=0; i<iNumControllers; i++){
			if(controllerGUIs[i] != null){	
				controllerGUIs[i].dispose();
			}				
		}
		iNumControllers = 0;
		projectManager.closeProject();
		projectManager = null;
		DactaControllerGUI.openFrameCount = 0;
		//update the menubar
		projectLoaded = false;
		controllersMenu.setEnabled(false);
		programsMenu.setEnabled(false);
		projectCloseMenuItem.setEnabled(false);
		projectReloadMenuItem.setEnabled(false);
		projectLoadMenuItem.setEnabled(true);

	}	

	protected JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createProgramsMenu());
		menuBar.add(createControllersMenu());				
		return menuBar;
	}
	
	private JMenu createFileMenu(){
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);		
				
		projectReloadMenuItem = new JMenuItem("Reload Project");
		projectReloadMenuItem.setActionCommand("project_reload");
		projectReloadMenuItem.setMnemonic(KeyEvent.VK_R);
		projectReloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(
			  KeyEvent.VK_R, ActionEvent.ALT_MASK));
		projectReloadMenuItem.addActionListener(this);
		fileMenu.add(projectReloadMenuItem);
		
		projectLoadMenuItem = new JMenuItem("Load Project");
		projectLoadMenuItem.setActionCommand("project_load");
		projectLoadMenuItem.setMnemonic(KeyEvent.VK_L);
		projectLoadMenuItem.setAccelerator(KeyStroke.getKeyStroke(
			  KeyEvent.VK_L, ActionEvent.ALT_MASK));
		projectLoadMenuItem.addActionListener(this);
		fileMenu.add(projectLoadMenuItem);
		
		projectCloseMenuItem = new JMenuItem("Close Project");
		projectCloseMenuItem.setActionCommand("project_close");
		projectCloseMenuItem.addActionListener(this);
		fileMenu.add(projectCloseMenuItem);		
		
		fileMenu.addSeparator();
	  	
		eventsViewMenuItem = new JMenuItem("View Events");
		eventsViewMenuItem.setMnemonic(KeyEvent.VK_E);
		eventsViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(
			  KeyEvent.VK_E, ActionEvent.ALT_MASK));
		eventsViewMenuItem.setActionCommand("viewEvents");
		eventsViewMenuItem.addActionListener(this);
		fileMenu.add(eventsViewMenuItem);
		
		systemOutMenuItem = new JCheckBoxMenuItem("system.out", ProjectManager.stdOut);
		systemOutMenuItem.setActionCommand("stdOut");
		systemOutMenuItem.addActionListener(this);
		fileMenu.add(systemOutMenuItem);
		
		systemErrMenuItem = new JCheckBoxMenuItem("system.err", ProjectManager.stdErr);
		systemErrMenuItem.setActionCommand("stdErr");
		systemErrMenuItem.addActionListener(this);
		fileMenu.add(systemErrMenuItem);
		
		fileMenu.addSeparator();
		
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
			  KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		quitMenuItem.setActionCommand("quit");
		quitMenuItem.addActionListener(this);
	  	fileMenu.add(quitMenuItem);
		
		return fileMenu;
	}
	
	private JMenu createControllersMenu(){
		controllersMenu = new JMenu("Controllers");
		controllersMenu.setMnemonic(KeyEvent.VK_C);	
		
		//Set up the first menu item.
		JCheckBoxMenuItem cbMenuItem = null;
		JMenuItem menuItem = new JMenuItem("View All");
		menuItem.setMnemonic(KeyEvent.VK_V);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("viewAll");
		menuItem.addActionListener(this);
		controllersMenu.add(menuItem);
		
		menuItem = new JMenuItem("Hide All");
		menuItem.setMnemonic(KeyEvent.VK_H);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			  KeyEvent.VK_H, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("hideAll");
		menuItem.addActionListener(this);
		controllersMenu.add(menuItem);
		
//				a group of check box menu items
		controllersMenu.addSeparator();
		for(int i=0; i<iNumControllers; i++){
			String guiKey = controllers[i].getName();
			DactaControllerGUI gui = controllerGUIs[i];		
			if(gui!=null){	
				cbMenuItem = new JCheckBoxMenuItem("View " + guiKey, gui.isVisible());
			}
			else {//add disabled menuItem
				cbMenuItem = new JCheckBoxMenuItem("View " + guiKey, false);
				cbMenuItem.setEnabled(false);
			}			
			cbMenuItem.setActionCommand("viewGui" + i);
			if(controllers[i].getStatusCode()!=0){//disable menu item if not connected
				
			}
			cbMenuItem.addActionListener(this);
			controllersMenu.add(cbMenuItem);
		}
			
		controllersMenu.addSeparator();
		
		menuItem = new JMenuItem("Disable Power");
		menuItem.setActionCommand("output_power_off");		
		menuItem.addActionListener(this);
		controllersMenu.add(menuItem);	
		
		menuItem = new JMenuItem("Enable Power");
		menuItem.setActionCommand("output_power_on");		
		menuItem.addActionListener(this);
		controllersMenu.add(menuItem);	
		
		controllersMenu.addSeparator();		
		
		ButtonGroup controlGroup = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Enable Controls");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_E);
		rbMenuItem.setActionCommand("controls_enable");
		rbMenuItem.addActionListener(this);
		controlGroup.add(rbMenuItem);
		controllersMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Read Only");
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		rbMenuItem.setActionCommand("controls_readonly");
		rbMenuItem.addActionListener(this);
		controlGroup.add(rbMenuItem);
		controllersMenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Read Only with Power Override");
		rbMenuItem.setMnemonic(KeyEvent.VK_P);		
		rbMenuItem.setActionCommand("controls_readonly_with_power");
		rbMenuItem.addActionListener(this);
		controlGroup.add(rbMenuItem);
		controllersMenu.add(rbMenuItem);

		return controllersMenu;
	}
	
	private JMenu createProgramsMenu(){
		//Set up the Programs menu.
		programsMenu = new JMenu("Programs");
		programsMenu.setMnemonic(KeyEvent.VK_P);
		
		JMenuItem menuItem = new JMenuItem("Stop All");		
		programsMenu.add(menuItem);
		
		menuItem = new JMenuItem("Run All");		
		programsMenu.add(menuItem);
		
		programsMenu.addSeparator();
		
		menuItem = new JMenuItem("List Programs");		
		programsMenu.add(menuItem);
		
		menuItem = new JMenuItem("with each ");		
		programsMenu.add(menuItem);
		
		menuItem = new JMenuItem("run, stop, view checkboxes");		
		programsMenu.add(menuItem);
		
		return programsMenu;

	}
	
	
	/**
	 *  
	 *
	 */
	protected static void quitDialog() {		
		int n = JOptionPane.showConfirmDialog(
		    null,
		    "Quit Project?",
		    "Confirm",
		    JOptionPane.CANCEL_OPTION,
		    JOptionPane.WARNING_MESSAGE);
		if(n==0){
			EventsViewer.getConsole().stopConsole();		
			projectManager.closeProject();
			System.out.println("Shutting down...");
			System.exit(0);
		}
		
	}
	
	public static void setProjectManager(ProjectManager projectManager){
		DegoGUI.projectManager = projectManager;
	}
	  
	public void sensorEvent(SensorEvent sensorEvent){
	
	}
	
	public void outputChanged(DACTA70909Controller controller, DACTAOutput output, int aOldValue, int errorCode){
		//String eventString = controller.getName() + " - Output #: " + output.getId();
		//String error = 	DACTA70909Controller.errorString(errorCode);
	}
	

	/**
   	* @param int controllerId
   	* @param int status
   	*/
  	public synchronized void controllerStatus(int controllerId, int status){
  		String message = controllers[controllerId].getName() + " on port " + 
  			controllers[controllerId].getPort() + " status updated: " + DACTA70909Controller.errorString(status);
  		System.out.println(message);
	}
	
	public void internalFrameClosing(InternalFrameEvent e) {
		//System.out.println("Internal frame closing: " + e.getSource());
		//closing happens before hiding the frame, no hide frame event so setting menu bar still sees the qui when refreshing
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE) for controllerGuis so we can update menus 
		if(e.getSource() instanceof DactaControllerGUI){
			int guiId = ((DactaControllerGUI)e.getSource()).getId();
			controllerGUIs[guiId].setVisible(false);
			setJMenuBar(createMenuBar());
		}		
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		//System.out.println("Internal frame closed: " + e.getSource());
		//update the menubar
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		//System.out.println("Internal frame opened: " + e.getSource());
		//update the menubar	
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		//System.out.println("Internal frame iconified: " + e.getSource());
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
		//System.out.println("Internal frame deiconified: " + e.getSource());
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		//System.out.println("Internal frame activated: " + e.getSource());
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		//System.out.println("Internal frame deactivated: " + e.getSource());
	}
	  
}
