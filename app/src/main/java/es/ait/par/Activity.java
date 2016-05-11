package es.ait.par;

/**
 * Class to englobe the activities supported by the application.
 *
 * Created by aitkiar on 6/05/16.
 */
public class Activity
{
    private String icon;
    private String name;

    // gets the aproximated Met
    private double metSpeed[];
    private double met[];

    public Activity ( String name, String icon, double metSpeed[], double met[] )
    {
        this.name = name;
        this.icon = icon;
        this.metSpeed = metSpeed;
        this.met = met;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double[] getMetSpeed()
    {
        return metSpeed;
    }

    public void setMetSpeed(double[] metSpeed)
    {
        this.metSpeed = metSpeed;
    }

    public double[] getMet()
    {
        return met;
    }

    public void setMet(double[] met)
    {
        this.met = met;
    }

    /**
     * gets the met that better fits the speed for the activity and returns de calories consumed in
     * the time asumming the speed was constant on that time.
     * @param speed
     * @param weight
     * @param seconds
     * @return
     */
    public double calories( double speed, double weight, double seconds )
    {
        if ( metSpeed == null || met == null || metSpeed.length == 0 || metSpeed.length != met.length )
        {
            return 0;
        }
        int i = 0;
        double bestMet = met[0];
        while ( i < metSpeed.length  )
        {
            if ( speed > metSpeed[i] )
            {
                bestMet = met[i];
                i++;
            }
            else
            {
                break;
            }
        }

        return bestMet * weight * seconds / 60;
    }
}
