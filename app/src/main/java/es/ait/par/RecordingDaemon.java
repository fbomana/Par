package es.ait.par;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by aitkiar on 14/05/16.
 */
public class RecordingDaemon extends Service implements LocationListener
{
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    private final int GPS_INTERVAL = 5000;
    private final int GPS_MINIMUN_DISTANCE = 3;
    private final int ACCURACY_LIMIT = 20;

    private final String LOGCAT_TAG="[PAR]";
    LocationManager locationManager = null;
    
    
    private long lastTime;
    private Location lastLocation = null;
    private boolean gpsDisabled = false;
    private RecordedData data = RecordedData.getInstance();
    private Activity activity;
    private double weight = 98;
    private int accuracy;

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
                stopSelf();
                break;
            }
            case ACTION_PAUSE:
            {
                data.setStatus( RecordedData.STATUS_PAUSE );
                lastLocation = null;
                lastTime = 0;
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
        long actualTime = System.currentTimeMillis();
        long partialTime = (  actualTime - lastTime ) / 1000;
        if ( data.getStatus() == RecordedData.STATUS_RECORDING && !gpsDisabled )
        {
            Log.d( LOGCAT_TAG, "\tProcessing Location Changed Event" );
            data.setActualAccuracy( location.getAccuracy() );
            if ( location.getAccuracy() < ACCURACY_LIMIT )
            {
                Log.d( LOGCAT_TAG, "\tAccuracy acceptable" );
                if (lastLocation != null)
                {
                    double speed = 0;
                    double partialDistance = lastLocation.distanceTo(location);
                    if ( partialDistance > location.getAccuracy()  )
                    {
                        Log.d( LOGCAT_TAG, "\tSignificant change accepted" );
                        data.updateDistance( partialDistance );
                        data.updateTime( partialTime );
                        speed = partialDistance / partialTime;
                        data.setActualSpeed( speed );

                        data.updateCalories( speed * 3.6, weight, partialTime );

                        lastLocation = location;
                        lastTime = actualTime;
                    }
                }
                else
                {
                    lastLocation = location;
                    lastTime = actualTime;
                }
            }
        }
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
}
