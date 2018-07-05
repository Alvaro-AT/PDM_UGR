package atlc.mmaquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    private Switch switchSonido;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchSonido = (Switch) findViewById(R.id.switchSonido);
        switchSonido.setChecked(GameActivity.sonidoActivado);

        switchSonido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                    GameActivity.sonidoActivado = true;
                else
                    GameActivity.sonidoActivado = false;
            }
        });
    }
}
