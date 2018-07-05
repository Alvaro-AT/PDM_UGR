package atlc.granadaaccessibilityranking;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewOpinion extends AppCompatActivity {
    private RatingBar ratingBar = null;
    private Button sendEvaluation = null;
    private EditText name = null;
    private EditText comment = null;
    DBReader mDbHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_opinion);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        sendEvaluation = (Button) findViewById(R.id.send_evaluation);
        name = (EditText) findViewById(R.id.name);
        comment = (EditText) findViewById(R.id.comment);
        mDbHelper = new DBReader(getBaseContext());

        sendEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOpinion();
            }
        });
    }

    private void addOpinion()
    {
        ContentValues values = new ContentValues();
        values.put("name", name.getText().toString());
        values.put("place_name", getIntent().getExtras().getString("NAME").toLowerCase());
        values.put("comment", comment.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(new Date());
        values.put("date", date);
        values.put("nota", ratingBar.getRating());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert("OPINION", null, values);
        db.close();
        finish();
    }
}
