package es.ait.par.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.ait.par.Activity;
import es.ait.par.RecordedData;
import es.ait.par.history.HistoryAnnotation;

/**
 * Created by aitkiar on 30/06/16.
 */
public class ActivityDataBaseHelper extends SQLiteOpenHelper
{
    private static final String dbName = "DBActivities";
    private static final int dbVersion = 1;

    public ActivityDataBaseHelper(Context context, SQLiteDatabase.CursorFactory factory )
    {
        super( context, dbName, factory, dbVersion );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table if not exists activities ( " +
            " activity_date INTEGER PRIMARY KEY NOT NULL, " +
            " activity_name TEXT NOT NULL," +
            " activity_distance REAL NOT NULL," +
            " activity_time REAL NOT NULL, " +
            " activity_calories REAL NOT NULL," +
            " activity_resume INTEGER NOT NULL default 0 )");

        sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_activity_date ON activities( activity_name, activity_date ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL( "drop table if exists activities");
        onCreate( sqLiteDatabase );
    }

    /**
     * Saves the data on the current activity.
     * @param data
     */
    public void saveRecordedData( RecordedData data )
    {
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            db.execSQL("insert into activities ( activity_date, activity_name, activity_distance, " +
                            "activity_time, activity_calories ) values ( ?, ?, ?, ?, ? )",
                    new Object[]{System.currentTimeMillis(), data.getActivity().getName(), data.getDistance(),
                            data.getTimeSeconds(), data.getCalories()});
        }
        finally
        {
            if ( db != null )
            {
                db.close();
            }
        }
    }

    public List<HistoryAnnotation> getHistory(String activity  )
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        String args[] = null;
        String sql = "select activity_date, activity_name, activity_distance, activity_time, activity_calories, activity_resume from activities";
        if ( activity != null )
        {
            sql += " where activity_name = ?";
            args = new String[1];
            args[0] = activity;
        }
        sql += " order by activity_date desc ";

        try
        {
            db = getWritableDatabase();
            c = db.rawQuery(sql, args);
            List<HistoryAnnotation> result = new ArrayList<>();
            while (c.moveToNext())
            {
                HistoryAnnotation annotation = new HistoryAnnotation();
                annotation.setCalories(c.getDouble(4));
                annotation.setDate(new Date(c.getLong(0)));
                annotation.setDistance(c.getDouble(2));
                annotation.setName(c.getString(1));
                annotation.setResume(c.getInt(5) == 1);
                annotation.setTime(c.getDouble(3));
                result.add(annotation);
            }
            return result;
        }
        finally
        {
            if ( c != null )
            {
                c.close();
            }
            if ( db != null )
            {
                db.close();
            }
        }


    }

    /**
     * method for running arbitrary sql querys in app depuration.
     * @param Query
     * @return
     */
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
