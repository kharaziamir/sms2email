/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import android.util.Log;

/**
 * sends an email as background job
 * 
 * @author juergen
 */
public class SendEmailThread extends Thread
{
    private static final String TAG = "Sms2Email - SendEmailThread"; /**< log tag */

    private Mail m_mail; /**< Mail opject to be sent */

    /**
     * constructor takes the mail object and fills the corresponfding attribute
     * 
     * @param   mail  mail object to be sent
     */
    public SendEmailThread(final Mail mail)
    {
        m_mail = mail;
    }

    /**
     * sends the mail object
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        try
        {
            if (m_mail.send())
            {
                Log.i(TAG, "Mail.send() SUCCESS");
            }
            else
            {
                Log.e(TAG, "Mail.send() FAILED");
            }
        }
        catch (final Exception e)
        {
            Log.e(TAG, "Mail.send(): " + e.toString());
        }
    }
}
