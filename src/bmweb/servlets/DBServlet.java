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
		
	}

	public static ApplicationContext getApplicationContext(){
		return context;
	}
	
}
