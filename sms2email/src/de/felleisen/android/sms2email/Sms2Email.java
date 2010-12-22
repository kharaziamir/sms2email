/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The Sms2Email Activity
 * 
 * @author Juergen Felleisen <juergen.felleisen@googlemail.com>
 */
public class Sms2Email extends Activity implements OnClickListener, OnDismissListener, OnCancelListener
{
	/**
	 * configuration dialog ID
	 */
	static final int DIALOG_CONFIG = 0;
	
	/**
	 * email receiver configuration file
	 */
	static final String EMAIL_ADDRESS_CFG = "email_address.cfg";
	
	/**
	 * configuration dialog
	 */
	Dialog m_configDialog = null;
	
	/**
	 * receiver email address
	 */
	String m_emailAddress = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        /* initialize configured email address and show main view */
        m_emailAddress = new String (getEmailAddress());
        setContentView(R.layout.main);
        ((TextView)findViewById(R.id.email_address)).setText(m_emailAddress);
    }
    
	/**
	 * shows an alert;
	 * 
	 * @param title  title of the alert
	 * @param text   message of the alert
	 */
	private void showAlert (String title, String text)
	{
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(title);
    	builder.setMessage(text);
    	builder.setPositiveButton("OK", null);
    	builder.create().show();
	}
	
	/**
	 * shows an alert describing an exception
	 * 
	 * @param e  exception to be described
	 */
	private void showException (Exception e)
    {
		showAlert("Exception", e.toString());
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case R.id.menu_config: /* configuration menu entry */
   				showDialog(DIALOG_CONFIG);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    protected Dialog onCreateDialog(int id)
    {
    	Dialog dialog = null;
    	
    	switch (id)
    	{
    		case DIALOG_CONFIG:
    			/* create and initialize configuration dialog */
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    			View layout = inflater.inflate(R.layout.config, (ViewGroup)findViewById(R.layout.main));
    			((TextView)layout.findViewById(R.id.edit_email_address)).setText(m_emailAddress);
    			builder.setTitle(getText(R.string.config));
    			builder.setView(layout);
    			builder.setPositiveButton(getText(R.string.ok), this);
    			builder.setNegativeButton(getText(R.string.cancel), this);
    			m_configDialog = builder.create();
    			dialog = m_configDialog;
    			break;
    	}
    	
    	return dialog;
    }
    
    public void onClick(DialogInterface dialog, int which)
    {
    	switch (which)
    	{
    		case DialogInterface.BUTTON_POSITIVE:
    			/* set new email address */
    			m_emailAddress = ((TextView)m_configDialog.findViewById(R.id.edit_email_address)).getText().toString();
    			((TextView)findViewById(R.id.email_address)).setText(m_emailAddress);
    			setEmailAddress(m_emailAddress);
    			break;
    			
    		case DialogInterface.BUTTON_NEGATIVE:
    			/* restore email address in configuration dialog */
    			((TextView)m_configDialog.findViewById(R.id.edit_email_address)).setText(m_emailAddress);
    			break;
    	}
    }
    
    public void onDismiss(DialogInterface dialog)
    {
		/* restore email address in configuration dialog */
    	/* TODO: does not work when user presses back */
		((TextView)m_configDialog.findViewById(R.id.edit_email_address)).setText(m_emailAddress);
    }
    
    public void onCancel(DialogInterface dialog)
    {
		/* restore email address in configuration dialog */
    	/* TODO: does not work when user presses back */
		((TextView)m_configDialog.findViewById(R.id.edit_email_address)).setText(m_emailAddress);
    }
    
    /**
     * writes a email address into the email address configuration file
     * 
     * @param emailAddress
     */
    private void setEmailAddress(String emailAddress)
    {
    	try
    	{
    		FileOutputStream fos = openFileOutput (EMAIL_ADDRESS_CFG, Context.MODE_PRIVATE);
    		DataOutputStream dos = new DataOutputStream(fos);
    		dos.write((emailAddress + "\n").getBytes());
			fos.close();
    	}
    	catch (Exception e)
    	{
    		showException(e);
    	}
    }
    
    /**
     * reads the email address from the email address configuration file
     * 
     * @return read email address
     */
    private String getEmailAddress()
    {
    	String emailAddress = new String (this.getString(R.string.no_address_specified));
    	
    	try
    	{
			FileInputStream fis = openFileInput(EMAIL_ADDRESS_CFG);
			DataInputStream dis = new DataInputStream(fis);
			emailAddress = dis.readLine();
			fis.close();
    	}
    	catch (Exception e)
    	{
    		setEmailAddress(emailAddress);
    	}
    	
    	return emailAddress;
    }
}