package atlc.mmajourney;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView textoFinal = (TextView)findViewById(R.id.textoFinal);
        Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/KGNeatlyPrinted.ttf");
        textoFinal.setTypeface(fuente);

        if (JourneyActivity.sonidoActivado)
        {
            MediaPlayer sonidoFin = MediaPlayer.create(EndActivity.this, R.raw.win);
            sonidoFin.start();
        }
    }

    public void volverMenu(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
