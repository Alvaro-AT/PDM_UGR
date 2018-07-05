package atlc.mmaquiz;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import atlc.mmaquiz.adapter.CustomListAdapter;
import atlc.mmaquiz.model.Items;

public class ScoreActivity extends AppCompatActivity
{
    private List<Items> itemsList = new ArrayList<Items>();
    private ListView lv;
    private CustomListAdapter cla;

    //Connection
    private static final String host = "192.168.0.21";
    private static final int port = 8989;
    private Socket socket = null;
    private OutputStream rankingRequest = null;
    private InputStream rankingReceiver = null;
    private ArrayList<String> scores = null;
    private ArrayList<String> usernames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // AQUÍ VA LA CONEXIÓN A LA BASE DE DATOS
        lv = (ListView) findViewById(R.id.puntuaciones);
        cla = new CustomListAdapter(this, itemsList);
        lv.setAdapter(cla);

        scores=new ArrayList<>();
        usernames=new ArrayList<>();

        /*Items item = new Items();
        Items item2 = new Items();
        Items item3 = new Items();
        item.setNombre("Miguelito");
        item.setPuntuacion("5");
        cla.notifyDataSetChanged();
        itemsList.add(item);
        item2.setNombre("Alv");
        item2.setPuntuacion("4");
        itemsList.add(item2);
        item3.setNombre("Prueba");
        item3.setPuntuacion("2");
        cla.notifyDataSetChanged();
        itemsList.add(item3);*/

        //cla.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte [] request;
                request = "\r\rranking".getBytes();

                try{
                    if(socket == null)
                    {
                        socket = new Socket(host, port);
                        Log.i("conexion", "se ha conectao");
                        rankingRequest = socket.getOutputStream();
                        rankingReceiver = socket.getInputStream();
                    }
                    rankingRequest.write(request, 0, request.length);
                    int bytesRead;
                    byte [] ranking = new byte[2048];
                    bytesRead = rankingReceiver.read(ranking);
                    //We create the string
                    String aux = new String(ranking, 0, bytesRead);
                    usernames = usernamesParser(aux);
                    scores = scoresParser(aux);
                    socket.close();
                    rankingReceiver.close();
                    rankingRequest.close();
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0 ; i < usernames.size() ; i++)
                        {
                            Items item = new Items();
                            item.setNombre(usernames.get(i));
                            item.setPuntuacion(scores.get(i));
                            itemsList.add(item);
                            cla.notifyDataSetChanged();
                        }
                    }
                });
            }

            ///We parse our string received by the service to get the userList
            private ArrayList<String> usernamesParser(String input)
            {
                String aux = "";
                ArrayList<String> output = new ArrayList<String>();
                boolean isUsername = true;

                for(int i = 0 ; i < input.length() ; i++)
                {
                    if(input.charAt(i) == ':')
                    {
                        isUsername = false;
                        output.add(aux);
                        aux="";
                    }else if(input.charAt(i) == ';')
                    {
                        isUsername = true;
                    }else if(isUsername)
                    {
                        aux = aux+input.charAt(i);
                    }
                }

                return output;
            }

            ///We parse our string received by the service to get the scoreList
            private ArrayList<String> scoresParser(String input)
            {
                String aux = "";
                ArrayList<String> output = new ArrayList<String>();
                boolean isScore = false;

                for(int i = 0 ; i < input.length() ; i++)
                {
                    if(input.charAt(i) == ':')
                    {
                        isScore = true;
                    }else if(input.charAt(i) == ';')
                    {
                        isScore=false;
                        output.add(aux);
                        aux="";
                    }else if(isScore)
                    {
                        aux = aux+input.charAt(i);
                    }
                }

                return output;
            }
        }).start();


    }
}
