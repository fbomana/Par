package es.ait.par.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import es.ait.par.Activity;
import es.ait.par.ActivityAdapter;
import es.ait.par.R;
import es.ait.par.Utility;
import es.ait.par.db.ActivityDataBaseHelper;

/**
 * Created by aitkiar on 18/07/16.
 */
public class HistoryListFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
{
    private Spinner activitiesSprinner;
    private ListView list;
    private ActivityDataBaseHelper db;
    private String selectedActivity;




    public HistoryListFragment()
    {
        super();
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        db = new ActivityDataBaseHelper( this.getContext(), null );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate( R.layout.history_list, container, false );
        this.activitiesSprinner = ((Spinner) fragmentView.findViewById(R.id.historyActivitySelector));
        this.activitiesSprinner.setAdapter(new ActivityAdapter(this.getActivity(), R.layout.activity_row_layout, Utility.getActivities()));
        this.activitiesSprinner.setOnItemSelectedListener( this );

        list = ( ListView )fragmentView.findViewById( R.id.historyActivityListView );
        list.setAdapter( new HistoryListAdapter( this.getContext(), R.layout.history_list_data, db.getHistory( selectedActivity )));
        list.setOnItemClickListener( this );

        return fragmentView;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id )
    {
        if ( position != 0 )
        {
            Activity activity = ( Activity ) adapterView.getItemAtPosition( position );
            selectedActivity = activity.getName();
        }
        else
        {
            selectedActivity = null;
        }

        list.invalidate();
        list.setAdapter(  new HistoryListAdapter( this.getContext(), R.layout.history_list_data, db.getHistory( selectedActivity )));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        HistoryActivityClickListener listener = ( HistoryActivityClickListener ) getActivity();
        listener.onHistoryActivityClick( ( HistoryAnnotation ) adapterView.getItemAtPosition( position ));
    }
}
