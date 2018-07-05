package atlc.granadaaccessibilityranking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class OpinionActivity extends AppCompatActivity {
    private TextView placeName = null;
    private TextView placeNota = null;
    private DBReader mDbHelper = null;
    private Button add_evaluation = null;
    private AdapterCategory adapter = null;
    private ArrayList<Category> categories = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        categories = new ArrayList<Category>();

        //instancio las variables
        placeName = (TextView) findViewById(R.id.place_name);
        placeNota = (TextView) findViewById(R.id.nota);
        add_evaluation = (Button) findViewById(R.id.add_evaluation);
        mDbHelper = new DBReader(getBaseContext());

        Bundle extras = getIntent().getExtras();
        placeName.setText(extras.getString("NAME"));
        setNota();

        add_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddNewOpinion.class);
                intent.putExtra("NAME", placeName.getText());
                startActivity(intent);
            }
        });

        ListView lv = (ListView) findViewById(R.id.valoraciones);
        adapter = new AdapterCategory(this, categories);
        lv.setAdapter(adapter);
        showComments();
    }

    private void showComments()
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                "name",
                "nota",
                "comment"
        };



        Cursor c = db.query(
                "OPINION",                     // The table to query
                projection,                               // The columns to return
                "place_name=?",                                // The columns for the WHERE clause
                new String[]{placeName.getText().toString().toLowerCase()},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                "datetime(date) DESC"                              // The sort order
        );

        while(c.moveToNext())
        {
            Category cat = new Category();
            cat.setNombre(c.getString(c.getColumnIndexOrThrow("name")));
            cat.setValoracion(c.getDouble(c.getColumnIndexOrThrow("nota")));
            cat.setComentario(c.getString(c.getColumnIndexOrThrow("comment")));
            categories.add(cat);
            adapter.notifyDataSetChanged();
        }
    }

    private void setNota()
    {
        Bundle extras = getIntent().getExtras();
        double nota_media = mDbHelper.getMeanMark(extras.getString("NAME").toLowerCase());
        if(nota_media != -1) placeNota.setText(String.format( "%.2f", nota_media ));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setNota();
        categories.clear();
        showComments();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
