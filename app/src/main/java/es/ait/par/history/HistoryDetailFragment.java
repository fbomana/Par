package es.ait.par.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.ait.par.Activity;
import es.ait.par.R;
import es.ait.par.Utility;

/**
 * Created by aitkiar on 18/07/16.
 */
public class HistoryDetailFragment extends Fragment
{
    private ImageView icon;
    private TextView activity;
    private TextView dateText;
    private TextView date;
    private TextView distanceText;
    private TextView distance;
    private TextView timeText;
    private TextView time;
    private TextView avgSpeedText;
    private TextView avgSpeed;
    private TextView caloriesText;
    private TextView calories;
    HistoryAnnotation annotation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate( R.layout.history_detail, container, false  );
        icon = ( ImageView )view.findViewById( R.id.historyDetailIcon );
        activity = ( TextView ) view.findViewById( R.id.historyDetailActivity );
        dateText = ( TextView ) view.findViewById( R.id.historyDetailDateText );
        date = ( TextView ) view.findViewById( R.id.historyDetailDate );
        distanceText = ( TextView ) view.findViewById( R.id.historyDetailDistanceText );
        distance = ( TextView ) view.findViewById( R.id.historyDetailDistance );
        timeText = ( TextView ) view.findViewById( R.id.historyDetailTimeText );
        time = ( TextView ) view.findViewById( R.id.historyDetailTime );
        avgSpeedText = ( TextView ) view.findViewById( R.id.historyDetailAvgSpeedText );
        avgSpeed = ( TextView ) view.findViewById( R.id.historyDetailAvgSpeed );
        caloriesText = ( TextView ) view.findViewById( R.id.historyDetailCaloriesText );
        calories = ( TextView ) view.findViewById( R.id.historyDetailCalories );
        if ( getArguments().getSerializable( "annotation") != null )
        {
            setHistoryAnnotation((HistoryAnnotation) getArguments().getSerializable( "annotation" ));
        }
        return view;
    }

    public void setHistoryAnnotation(HistoryAnnotation annotation )
    {
        if ( annotation != null )
        {
            icon.setImageDrawable(Utility.getImage( getActivity(), Utility.getActivityByName( annotation.getName())));
            icon.setVisibility( View.VISIBLE );

            activity.setText( annotation.getName());
            activity.setVisibility( View.VISIBLE );

            dateText.setVisibility( View.VISIBLE );
            date.setText( Utility.formatDate( annotation.getDate() ));
            date.setVisibility( View.VISIBLE );

            distanceText.setVisibility( View.VISIBLE );
            distance.setText( Utility.formatSpeedAndDistance( annotation.getDistance() / 1000) + " Km");
            distance.setVisibility( View.VISIBLE );

            timeText.setVisibility( View.VISIBLE );
            time.setText( Utility.formatTime( annotation.getTime() ));
            time.setVisibility( View.VISIBLE );

            avgSpeedText.setVisibility( View.VISIBLE );
            avgSpeed.setText( Utility.formatSpeedAndDistance( annotation.getDistance() * 3.6 / annotation.getTime()));
            avgSpeed.setVisibility( View.VISIBLE );

            caloriesText.setVisibility( View.VISIBLE );;
            calories.setText( Utility.formatCalories( annotation.getCalories()));
            calories.setVisibility( View.VISIBLE );;
        }
    }
}
