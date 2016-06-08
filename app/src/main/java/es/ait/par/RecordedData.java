package es.ait.par;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class created to acumulate the data recorded.
 */
public class RecordedData
{
    public static final int STATUS_NOT_RECORDING = 1;
    public static final int STATUS_RECORDING = 4;
    public static final int STATUS_PAUSE = 5;

    private static RecordedData instance;
    private Activity activity;
    private double actualAccuracy;
    private double actualSpeed;
    private double distance;
    private double calories;
    private int status;

    private long time;

    private long lastTime;

    private List<RecordedDataChangeListener> listeners;

    private RecordedData()
    {
        listeners =  new ArrayList<>();
    }

    /**
     * Get's the instance of the singleton. If it's not been created it creates it.
     *
     * @return
     */
    public static RecordedData getInstance()
    {
        if ( instance == null )
        {
            instance = new RecordedData();
            instance.setStatus( STATUS_NOT_RECORDING );
        }
        return instance;
    }

    public long getTime()
    {
        return time;
    }

    public long getTimeSeconds() {
        return time / 1000;
    }

    public double getCalories()
    {
        return calories;
    }

    public double getDistance()
    {
        return distance;
    }

    public double getActualAccuracy() { return actualAccuracy; }

    public void setActualAccuracy( double actualAccuracy )
    {
        this.actualAccuracy = actualAccuracy;
        fireListeners();
    }

    public Activity getActivity()
    {
        return this.activity;
    }
    public void setActivity ( Activity activity )
    {
        this.activity = activity;
    }

    public double getActualSpeed() { return actualSpeed; }

    public void setActualSpeed( double actualSpeed) {
        this.actualSpeed = actualSpeed;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus( int status )
    {
        this.status = status;
        if ( status == STATUS_PAUSE )
        {
            nextTime();
            lastTime = 0;
        }
    }

    public synchronized void updateCalories( double speed, double weight, long partialTime )
    {
        calories += activity.calories( speed, weight, partialTime );
        fireListeners();
    }

    public synchronized void updateDistance( double delta )
    {
        distance += delta;
        fireListeners();
    }

    public synchronized void updateTime( long delta )
    {
        time += delta;
        fireListeners();
    }

    public synchronized void reset()
    {
        distance = 0;
        time = 0;
        calories = 0;
        lastTime = 0;
        status = STATUS_NOT_RECORDING;
        fireListeners();
    }

    public void addRecordedDataChangeListener( RecordedDataChangeListener listener )
    {
        if ( !listeners.contains( listener ))
        {
            listeners.add(listener);
        }
    }

    private void fireListeners()
    {
        for ( int i = 0; i < listeners.size(); i ++ )
        {
            listeners.get(i).onDataChanged();
            listeners.get(i).onGPSStatusChange( time != 0 );
        }
    }

    public synchronized void nextTime()
    {
        long now = System.currentTimeMillis();
        if ( lastTime != 0 )
        {
            time += now - lastTime;
            fireListeners();
        }
        lastTime = now;
    }

    public interface RecordedDataChangeListener {

        public void onDataChanged();

        public void onGPSStatusChange( boolean positionFix );
    }



}
