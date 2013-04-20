/*
 * EventsPanel.java
 * This is a rework of JConsolePane.java
 * will flag System.err messages by wrapping ### around the message ### 
 * use this if performance is an issue vs using the EventsPane which 
 * uses JTextPane with Styles instead of JTextArea 
 * 
 * 
 * Copyright (c) 2004-2006 Gregory Kotsaftis
 * gregkotsaftis@yahoo.com
 * http://zeus-jscl.sourceforge.net/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.degoworks.swing;

import gr.zeus.util.CurrentDateHelper;
import gr.zeus.util.IOHelper;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.degoworks.util.EventsFilter;

/**
 * A java console to replace the command line window. Redirects the stdout
 * and stderr. Customizable. Can save its messages to text file. In addition, 
 * it can auto dump to a log file and clear the textarea. Can be used with
 * JFrame, JInternalFrame or as a JPanel. If used in conjunction with any exe
 * creator for java, e.g: 
 * <a href="http://launch4j.sourceforge.net/">launch4j</a> or
 * <a href="http://jsmooth.sourceforge.net/">JSmooth</a>, 
 * it eliminates the dos application windows and your application looks more
 * professional.
 * <p>
 * @author Gregory Kotsaftis
 * @since 1.0
 */
public final class EventsPanel extends JPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Singleton implementation.
     */
    private static EventsPanel s_consolePane = null;
    
    /**
     * Default output stream.
     */
    private static final PrintStream STDOUT = System.out;

    /**
     * Default error stream.
     */
    private static final PrintStream STDERR = System.err;

    /**
     * Foreground color.
     */
    private static final Color FG_COLOR = Color.WHITE;

    /**
     * Background color.
     */
    private static final Color BG_COLOR = Color.BLACK;

    /**
     * Selected text color.
     */
    private static final Color SLT_COLOR = BG_COLOR;

    /**
     * Selection color.
     */
    private static final Color SL_COLOR = FG_COLOR;
    
    /**
     * Default text font.
     */
    private static final Font TEXT_FONT = new Font("Courier", 0, 12);    
    
    /**
     * Out print stream.
     */
    private transient PrintStream  m_stdoutPS = new PrintStream( 
        new JTextAreaOutStream( new ByteArrayOutputStream() ) );

    /**
     * Error print stream.
     */
    private transient PrintStream  m_stderrPS = new PrintStream( 
        new JTextAreaOutStream( new ByteArrayOutputStream(), true ) );

    /**
     * File chooser for saving messages.
     */
    private JFileChooser m_fileChooser = null;

    /**
     * Confirm title.
     */
    private String m_confimTitle = ResourceBundle.getBundle(
        "gr/zeus/res/jconsole").getString("confimTitle");

    /**
     * File exists warning message.
     */
    private String m_confimMessage = ResourceBundle.getBundle(
        "gr/zeus/res/jconsole").getString("confimMessage");

    /**
     * Default file name, used by the filechooser.
     */
    private String m_messagesFilename = "messages.txt";

    /**
     * Default file name, used by the auto dump functions.
     */
    private String m_traceFilename = "trace.log";

    /**
     * Append or overwrite the trace file
     * the first time JConsolePane tries to write to it?
     */
    private boolean m_appendFirstTime = false;

    /**
     * Auto save the trace file?
     */
    private boolean m_autoSave = false;

    /**
     * Display infinite characters in the textarea, no limit.
     * <p>
     * <b>NOTE:</b> Will slow down your application if a lot of messages
     * are to be displayed to the textarea (more than a couple of Kbytes).
     */
    private int m_maxChars = -1;

    /**
     * Number of times messages have been flushed to the trace file.
     */
    private int m_numFlushes = 0;

    /**
     * Store here all the text for <code>dumpConsole()</code> usage.
     */
    private StringBuffer m_consoleText = new StringBuffer( 64 * 1024 );
    

    /**
     * Private constructor. Initializes the GUI and prepares new streams.
     */
    private EventsPanel()
    {
        initComponents();
        initEventsFilter();
        hideConsole();
        
        ta_messages.setForeground( FG_COLOR );
        ta_messages.setBackground( BG_COLOR );
        ta_messages.setSelectedTextColor( SLT_COLOR );
        ta_messages.setSelectionColor( SL_COLOR );
        ta_messages.setFont( TEXT_FONT );

        m_fileChooser = new JFileChooser();
        m_fileChooser.setDialogType( JFileChooser.SAVE_DIALOG );
        m_fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        m_fileChooser.setMultiSelectionEnabled( false );
        m_fileChooser.setAcceptAllFileFilterUsed( true );
        m_fileChooser.setControlButtonsAreShown( true );
    }
    
    
    /**
     * Singleton constructor.
     * <p>
     * @return  <code>JConsolePane</code>.
     */
    public synchronized static EventsPanel getConsolePane()
    {
        if( s_consolePane==null )
        {
            s_consolePane = new EventsPanel();
        }
        
        return( s_consolePane );
    }

    
    /**
     * Attachs the new streams to stdout and stderr.
     */
    public synchronized void startConsole()
    {
        System.setOut( m_stdoutPS );
        System.setErr( m_stderrPS );
    }
    
    
    /**
     * Attachs the original streams to stdout and stderr.
     */
    public synchronized void stopConsole()
    {
        System.setOut( STDOUT );
        System.setErr( STDERR );
    }
    

    /**
     * Shows the console.
     */
    public synchronized void showConsole()
    {
        setVisible( true );
    }
    

    /**
     * Hides the console.
     */
    public synchronized void hideConsole()
    {
        setVisible( false );
    }
    
    
    /**
     * Shows the control buttons.
     */
    public synchronized void showControlButtons()
    {
        button_panel.setVisible( true );        
    }
    
    
    /**
     * Hides the control buttons.
     */
    public synchronized void hideControlButtons()
    {
        button_panel.setVisible( false );        
    }
    
    
    /**
     * Clears all the messages stored in the internal buffer from the begining
     * of the <code>JConsolePane</code>. Use this if you have printed too many 
     * messages and you want to free up the memory used. Perhaps you should
     * invoke <code>dumpConsole()</code> to store the messages first, unless
     * of course you already have enabled autosave.
     */
    public synchronized void clearBufferMessages()
    {
        m_consoleText.delete(0, m_consoleText.length());
    }
    
    
    /**
     * Clears only the messages that are displayed in the textarea.
     */
    public synchronized void clearScreenMessages()
    {
        ta_messages.setText("");
    }
    
    
    /**
     * Dumps all the console messages (up to now) to a file (not only the
     * messages displayed in the textarea).
     * This method does not clear the messages buffer, use 
     * <code>clearBufferMessages()</code> for this.
     * <p>
     * @param filename  The filename to store all console's messages.
     * @param append    If <code>true</code> text is appended in the file,
     *                  else the file gets overwritten.
     */
    public void dumpConsole(String filename, boolean append)
    {
        String txt = createTimestamp() + m_consoleText.toString();
        try
        {
            IOHelper.saveTxtFile(filename, txt, append);
        }
        catch(Exception e)
        {
            // not much to do if we have error here...
            e.printStackTrace();
        }
    }

    
    /**
     * Destroys the console.
     * Actually what it does is this:
     * Hides the console, clears displayed and buffered messages and stops the
     * console. If you are to invoke <code>getConsole()</code> after this
     * method you will get a brand new console with no messages at all. This
     * method is not needed for simple applications that are about to invoke
     * System.exit() or normaly close.
     */
    public synchronized void destroyConsole()
    {
        hideConsole();
        
        // Clears the messages from JConsolePane (if not done already), because
        // if we have invoked destroyConsole() and will not call System.exit(),
        // right next, invoking getConsole() again and executing showConsole()
        // will show us our old messages...
        clearScreenMessages();
        
        // for the same reason, also clear the string buffer.
        clearBufferMessages();
        
        stopConsole();
        //dispose();
    }


    /**
     * Updates the look and feel of the <code>JConsolePane</code> and it's
     * components. <b>MUST</b> be called <b>AFTER</b> invoking: 
     * <code>UIManager.setLookAndFeel()</code> in your application.
     */
    public synchronized void updateLNF()
    {
        SwingUtilities.updateComponentTreeUI( this );
        SwingUtilities.updateComponentTreeUI( m_fileChooser );
    }
    

    /**
     * Gets the title for the popup window that confirms file overwrite.
     * <p>
     * @return  The title.
     */
    public String getConfimTitle()
    {
        return( m_confimTitle );
    }

    
    /**
     * Sets the title for the popup window that confirms file overwrite.
     * <p>
     * @param s The title.
     */
    public synchronized void setConfimTitle(String s)
    {
        m_confimTitle = s;
    }

    
    /**
     * Gets the text for the popup window that confirms file overwrite.
     * <p>
     * @return  The message.
     */
    public String getConfimMessage()
    {
        return( m_confimMessage );
    }

    
    /**
     * Sets the text for the popup window that confirms file overwrite.
     * <p>
     * @param s The message.
     */
    public synchronized void setConfimMessage(String s)
    {
        m_confimMessage = s;
    }

    
    /**
     * Gets the default filename for the filechooser.
     * <p>
     * @return  The filename.
     */
    public String getMessagesFilename()
    {
        return( m_messagesFilename );
    }

    
    /**
     * Sets the default filename for the filechooser.
     * <p>
     * @param s The filename.
     */
    public synchronized void setMessagesFilename(String s)
    {
        m_messagesFilename = s;
    }

    
    /**
     * Gets the file used by <code>dumpConsole()</code> and 
     * all auto save methods.
     * <p>
     * @return  The filename.
     */
    public String getTraceFilename()
    {
        return( m_traceFilename );
    }

    
    /**
     * Sets the file used by <code>dumpConsole()</code> and 
     * all auto save methods.
     * <p>
     * @param s The filename.
     */
    public synchronized void setTraceFilename(String s)
    {
        m_traceFilename = s;
    }

    
    /**
     * Gets append policy.
     * If the 'traceFilename' exists the first time we try to save the messages,
     * should we append or overwrite?
     * <p>
     * @return  true/false
     */
    public boolean getAppendFirstTime()
    {
        return( m_appendFirstTime );
    }

    
    /**
     * Sets append policy.
     * If the 'traceFilename' exists the first time we try to save the messages,
     * should we append or overwrite?
     * <p>
     * @param b true/false
     */
    public synchronized void setAppendFirstTime(boolean b)
    {
        m_appendFirstTime = b;
    }

    
    /**
     * Gets auto save status.
     * Auto save the console messages to file 'traceFilename' when 'maxChars'
     * are reached and clear the messages (append new messages when required)?
     * <p>
     * @return  true/false
     */
    public boolean getAutoSave()
    {
        return( m_autoSave );
    }

    
    /**
     * Sets auto save status.
     * Auto save the console messages to file 'traceFilename' when 'maxChars'
     * are reached and clear the messages (append new messages when required)?
     * <p>
     * @param b true/false
     */
    public synchronized void setAutoSave(boolean b)
    {
        m_autoSave = b;
    }

    
    /**
     * If -1 no limit, else the messages will be flushed to 'traceFilename' and
     * cleared when this limit is reached.
     * <p>
     * @return  The limit.
     */
    public int getMaxChars()
    {
        return( m_maxChars );
    }

    
    /**
     * If -1 no limit, else the messages will be flushed to 'traceFilename' and
     * cleared when this limit is reached.
     * <p>
     * @param i The limit.
     */
    public synchronized void setMaxChars(int i)
    {
        m_maxChars = i;
    }

    
    /**
     * Gets the foreground color of the textarea.
     * <p>
     * @return  The color.
     */
    public Color getForegroundColor()
    {
        return( ta_messages.getForeground() );
    }
    
    
    /**
     * Sets the foreground color of the textarea.
     * <p>
     * @param c The color.
     */
    public synchronized void setForegroundColor(Color c)
    {
        ta_messages.setForeground( c );
    }


    /**
     * Gets the background color of the textarea.
     * <p>
     * @return  The color.
     */
    public Color getBackgroundColor()
    {
        return( ta_messages.getBackground() );
    }
    
    
    /**
     * Sets the background color of the textarea.
     * <p>
     * @param c The color.
     */
    public synchronized void setBackgroundColor(Color c)
    {
        ta_messages.setBackground( c );
    }


    /**
     * Gets the selected text color of the textarea.
     * <p>
     * @return  The color.
     */
    public Color getSelectedTextColor()
    {
        return( ta_messages.getSelectedTextColor() );
    }
    
    
    /**
     * Sets the selected text color of the textarea.
     * <p>
     * @param c The color.
     */
    public synchronized void setSelectedTextColor(Color c)
    {
        ta_messages.setSelectedTextColor( c );
    }


    /**
     * Gets the selection color of the textarea.
     * <p>
     * @return  The color.
     */
    public Color getSelectionColor()
    {
        return( ta_messages.getSelectionColor() );
    }
    
    
    /**
     * Sets the selection color of the textarea.
     * <p>
     * @param c The color.
     */
    public synchronized void setSelectionColor(Color c)
    {
        ta_messages.setSelectionColor( c );
    }

    
    /**
     * Gets the text of the clear button.
     * <p>
     * @return  The text.
     */
    public String getClearButtonText()
    {
        return( btn_clear.getText() );
    }
    
    
    /**
     * Sets the text of the clear button.
     * <p>
     * @param s The text.
     */
    public synchronized void setClearButtonText(String s)
    {
        btn_clear.setText( s );
    }


    /**
     * Gets the text of the save button.
     * <p>
     * @return  The text.
     */
    public String getSaveButtonText()
    {
        return( btn_save.getText() );
    }
    
    
    /**
     * Sets the text of the save button.
     * <p>
     * @param s The text.
     */
    public synchronized void setSaveButtonText(String s)
    {
        btn_save.setText( s );
    }
    

    /**
     * Gets the text of the close button.
     * <p>
     * @return  The text.
     */
    public String getCloseButtonText()
    {
        return( btn_close.getText() );
    }
    
    
    /**
     * Sets the text of the close button.
     * <p>
     * @param s The text.
     */
    public synchronized void setCloseButtonText(String s)
    {
        btn_close.setText( s );
    }

    
    /**
     * Gets the filechooser title.
     * <p>
     * @return  The title.
     */
    public String getFilechooserTitle()
    {
        return( m_fileChooser.getDialogTitle() );
    }
    

    /**
     * Sets the filechooser title.
     * <p>
     * @param s The title.
     */
    public synchronized void setFilechooserTitle(String s)
    {
        m_fileChooser.setDialogTitle( s );
    }
    

    /**
     * Gets the filechooser approve button text.
     * <p>
     * @return  The text.
     */
    public String getFilechooserApproveButtonText()
    {
        return( m_fileChooser.getApproveButtonText() );
    }
    

    /**
     * Sets the filechooser approve button text.
     * <p>
     * @param s The text.
     */
    public synchronized void setFilechooserApproveButtonText(String s)
    {
        m_fileChooser.setApproveButtonText( s );
    }

    
    /**
     * Gets the font of the textarea.
     * <p>
     * @return  The font.
     */
    public Font getTextFont()
    {
        return( ta_messages.getFont() );
    }
    
    
    /**
     * Sets the font of the textarea.
     * <p>
     * @param f The font.
     */
    public synchronized void setTextFont(Font f)
    {
        ta_messages.setFont( f );
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        javax.swing.JScrollPane text_scrollpane;

        button_panel = new javax.swing.JPanel();
        btn_clear = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        text_scrollpane = new javax.swing.JScrollPane();
        ta_messages = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        button_panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        button_panel.setPreferredSize(new java.awt.Dimension(150, 33));
        btn_clear.setText(java.util.ResourceBundle.getBundle("gr/zeus/res/jconsole").getString("clearMsg"));
        btn_clear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_clearActionPerformed(evt);
            }
        });

        button_panel.add(btn_clear);

        btn_save.setText(java.util.ResourceBundle.getBundle("gr/zeus/res/jconsole").getString("saveMsg"));
        btn_save.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_saveActionPerformed(evt);
            }
        });

        button_panel.add(btn_save);

        btn_close.setText(java.util.ResourceBundle.getBundle("gr/zeus/res/jconsole").getString("closeMsg"));
        btn_close.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btn_closeActionPerformed(evt);
            }
        });

        button_panel.add(btn_close);

        add(button_panel, java.awt.BorderLayout.SOUTH);

        text_scrollpane.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        ta_messages.setColumns(40);
        ta_messages.setEditable(false);
        ta_messages.setRows(20);
        text_scrollpane.setViewportView(ta_messages);

        add(text_scrollpane, java.awt.BorderLayout.CENTER);

    }
    
    private void initEventsFilter(){
    	eventsFilter = new EventsFilter();
    }
    // </editor-fold>//GEN-END:initComponents

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_closeActionPerformed
    {//GEN-HEADEREND:event_btn_closeActionPerformed
        
        do_btn_close();
        
    }//GEN-LAST:event_btn_closeActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_saveActionPerformed
    {//GEN-HEADEREND:event_btn_saveActionPerformed
        
        do_btn_save();
        
    }//GEN-LAST:event_btn_saveActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btn_clearActionPerformed
    {//GEN-HEADEREND:event_btn_clearActionPerformed
        
        do_btn_clear();
        
    }//GEN-LAST:event_btn_clearActionPerformed
    
    
    /**
     * Executes close button command.
     */
    private void do_btn_close()
    {
        hideConsole();
    }
    
    
    /**
     * Executes clear button command.
     */
    private void do_btn_clear()
    {
        clearScreenMessages();
    }
    
    
    /**
     * Executes save button command.
     * Saves only text displayed in the textarea.
     */
    private void do_btn_save()
    {
        m_fileChooser.setSelectedFile( new File(m_messagesFilename) );
        int returnVal = m_fileChooser.showSaveDialog( this );
        if( returnVal==JFileChooser.APPROVE_OPTION )
        {
            File f = m_fileChooser.getSelectedFile();
            
            try
            {
                if( f.exists() )
                {
                    int res = JOptionPane.showConfirmDialog(
                                    this, 
                                    m_confimMessage,
                                    m_confimTitle,
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.QUESTION_MESSAGE);
                    
                    if( res!=0 )
                        return;
                }
                
                IOHelper.saveTxtFile(f, ta_messages.getText(), false);
            }
            catch(Exception e)
            {
                System.err.println( ResourceBundle.getBundle(
                    "gr/zeus/res/jconsole").getString("saveErrorMsg") + " " +
                    e.getMessage());
                
                e.printStackTrace();
            }
        }
        else if( returnVal==JFileChooser.CANCEL_OPTION )
        {
            
        }
        else
        {
            
        }
    }
    

    /**
     * Generate a timestamp signature for the log file.
     */
    private String createTimestamp()
    {
        String timestamp = ResourceBundle.getBundle(
            "gr/zeus/res/jconsole").getString("timestampMessage") + " " +
            String.valueOf( CurrentDateHelper.getCurrentDay() ) + "/" +
            String.valueOf( CurrentDateHelper.getCurrentMonth() ) + "/" +
            String.valueOf( CurrentDateHelper.getCurrentYear() ) + " " +
            String.valueOf( CurrentDateHelper.getCurrentHour() ) + ":" +
            String.valueOf( CurrentDateHelper.getCurrentMinutes() ) + ":" +
            String.valueOf( CurrentDateHelper.getCurrentSeconds() + "\r\n\r\n");
        
        return( timestamp );
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_save;
    private javax.swing.JPanel button_panel;
    private javax.swing.JTextArea ta_messages;
    
    
    private EventsFilter eventsFilter;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Private inner class. Filter to redirect the data to the textarea.
     */
    private final class JTextAreaOutStream extends FilterOutputStream {

        boolean errOutput = false;
        /**
         * Constructor.
         * <p>
         * @param aStream   The <code>OutputStream</code>.
         */
        public JTextAreaOutStream( OutputStream aStream )
        {
            super( aStream );  
        }
        /**
         * Constructor.
         * <p>
         * @param aStream   The <code>OutputStream</code>.
         */
        public JTextAreaOutStream( OutputStream aStream, boolean errOutput )
        {
            super( aStream );  
            this.errOutput = errOutput;
        }

        /**
         * Writes the messages.
         * <p>
         * @param b     The message in a <code>byte[]</code> array.
         * <p>
         * @throws IOException
         */
        public synchronized void write( byte b[] )
            throws IOException
        {
            String s = new String( b );
            appendMessage( s );
            
            flushTextArea();
        }


        /**
         * Writes the messages.
         * <p>
         * @param b     The message in a <code>byte[]</code> array.
         * @param off   The offset.
         * @param len   Length.
         * <p>
         * @throws IOException
         */
        public synchronized void write( byte b[], int off, int len )
            throws IOException
        {
            String s = new String(b, off, len);
            appendMessage( s );
            flushTextArea();
        }

        
        /**
         * Appends a message to the textarea and the 
         * <p>
         * @param s     The message.
         */
        private synchronized void appendMessage(String s)
        {
        	//every second s is newline character
        	boolean newLine = s.equals(System.getProperty("line.separator"));
        	if(newLine || !eventsFilter.filterEventMessage(s)){
        		//if(!newLine)
        		//	s = "### " +  s;
        		if(errOutput){
        			s= "### " +  s;
        		}
        		
	            ta_messages.append( s );
	            m_consoleText.append( s );
	
	            if( m_autoSave )
	            {
	                boolean append = true;
	                if( m_numFlushes==0 && !m_appendFirstTime )
	                {
	                    append = false;
	                }
	
	                try
	                {
	                    IOHelper.saveTxtFile(m_traceFilename, s, append);
	                    m_numFlushes++;
	                }
	                catch(Exception e)
	                {
	                    // not much to do if we have error here...
	                    e.printStackTrace();
	                }
	            }
        	}
        }


        private synchronized void flushTextArea()
        {
            int len = ta_messages.getText().length();

            // Always scroll down to the last line
            ta_messages.setCaretPosition( len );

            // if we have set a maximum characters limit and 
            // we have exceeded that limit, clear the messages
            if( m_maxChars>0 && len > m_maxChars )
            {
                clearScreenMessages();
            }
        }

    }

}
