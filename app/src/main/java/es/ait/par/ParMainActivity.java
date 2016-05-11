package es.ait.par;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class ParMainActivity extends AppCompatActivity implements   AdapterView.OnItemSelectedListener,
                                                                    GoogleApiClient.ConnectionCallbacks,
                                                                    GoogleApiClient.OnConnectionFailedListener
{

    // GUI
    private Spinner activitiesSprinner;
    private Button pauseButton;
    private Button startButton;
    private Button stopButton;

    //Geolocation:
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest request;

    // Data
    private Activity[] activities = new Activity[] {
            new Activity( R.string.activityEmpty, "Empty", "empty", null, null ),
            new Activity( R.string.activityCycling, "cycling", "cycling", new double[] { 16, 19, 22.5, 24, 30, 32.2 }, new double[]{4, 6, 8, 10, 12, 16 }),
            new Activity( R.string.activityRunning, "running", "running", new double[] {8.4, 9.6, 10.8, 11.3, 12.1, 12.9, 13.8, 14.5, 16.1, 17.5}, new double[]{9.0, 10.0, 11.0, 11.5, 12.5, 13.5, 14.0, 15.0, 16.0, 18.0}),
            new Activity( R.string.activityWalking, "walking", "walking", new double[] {4.5, 5.3, 6.4}, new double[]{ 3.3, 3.8, 5})
    };
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

        this.stopButton = (( Button )findViewById( R.id.stopButton ));
        this.stopButton.setEnabled( false );
        this.stopButton.setClickable( false );

        this.activitiesSprinner = ((Spinner)findViewById( R.id.activitySelector ));
        this.activitiesSprinner.setAdapter( new ActivityAdapter( this, R.layout.activity_row_layout, this.activities ));
        this.activitiesSprinner.setOnItemSelectedListener( this );


        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi(LocationServices.API)
                .build();
        }
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

    // Location Apis

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
