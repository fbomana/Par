package es.ait.par;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;


public class ParMainActivity extends AppCompatActivity implements   AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    LocationListener
{

    private final String LOGCAT_TAG="PAR";

    // Constants
    private final String SAVE_SELECTED_ACTIVITY = "SAVE_SELECTED_ACTIVITY";
    private final String SAVE_STATUS = "SAVE_STATUS";

    private final int STATUS_NOT_RECORDING = 1;
    private final int STATUS_WAITING_FOR_SERVICES_CONNECTION = 2;
    private final int STATUS_CHECKING_LOCATION_SERVICES = 3;
    private final int STATUS_RECORDING = 4;
    private final int STATUS_PAUSE = 5;

    private int GPS_INTERVAL = 5000;
    private int GPS_MINIMUN_DISTANCE = 3;
    private int ACCURACY_LIMIT = 10;

    private int REQUEST_CHECK_SETTINGS = 1;

    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    //Geolocation:
    private LocationManager locationManager;
    private boolean canRecord = false;


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

    private Location lastLocation;
    private long lastTime;

    private long time;
    private double distance;
    private double calories;

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


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                status = STATUS_NOT_RECORDING;
            }
        }
    }

    //
    // Listeners //////////////////////////////////////////////////////////////////////////////////////
    //

    // Spinner

    /**
     * Tests if the selected activity it's different than the empty activity
     *
     * @param paramAdapterView
     * @param paramView
     * @param position
     * @param id
     */
    public void onItemSelected (AdapterView<?> paramAdapterView, View paramView, int position, long id )
    {
        if ( position != 0 )
        {
            startButton.setEnabled( true );
            startButton.setClickable( true );
        }
        else
        {
            startButton.setEnabled( false );
            startButton.setClickable( false );
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
                startRecording();
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
                    clickPauseButton();
                }
                else
                {
                    if ( canRecord )
                    {
                        clickResumeButton();
                    }
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
    public void onLocationChanged(Location location)
    {
        Log.d(LOGCAT_TAG, "New location notification");
        long actualTime = System.currentTimeMillis();
        long partialTime = (  actualTime - lastTime ) / 1000;
        if ( status == STATUS_RECORDING  )
        {
            ((TextView) findViewById( R.id.accuracyValue )).setText( "" + location.getAccuracy() );
            if ( location.getAccuracy() < ACCURACY_LIMIT )
            {
                Log.d( LOGCAT_TAG, "Location Accuracy < 10m");
                Log.d( LOGCAT_TAG, "Partial time:" + partialTime );
                if (lastLocation != null)
                {
                    double speed = 0;
                    double partialDistance = lastLocation.distanceTo(location);
                    if ( partialDistance > ACCURACY_LIMIT / 2 )
                    {
                        Log.d(LOGCAT_TAG, "Location Partial distance:" + partialDistance);
                        Log.d(LOGCAT_TAG, "Acummulated distance:" + distance);
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if ( provider.equals( LocationManager.GPS_PROVIDER ) )
        {
            canRecord = true;
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if ( provider.equals( LocationManager.GPS_PROVIDER ) )
        {
            if ( status == STATUS_RECORDING )
            {
                clickPauseButton();
            }

            Toast.makeText(this, getString( R.string.gpsDisabledToast ), Toast.LENGTH_LONG).show();
            canRecord = false;
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
        if ( locationManager == null || !locationManager.isProviderEnabled( LocationManager.PASSIVE_PROVIDER ))
        {
            Toast.makeText(this, getString( R.string.gpsDisabledToast ), Toast.LENGTH_LONG).show();
            return;
        }
        resetValues();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, GPS_INTERVAL, GPS_MINIMUN_DISTANCE, this);
        canRecord = true;
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
        locationManager.removeUpdates( this );
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
        ((TextView) findViewById( R.id.averageSpeedValue )).setText( speedAndDistanceFormat.format( ( distance / time ) * 3.6 ));
        ((TextView) findViewById( R.id.timeValue )).setText( twoDigitsFormat.format( time / 3600 ) + ":" + twoDigitsFormat.format((time % 3600)/ 60 ));
        ((TextView) findViewById( R.id.caloriesValue )).setText( caloriesFormat.format( calories ));
    }

    private void clickPauseButton()
    {
        status = STATUS_PAUSE;
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.resumeButton ));
    }

    private void clickResumeButton()
    {
        status = STATUS_RECORDING;
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.pauseButton ));
        resetDeltas();
    }

    /**
     * Resets all recorded totals and prepare everything for a new recording.
     */
    private void resetValues()
    {
        distance = 0;
        time = 0;
        calories = 0;
        resetDeltas();
    }

    /**
     * resets partial location and time positions for tracking deltas.
     */
    private void resetDeltas()
    {
        lastLocation = null;
        lastTime = 0;
    }

}
