package es.ait.par;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationListener;

/**
 * Created by aitkiar on 14/05/16.
 */
public class RecordingDaemon extends Service implements LocationListener
{
    private long lastTime;
    private Location lastLocation = null;
    private boolean paused;
    private RecordedData data = RecordedData.getInstance();
    private Activity activity;
    private double weight;
    private int accuracy;

    /**
     * Service constructor
     * @param accuracy max error margin accepted
     * @param weight weight of the user.
     * @param activity activity to record.
     */
    public RecordingDaemon( int accuracy, double weight, Activity activity )
    {
        this.activity = activity;
        this.accuracy = accuracy;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
