package es.ait.par.history;

import java.io.Serializable;
import java.util.Date;

import es.ait.par.RecordedData;

/**
 * Helper class for the HistoryListAdapter and DB access
 */
public class HistoryAnnotation implements Serializable
{
    private Date date;
    private String name;
    private double distance;
    private double time;
    private double calories;
    private boolean resume;


    public HistoryAnnotation()
    {
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public double getTime()
    {
        return time;
    }

    public void setTime(double time)
    {
        this.time = time;
    }

    public double getCalories()
    {
        return calories;
    }

    public void setCalories(double calories)
    {
        this.calories = calories;
    }

    public boolean isResume()
    {
        return resume;
    }

    public void setResume(boolean resume)
    {
        this.resume = resume;
    }

}
