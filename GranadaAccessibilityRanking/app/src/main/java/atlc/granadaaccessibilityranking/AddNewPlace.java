package atlc.granadaaccessibilityranking;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewPlace extends AppCompatActivity {
    private Button sendButton = null;
    private Button cancelButton = null;
    private EditText placeName = null;
    DBReader mDbHelper = null;
    private NumberPicker categories_picker = null;
    String [] categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        sendButton = (Button) findViewById(R.id.boton_enviar);
        cancelButton = (Button) findViewById(R.id.boton_cancelar);
        placeName = (EditText) findViewById(R.id.place_name);
        categories_picker = (NumberPicker) findViewById(R.id.categories_picker);
        mDbHelper = new DBReader(getBaseContext());
        categories = new String[]{"Tiendas", "Naturaleza", "Turismo", "Deportes", "Salud y educación",
                "Cultura y entretenimiento", "Restaurantes y hoteles"};

        categories_picker.setMinValue(0);
        categories_picker.setMaxValue(6);
        categories_picker.setDisplayedValues(categories);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = placeName.getText().toString();
                ContentValues values = new ContentValues();

                Bundle extras = getIntent().getExtras();
                double lat = extras.getDouble("LATITUDE");
                double lon = extras.getDouble("LONGITUDE");
                Log.i("latitude", Double.toString(lat));
                Log.i("longitude", Double.toString(lon));
                //SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:MM");
               // String date = sdf.format(new Date());
                nombre = nombre.toLowerCase();
                values.put("place_name", nombre);
                values.put("place_lat", lat);
                values.put("place_lon", lon);
                values.put("categoria", categories[categories_picker.getValue()]);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long newRowId = db.insert("SITIOS", null, values);
                db.close();
                finish();
            }
        });
    }
}
