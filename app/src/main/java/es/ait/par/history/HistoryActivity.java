package es.ait.par.history;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import es.ait.par.Activity;
import es.ait.par.R;
import es.ait.par.Utility;

/**
 * Activity that shows an historical representation of the database activities.
 *
 */
public class HistoryActivity extends AppCompatActivity implements HistoryActivityClickListener
{
    private HistoryListFragment listFragment = null;
    private HistoryDetailFragment detailFragment = null;
    private HistoryAnnotation annotation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.history);

            if ( findViewById( R.id.historyFragmentContainer) != null )
            {
                // One fragment on screen only
                if ( detailFragment == null )
                {
                    detailFragment = new HistoryDetailFragment();
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                if ( listFragment == null )
                {
                    listFragment = new HistoryListFragment();
                    ft.replace(R.id.historyFragmentContainer, listFragment );
                }
                ft.commit();
            }
            else
            {
                FragmentManager fm = getSupportFragmentManager();
                detailFragment = ( HistoryDetailFragment) fm.findFragmentById( R.id.historyDetailFragment );
                // Two fragments on screen.
                if ( annotation != null )
                {
                    onHistoryActivityClick( annotation );
                }
            }
        }
        catch ( Exception e )
        {
            Log.e(Utility.LOGCAT_TAG, "Error during onCreate Proccess", e );
            throw e;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onHistoryActivityClick(HistoryAnnotation annotation )
    {
        this.annotation = annotation;
        if ( findViewById( R.id.historyFragmentContainer) != null )
        {
            // One fragment on screen
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("annotation", annotation );
            detailFragment.setArguments( bundle );
            ft.replace(R.id.historyFragmentContainer, detailFragment );
            ft.addToBackStack(null);
            ft.commit();
        }
    }

}
