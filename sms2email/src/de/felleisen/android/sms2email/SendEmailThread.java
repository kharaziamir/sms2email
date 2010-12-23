/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import android.util.Log;


/**
 * Sends an email.
 * 
 * @author juergen
 */
public class SendEmailThread extends Thread
{
	Mail m_mail;
	
	public SendEmailThread(Mail mail)
	{
		m_mail = mail;
	}
	
	@Override
	public void run()
	{
		try
		{
			if (m_mail.send())
			{
				Log.i("Sms2Email - Mail::send()", "SUCCESS");
			}
			else
			{
				Log.e("Sms2Email - Mail::send()", "FAILED");
			}
		}
		catch (Exception e)
		{
			Log.e("Sms2Email - Mail::send()", e.toString());
		}
	}
}
