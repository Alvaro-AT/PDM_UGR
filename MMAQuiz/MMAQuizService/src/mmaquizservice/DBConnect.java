/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmaquizservice;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Windows 8
 */
public class DBConnect {
    
    private Connection conn = null;
    private Statement st = null;
    private ResultSet rs = null;
    
    public DBConnect() throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/mmaquiz","root", "");
            st = conn.createStatement();
        }catch(SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    public Connection getConnection()
    {
        return conn;
    }
    
}
