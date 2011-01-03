/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

/*
 *  Copyright 2011 Juergen Felleisen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
     * sends an email
     * 
     * @param   context             context
     * @param   originatingAddress  originating address
     * @param   message             message contents
     */
    private void sendEmail(Context context, String originatingAddress, String message)
    {
        Mail m = new Mail(m_googleAddress, m_googlePassword);
        String[] toArr = { m_emailAddress };
        m.setTo(toArr);
        m.setFrom(m_googleAddress);
        m.setSubject(context.getString(R.string.mail_subject) + " " + originatingAddress);
        m.setBody(context.getString(R.string.sender) + ": "  + originatingAddress + "\n" +
                  context.getString(R.string.message) + ":\n" + message + "\n");
        SendEmailThread sendEmailThread = new SendEmailThread(m);
        sendEmailThread.start();
        Toast toast = Toast.makeText(context, "[Sms2Email] - SMS from " + originatingAddress, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * unpacks the SMS and forms an Mail object that is passed to
     * the email sending thread
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        getConfig(context);
        String originatingAddress = "";
        String message = "";
        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        for (int n = 0; n < messages.length; n++)
        {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) messages[n]);
            
            if (originatingAddress.contentEquals(""))
            {
                originatingAddress = smsMessage.getOriginatingAddress();
                message = smsMessage.getMessageBody();
            }
            else if (!originatingAddress.contentEquals(smsMessage.getOriginatingAddress()))
            {
                sendEmail(context, originatingAddress, message);
                originatingAddress = smsMessage.getOriginatingAddress();
                message = smsMessage.getMessageBody();
            }
            else
            {
                message += smsMessage.getMessageBody();
            }
        }
        sendEmail(context, originatingAddress, message);
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
