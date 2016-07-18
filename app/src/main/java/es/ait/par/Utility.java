package es.ait.par;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aitkiar on 18/07/16.
 */
public class Utility
{
    private final static Activity[] activities = new Activity[] {
            new Activity( R.string.activityEmpty, "Empty", "empty", null, null ),
            new Activity( R.string.activityCycling, "cycling", "cycling", new double[] { 16, 19, 22.5, 24, 30, 32.2 }, new double[]{4, 6, 8, 10, 12, 16 }),
            new Activity( R.string.activityRunning, "running", "running", new double[] {8.4, 9.6, 10.8, 11.3, 12.1, 12.9, 13.8, 14.5, 16.1, 17.5}, new double[]{9.0, 10.0, 11.0, 11.5, 12.5, 13.5, 14.0, 15.0, 16.0, 18.0}),
            new Activity( R.string.activityWalking, "walking", "walking", new double[] {4.5, 5.3, 6.4}, new double[]{ 3.3, 3.8, 5})
    };

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private static final DecimalFormat speedAndDistanceFormat = new DecimalFormat("###.##");
    private static final DecimalFormat twoDigitsFormat = new DecimalFormat("00");
    private static final DecimalFormat caloriesFormat = new DecimalFormat("######");

    public static final String LOGCAT_TAG="[PAR]";


    public static Activity[] getActivities()
    {
        return activities;
    }

    public static Activity getActivityByName( String name )
    {
        for ( Activity activty : activities )
        {
            if ( activty.getName().equals( name ))
            {
                return activty;
            }
        }
        return null;
    }


    /**
     * Gets a drawable from the icon method.
     * @param activity
     * @return
     */
    public static Drawable getImage(Context context, Activity activity )
    {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier( activity.getIcon(), "drawable",
                context.getPackageName());
        return  ResourcesCompat.getDrawable( resources, resourceId, null );
    }

    public static synchronized String formatDate( Date date )
    {
        return sdf.format( date );
    }

    public static synchronized String formatSpeedAndDistance( double value )
    {
        return speedAndDistanceFormat.format( value );
    }

    public static synchronized String formatTime( long seconds )
    {
        return twoDigitsFormat.format( seconds / 3600 ) + ":" + twoDigitsFormat.format((seconds % 3600)/ 60 ) + ":" + twoDigitsFormat.format((seconds % 3600)% 60 );
    }

    public static synchronized String formatTime( double seconds )
    {
        return twoDigitsFormat.format( seconds / 3600 ) + ":" + twoDigitsFormat.format((seconds % 3600)/ 60 ) + ":" + twoDigitsFormat.format((seconds % 3600)% 60 );
    }

    public static synchronized String formatCalories( double calories )
    {
        return caloriesFormat.format( calories );
    }
}
