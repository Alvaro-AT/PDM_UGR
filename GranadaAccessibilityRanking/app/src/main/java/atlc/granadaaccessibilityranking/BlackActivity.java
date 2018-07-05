package atlc.granadaaccessibilityranking;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class BlackActivity extends VoiceActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{
    private GestureDetectorCompat gestureDetector = null;
    private static final String LOGTAG = "GAR";
    private long startListeningTime = 0; // To skip errors (see processAsrError method)
    private static Integer ID_PROMPT_INFO = 1;
    private static Integer ID_PROMPT_QUERY = 0;
    boolean addNewPlace;
    boolean addNewComment;
    boolean placeTold;
    boolean rateTold;
    boolean commentTold;
    boolean placeToAdd;
    boolean categoryTold;
    boolean getRating;
    DBReader mDbHelper = null;
    //Datos auxiliares necesarios para realizar la inserción
    private String nombreUsuario;
    private String nombreLugar;
    private String nota;
    private String comentario;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        gestureDetector = new GestureDetectorCompat(this, this);
        initSpeechInputOutput(this);
        addNewPlace = false;
        addNewComment = false;
        placeTold = false;
        rateTold = false;
        commentTold = false;
        placeToAdd = false;
        categoryTold = true;
        getRating = false;
        mDbHelper = new DBReader(getBaseContext());
    }

    @Override
    public void onInit(int status) {
        super.onInit(status);
        try {
            speak("Bienvenido a GAR para invidentes. Pulse dos veces en la pantalla para poder realizar alguna acción.", "es-ES", ID_PROMPT_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), "Esta opción de GAR necesita acceder al micrófono para realizar el reconocimiento de voz", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Lo siento, esta opción de GAR no puede funcionar sin acceso al micrófono", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        if(nBestList!=null){
            if(nBestList.size()>0){
                String bestResult = nBestList.get(0); //We will use the best result
                try {
                    String recognised_word = bestResult;
                    voiceMainAction(recognised_word);
                } catch (Exception e) {  }

            }
        }
    }

    private void voiceMainAction(String word)
    {
        String valor_recibido = word.toLowerCase();

        if(!addNewComment && !addNewPlace && !placeTold && !rateTold && !commentTold && !addNewPlace & !placeToAdd && !getRating) {
            if (valor_recibido.contains("añadir")) {
                if (valor_recibido.contains("valorar") || valor_recibido.contains("valoración") ||
                        valor_recibido.contains("comentario") || valor_recibido.contains("opinión")) {
                    addNewComment = true;

                    try {
                        speak("Diga el nombre del lugar que quiere valorar", "es-ES", ID_PROMPT_QUERY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (valor_recibido.contains("lugar") || valor_recibido.contains("sitio") ||
                        valor_recibido.contains("localización") || valor_recibido.contains("punto")) {

                    addNewPlace = true;
                    try {
                        speak("Diga el nombre del lugar que quiere añadir", "es-ES", ID_PROMPT_QUERY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if (valor_recibido.contains("valoraci") || valor_recibido.contains("puntuaci"))
            {
                getRating = true;
                try {
                    speak("Diga el nombre del lugar del que quiere saber su puntuación", "es-ES", ID_PROMPT_QUERY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(addNewComment)
        {
            placeTold = true;
            addNewComment = false;

            boolean exists = mDbHelper.placeExists(valor_recibido);

            if(exists)
            {
                try {
                    speak("Diga su nombre", "es-ES", ID_PROMPT_QUERY);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                nombreLugar = valor_recibido;
            }else
            {
                placeTold = false;

                try {
                    speak("Lo siento el lugar que ha dicho no existe", "es-ES", ID_PROMPT_INFO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(placeTold)
        {
            nombreUsuario = valor_recibido;
            placeTold = false;
            rateTold = true;
            try {
                speak("Diga una nota, por ejemplo, 4 punto 5", "es-ES", ID_PROMPT_QUERY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(rateTold)
        {
            nota = valor_recibido;
            rateTold = false;
            commentTold = true;

            try {
                speak("Haga un comentario sobre el lugar", "es-ES", ID_PROMPT_QUERY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(commentTold)
        {
            comentario = valor_recibido;
            if(nota.equals("cuatro"))
            {
                nota = "4";
            }

            float nota_ = Float.valueOf(nota);
            mDbHelper.addOpinion(nombreUsuario, nombreLugar, comentario, nota_);
            commentTold = false;
        }else if(addNewPlace)
        {
            addNewPlace = false;
            boolean exists = mDbHelper.placeExists(valor_recibido);

            if(!exists)
            {
                nombreLugar = valor_recibido;
                placeToAdd = true;

                try {
                    speak("Diga la categoría a la que pertenece el lugar. Las categorías son: Tiendas, Naturaleza, Turismo, Deportes, Salud y educación," +
                            "Cultura y entretenimiento, Restaurantes y hoteles", "es-ES", ID_PROMPT_QUERY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else
            {
                placeToAdd = false;
                try {
                    speak("Lo siento el lugar que ha dicho ya existe", "es-ES", ID_PROMPT_INFO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(placeToAdd)
        {
            placeToAdd = false;

            if(valor_recibido.equals("tiendas") || valor_recibido.equals("naturaleza") || valor_recibido.equals("turismo") || valor_recibido.equals("deportes")
                    || valor_recibido.contains("salud") || valor_recibido.contains("educación") || valor_recibido.contains("cultura") ||valor_recibido.contains("entretenimiento") ||
                    valor_recibido.contains("restaurante") ||valor_recibido.contains("hotel"))
            {
                ContentValues values = new ContentValues();

                Bundle extras = getIntent().getExtras();
                double lat = extras.getDouble("LATITUDE");
                double lon = extras.getDouble("LONGITUDE");
                category = valor_recibido;
                values.put("place_name", nombreLugar);
                values.put("place_lat", lat);
                values.put("place_lon", lon);
                values.put("categoria", category);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long newRowId = db.insert("SITIOS", null, values);
                db.close();

            }else
            {
                try {
                    speak("Lo siento la categoría que ha dicho no está entre las disponibles", "es-ES", ID_PROMPT_INFO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(getRating)
        {
            getRating = false;

            boolean exists = mDbHelper.placeExists(valor_recibido);

            if(!exists)
            {
                try {
                    speak("Lo siento el lugar que ha dicho no existe", "es-ES", ID_PROMPT_INFO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else
            {
                nombreLugar = valor_recibido;
                double mean = mDbHelper.getMeanMark(nombreLugar);

                try {
                    speak("La valoración media de este lugar es de " + Double.toString(mean), "es-ES", ID_PROMPT_INFO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void processAsrReadyForSpeech() {

    }

    @Override
    public void processAsrError(int errorCode) {
        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognized. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        }
        else {
            String errorMsg = "";
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = "Audio recording error";
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = "Unknown client side error";
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = "Insufficient permissions";
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = "Network related error";
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = "Network operation timed out";
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = "No recognition result matched";
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = "RecognitionService busy";
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = "Server sends error status";
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = "No speech input";
                default:
                    errorMsg = ""; //Another frequent error that is not really due to the ASR, we will ignore it
            }
            if (errorMsg != "") {
                this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error en el reconocimiento de voz", Toast.LENGTH_LONG).show();
                    }
                });

                Log.e(LOGTAG, "Error when attempting to listen: " + errorMsg);
                try { speak(errorMsg,"EN", ID_PROMPT_INFO); } catch (Exception e) { Log.e(LOGTAG, "TTS not accessible"); }
            }
        }
    }

    @Override
    public void onTTSError(String uttId) {

    }

    @Override
    public void onTTSStart(String uttId) {

    }

    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    private void startListening()
    {
        if(deviceConnectedToInternet()){

            try {
				/*Start listening, with the following default parameters:
					* Language = Spanish
					* Recognition model = Free form,
					* Number of results = 1 (we will use the best result to perform the search)
					*/
                startListeningTime = System.currentTimeMillis();
                Locale l = new Locale("es", "ES");
                listen(l, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start listening
            } catch (Exception e) {
                this.runOnUiThread(new Runnable() {  //Toasts must be in the main thread
                    public void run() {
                        Toast.makeText(getApplicationContext(),"ASR no se pudo inicializar", Toast.LENGTH_SHORT).show();
                    }
                });

                try { speak("No se pudo inicialiar el reconocimiento de voz", "es-ES", ID_PROMPT_INFO); } catch (Exception ex) { }

            }
        } else {
            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(),"Por favor, compruebe su conexión a internet", Toast.LENGTH_SHORT).show();
                }
            });
            try { speak("Por favor, compruebe su conexión a internet", "es-ES", ID_PROMPT_INFO); } catch (Exception ex) { }
        }
    }

    @Override
    public void onTTSDone(String uttId) {
        if(uttId.equals(ID_PROMPT_QUERY.toString())) {
            runOnUiThread(new Runnable() {
                public void run() {
                    startListening();
                }
            });
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        try {
            speak("¿Qué acción deseas realizar?", "es-ES", ID_PROMPT_QUERY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(action == KeyEvent.ACTION_UP)
                {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void finish() {
        try {
            speak("Ha salido del modo para invidentes. Gracias por utilizar GAR.", "es-ES", ID_PROMPT_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
