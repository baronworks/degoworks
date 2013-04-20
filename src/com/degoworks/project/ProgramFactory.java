/*
 * Created on Mar 6, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.project;


public class ProgramFactory {
	/**
     * This method instantiates a particular subclass implementing
     * the abstract methods based on the information obtained from the
     * deployment descriptor
     */
    public static Program getProgram(String className) throws InvalidProjectConfigException {
    	Program program = null;
        if(className==null || className == "")
        {
            throw new InvalidProjectConfigException("Program is not defined");
        }
        else
        {
            try {
            	program = (Program) Class.forName(className).newInstance();
            } catch (Exception e) {
            	String message = "Program.getProgram: className '" + className + "' not found";
            	System.err.println("InvalidProjectConfigException " + message);
                throw new InvalidProjectConfigException(message);
            }
        }
        return program;
    }
    
    public static Program getProgram(ProgramConfig programConfig) throws InvalidProjectConfigException {
    	Program program = null;
        if(programConfig.className==null || programConfig.className == "")
        {
            throw new InvalidProjectConfigException("Program is not defined");
        }
        else
        {
            try {
            	program = (Program) Class.forName(programConfig.className).newInstance();
            	program.setId(programConfig.id);
            	program.setName(programConfig.name);
            } catch (Exception e) {
            	String message = "Program.getProgram: className '" + programConfig.className + "' not found";
            	System.err.println("InvalidProjectConfigException " + message);
                throw new InvalidProjectConfigException(message);
            }
        }
        return program;
    }    
}
