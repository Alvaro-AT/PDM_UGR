package atlc.mmaquiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private ImageSwitcher imageSwitcher;
    private int[] galeria = { R.drawable.portada, R.drawable.portada2, R.drawable.portada3 };
    private int posicion;
    private static final int DURACION = 9000;
    private Timer timer = null;
    private static final String host = "192.168.1.135";
    private static final int port = 8989;
    private Socket socket = null;
    //Buttons and other elements
    private ImageSwitcher newGame;
    private ImageView gameView;
    private int[] gameImages = { R.mipmap.ic_play, R.drawable.imagenmenu};
    private ImageSwitcher scores;
    private ImageView scoreView;
    private int[] scoreImages = { R.mipmap.ic_score, R.drawable.imagenmenu2};
    private ImageSwitcher settings;
    private ImageView settingsView;
    private int[] settingsImages = { R.mipmap.ic_settings, R.drawable.imagenmenu3};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            public View makeView()
            {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                return imageView;
            }
        });

        newGame = (ImageSwitcher) findViewById(R.id.imageButton);
        newGame.setFactory(new ViewSwitcher.ViewFactory()
        {
            public View makeView()
            {
                gameView = new ImageView(MainActivity.this);
                gameView.setAdjustViewBounds(true);
                gameView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                gameView.setBackgroundColor(Color.rgb(127, 34, 33));

                return gameView;
            }
        });

        scores = (ImageSwitcher) findViewById(R.id.imageButton2);
        scores.setFactory(new ViewSwitcher.ViewFactory()
        {
            public View makeView()
            {
                scoreView = new ImageView(MainActivity.this);
                scoreView.setAdjustViewBounds(true);
                scoreView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                scoreView.setBackgroundColor(Color.rgb(127, 34, 33));

                return scoreView;
            }
        });

        settings = (ImageSwitcher) findViewById(R.id.imageButton3);
        settings.setFactory(new ViewSwitcher.ViewFactory()
        {
            public View makeView()
            {
                settingsView = new ImageView(MainActivity.this);
                settingsView.setAdjustViewBounds(true);
                settingsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                settingsView.setBackgroundColor(Color.rgb(127, 34, 33));

                return settingsView;
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation animChangeButton = AnimationUtils.loadAnimation(this, R.anim.anim_changebutton);
        Animation animReverseButton = AnimationUtils.loadAnimation(this, R.anim.anim_reversebutton);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);
        newGame.setInAnimation(animChangeButton);
        newGame.setOutAnimation(animReverseButton);
        scores.setInAnimation(animChangeButton);
        scores.setOutAnimation(animReverseButton);
        settings.setInAnimation(animChangeButton);
        settings.setOutAnimation(animReverseButton);

        startSlider();
        newGame.setImageResource(gameImages[0]);
        scores.setImageResource(scoreImages[0]);
        settings.setImageResource(settingsImages[0]);

        //Simulating down touch event
        long downTime = SystemClock.uptimeMillis()+100;
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;

        newGame.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    gameView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    newGame.setImageResource(gameImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    gameView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newGame.setImageResource(gameImages[0]);
                    //Intent intent = new Intent(getBaseContext(), GameActivity.class);
                    //startActivity(intent);

                    return true;
                }
                return false;
            }
        });



        scores.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    scoreView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    scores.setImageResource(scoreImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    scoreView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    scores.setImageResource(scoreImages[0]);
                    //Intent intent = new Intent(getBaseContext(), ScoreActivity.class);
                    //startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        settings.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    settingsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    settings.setImageResource(settingsImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    settingsView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    settings.setImageResource(settingsImages[0]);
                    //Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    //startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        //To fix problems with icons, we do this
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );

        MotionEvent motionEvent2 = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                metaState
        );

        newGame.dispatchTouchEvent(motionEvent2);
        settings.dispatchTouchEvent(motionEvent2);
        scores.dispatchTouchEvent(motionEvent2);
        newGame.dispatchTouchEvent(motionEvent);
        settings.dispatchTouchEvent(motionEvent);
        scores.dispatchTouchEvent(motionEvent);

        newGame.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    gameView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    newGame.setImageResource(gameImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    gameView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    newGame.setImageResource(gameImages[0]);
                    Intent intent = new Intent(getBaseContext(), GameActivity.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });



        scores.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    scoreView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    scores.setImageResource(scoreImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    scoreView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    scores.setImageResource(scoreImages[0]);
                    Intent intent = new Intent(getBaseContext(), ScoreActivity.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        settings.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    settingsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    settings.setImageResource(settingsImages[1]);

                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    settingsView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    settings.setImageResource(settingsImages[0]);
                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

        //PRUEBA
        new Thread(new ClientThread()).start();
    }

    //PRUEBA
    private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(host);
                socket = new Socket(serverAddr, port);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
