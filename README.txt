DegoWorks Software Overview
---------------------------
Is a java project built to allow for communication with Lego's dacta 70909 serial port controllers 
as well as lejos enabled mindstorms robotics.

- 8 dacta controllers to be ran simultaneously (gui only limitation)
- lego USB tower and remote integration WILL NOT work on Windows x64 systems
	- (not under active development)
	- possible Serial Tower integration http://www.rjmcnamara.com/lego-minstorms/lego-mindstorms-ris-windows7/
- XML configuration to map functionality to Controllers and Mindstorms
- Individual Programs can be programmed by extending the com.degoworks.DegoManager.ProgramImpl class
	which then allows for the degoworks software to use reflection for mapping
	custom code to events triggered by the controllers as well as outputs

- the DACTA70909Controller is based on code from http://www.timo.dk/wp/2009/01/25/java-control-class-for-dacta-70909/
	which handles the communication with a dacta controller

	
Usage
-----
 - run com.degoworks.rxtx.MyRS232 to determine what serial ports are available
	run once without any controllers to see if any are already being used
 - set project in com.degoworks.project.DomProjectParser, 
 	- test_controller.project.xml 
 		configuration for one dacta controller 
 		uses program com.degoworks.DegoManager.ProgramImpl 		
	- light_me_up.project.xml uses 2 controllers and has remote configuration
		- configuration for 2 dacta controllers and remote (remote is commented out)
		- uses program programs.LightMeUp
		
Platforms
---------
 - In theory should be able to get it to work on any platform
 - originally built with java 1.5 on windows XP
 - most of it working with java 1.7 on Windows 7 x 64
 - windows 32 bit versions should be able to use the lego USB tower and remote
  	- no 64 bit drivers for the usb tower  
  	- http://www.sevenforums.com/drivers/3445-lego-usb-tower.html
		use serial port tower with usb-serial adaptar 

RXTX - Java library for serial and parallel communication
---------------------------------------------------------
RXTX is an open source library that is used to communicate with the DACTA 70909
serial controller

they have their own drivers and jar file

http://rxtx.qbang.org/wiki/index.php/Main_Page
http://rxtx.qbang.org/wiki/index.php/Installation_for_Windows
64 bit versions for different platforms- http://www.cloudhopper.com/opensource/rxtx/

Identify your Java Development Kit's folder. For version 1.6.0, this usually is

    c:\Program Files\Java\jdk1.6.0_01\ 
    * Copy rxtxSerial.dll to c:\Program Files\Java\jre6\bin\
    * Copy RXTXcomm.jar to build path of project or to c:\Program Files\Java\jre6\lib\ext\
    
Java look and feel
------------------
 - java look and feel graphics are in jlfgr-1_0.jar and tango-icon-theme-0.8.0.jar
 	

TODO
----
 - a lot
 - update existing code to have it working without the deprecation warnings
 - lejos NXT generation integration, this will likely become the main tool for 
 	sending commands to the program using bluetooth and/or power function remotes
 	- going to 
 - dynamic mapping of controllers to ports
 - flash window with project selector
 - allow for multiple programs to be run as one project

Other Notes
-----------
 It has been several years since I have had a chance to work on this program, as such there
 is a lot of code that needs to be updated to the latest java versions. In addition the java
 skills are a little lacking these days.
 
 Occasionally when closed the interface the javaw process may not exit properly and the process
 needs to be manually shut down.
 
 Eclipse is the preferred IDE simply because of its lejos NXT integration. 



