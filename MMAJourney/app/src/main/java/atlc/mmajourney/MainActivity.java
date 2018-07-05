package atlc.mmajourney;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private ImageSwitcher imageSwitcher;
    private int[] galeria = {R.drawable.imagenportada, R.drawable.imagenportada2};
    private int posicion;
    private static final int DURACION = 9000;
    private Timer timer = null;
    private Button botonJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textoTitulo = (TextView)findViewById(R.id.textoTitulo);
        Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/KGNeatlyPrinted.ttf");
        textoTitulo.setTypeface(fuente);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                return imageView;
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);

        startSlider();

        botonJugar = (Button) findViewById(R.id.botonJugar);
        botonJugar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getBaseContext(), JourneyActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ToggleButton botonSonido = (ToggleButton) findViewById(R.id.botonSonido);
        botonSonido.setChecked(JourneyActivity.sonidoActivado);
        botonSonido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                    JourneyActivity.sonidoActivado = true;
                else
                    JourneyActivity.sonidoActivado = false;
            }
        });
    }

    private void startSlider()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        imageSwitcher.setImageResource(galeria[posicion]);
                        posicion++;
                        if (posicion == galeria.length)
                            posicion = 0;
                    }
                });
            }
        }, 0, DURACION);
    }
}
