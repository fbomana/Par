package es.ait.par;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Class created to manage the preferences of the PAR application.
 */
public class PreferencesScreen extends PreferenceActivity
{

    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_GPX_SAVE = "gpx_save";
    public static final String KEY_GPX_CLEANUP = "gpx_cleanup";
    public static final String KEY_DB_SAVE = "db_save";
    public static final String KEY_DB_CLEANUP_MONTH = "db_cleanup_month";
    public static final String KEY_DB_CLEANUP_YEAR = "db_cleanup_year";

    @Override
    public void onCreate( Bundle savedInstance )
    {
        super.onCreate( savedInstance );
        addPreferencesFromResource( R.xml.par_preferences );
    }
}
