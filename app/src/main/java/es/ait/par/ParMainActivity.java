package es.ait.par;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DecimalFormat;


public class ParMainActivity extends AppCompatActivity implements   AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    GoogleApiClient.ConnectionCallbacks,
                                                                    GoogleApiClient.OnConnectionFailedListener,
                                                                    LocationListener
{
    // Constants
    private final String SAVE_SELECTED_ACTIVITY = "SAVE_SELECTED_ACTIVITY";
    private final String SAVE_STATUS = "SAVE_STATUS";

    private final int STATUS_NOT_RECORDING = 1;
    private final int STATUS_WAITING_FOR_SERVICES_CONNECTION = 2;
    private final int STATUS_CHECKING_LOCATION_SERVICES = 3;
    private final int STATUS_RECORDING = 4;
    private final int STATUS_PAUSE = 5;

    private final int REQUEST_CHECK_SETTINGS = 1;

    private GoogleApiClient googleServicesClient;

    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    // Data
    private int status = STATUS_NOT_RECORDING;

    private Activity[] activities = new Activity[] {
            new Activity( R.string.activityEmpty, "Empty", "empty", null, null ),
            new Activity( R.string.activityCycling, "cycling", "cycling", new double[] { 16, 19, 22.5, 24, 30, 32.2 }, new double[]{4, 6, 8, 10, 12, 16 }),
            new Activity( R.string.activityRunning, "running", "running", new double[] {8.4, 9.6, 10.8, 11.3, 12.1, 12.9, 13.8, 14.5, 16.1, 17.5}, new double[]{9.0, 10.0, 11.0, 11.5, 12.5, 13.5, 14.0, 15.0, 16.0, 18.0}),
            new Activity( R.string.activityWalking, "walking", "walking", new double[] {4.5, 5.3, 6.4}, new double[]{ 3.3, 3.8, 5})
    };

    private double weight = 98;

    private Activity selectedActivity;

    private DecimalFormat speedAndDistanceFormat = new DecimalFormat("###.##");
    private DecimalFormat caloriesFormat = new DecimalFormat("######");
    private DecimalFormat twoDigitsFormat = new DecimalFormat("00");

    //
    // Activity Lifecycle methods
    //
    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        setContentView(R.layout.activity_par_main);

        startButton = ((Button) findViewById(R.id.startButton));
        startButton.setEnabled(false);
        startButton.setClickable(false);
        startButton.setOnClickListener(this);


        pauseButton = ((Button) findViewById(R.id.pauseButton));
        pauseButton.setEnabled(false);
        pauseButton.setClickable(false);
        pauseButton.setOnClickListener(this);

        stopButton = ((Button) findViewById(R.id.stopButton));
        stopButton.setEnabled(false);
        stopButton.setClickable(false);
        stopButton.setOnClickListener(this);

        this.activitiesSprinner = ((Spinner) findViewById(R.id.activitySelector));
        this.activitiesSprinner.setAdapter(new ActivityAdapter(this, R.layout.activity_row_layout, this.activities));
        this.activitiesSprinner.setOnItemSelectedListener(this);
    }

    /**
     * Save the global variables state to be able to restore the correct behabiour of the application
     * @param outState
     */
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putSerializable( SAVE_SELECTED_ACTIVITY, selectedActivity );
        outState.putInt( SAVE_STATUS, status );
    }

    /**
     * Resotres the state of the global variables.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState)
    {
        selectedActivity = ( Activity ) savedInstanceState.getSerializable( SAVE_SELECTED_ACTIVITY );
    }


    //
    // Listeners //////////////////////////////////////////////////////////////////////////////////////
    //

    // Spinner
    public void onItemSelected (AdapterView<?> paramAdapterView, View paramView, int position, long id )
    {
        if ( position != 0 )
        {
            startButton.setEnabled( true );
            startButton.setClickable( true );
        }
        selectedActivity = activities[position];
    }


    /**
     * Listener method that control's the onclick event on the three bottom buttons
     * @param v the view that fires the event
     */
    @Override
    public void onClick(View v)
    {
        switch ( v.getId())
        {
            case R.id.startButton:
            {
                resetValues();
                status = STATUS_WAITING_FOR_SERVICES_CONNECTION;
                googleServicesClient.connect();
                break;
            }
            case R.id.stopButton:
            {
                stopRecording();
                break;
            }
            case R.id.pauseButton:
            {
                if ( status == STATUS_RECORDING )
                {
                    status = STATUS_PAUSE;
                    (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.resumeButton ));
                }
                else
                {
                    status = STATUS_RECORDING;
                    (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.pauseButton ));
                    resetDeltas();
                }

                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> paramAdapterView)
    {

    }

    // Location Apis

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        status = STATUS_CHECKING_LOCATION_SERVICES;
        request = new LocationRequest();
        request.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        request.setInterval( GPS_INTERVAL );
        request.setFastestInterval( GPS_FASTEST_INTERVAL );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest( request );
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings( googleServicesClient, builder.build());

        result.setResultCallback( new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status requestStatus = result.getStatus();
                switch (requestStatus.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                    {
                        startRecording();
                        break;
                    }
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    {
                        // Ask the user for location preferences change in order to proceed.
                        try
                        {
                            requestStatus.startResolutionForResult(ParMainActivity.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            status = STATUS_NOT_RECORDING;
                        }
                        break;
                    }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    {
                        status = STATUS_NOT_RECORDING;
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        status = STATUS_NOT_RECORDING;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        long partialTime = ( System.currentTimeMillis() - lastTime ) / 1000;
        if ( status == STATUS_RECORDING  )
        {
            ((TextView) findViewById( R.id.accuracyValue )).setText( "" + location.getAccuracy() );
            if ( location.getAccuracy() < 10 )
            {
                if (lastLocation != null)
                {
                    double speed = 0;
                    double partialDistance = lastLocation.distanceTo(location);
                    distance += partialDistance;
                    time += partialTime;
                    speed = partialDistance / partialTime;

                    calories += selectedActivity.calories(speed * 3.6, weight, partialTime );
                    updateGUIValues(speed);
                }

                lastLocation = location;
                lastTime = partialTime;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Buissnes methods ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initializes the recording process
     */
    private void startRecording()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates( googleServicesClient, request, this);
        status = STATUS_RECORDING;
        activitiesSprinner.setEnabled( false );

        startButton.setClickable( false );
        startButton.setEnabled( false );

        pauseButton.setClickable( true );
        pauseButton.setEnabled( true );

        stopButton.setClickable( true );
        stopButton.setEnabled( true );
    }

    private void stopRecording()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates( googleServicesClient,this);
        googleServicesClient.disconnect();
        status = STATUS_NOT_RECORDING;
        activitiesSprinner.setEnabled( true );

        startButton.setClickable( true );
        startButton.setEnabled( true );

        pauseButton.setClickable( false );
        pauseButton.setEnabled( false );

        stopButton.setClickable( false );
        stopButton.setEnabled( false );
    }

    private void updateGUIValues( double speed )
    {
        ((TextView) findViewById( R.id.distanceValue )).setText( speedAndDistanceFormat.format( distance / 1000 ));
        ((TextView) findViewById( R.id.speedValue )).setText( speedAndDistanceFormat.format( speed * 3.6 ));
        ((TextView) findViewById( R.id.averageSpeedValue )).setText( speedAndDistanceFormat.format( ( distance / ( time / 1000 )) * 3.6 ));
        ((TextView) findViewById( R.id.timeValue )).setText( twoDigitsFormat.format( time / 3600 ) + ":" + twoDigitsFormat.format((time % 3600 )/ 60 ));
        ((TextView) findViewById( R.id.caloriesValue )).setText( caloriesFormat.format( calories ));
    }

    /**
     * Method to chect the calbacks returned from another activity. It checks for:
     * - Request the user to turn on GPS.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == REQUEST_CHECK_SETTINGS )
        {
            if ( resultCode == android.app.Activity.RESULT_OK )
            {
                startRecording();
            }
            else
            {

            }
        }
    }

    private void checkGPS()
    {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest( request );
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings( googleServicesClient, builder.build());

        result.setResultCallback( new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status requestStatus = result.getStatus();
                switch (requestStatus.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                    {
                        startRecording();
                        break;
                    }
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    {
                        // Ask the user for location preferences change in order to proceed.
                        try
                        {
                            requestStatus.startResolutionForResult( ParMainActivity.this, REQUEST_CHECK_SETTINGS );
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                        }
                        break;
                    }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    {

                        break;
                    }
                }
            }
        });
    }

}
