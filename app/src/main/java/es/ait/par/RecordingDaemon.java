package es.ait.par;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by aitkiar on 14/05/16.
 */
public class RecordingDaemon extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    private int GPS_INTERVAL = 5000;
    private int GPS_MINIMUN_DISTANCE = 3;
    private int ACCURACY_LIMIT = 20;


    LocationManager locationManager = null;
    
    
    private long lastTime;
    private Location lastLocation = null;
    private boolean paused;
    private RecordedData data = RecordedData.getInstance();
    private Activity activity;
    private double weight;
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
        if ( locationmanager == null )
        {
            (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        }
        switch ( intent.getAction() )
        {
            case ACTION_START:
            {
                if ( locationManager == null )
                {
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, GPS_INTERVAL, GPS_MINIMUN_DISTANCE, this);
                }
                break;
            }
            case ACTION_STOP:
            {
                if ( locationManager != null )
                {
                    locationManager.removeUpdates( this );
                }
                stopSelf();
                break;
            }
            case ACTION_PAUSE:
            {
                paused = true;
                lastLocation = null;
                lastTime = null;
                break;
            }
            case ACTION_RESUME:
            {
                paused = false;
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
        long actualTime = System.currentTimeMillis();
        long partialTime = (  actualTime - lastTime ) / 1000;
        if ( !paused  )
        {
            ((TextView) findViewById( R.id.accuracyValue )).setText( "" + location.getAccuracy() );
            if ( location.getAccuracy() < ACCURACY_LIMIT )
            {
                if (lastLocation != null)
                {
                    double speed = 0;
                    double partialDistance = lastLocation.distanceTo(location);
                    if ( partialDistance > location.getAccuracy()  )
                    {
                        distance += partialDistance;
                        time += partialTime;
                        speed = partialDistance / partialTime;

                        calories += selectedActivity.calories(speed * 3.6, weight, partialTime);
                        updateGUIValues(speed);
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

    private void startUpdates()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates( googleServicesClient, request, this );
    }


}
