/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private String m_emailAddress   = null; /**< receiver email address */
    private String m_googleAddress  = null; /**< sender Google email address */
    private String m_googlePassword = null; /**< sender Google email password */

    /**
     * Formats a SMS message into a user friendly string.
     * 
     * @param smsMessage
     *            array of received SMS messages
     * @return formatted string
     */
    private String formatSms(final SmsMessage smsMessage)
    {
        return ("Absender : " + smsMessage.getOriginatingAddress() + "\n" +
                "Nachricht:\n" + smsMessage.getMessageBody() + "\n");
    }

    /**
     * unpacks the SMS and forms an Mail object that is passed to
     * the email sending thread
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        getConfig(context);
        final Bundle bundle = intent.getExtras();
        final Object messages[] = (Object[]) bundle.get("pdus");
        final SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++)
        {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);

            SendEmailThread sendEmailThread;
            Mail m = new Mail(m_googleAddress, m_googlePassword);
            String[] toArr = { m_emailAddress };
            m.setTo(toArr);
            m.setFrom(m_googleAddress);
            m.setSubject("[Sms2Email] - SMS from "
                    + smsMessage[n].getOriginatingAddress());
            m.setBody(formatSms(smsMessage[n]));
            sendEmailThread = new SendEmailThread(m);
            sendEmailThread.start();
            final Toast toast = Toast.makeText(context, formatSms(smsMessage[n]), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * reads configuration and saves it into the corresponding attributes
     * 
     * @param   context  application context
     */
    private void getConfig(Context context)
    {
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            m_emailAddress = preferences.getString("emailAddress", context.getString(R.string.no_address_specified));
            m_googleAddress = preferences.getString("googleAddress", context.getString(R.string.no_address_specified));
            m_googlePassword = preferences.getString("googlePassword", context.getString(R.string.no_address_specified));
        }
        catch (Exception e)
        {
            Log.e("Sms2Email", "Sms2EmailForwarder.getConfig(): " + e.toString());
        }
    }
}
