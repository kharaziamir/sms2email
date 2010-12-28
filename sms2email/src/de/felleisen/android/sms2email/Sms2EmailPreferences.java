/**
 * Sms2Email:
 * Android application that automatically forwards received SMS
 * to a email addresses.
 */

package de.felleisen.android.sms2email;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * PreferenceActivita for Sms2Email
 * 
 * @author juergen
 */
public class Sms2EmailPreferences extends PreferenceActivity
{
    /**
     * creates preference dialog from XML
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
