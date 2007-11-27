package bmweb.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author denis
 *
 * Servlet que siempre realiza el logout y redirige a "index.jsp"
 */

public class LogoutServlet extends HttpServlet {

	public void init() throws ServletException {
		super.init();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		doPost(request, response);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession sesion = request.getSession();
		if (sesion!=null){ sesion.invalidate(); }
		
		response.addCookie(new Cookie("mensaje", "Ha salido de la aplicacion"));
		//request.setAttribute("mensaje", "Ha salido de la aplicación");
		
		if (request.getParameterMap().containsKey("timeout")){
			response.addCookie(new Cookie("mensaje", "Ha salido de la aplicacion por inactividad. Debe ingresar nuevamente."));
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("logout.jsp");
		rd.forward(request, response);

	}
	
	
}
