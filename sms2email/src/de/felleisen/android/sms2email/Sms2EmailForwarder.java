/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import java.io.DataInputStream;
import java.io.FileInputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Receives SMS and forwards it to email recipients.
 * 
 * @author Juergen Felleisen <juergen.felleisen@googlemail.com>
 */
public class Sms2EmailForwarder extends BroadcastReceiver
{
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
	
	/**
	 * Formats a SMS message into a user friendly string.
	 * 
	 * @param   smsMessage  array of received SMS messages
	 * @return  formatted string
	 */
	private String formatSms(SmsMessage smsMessage)
	{
		return ( 
			"Absender : " + smsMessage.getOriginatingAddress() + "\n" +
			"Nachricht:\n" + smsMessage.getMessageBody() + "\n");
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		getEmailConfig(context);
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[])bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++)
		{
			smsMessage[n] = SmsMessage.createFromPdu((byte[])messages[n]);
			
			SendEmailThread sendEmailThread;
			Mail m = new Mail(m_googleAddress, m_googlePassword);
			String[] toArr = {m_emailAddress};
			m.setTo(toArr);
			m.setFrom(m_googleAddress); 
			m.setSubject("[Sms2Email] - SMS from " + smsMessage[n].getOriginatingAddress());
			m.setBody(formatSms(smsMessage[n]));
			sendEmailThread = new SendEmailThread(m);
			sendEmailThread.start();
	    	Toast toast = Toast.makeText(context, formatSms(smsMessage[n]), Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
    private void getEmailConfig(Context context)
    {
    	try
    	{
			FileInputStream fis = context.openFileInput(Sms2Email.EMAIL_ADDRESS_CFG);
			DataInputStream dis = new DataInputStream(fis);
			m_emailAddress = dis.readLine();
			m_googleAddress = dis.readLine();
			m_googlePassword = dis.readLine();
			fis.close();
    	}
    	catch (Exception e)
    	{
    		Log.e("Sms2Email", "getEmailConfig: " + e.toString());
    	}
    }
}
