/**************************************************************************
 *
 * Licensed Materials - Property of IBM Corporation
 *
 * Restricted Materials of IBM Corporation
 *
 * IBM Informix JDBC Driver
 * (c) Copyright IBM Corporation 1998, 2004 All rights reserved.
 *
 ****************************************************************************/
/*****************************************************************************
 *
 *  Title:         BatchInsert.java
 *
 *  Description: This program demonstrates single
 *               insert statement in batch execution mode.
 *
 *  Instructions to run the program
 *
 *  1. Use CreateDB to create the Database testdb if not already done.
 *           java CreateDB 'jdbc:informix-sqli:
 *           //myhost:1533:informixserver=myserver;user=<username>;password=<password>'
 *  2. Run the program by:
 *          java BatchInsert
 *          'jdbc:informix-sqli://myhost:1533:informixserver=myserver;
 *          user=<username>;password=<password>'
 *  3. Expected Result 
 *     URL = "jdbc:informix-sqli://myhost:1533/testDB:informixserver=myserver;user=<username>;
 *           password=<password>"
 * Trying to insert data using PreparedStatement ...
 * Number of records inserted (should be 1000):
 *  recodes inserted = 1000
 *
 ***************************************************************************
 */

package pruebas;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.StringTokenizer;

public class BatchInsert 
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
        Statement stmt = null; 
        StringTokenizer st = new StringTokenizer(url, ":");
        String token;
        String newUrl = "";

        for (int i = 0; i < 4; ++i)
        {
            if (!st.hasMoreTokens())
            {
                System.out.println("FAILED: incorrect URL format!");
                return;
            }
            token = st.nextToken();
            if (newUrl != "")
                newUrl += ":";
            newUrl += token;
        }

        newUrl += "/testDB";

        while (st.hasMoreTokens())
        {
            newUrl += ":" + st.nextToken();
        }

        String cmd=null;

        System.out.println("URL = \"" + newUrl + "\"");

        try
        {
            Class.forName("com.informix.jdbc.IfxDriver");

            conn = DriverManager.getConnection(newUrl);

            try {
				stmt = conn.createStatement();
				//stmt.executeUpdate("drop table Product");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

//            try {
//				stmt = conn.createStatement();
//				stmt.executeUpdate("create table Product(id integer, name char(100), price float)");
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
         

            PreparedStatement pst = conn.prepareStatement("insert into bm_bono(dom_tipbon,bo_folio) values (?,?)");
            for (int i = 0; i < 10000 ; i++)
            {
                pst.setString(1, "W");
                pst.setInt(2, i+1000);
                pst.addBatch();
                System.out.println(i);
            }
            pst.executeBatch();

//            stmt.executeUpdate("insert into Product(id, name, price) values (1, 'Primer Nombre', 1.9)");
//            stmt.executeUpdate("insert into Product(id, name, price) values (2, 'Segundo Nombre', 2.9)");
//            stmt.executeUpdate("insert into Product(id, name, price) values (3, 'TerceroNombre', 3.9)");

            conn.commit();

            /*
            ResultSet r = stmt.executeQuery("select count(*) as total from Product");
            int j;

            System.out.println("Number of records inserted :");
            while(r.next())
            {
                int count = r.getInt(1);
                System.out.println(" recodes inserted = " + count);
            }
            r.close();
            */
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
   }
}
