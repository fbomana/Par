package es.ait.par;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aitkiar on 14/05/16.
 */
public class RecordingDaemon extends Service implements LocationListener
{
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    private static final int NOTIFICATION_ID = 1;

    private final int GPS_INTERVAL = 2000;
    private final int GPS_MINIMUN_DISTANCE = 0;
    private final int ACCURACY_LIMIT = 20;

    private final String LOGCAT_TAG="[PAR]";
    LocationManager locationManager = null;
    
    // Location save.
    private long bestLocationTime;
    private Location bestLocation = null;
    private long lastSavedLocationTime;
    private Location lastSavedLocation = null;

    private boolean gpsDisabled = false;
    private RecordedData data = RecordedData.getInstance();
    private Activity activity;
    private double weight = 98;
    private int accuracy;

    private DecimalFormat speedAndDistanceFormat = new DecimalFormat("###.##");
    private DecimalFormat twoDigitsFormat = new DecimalFormat("00");

    private Timer timer;

    /**
     * Process the actions sent to the service.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        Log.d( LOGCAT_TAG, "OnStartCommand. Action: " + intent.getAction());
        if ( locationManager == null )
        {
            Log.d( LOGCAT_TAG, "Null locationManager. Getting new one");
            locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        }
        switch ( intent.getAction() )
        {
            case ACTION_START:
            {
                data.reset();
                if ( locationManager != null )
                {
                    Log.d( LOGCAT_TAG, "Requesting location updates");
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, GPS_INTERVAL, GPS_MINIMUN_DISTANCE, this);
                    data.setStatus( RecordedData.STATUS_RECORDING );
                    startForeground( NOTIFICATION_ID, getNotification() );

                    timer = new Timer();
                    timer.scheduleAtFixedRate( new TimerTask() {
                            public void run() {
                                // We only count time if we have a location save.
                                if ( lastSavedLocation != null && data.getStatus() == RecordedData.STATUS_RECORDING )
                                {
                                    data.nextTime();
                                }
                            }
                        }, 0, 1000);
                }
                break;
            }
            case ACTION_STOP:
            {
                if ( locationManager != null )
                {
                    Log.d( LOGCAT_TAG, "Requesting end of location updates");
                    locationManager.removeUpdates( this );
                }
                data.setStatus( RecordedData.STATUS_NOT_RECORDING );
                timer.cancel();
                NotificationManagerCompat.from( this ).cancel( NOTIFICATION_ID );
                stopSelf();
                break;
            }
            case ACTION_PAUSE:
            {
                saveBestLocation();
                data.setStatus( RecordedData.STATUS_PAUSE );
                lastSavedLocation = null;
                lastSavedLocationTime = 0;
                bestLocation = null;
                bestLocationTime = 0;
                break;
            }
            case ACTION_RESUME:
            {
                data.setStatus( RecordedData.STATUS_RECORDING );
                break;
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Updates the RecordedData singleton with the new location if it's accuracy it's good enough.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location)
    {
        Log.d( LOGCAT_TAG, "Location Changed Event recived" );
        Log.d( LOGCAT_TAG, "["+ location.getProvider() + "] lat:" + location.getLatitude() + " Long: " + location.getLongitude() + " Acc:" + location.getAccuracy());
        long actualTime = System.currentTimeMillis();
        if ( bestLocation == null )
        {
            Log.d( LOGCAT_TAG, "Best location null" );
            bestLocation = location;
            bestLocationTime = actualTime;
            if ( lastSavedLocation == null )
            {
                lastSavedLocationTime = actualTime;
            }
        }
        else
        {
            if ( actualTime - lastSavedLocationTime >= 10000  ||
                bestLocation.distanceTo( location ) > bestLocation.getAccuracy() + location.getAccuracy()  )
            {
                Log.d( LOGCAT_TAG, "Ten secconds or distance trigger" );
                saveBestLocation();
                bestLocationTime = actualTime;
                bestLocation = location;
            }
            else if ( location.getAccuracy() < bestLocation.getAccuracy() )
            {
                bestLocation = location;
                bestLocationTime = actualTime;
            }
        }
    }

    private void saveBestLocation()
    {
        Log.d( LOGCAT_TAG, "Save best location" );
        if (lastSavedLocation != null )
        {
            Log.d( LOGCAT_TAG, "lastSavedLocation != null " );
            double partialDistance = lastSavedLocation.distanceTo(bestLocation);
            long partialTime = (bestLocationTime - lastSavedLocationTime) / 1000;
            double speed = partialDistance / partialTime;
            data.updateDistance(partialDistance);
            data.updateCalories(speed * 3.6, weight, partialTime);
            data.setActualAccuracy(bestLocation.getAccuracy());
            data.setActualSpeed(speed);
            notifyRecording();
        }
        lastSavedLocation = bestLocation;
        lastSavedLocationTime = bestLocationTime;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d( LOGCAT_TAG, "Location provider " + provider + " enabled" );
        if ( provider.equals( LocationManager.GPS_PROVIDER ) )
        {
            gpsDisabled = false;
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d( LOGCAT_TAG, "Location provider " + provider + " disabled" );
        if ( provider.equals( LocationManager.GPS_PROVIDER ) )
        {
            gpsDisabled = false;
            Toast.makeText( this, "GPS disabled", Toast.LENGTH_LONG ).show();
        }
    }

    private void notifyRecording()
    {
        NotificationManagerCompat.from( this ).notify( NOTIFICATION_ID, getNotification() );
    }

    private Notification getNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( getString( data.getActivity().getId()));
        builder.setContentText( getString( R.string.time ) + ":" + twoDigitsFormat.format( data.getTime() / 3600 ) + ":" + twoDigitsFormat.format( ( data.getTime() % 3600 ) / 60 ) + "." + twoDigitsFormat.format( ( data.getTime() % 3600 ) % 60 )  + "   " + getString( R.string.distance ) + ":" + speedAndDistanceFormat.format( data.getDistance()));
        builder.setSmallIcon(  android.R.drawable.star_on );

        Intent resultIntent = new Intent(this, ParMainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ParMainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }
}
