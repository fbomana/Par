package es.ait.par;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter class for show the customized activities spinner
 * Created by aitkiar on 6/05/16.
 */
public class ActivityAdapter extends ArrayAdapter
{
    Activity[] activities;
    public ActivityAdapter(Context context, int resource, Object[] objects )
    {
        super(context, resource, objects );
        activities = (Activity[]) objects;
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
        Activity activity = activities[position];
        LayoutInflater inflater = ((AppCompatActivity) getContext()).getLayoutInflater();
        View row = inflater.inflate(R.layout.activity_row_layout, parent, false);
        ImageView image = (ImageView)row.findViewById(R.id.activityRowImage );
        image.setImageDrawable( getImage( activity ));
        TextView text = (TextView)row.findViewById(R.id.activityRowText );
        text.setText( row.getResources().getString( activity.getId()));
        return row;
    }

    /**
     * Gets a drawable from the icon method.
     * @param activity
     * @return
     */
    private Drawable getImage(Activity activity )
    {
        Resources resources = getContext().getResources();
        final int resourceId = resources.getIdentifier( activity.getIcon(), "drawable",
                getContext().getPackageName());
        return  ResourcesCompat.getDrawable( resources, resourceId, null );
    }
}
