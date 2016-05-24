package es.ait.par;

import android.content.Context;
import android.content.Intent;
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
                                                                    RecordedData.RecordedDataChangeListener
{

    // Constants
    private final String SAVE_SELECTED_ACTIVITY = "SAVE_SELECTED_ACTIVITY";
    private final String SAVE_STATUS = "SAVE_STATUS";

    private final String LOGCAT_TAG="[PAR]";


    private int GPS_INTERVAL = 5000;
    private int GPS_MINIMUN_DISTANCE = 3;
    private int ACCURACY_LIMIT = 20;


    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    // Data

    private Activity[] activities = new Activity[] {
            new Activity( R.string.activityEmpty, "Empty", "empty", null, null ),
            new Activity( R.string.activityCycling, "cycling", "cycling", new double[] { 16, 19, 22.5, 24, 30, 32.2 }, new double[]{4, 6, 8, 10, 12, 16 }),
            new Activity( R.string.activityRunning, "running", "running", new double[] {8.4, 9.6, 10.8, 11.3, 12.1, 12.9, 13.8, 14.5, 16.1, 17.5}, new double[]{9.0, 10.0, 11.0, 11.5, 12.5, 13.5, 14.0, 15.0, 16.0, 18.0}),
            new Activity( R.string.activityWalking, "walking", "walking", new double[] {4.5, 5.3, 6.4}, new double[]{ 3.3, 3.8, 5})
    };

    private double weight = 98;

    private RecordedData data;

    private Activity selectedActivity;

    private DecimalFormat speedAndDistanceFormat = new DecimalFormat("###.##");
    private DecimalFormat caloriesFormat = new DecimalFormat("######");
    private DecimalFormat twoDigitsFormat = new DecimalFormat("00");

    //
    // Activity Lifecycle methods
    //
    public void onCreate(Bundle paramBundle)
    {
        Log.d( LOGCAT_TAG, "onCreate"  );
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

        data = RecordedData.getInstance();
        data.addRecordedDataChangeListener( this );
        if ( data.getActivity() != null )
        {
            selectedActivity = data.getActivity();
            int i = 0;
            while ( !activities[i].getName().equals( data.getActivity().getName()))
            {
                i++;
            }
            this.activitiesSprinner.setSelection( i );

        }
        if ( data.getStatus() != RecordedData.STATUS_NOT_RECORDING )
        {
            updateGUIValues();
            recordingButtonsState();
            if ( data.getStatus() == RecordedData.STATUS_RECORDING )
            {
                (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.pauseButton ));
            }
            else if ( data.getStatus() == RecordedData.STATUS_PAUSE )
            {
                (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.resumeButton ));
            }
        }
    }

    /**
     * Save the global variables state to be able to restore the correct behabiour of the application
     * @param outState
     */
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
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
        if ( position != 0 && data.getStatus() == RecordedData.STATUS_NOT_RECORDING )
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
        if ( data.getStatus() == RecordedData.STATUS_NOT_RECORDING )
        {
            data.setActivity( selectedActivity );
        }
        Log.d( LOGCAT_TAG, "Activity selected: " + selectedActivity.getName() );
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
                Log.d( LOGCAT_TAG, "Start Button clicked" );
                startRecording();
                break;
            }
            case R.id.stopButton:
            {
                Log.d( LOGCAT_TAG, "Stop Button clicked" );
                stopRecording();
                break;
            }
            case R.id.pauseButton:
            {
                Log.d( LOGCAT_TAG, "Pause Button clicked" );
                if ( data.getStatus() == RecordedData.STATUS_RECORDING )
                {
                    clickPauseButton();
                }
                else
                {
                    clickResumeButton();
                }
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> paramAdapterView)
    {

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Buissnes methods ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void sendMessageToRecordingService ( String message )
    {
        Log.d( LOGCAT_TAG, "Sending intent to RecordingDaemon: " + message );
        Intent serviceIntent = new Intent( this, RecordingDaemon.class );
        serviceIntent.setAction( message );
        startService( serviceIntent );
        Log.d( LOGCAT_TAG, "Message sent" );
    }

    /**
     * Initializes the recording process
     */
    private void startRecording()
    {
        Log.d( LOGCAT_TAG, "Start Recording" );
        data.setActivity( selectedActivity );

        sendMessageToRecordingService( RecordingDaemon.ACTION_START );
        recordingButtonsState();
    }

    private void recordingButtonsState()
    {
        runOnUiThread( new Runnable() {
            public void run() {

                activitiesSprinner.setEnabled(false);

                startButton.setClickable(false);
                startButton.setEnabled(false);

                pauseButton.setClickable(true);
                pauseButton.setEnabled(true);

                stopButton.setClickable(true);
                stopButton.setEnabled(true);
            }
        });
    }

    private void stopRecording()
    {
        Log.d( LOGCAT_TAG, "Stop Recording" );
        sendMessageToRecordingService( RecordingDaemon.ACTION_STOP );
        activitiesSprinner.setEnabled( true );

        startButton.setClickable( true );
        startButton.setEnabled( true );

        pauseButton.setClickable( false );
        pauseButton.setEnabled( false );

        stopButton.setClickable( false );
        stopButton.setEnabled( false );
    }

    private void updateGUIValues()
    {
        Log.d( LOGCAT_TAG, "Updating GUI" );
        ((TextView) findViewById( R.id.distanceValue )).setText( speedAndDistanceFormat.format( data.getDistance() / 1000 ));
        ((TextView) findViewById( R.id.speedValue )).setText( speedAndDistanceFormat.format( data.getActualSpeed() * 3.6 ));
        if ( data.getTime() > 0 )
        {
            ((TextView) findViewById( R.id.averageSpeedValue )).setText( speedAndDistanceFormat.format( ( data.getDistance() / data.getTime() ) * 3.6 ));
        }
        else
        {
            ((TextView) findViewById( R.id.averageSpeedValue )).setText( speedAndDistanceFormat.format( 0 ));
        }
        ((TextView) findViewById( R.id.timeValue )).setText( twoDigitsFormat.format( data.getTime() / 3600 ) + ":" + twoDigitsFormat.format((data.getTime() % 3600)/ 60 ) + ":" + twoDigitsFormat.format((data.getTime() % 3600)% 60 ));
        ((TextView) findViewById( R.id.caloriesValue )).setText( caloriesFormat.format( data.getCalories() ));
        ((TextView) findViewById( R.id.accuracyValue )).setText( speedAndDistanceFormat.format( data.getActualAccuracy() ));
    }

    private void clickPauseButton()
    {
        sendMessageToRecordingService( RecordingDaemon.ACTION_PAUSE );
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.resumeButton ));
    }

    private void clickResumeButton()
    {
        sendMessageToRecordingService( RecordingDaemon.ACTION_RESUME );
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.pauseButton ));

    }

    @Override
    public void onDataChanged() {
        Log.d( LOGCAT_TAG, "Data changed event listenned" );
        runOnUiThread( new Runnable(){
            public void run() {
                updateGUIValues();
            }
        });
    }
}
