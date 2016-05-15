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

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by aitkiar on 14/05/16.
 */
public class RecordingDaemon extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    private final int GPS_INTERVAL = 5000;
    private final int GPS_FASTEST_INTERVAL = 5000;

    private GoogleApiClient googleServicesClient;
    private LocationRequest request;
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
        switch ( intent.getAction() )
        {
            case ACTION_START:
            {
                if (googleServicesClient == null)
                {
                    // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
                    // See https://g.co/AppIndexing/AndroidStudio for more information.
                    googleServicesClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks( this )
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .addApi(AppIndex.API).build();
                    googleServicesClient.connect();
                }
                break;
            }
            case ACTION_STOP:
            {
                LocationServices.FusedLocationApi.removeLocationUpdates( googleServicesClient,this);
                googleServicesClient.disconnect();
                stopSelf();
                break;
            }
            case ACTION_PAUSE:
            {
                paused = true;
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

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        request = new LocationRequest();
        request.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        request.setInterval( GPS_INTERVAL );
        request.setFastestInterval( GPS_FASTEST_INTERVAL );
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(RecordingDaemon.this, "Can't connect to Location Service", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the RecordedData singleton with the new location if it's accuracy it's good enough.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location)
    {
        long partialTime = ( System.currentTimeMillis() - lastTime ) / 1000;
        if ( !paused )
        {
            if ( location.getAccuracy() <= accuracy )
            {
                if (lastLocation != null)
                {
                    double speed = 0;
                    double partialDistance = lastLocation.distanceTo(location);
                    data.updateDistance( partialDistance );
                    data.updateTime( partialTime );
                    speed = partialDistance / partialTime;

                    data.updateCalories( activity.calories(speed * 3.6, weight, partialTime ));
                }

                lastLocation = location;
                lastTime = partialTime;
            }
        }
    }

    private void startUpdates()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates( googleServicesClient, request, this );
    }


}
