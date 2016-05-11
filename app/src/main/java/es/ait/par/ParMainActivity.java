package es.ait.par;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;


public class ParMainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    // Data
    private Activity[] activities;
    private Activity selectedActivity;


    //
    // Activity Lifecycle methods
    //
    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        setContentView( R.layout.activity_par_main );

        this.startButton = (( Button ) findViewById(R.id.startButton ));
        this.startButton.setEnabled( false );
        this.startButton.setClickable( false );

        this.pauseButton = (( Button )findViewById(R.id.pauseButton ));
        this.pauseButton.setEnabled(false);
        this.pauseButton.setClickable(false);

        this.stopButton = (( Button )findViewById( R.id.startButton ));
        this.stopButton.setEnabled( false );
        this.stopButton.setClickable( false );

        this.activitiesSprinner = ((Spinner)findViewById( R.id.activitySelector ));
        this.activitiesSprinner.setAdapter( new ActivityAdapter( this, R.layout.activity_row_layout, this.activities ));
        this.activitiesSprinner.setOnItemSelectedListener( this );
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

    public void onNothingSelected(AdapterView<?> paramAdapterView)
    {

    }
}
