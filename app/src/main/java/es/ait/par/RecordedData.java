package es.ait.par;

/**
 * Singleton class created to acumulate the data recorded.
 */
public class RecordedData
{
    private static RecordedData instance;
    private double distance;
    private double calories;
    private long time;

    private RecordedData()
    {
    }

    /**
     * Get's the instance of the singleton. If it's not been created it creates it.
     *
     * @return
     */
    public static RecordedData getInstance()
    {
        if ( instance != null )
        {
            instance = new RecordedData();
        }
        return instance;
    }

    public long getTime()
    {
        return time;
    }

    public double getCalories()
    {
        return calories;
    }

    public double getDistance()
    {
        return distance;
    }

    public synchronized void updateCalories( double delta )
    {
        calories += delta;
    }

    public synchronized void updateDistance( double delta )
    {
        distance += delta;
    }

    public synchronized void updateTime( double delta )
    {
        time += delta;
    }
    public synchronized void reset()
    {
        distance = 0;
        time = 0;
        calories = 0;
    }

}
