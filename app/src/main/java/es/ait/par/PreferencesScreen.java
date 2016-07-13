package es.ait.par;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Class created to manage the preferences of the PAR application.
 */
public class PreferencesScreen extends PreferenceActivity
{
    @Override
    public void onCreate( Bundle savedInstance )
    {
        super.onCreate( savedInstance );
        addPreferencesFromResource( R.xml.par_preferences );
    }
}
