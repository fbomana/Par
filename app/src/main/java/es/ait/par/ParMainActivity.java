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
                                                                    View.OnClickListener
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
    private int ACCURACY_LIMIT = 20;


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
        Intent serviceIntent = new Intent( this, RecordingDaemon.class );
        serviceIntent.setAction( message );
        startService( serviceIntent );
    }

    /**
     * Initializes the recording process
     */
    private void startRecording()
    {
        RecordedData.getInstance().reset();
        status = STATUS_RECORDING;

        sendMessageToRecordingService( RecordingDaemon.ACTION_START );

        RecordedData.getInstance().reset();

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
        status = STATUS_NOT_RECORDING;
        sendMessageToRecordingService( RecordingDaemon.ACTION_STOP );
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
        ((TextView) findViewById( R.id.distanceValue )).setText( speedAndDistanceFormat.format( RecordedData.getInstance().getDistance() / 1000 ));
        ((TextView) findViewById( R.id.speedValue )).setText( speedAndDistanceFormat.format( speed * 3.6 ));
        ((TextView) findViewById( R.id.averageSpeedValue )).setText( speedAndDistanceFormat.format( ( RecordedData.getInstance().getDistance() / RecordedData.getInstance().getTime() ) * 3.6 ));
        ((TextView) findViewById( R.id.timeValue )).setText( twoDigitsFormat.format( RecordedData.getInstance().getTime() / 3600 ) + ":" + twoDigitsFormat.format((RecordedData.getInstance().getTime() % 3600)/ 60 ));
        ((TextView) findViewById( R.id.caloriesValue )).setText( caloriesFormat.format( RecordedData.getInstance().getCalories() ));
    }

    private void clickPauseButton()
    {
        sendMessageToRecordingService( RecordingDaemon.ACTION_PAUSE );
        status = STATUS_PAUSE;
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.resumeButton ));
    }

    private void clickResumeButton()
    {
        sendMessageToRecordingService( RecordingDaemon.ACTION_RESUME );
        status = STATUS_RECORDING;
        (( Button )findViewById( R.id.pauseButton )).setText( getString( R.string.pauseButton ));

    }

}
