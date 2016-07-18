package es.ait.par;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;

import es.ait.par.history.HistoryActivity;


public class ParMainActivity extends AppCompatActivity implements   AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    RecordedData.RecordedDataChangeListener
{

    // Constants
    private final String SAVE_SELECTED_ACTIVITY = "SAVE_SELECTED_ACTIVITY";
    private final String SAVE_STATUS = "SAVE_STATUS";

    private int GPS_INTERVAL = 5000;
    private int GPS_MINIMUN_DISTANCE = 3;
    private int ACCURACY_LIMIT = 20;


    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    // Data

    private double weight = 98;

    private RecordedData data;

    private Activity selectedActivity;

    //
    // Activity Lifecycle methods
    //
    public void onCreate(Bundle paramBundle)
    {
        Log.d( Utility.LOGCAT_TAG, "onCreate"  );
        super.onCreate(paramBundle);

        PreferenceManager.setDefaultValues( this, R.xml.par_preferences, false ); // Cargamos los valores por defecto en la primera ejecución.

        try
        {
            if (PreferenceManager.getDefaultSharedPreferences(this) != null)
            {
                this.weight = Double.valueOf( PreferenceManager.getDefaultSharedPreferences(this).getString(PreferencesScreen.KEY_WEIGHT, "80"));
            } else
            {
                Log.e(Utility.LOGCAT_TAG, "PreferenceManager.getDefaultSharedPreferences( this ) devuelve null");
            }
        }
        catch ( Exception e )
        {
            Log.e(Utility.LOGCAT_TAG, "Error al leer las preferencias", e );
        }

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
        this.activitiesSprinner.setAdapter(new ActivityAdapter(this, R.layout.activity_row_layout, Utility.getActivities()));
        this.activitiesSprinner.setOnItemSelectedListener(this);

        data = RecordedData.getInstance();
        data.addRecordedDataChangeListener( this );
        if ( data.getActivity() != null )
        {
            selectedActivity = data.getActivity();
            int i = 0;
            while ( !Utility.getActivities()[i].getName().equals( data.getActivity().getName()))
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
     * We need this callback in order to start/stop the animations because they can be set in motion in the
     * onCreate method.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged (boolean hasFocus)
    {
        super.onWindowFocusChanged( hasFocus );
        if ( hasFocus )
        {
            showHideGPSStatusIcon();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate( R.menu.main_activity, menu );
        return super.onCreateOptionsMenu(menu);
    }


    //
    // Listeners //////////////////////////////////////////////////////////////////////////////////////
    //

    // Options Menu

    /**
     * Calls the diferent activities that can be invoked from the options menu.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch ( item.getItemId())
        {
            case R.id.menu_gpx_files:
            {
                Intent intent = new Intent(this, GPXManagment.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_recorded_data:
            {
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_preferences:
            {
                Intent intent = new Intent(this, PreferencesScreen.class);
                startActivity(intent);
                break;
            }
            case R.id.databasemanager:
            {
                Intent intent = new Intent(this, AndroidDatabaseManager.class);
                startActivity(intent);
                break;
            }
        }

        return true;
    }


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
        selectedActivity = Utility.getActivities()[position];
        if ( data.getStatus() == RecordedData.STATUS_NOT_RECORDING )
        {
            data.setActivity( selectedActivity );
        }
        Log.d( Utility.LOGCAT_TAG, "Activity selected: " + selectedActivity.getName() );
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
                Log.d( Utility.LOGCAT_TAG, "Start Button clicked" );
                startRecording();
                break;
            }
            case R.id.stopButton:
            {
                Log.d( Utility.LOGCAT_TAG, "Stop Button clicked" );
                stopRecording();
                break;
            }
            case R.id.pauseButton:
            {
                Log.d( Utility.LOGCAT_TAG, "Pause Button clicked" );
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
        Log.d( Utility.LOGCAT_TAG, "Sending intent to RecordingDaemon: " + message );
        Intent serviceIntent = new Intent( this, RecordingDaemon.class );
        serviceIntent.setAction( message );
        startService( serviceIntent );
        Log.d( Utility.LOGCAT_TAG, "Message sent" );
    }

    /**
     * Initializes the recording process
     */
    private void startRecording()
    {
        Log.d( Utility.LOGCAT_TAG, "Start Recording" );
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
        Log.d( Utility.LOGCAT_TAG, "Stop Recording" );
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
        Log.d( Utility.LOGCAT_TAG, "Updating GUI" );
        ((TextView) findViewById( R.id.distanceValue )).setText( Utility.formatSpeedAndDistance( data.getDistance() / 1000 ));
        ((TextView) findViewById( R.id.speedValue )).setText( Utility.formatSpeedAndDistance( data.getActualSpeed() * 3.6 ));
        if ( data.getTimeSeconds() > 0 )
        {
            ((TextView) findViewById( R.id.averageSpeedValue )).setText( Utility.formatSpeedAndDistance( ( data.getDistance() / data.getTimeSeconds() ) * 3.6 ));
        }
        else
        {
            ((TextView) findViewById( R.id.averageSpeedValue )).setText( Utility.formatSpeedAndDistance( 0.0 ));
        }
        ((TextView) findViewById( R.id.timeValue )).setText( Utility.formatTime( data.getTimeSeconds()));
        ((TextView) findViewById( R.id.caloriesValue )).setText( Utility.formatCalories( data.getCalories() ));
        ((TextView) findViewById( R.id.accuracyValue )).setText( Utility.formatSpeedAndDistance( data.getActualAccuracy() ));
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
        Log.d( Utility.LOGCAT_TAG, "Data changed event listenned" );
        runOnUiThread( new Runnable(){
            public void run() {
                updateGUIValues();
            }
        });
    }

    public void onStatusChanged()
    {
        Log.d( Utility.LOGCAT_TAG, "GPS Status changed to :" + data.getStatus() );
        runOnUiThread( new Runnable() {
            public void run()
            {
                showHideGPSStatusIcon();
            }
        });
    }

    public void showHideGPSStatusIcon()
    {
        AppCompatImageView view = (AppCompatImageView)findViewById(R.id.gpsIcon);
        if ( data.getStatus() == RecordedData.STATUS_WAITING_FIRST_LOCATION )
        {
            view.setVisibility( View.VISIBLE );
            ((AnimationDrawable)view.getDrawable()).start();
        }
        else
        {
            ((AnimationDrawable)view.getDrawable()).stop();
            view.setVisibility( View.INVISIBLE );
        }
    }
}
