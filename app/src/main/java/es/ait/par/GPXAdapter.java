package es.ait.par;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by aitkiar on 29/06/16.
 */
public class GPXAdapter extends ArrayAdapter
{

    public static GPXAdapter getInstance( Context context, int resource )
    {
        return new GPXAdapter( context, resource, getTrackList( context ));
    }

    private GPXAdapter(Context context, int resource, List<GPXTrack> list)
    {
        super(context, resource, list );
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        GPXTrack track = ( GPXTrack )getItem( position );
        LayoutInflater inflater = ((AppCompatActivity) getContext()).getLayoutInflater();
        View row = inflater.inflate(R.layout.gpx_row, parent, false);
        ((AppCompatTextView)row.findViewById( R.id.gpxRowActivity )).setText( track.getActivity());
        ((AppCompatTextView)row.findViewById( R.id.gpxRowDate )).setText( Utility.formatDate( track.getDate()));

        return row;
    }

    private static List<GPXTrack> getTrackList( Context context )
    {
        List<GPXTrack> tracks = new ArrayList<>();
        List<File> directories = new ArrayList();
        directories.addAll( Arrays.asList( ContextCompat.getExternalFilesDirs( context, "GPX")) );
        directories.add( new File( context.getFilesDir(), "GPX") );
        for ( File dir : directories )
        {
            if ( dir != null )
            {
                File[] files = dir.listFiles();
                if ( files != null )
                {
                    for (File file : files)
                    {
                        if (file.getName().endsWith(".gpx"))
                        {
                            try
                            {
                                tracks.add(new GPXTrack(file));
                            } catch (ParseException e)
                            {
                                Log.d(Utility.LOGCAT_TAG, "Error parsing file:" + file.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        Collections.sort( tracks );
        return tracks;
    }

    /**
     * Internal type use for better managment in the Adapter
     */
    public static class GPXTrack implements Comparable<GPXTrack>
    {
        private String activity;
        private Date date;
        private File file;

        public GPXTrack( File file ) throws ParseException
        {
            this.file = file;
            String fileName = file.getName().split("\\.")[0];
            String[] parts = fileName.split("_");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm");
            date = sdf.parse( parts[0] + "_" + parts[1] + "_" + parts[2]);
            activity = parts[3];
        }

        public String getActivity()
        {
            return activity;
        }

        public void setActivity(String activity)
        {
            this.activity = activity;
        }

        public Date getDate()
        {
            return date;
        }

        public void setDate( Date date )
        {
            this.date = date;
        }

        public File getFile()
        {
            return file;
        }

        public void setFile ( File file )
        {
            this.file = file;
        }

        public int compareTo ( GPXTrack track )
        {
            if ( track == null )
            {
                return 1;
            }

            return new Long( date.getTime()).compareTo( track.getDate().getTime());
        }

    }
}
