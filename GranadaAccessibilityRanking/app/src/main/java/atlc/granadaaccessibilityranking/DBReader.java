package atlc.granadaaccessibilityranking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Windows 8 on 17/06/2017.
 */

public class DBReader extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "accesibilityRanking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE SITIOS(place_lat REAL," +
            " place_lon REAL, place_name TEXT, categoria TEXT, PRIMARY KEY(place_name))";
    private static final String CREATE_TABLE_EVALUATION = "CREATE TABLE OPINION(name TEXT NOT NULL, place_name TEXT NOT NULL, comment TEXT," +
            "nota REAL NOT NULL, date TEXT NOT NULL, PRIMARY KEY(place_name, date), FOREIGN KEY(place_name) REFERENCES SITIOS(place_name))";

    public int getNumRows(String place_name)
    {
        int rows = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "count(*)"
        };

        String selection = "place_name" + " = ?";
        String[] selectionArgs = { place_name };

        Cursor c = db.query(
                "OPINION",                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(c.moveToFirst())
        {
            rows = c.getInt(0);
        }

        return rows;
    }

    public double getMeanMark(String place_name)
    {
        double nota = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "avg(nota)"
        };

        String selection = "place_name" + " = ?";
        String[] selectionArgs = { place_name };

        Cursor c = db.query(
                "OPINION",                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(c.moveToFirst())
        {
            nota = c.getDouble(0);
        }

        return nota;
    }

    public DBReader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLACES);
        db.execSQL(CREATE_TABLE_EVALUATION);
    }

    public boolean placeExists(String place_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "place_name"
        };

        String selection = "place_name" + " = ?";
        String[] selectionArgs = { place_name };

        Cursor c = db.query(
                "SITIOS",                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        boolean aux = false;
        if(c.moveToFirst())
        {
            aux = true;
        }else
        {
            aux = false;
        }

        db.close();
        return aux;
    }

    public void addOpinion(String name, String place_name, String comment, float nota)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("place_name", place_name);
        values.put("comment", comment);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(new Date());
        values.put("date", date);
        values.put("nota", nota);

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert("OPINION", null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }
}
