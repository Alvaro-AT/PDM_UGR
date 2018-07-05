package atlc.mmajourney;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.Text;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.R.id.input;

public class JourneyActivity extends AppCompatActivity
{
    private ProgressBar barraProgreso;
    private TextView textoProgreso;
    private List<String> textoQR = Arrays.asList("uno", "dos", "tres", "cuatro", "cinco", "seis", "siete");
    private int QRActual = 0;
    private ImageView imagen = null;
    private TextView descripcion = null;
    public static boolean sonidoActivado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        descripcion = (TextView)findViewById(R.id.descripcion);
        textoProgreso = (TextView)findViewById(R.id.textoProgreso);
        Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/KGNeatlyPrinted.ttf");
        descripcion.setTypeface(fuente);
        textoProgreso.setTypeface(fuente);
        imagen = (ImageView) findViewById(R.id.imageView);

        barraProgreso = (ProgressBar) findViewById(R.id.barraProgreso);
        textoProgreso.setText("Progreso: 0/" + barraProgreso.getMax()/20);

        try {
            setImageFromXml(textoQR.get(QRActual));
            setDescriptionFromXml(textoQR.get(QRActual));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void escanearQR(View view)
    {
        Intent intent = new Intent(this, QRActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0)
        {
            if (resultCode == CommonStatusCodes.SUCCESS)
            {
                if (data != null)
                {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    if (barcode.displayValue.equals(textoQR.get(QRActual)))
                    {
                        barraProgreso.incrementProgressBy(20);
                        textoProgreso.setText("Progreso: " + barraProgreso.getProgress()/20 + "/" + barraProgreso.getMax()/20);

                        Toast.makeText(this, "¡Bien hecho! Tu aventura continúa...", Toast.LENGTH_SHORT).show();

                        if (QRActual == barraProgreso.getMax()/20 - 1)
                        {
                            Intent intent = new Intent(this, EndActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if (QRActual < barraProgreso.getMax()/20 - 1)
                        {
                            QRActual++;

                            if (sonidoActivado)
                            {
                                MediaPlayer sonidoAcierto = MediaPlayer.create(JourneyActivity.this, R.raw.correctanswer);
                                sonidoAcierto.start();
                            }

                            try {
                                setImageFromXml(textoQR.get(QRActual));
                                setDescriptionFromXml(textoQR.get(QRActual));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (SAXException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    else
                    {
                        if (sonidoActivado)
                        {
                            MediaPlayer sonidoFallo = MediaPlayer.create(JourneyActivity.this, R.raw.incorrectanswer);
                            sonidoFallo.start();
                        }

                        Toast.makeText(this, "El código QR no es el correcto.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(this, "No se ha encontrado ningún código QR.", Toast.LENGTH_SHORT).show();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    void setImageFromXml(String id) throws IOException, ParserConfigurationException, SAXException {
        InputStream is = getAssets().open("data/data.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        Element element=doc.getDocumentElement();
        element.normalize();

        NodeList elementList = element.getElementsByTagName("prueba");
        boolean found = false;
        String imageDirectory = "";

        for(int i = 0 ; i < elementList.getLength() && !found ; i++)
        {
            Node n = elementList.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e = (Element) n;
                String id_ = getValue("qr", e);
                if(id_.equals(id))
                {
                    imageDirectory = getValue("imagen", e);
                    found = true;
                }
            }
        }
        is.close();

        is = getAssets().open("data/Images/"+imageDirectory);
        Drawable d = Drawable.createFromStream(is, null);
        is.close();
        imagen.setImageDrawable(d);
    }

    //Obtenemos la descripción del xml
    void setDescriptionFromXml(String id) throws IOException, ParserConfigurationException, SAXException {
        InputStream is = getAssets().open("data/data.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        Element element=doc.getDocumentElement();
        element.normalize();

        NodeList elementList = element.getElementsByTagName("prueba");
        boolean found = false;
        String description = "";
        is.close();

        for(int i = 0 ; i < elementList.getLength() && !found ; i++)
        {
            Node n = elementList.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e = (Element) n;
                String id_ = getValue("qr", e);
                if(id_.equals(id))
                {
                    description = getValue("descripcion", e);
                    found = true;
                }
            }
        }

        descripcion.setText(description);
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}