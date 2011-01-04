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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * The Sms2Email Activity
 * 
 * @author Juergen Felleisen <juergen.felleisen@googlemail.com>
 */
public class Sms2Email extends Activity implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "Sms2Email"; /**< log tag */
    
    private String  m_emailAddress     = null; /**< receiver email address */
    private String  m_googleAddress    = null; /**< sender Google email address */
    private String  m_googlePassword   = null; /**< sender Google password */
    private Boolean m_forwardingActive = true; /**< forwarding active flag */
    
    /**
     * updates the contents of the main screen
     */
    private void updateMainScreen()
    {
        ((TextView) findViewById(R.id.email_address)).setText(m_emailAddress);
        ((TextView) findViewById(R.id.forwarding)).setText(
                getString(R.string.forwarding) + " " +
                (m_forwardingActive ? getString(R.string.active) : getString(R.string.inactive)));
        ((TextView) findViewById(R.id.forwarding)).setTextColor(
                (m_forwardingActive ? 0xff00ff00 : 0xffff0000));
    }

    /**
     * reads the configuration and shows the start screen
     * @see android.app.Activity#onCreate()
     */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* initialize configured email address and show main view */
        getConfig();
        setContentView(R.layout.main);
        updateMainScreen();
    }
    
    /**
     * rereads the configuration and updates the start screen
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        getConfig();
        updateMainScreen();
    }

    /**
     * initializes the main menu
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * reacts on main menu selections
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_config: /* configuration menu entry */
                try
                {
                    Intent preferencesActivity = new Intent(getBaseContext(), Sms2EmailPreferences.class);
                    startActivity(preferencesActivity);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "start preferences activity - " + e.toString());
                }
                return true;

            case R.id.menu_test: /* test mail */
                SendEmailThread sendEmailThread;
                Mail m = new Mail(m_googleAddress, m_googlePassword);
                String[] toArr = { m_emailAddress };
                m.setTo(toArr);
                m.setFrom(m_googleAddress);
                m.setSubject(getString(R.string.testmail_subject));
                m.setBody(getString(R.string.testmail_subject) + "\n" +
                          getString(R.string.sender) + ": " + m_googleAddress + "\n" +
                          getString(R.string.receiver) + ": " + m_emailAddress + "\n");
                sendEmailThread = new SendEmailThread(m);
                sendEmailThread.start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * updates the members according to the changed preference
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if      (key.contentEquals("emailAddress"))
        {
            m_emailAddress = sharedPreferences.getString("emailAddress",
                    getString(R.string.no_address_specified));
        }
        else if (key.contentEquals("googleAddress"))
        {
            m_googleAddress = sharedPreferences.getString("googleAddress",
                    getString(R.string.no_address_specified));
        }
        else if (key.contentEquals("googlePassword"))
        {
            m_googlePassword = sharedPreferences.getString("googlePassword",
                    getString(R.string.no_address_specified));
        }
        else if (key.contentEquals("forwardingActive"))
        {
            m_forwardingActive = sharedPreferences.getBoolean("forwardingActive", true);
        }
        else
        {
            Log.e(TAG, "onSharedPreferenceChanged: unknown key " + key);
        }
    }
    
    /**
     * reads configuration
     */
    private void getConfig()
    {
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            m_emailAddress = preferences.getString("emailAddress", getString(R.string.no_address_specified));
            m_googleAddress = preferences.getString("googleAddress", getString(R.string.no_address_specified));
            m_googlePassword = preferences.getString("googlePassword", getString(R.string.no_address_specified));
            m_forwardingActive = preferences.getBoolean("forwardingActive", true);
        }
        catch (Exception e)
        {
            Log.e(TAG, "getConfig(): " + e.toString());
        }
    }
}
