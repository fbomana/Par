package es.ait.par.history;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.ait.par.Activity;
import es.ait.par.R;
import es.ait.par.Utility;

/**
 * Created by aitkiar on 18/07/16.
 */
public class HistoryListAdapter extends ArrayAdapter
{
    private static final String LOGCAT_TAG="[PAR]";
    private DecimalFormat speedAndDistanceFormat = new DecimalFormat("###.##");

    public HistoryListAdapter(Context context, int resource, List<HistoryAnnotation> list)
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

    /**
     *  Gets the View for the activity with index position on the activities array.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        HistoryAnnotation annotation = (HistoryAnnotation)getItem( position );

        View view;
        if ( convertView == null )
        {
            view = ((android.app.Activity)getContext()).getLayoutInflater().inflate( R.layout.history_list_data, parent, false );
        }
        else
        {
            view = convertView;
        }

        ImageView image = (ImageView)view.findViewById(R.id.historyListAnnotationIcon );
        image.setImageDrawable( Utility.getImage( getContext(), Utility.getActivityByName( annotation.getName())));
        (( TextView )view.findViewById( R.id.historyListAnnotationDate ) ).setText( Utility.formatDate( annotation.getDate()));
        (( TextView )view.findViewById( R.id.historyListAnnotationName ) ).setText( annotation.getName());
        (( TextView )view.findViewById( R.id.historyListAnnotationDistance ) ).setText( speedAndDistanceFormat.format( annotation.getDistance() / 1000 ) );
        return view;

    }


}
