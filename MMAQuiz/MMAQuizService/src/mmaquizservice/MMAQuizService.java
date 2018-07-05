/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmaquizservice;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
/**
 *
 * @author Windows 8
 */
public class MMAQuizService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SAXException, ParserConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // TODO code application logic here
        
        int cliente = 0;
        ///Listening port
        int port=8989;
        byte []buffer=new byte[256];
        // Número de bytes leídos
        int bytesLeidos=0;


        ServerSocket socketServidor;
        Socket socketConexion=null;
        
        
        try{
            
            socketServidor = new ServerSocket(port);

            do
            {
                //Aceptamos la conexión
                System.out.println("Listening port");
                socketConexion = socketServidor.accept();
                System.out.println("Client accepted");
                InputStream input = socketConexion.getInputStream();
                
                QuestionMaker qm = new QuestionMaker(socketConexion, cliente);
                qm.start();
                cliente++;
            }while(true);
        }catch (IOException e) {
            System.err.println("Error al escuchar en el puerto "+port);
        }
        //DBConnect a = new DBConnect();
    }   
}