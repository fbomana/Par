package es.ait.par.gpx;

import android.location.Location;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

import es.ait.par.Activity;

/**
 * Class that generates a gpx from the activity the user is doing.
 */
public class GPXRecorder
{
    private static GPXRecorder instance;

    private GpxType gpx;
    private Activity activity;

    private GPXRecorder( Activity activity )
    {
        gpx = new GpxType();
        gpx.setVersion("1.0");
        gpx.setCreator("Personal Activity Recorder");
        gpx.setMetadata( new MetadataType());
        gpx.getMetadata().setTime( new java.util.Date().toString());
        gpx.getMetadata().setDesc( activity.getName() + " " + new java.util.Date().toString());
        this.activity = activity;
    }

    /**
     * gets an instance of a recorder for the activity. If a recorder instance of a new activity is
     * ask the previous recorded is overwritten. If no activity is passed but there is already a recorded
     * instantiated returns that recorer. If there is no activity and no recorder previously instantiated
     * throws an Exception
     * @param activity
     * @return
     */
    public static GPXRecorder getInstance( Activity activity ) throws Exception
    {
        if ( activity != null || instance != null )
        {
            if (instance == null || instance.getActivity().getId() != activity.getId())
            {
                instance = new GPXRecorder(activity);
            }
            return instance;
        }
        throw new Exception("No activity passed and no previously instantiated recorder.");
    }

    public GpxType getGpx()
    {
        return gpx;
    }

    public void setGpx(GpxType gpx)
    {
        this.gpx = gpx;
    }

    public Activity getActivity()
    {
        return activity;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void newTrack()
    {
        if ( gpx.getTrk() == null )
        {
            gpx.setTrk( new ArrayList<TrkType>());
        }
        gpx.getTrk().add( new TrkType());
    }

    public TrksegType newTrackSegment()
    {
        TrkType track = getLastTrack();
        if ( track == null )
        {
            newTrack();
        }
        if ( track.getTrkseg() == null )
        {
            track.setTrkseg( new ArrayList<TrksegType>());
        }

        track.getTrkseg().add( new TrksegType());
        return getLastTrackSegment();
    }

    public void saveLocation( Location location )
    {
        TrksegType trackSegment = getLastTrackSegment();
        if ( trackSegment == null )
        {
            trackSegment = newTrackSegment();
        }
        trackSegment.addPoint( location );
    }

    public void serialize( File file ) throws Exception
    {
        Serializer serializer = new Persister();
        serializer.write(gpx, file);
    }

    public void serialize( OutputStream stream) throws Exception
    {
        Serializer serializer = new Persister();
        serializer.write(gpx, stream );
    }

    private TrkType getLastTrack()
    {
        if ( gpx.getTrk() != null && ! gpx.getTrk().isEmpty())
        {
            return gpx.getTrk().get( gpx.getTrk().size() - 1);
        }
        return null;
    }

    private TrksegType getLastTrackSegment()
    {
        TrkType track = getLastTrack();
        if ( track != null && track.getTrkseg() != null && !track.getTrkseg().isEmpty())
        {
            return track.getTrkseg().get( track.getTrkseg().size() -1 );
        }
        return null;
    }

}

