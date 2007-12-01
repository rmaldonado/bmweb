/*
 * Creado en 17-08-2005 por denis
 *
 */
package bmweb.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBServlet extends HttpServlet {
	
	private static ApplicationContext context;
	
	private static int totalServers = 1;
	private static int serverNumber = 1;

	public void init() throws ServletException {
		super.init();
		
		/** 
		 * Este servlet solo sirve para ejecutar la
		 * una primera query a la base de datos, para
		 * inicializar la conexion, pool y otros
		 */
		
		//String path = getServletContext().getRealPath("WEB-INF/ApplicationContext.xml");
		//context = new FileSystemXmlApplicationContext(path);
		context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		
		// Cargo parametros de contexto
		
		// numero de servidores en ejecucion
		try {
			totalServers = new Integer(getInitParameter("totalServers")).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			totalServers = 1;
		}
		
		// numero de este servidor entre todos
		try {
			serverNumber = new Integer(getInitParameter("serverNumber")).intValue(); 
		} catch (Exception e) {
			e.printStackTrace();
			serverNumber = 1;
		}
		
		
	}

	public static ApplicationContext getApplicationContext(){
		return context;
	}

	public static int getTotalServers() {
		return totalServers;
	}

	public static int getServerNumber() {
		return serverNumber;
	}
	
	
}
