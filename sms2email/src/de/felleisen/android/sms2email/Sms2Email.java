/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
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
	 * configuration file
	 */
	static final String CFG_FILE = "Sms2Email.cfg";
	
	/**
	 * configuration dialog
	 */
	Dialog m_configDialog = null;
	
	/**
	 * receiver email address
	 */
	String m_emailAddress = null;
	
	/**
	 * sender email address
	 */
	String m_googleAddress = null;
	
	/**
	 * sender email password
	 */
	String m_googlePassword = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        /* initialize configured email address and show main view */
        getConfig();
        setContentView(R.layout.main);
        ((TextView)findViewById(R.id.email_address)).setText(m_emailAddress);
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
    		case R.id.menu_test:   /* test mail */
    			SendEmailThread sendEmailThread;
    			Mail m = new Mail(m_googleAddress, m_googlePassword);
    			String[] toArr = {m_emailAddress};
    			m.setTo(toArr); 
    			m.setFrom(m_googleAddress); 
    			m.setSubject("[Sms2Email] - Testmail"); 
    			m.setBody(
    					"Dies ist eine von Sms2Email generierte Testmail\n" +
    					"Absender : " + m_googleAddress + "\n" +
    					"Empfänger: " + m_emailAddress  + "\n");
    			sendEmailThread = new SendEmailThread(m);
    			sendEmailThread.start();
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
    			((TextView)layout.findViewById(R.id.edit_google_address)).setText(m_googleAddress);
    			((TextView)layout.findViewById(R.id.edit_google_password)).setText(m_googlePassword);
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
    			m_emailAddress   = ((TextView)m_configDialog.findViewById(R.id.edit_email_address)).getText().toString();
    			m_googleAddress  = ((TextView)m_configDialog.findViewById(R.id.edit_google_address)).getText().toString();
    			m_googlePassword = ((TextView)m_configDialog.findViewById(R.id.edit_google_password)).getText().toString();
    			((TextView)findViewById(R.id.email_address)).setText(m_emailAddress);
    			setConfig();
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
     * writes configuration
     */
    private void setConfig()
    {
    	try
    	{
    		SharedPreferences settings = getSharedPreferences(CFG_FILE, 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putString("emailAddress",   m_emailAddress);
    		editor.putString("googleAddress",  m_googleAddress);
    		editor.putString("googlePassword", m_googlePassword);
    		editor.commit();
    	}
    	catch (Exception e)
    	{
    		Log.e("Sms2Email", "Sms2Email.setEmailConfig():" + e.toString());
    	}
    }
    
    /**
     * reads configuration
     */
    private void getConfig()
    {
    	try
    	{
    		SharedPreferences settings = getSharedPreferences(CFG_FILE, 0);
    		m_emailAddress   = settings.getString("emailAddress",   getString(R.string.no_address_specified));
    		m_googleAddress  = settings.getString("googleAddress",  getString(R.string.no_address_specified));
    		m_googlePassword = settings.getString("googlePassword", getString(R.string.no_address_specified));
    	}
    	catch (Exception e)
    	{
    		Log.e("Sms2Email", "Sms2Email.getEmailConfig():" + e.toString());
    	}
    }
}
