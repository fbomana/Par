package es.ait.par;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GPXManagment extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener
{
    private ListViewCompat trackList;
    private GPXAdapter adapter;
    private boolean selectionMode = false;
    private List<GPXAdapter.GPXTrack> selectedTracks;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpxmanagment);

        trackList = (ListViewCompat)findViewById( R.id.trackList );

        adapter = GPXAdapter.getInstance( this, R.id.trackList );
        trackList.setAdapter( adapter );
        trackList.setOnItemClickListener( this );
        trackList.setOnItemLongClickListener( this );

        selectedTracks = new ArrayList<>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        getMenuInflater().inflate( R.menu.gpx_managment, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch ( item.getItemId() )
        {
            case R.id.menu_gpx_send:
                if ( !selectedTracks.isEmpty())
                {
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (GPXAdapter.GPXTrack track : selectedTracks)
                    {
                        uris.add( Uri.fromFile( track.getFile()));
                    }
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gpx");
                    if ( mime != null )
                    {
                        shareIntent.setType("mime");
                    }
                    else
                    {
                        shareIntent.setType("application/xml+gpx");
                    }
                    startActivity(Intent.createChooser(shareIntent, "Send tracks to..."));
                }
                break;
            case R.id.menu_gpx_open:
                if ( selectedTracks.size() == 1 )
                {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_VIEW );
                    shareIntent.setData( Uri.fromFile( selectedTracks.get(0).getFile()));
                    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gpx");
                    if ( mime != null )
                    {
                        shareIntent.setType("mime");
                    }
                    /*
                    else
                    {
                        shareIntent.setType("application/xml+gpx");
                    }*/
                    startActivity(Intent.createChooser(shareIntent, "Open Track with..."));
                }
                break;
            case R.id.menu_gpx_delete:
                if ( !selectedTracks.isEmpty())
                {
                    for (GPXAdapter.GPXTrack track : selectedTracks )
                    {
                        track.getFile().delete();
                        adapter.remove( track );
                    }
                    selectionMode = false;
                    updateMenu();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Log.d( Utility.LOGCAT_TAG, "onItemLongClick");
        if ( !selectionMode )
        {
            selectionMode = true;
        }
        onItemClick( adapterView, view, i, l );

        if ( selectionMode )
        {

        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {

        Log.d( Utility.LOGCAT_TAG, "onItemClick");
        if ( selectionMode )
        {
            GPXAdapter.GPXTrack track = (GPXAdapter.GPXTrack) adapterView.getItemAtPosition( i );
            if ( selectedTracks.contains( track ))
            {
                selectedTracks.remove( track );
                view.setBackgroundColor( Color.WHITE );
                selectionMode = !selectedTracks.isEmpty();
            }
            else
            {
                selectedTracks.add( track );
                view.setBackgroundColor( Color.GREEN );
            }
        }

        updateMenu();
    }

    private void updateMenu()
    {
        menu.findItem( R.id.menu_gpx_delete ).setEnabled( selectionMode );
        menu.findItem( R.id.menu_gpx_send ).setEnabled( selectionMode );
        menu.findItem( R.id.menu_gpx_open ).setEnabled( selectedTracks.size() == 1 );
    }
}
