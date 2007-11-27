/*****************************************************************************
 *
 *  Title:        CreateDB.java
 *
 *  Description:    Demo how to create a database 
 *        
 *  An example of running the program: 
 *        
 *   java CreateDB 
 *      'jdbc:informix-sqli://myhost:1533:informixserver=myserver;user=<username>;password=<password>'
 *        
 *   Expected result:
 * 
 * >>>Create Database test.
 * URL = "jdbc:informix-sqli://myhost:1533:informixserver=myserver;user=<username>;password=<password>"
 * >>>End of Create Database test.
 * 
 ***************************************************************************
 */

package pruebas;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDB 
{

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("FAILED: connection URL must be provided in order to run the demo!");
            return;
        }

        String url = args[0];
        Connection conn = null;
        int        rc;
        String     cmd=null;

        String testName = "Create Database";

        System.out.println(">>>" + testName + " test.");
        System.out.println("URL = \"" + url + "\"");

        try 
        {
            Class.forName("com.informix.jdbc.IfxDriver");
            conn = DriverManager.getConnection(url);

            Statement dstmt = conn.createStatement();
            dstmt.executeUpdate("drop database testDB");
            Statement stmt = conn.createStatement();
            cmd = "create database testDB with log;";
            rc = stmt.executeUpdate(cmd);
            stmt.close();
            conn.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return;
        }

        System.out.println(">>>End of " + testName + " test.");
    }
}
