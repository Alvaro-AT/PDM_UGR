/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmaquizservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Windows 8
 */
public class QuestionMaker extends Thread{
    private Socket socketServicio;
    
    private InputStream inputStream;
    
    private OutputStream outputStream;
    
    private Thread thr;
    
    private boolean conectado = true;
    
    private int id;
    
    private final String directory = "data/questions.xml";
    
    private ArrayList <Integer> questions = null;
    private ArrayList <Integer> question_aux = null;
    
    private Question xmlParser() throws ParserConfigurationException, SAXException, IOException{
        
        Question q;
        
        
        File input = new File(directory);
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();        
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(input);
        doc.getDocumentElement().normalize();
        
        Element a = (Element) doc.getElementsByTagName("data").item(0);
        Element e = (Element) a.getElementsByTagName("metadata").item(0);
        int num_questions = Integer.parseInt(e.getElementsByTagName("num").item(0).getTextContent());
        
        //Obtengo pregunta aleatoria
        Random r = new Random();
        int i = r.nextInt(questions.size());
        
        //Obtengo la pregunta
        Element b = (Element) a.getElementsByTagName("questions").item(0);
        Element eElement = (Element) b.getElementsByTagName("question").item(questions.get(i));
        String question = eElement.getElementsByTagName("q").item(0).getTextContent();
        //System.out.println(question);
        String A = eElement.getElementsByTagName("A").item(0).getTextContent();
        System.out.println(A);
        String B = eElement.getElementsByTagName("B").item(0).getTextContent();
        System.out.println(B);
        String C = eElement.getElementsByTagName("C").item(0).getTextContent();
        System.out.println(C);
        String correct_answer = eElement.getElementsByTagName("correctanswer").item(0).getTextContent();
        System.out.println(correct_answer);
        
        //Creo la nueva pregunta
        q= new Question(question, A, B, C, correct_answer);
        
        questions.remove(i);
        

       
        return q;
        
    }
    
    
    private ArrayList<String> puntuationParser(String a)
    {
        ArrayList <String> values = new ArrayList();
        String aux = "";
        
        for(int i = 0 ; i < a.length() ; i++)
        {
            if(a.charAt(i) == ';')
            {
                values.add(aux);
                aux = "";
            }else
            {
                aux = aux+a.charAt(i);
            }
        }
        
        values.add(aux);
        
        return values;
    }

    
    public QuestionMaker(Socket socketServicio, int id) throws IOException, SAXException, ParserConfigurationException
    {
        this.socketServicio = socketServicio;
        this.id = id;
        questions = new ArrayList();
        question_aux = new ArrayList();
        
        File input = new File(directory);
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();        
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(input);
        doc.getDocumentElement().normalize();
        
        Element a = (Element) doc.getElementsByTagName("data").item(0);
        Element e = (Element) a.getElementsByTagName("metadata").item(0);
        int num_questions = Integer.parseInt(e.getElementsByTagName("num").item(0).getTextContent());
        
        for(int i = 0 ; i < num_questions ; i++)
        {
            questions.add(i);
            question_aux.add(i);
        }
        
        thr = new Thread(this, "sender");
    }
    
    public void run()
    {
        byte [] datosRecibidos=new byte[1024];
        int bytesRecibidos=0;

        // Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviarka:
        byte [] datosEnviar;


        try {
            // Obtiene los flujos de escritura/lectura
            inputStream=socketServicio.getInputStream();
           
            ////////////////////////////////////////////////////////
            
           // lector = new LectorChat(chat, socketServicio, id);
           
            while (conectado)
            {
                byte [] mensajeRecibido = new byte[2048];
                int bytesRecibidosMensaje;
                bytesRecibidosMensaje = inputStream.read(mensajeRecibido);

               // while(chat.todosHanLeido() == false){}
                //System.out.println(mensajeRecibido.toString());
                String mensaje = new String (mensajeRecibido, 0, bytesRecibidosMensaje);
                
                if(mensaje.compareTo("\r\r\rexit") == 0){ 
                    socketServicio.close(); 
                    inputStream.close(); 
                    outputStream.close(); 
                    conectado=false;
                 }
                else if(mensaje.contains("\r\rpuntuation"))
                {
                    mensaje = mensaje.replace("\r\rpuntuation", "");
                    bytesRecibidosMensaje = inputStream.read(mensajeRecibido);
                    mensaje = new String(mensajeRecibido, 0, bytesRecibidosMensaje);
                    //System.out.println(mensaje);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    ArrayList<String> data = puntuationParser(mensaje);
                    
                    //System.out.println(data.get(0));
                    String id = data.get(0)+timeStamp;
                    int points = Integer.parseInt(data.get(1));
                    ContentProvider sender = new ContentProvider();
                    sender.sendPuntuation(id, data.get(0), points);
                }else if(mensaje.contains("\r\rranking")){
                    outputStream = socketServicio.getOutputStream();
                    ContentProvider ranking = new ContentProvider();
                    String datos = ranking.getRanking();
                    outputStream.write(datos.getBytes());
                }
                else{
               //System.out.println(mensaje);
                    try {  
                        
                        if(questions.size() == 0)
                        {
                            outputStream = socketServicio.getOutputStream();
                            outputStream.write("end;".getBytes());
                        }else{
                            Question q = xmlParser();
                            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            System.out.println(q.getQuestion());
                            outputStream = socketServicio.getOutputStream();
                            datosEnviar = q.getQuestion().getBytes();
                            outputStream.write(datosEnviar);
                            datosEnviar = ((String)q.getAnswerA()+";").getBytes();
                            outputStream.write(datosEnviar);
                            datosEnviar = ((String)q.getAnswerB()+";").getBytes();
                            outputStream.write(datosEnviar);
                            datosEnviar = ((String)q.getAnswerC()+";").getBytes();
                            outputStream.write(datosEnviar);
                            datosEnviar = ((String)q.getCorrectAnswer()+";").getBytes();
                            outputStream.write(datosEnviar);
                        }
                        

                    } catch (IOException ex) {
            
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(QuestionMaker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SAXException ex) {
                        Logger.getLogger(QuestionMaker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        } catch (IOException e) {
                System.err.println("Error al obtener los flujso de entrada/salida.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(QuestionMaker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(QuestionMaker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(QuestionMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
