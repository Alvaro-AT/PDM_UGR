/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmaquizservice;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Windows 8
 */
public class ContentProvider{
    private String message = null;
    //private Socket socketServicio = null;
    DBConnect dbconnection = null;
    
    
    public ContentProvider() throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.message = message;
        //this.socketServicio = socket;
        dbconnection = new DBConnect();
    }
    
    public void sendPuntuation(String id, String user, int puntuation)
    {
        try{
            String query = "INSERT INTO puntuaciones (ID, username, puntuacion) VALUES (?, ?, ?)";
            Connection c = dbconnection.getConnection();
            PreparedStatement prepared = c.prepareStatement(query);
            prepared.setString(1, id);
            prepared.setString(2, user);
            prepared.setInt(3, puntuation);
            
            prepared.execute();
            c.close();
        }catch(Exception ex)
        {
            
        }
    }
    
    public String getRanking()
    {
        String ranking="";
        
        try{
            String query="SELECT * FROM puntuaciones order by puntuacion DESC LIMIT 20";
            Connection c = dbconnection.getConnection();
            Statement prepared = c.createStatement();
            ResultSet rs = prepared.executeQuery(query);
            
            while(rs.next())
            {
                ranking = ranking+rs.getString("username");
                ranking = ranking+":";
                ranking = ranking+rs.getString("puntuacion");
                ranking = ranking+";";
            }
            
            c.close();
        }catch(Exception ex)
        {
            
        }
        
        return ranking;
    }
}
