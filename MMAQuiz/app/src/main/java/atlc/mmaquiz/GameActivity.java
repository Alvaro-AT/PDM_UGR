package atlc.mmaquiz;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private static final String host = "192.168.1.37";
    private static final int port = 8989;
    private Socket socket = null;
    private RadioButton answerA = null;
    private RadioButton answerB = null;
    private RadioButton answerC = null;
    private TextView question_ = null;
    private String correct_answer = null;
    private OutputStream questionRequest = null;
    private InputStream questionReceiver = null;
    private TextView puntuation_text = null;
    public static boolean sonidoActivado = true;

    //Listeners
    View.OnClickListener listenerA;
    View.OnClickListener listenerB;
    View.OnClickListener listenerC;
    View.OnClickListener emptyListener;

    //Player puntuation
    int points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        answerA = (RadioButton) findViewById(R.id.radioButton);
        answerB = (RadioButton) findViewById(R.id.radioButton2);
        answerC = (RadioButton) findViewById(R.id.radioButton3);
        puntuation_text = (TextView) findViewById(R.id.textView2);
        puntuation_text.setText("Puntuación: ");
        //Inicializamos los puntos
        points = 0;
        //Empty listener that is useful for the moment when we answer a question and the app pauses for some seconds
        //We assign this listener to every button
        emptyListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };

        listenerA = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerA.getText().toString().compareTo(correct_answer) == 0)
                {
                    //We change button colour and after some seconds we look for the next question
                    answerA.setBackgroundResource(R.drawable.button_border_green);
                    increasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerA.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);
                    //new Thread(new ClientThread()).start();
                    // answerA.setBackgroundResource(R.drawable.button_border_blue);
                }else
                {
                    //We change buttom colour and after some seconds we look for the next question
                    answerA.setBackgroundResource(R.drawable.button_border_red);
                    decreasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);


                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerA.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);
                }
            }
        };


        listenerB = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerB.getText().toString().compareTo(correct_answer) == 0)
                {
                    answerB.setBackgroundResource(R.drawable.button_border_green);
                    increasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerB.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);
                }
                else
                {
                    answerB.setBackgroundResource(R.drawable.button_border_red);
                    decreasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerB.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);
                }
            }
        };


        listenerC = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerC.getText().toString().compareTo(correct_answer) == 0)
                {
                    answerC.setBackgroundResource(R.drawable.button_border_green);
                    increasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerC.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);

                }
                else
                {
                    answerC.setBackgroundResource(R.drawable.button_border_red);
                    decreasePoints();
                    Handler h = new Handler();

                    answerA.setOnClickListener(emptyListener);
                    answerB.setOnClickListener(emptyListener);
                    answerC.setOnClickListener(emptyListener);

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerA.setOnClickListener(listenerA);
                            answerB.setOnClickListener(listenerB);
                            answerC.setOnClickListener(listenerC);
                            answerC.setBackgroundResource(R.drawable.button_border_blue);
                            new Thread(new ClientThread()).start();
                        }
                    }, 1000);
                }
            }
        };


        answerA.setOnClickListener(listenerA);

        answerB.setOnClickListener(listenerB);

        answerC.setOnClickListener(listenerC);

        question_ = (TextView) findViewById(R.id.textView);
        new Thread(new ClientThread()).start();

    }


    //////////////////////////////////
    //////////////////////////////////
    private void close()
    {
        super.onBackPressed();
    }

    /////////////////////
    /*Function that increase points and show the textview*/
    private void increasePoints()
    {
        points++;

        if (sonidoActivado)
        {
            MediaPlayer sonidoAcierto = MediaPlayer.create(GameActivity.this, R.raw.correctanswer);
            sonidoAcierto.start();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                puntuation_text.setText("Puntuación: " + Integer.toString(points));
            }
        });
    }

    //////////////////////
    /*Decreases points and update textview*/
    private void decreasePoints()
    {
        points--;

        if (sonidoActivado)
        {
            MediaPlayer sonidoFallo = MediaPlayer.create(GameActivity.this, R.raw.incorrectanswer);
            sonidoFallo.start();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                puntuation_text.setText("Puntuación: " + Integer.toString(points));
            }
        });
    }

    //This function will communicate with the service application and from there we will update
    //our database
    private void updatePuntuationInDB(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String m_Text = input.getText().toString();


                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte [] request;
                            request = "\r\rpuntuation".getBytes();
                            try{
                                questionRequest.write(request, 0, request.length);
                                String user_point = m_Text+";"+Integer.toString(points);
                                questionRequest.write(user_point.getBytes(), 0, user_point.getBytes().length);
                                socket.close();
                                questionReceiver.close();
                                questionRequest.close();
                            }catch (IOException ex)
                            {
                                ex.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    close();
                                }
                            });
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                try {
                    socket.close();
                    questionReceiver.close();
                    questionRequest.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            }
        });
        builder.show();
    }
    //Thread where we will connect with the web service.
    private class ClientThread implements Runnable {
        //We will temporary save answers and question in this variables
        ArrayList <String> answers;
        String msg;
        @Override
        public void run() {
            try {

                InetAddress serverAddr = InetAddress.getByName(host);
                if(socket==null) {
                    socket = new Socket(serverAddr, port);
                    Log.i("conexion", "se ha conectao");
                    questionRequest = socket.getOutputStream();
                    questionReceiver = socket.getInputStream();
                }

                byte [] request;
                request = "question".getBytes();

                try {
                    questionRequest.write(request, 0, request.length);
                    int bytesRead;
                    byte [] question = new byte[2048];
                    bytesRead = questionReceiver.read(question);
                    msg = new String(question, 0, bytesRead);
                    if(msg.compareTo("end;")==0)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                answerA.setOnClickListener(emptyListener);
                                answerB.setOnClickListener(emptyListener);
                                answerC.setOnClickListener(emptyListener);
                                updatePuntuationInDB("Has contestado todas las preguntas. " +
                                        "Indique el nombre con el que quiere aparecer en el ránking");
                            }
                        });

                    }else {
                        bytesRead = questionReceiver.read(question);
                        String msg2 = new String(question, 0, bytesRead);
                        answers = parse(msg2);
                        //We have to use this method to change UI from a thread
                        setUI();
                    }
                } catch (IOException ex) {

                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //Parser for answers received by the service
        private ArrayList<String> parse(String msg)
        {
            ArrayList<String> answers = new ArrayList<>();

            String aux = "";
            for(int i = 0 ; i < msg.length() ; i++)
            {
                if(msg.charAt(i) == ';')
                {
                    answers.add(aux);
                    aux="";
                }else
                {
                    aux = aux+msg.charAt(i);
                }
            }

            aux = answers.get(3);
            answers.remove(3);
            //Shuffle answers
            Collections.shuffle(answers);
            answers.add(aux);

            return answers;
        }

        private void setUI()
        {
            //We have to use runOnUiThread to be able to change interfaces features from a different thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    question_.setText(msg);
                    answerA.setText(answers.get(0));
                    answerB.setText(answers.get(1));
                    answerC.setText(answers.get(2));
                    correct_answer = answers.get(3);
                }
            });
        }
    }
}
