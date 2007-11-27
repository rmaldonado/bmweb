package bmweb.servlets;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import bmweb.util.UsuarioWeb;

/**
 * @author denis
 * 
 * Este servlet implementa la seguridad de usuario y rol.
 * 
 * Para acceder a la funcionalidad de los servlets que extiendan de ServletSeguro,
 * se deben implementar los métodos 
 * 
 * 		private void ejecutarLogica(HttpServletRequest, HttpServletResponse)
 * 
 * 			En este método se implementará la lógica de cada servlet
 * 
 * 		private String getNombrePermiso()
 * 
 * 			Este método debe retornar el nombre del permiso que debe
 * 			existir en la sesion del usuario para que se pueda ejecutar la lógica
 * 
 */
public abstract class ServletSeguro extends HttpServlet{
	
	Logger logger;
	
	public void init() throws ServletException {
		super.init();
		
		logger = Logger.getLogger(this.getClass());
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		doPost(request, response);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Valido si hay sesion. Si no hay sesion, va a la página de login (index.jsp)
		if (request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB) == null){
			mensaje("Debe ingresar a la aplicacion para realizar esta operacion.", request, response);
			redirigir(request, response, "index.jsp");
		}

		/**
		 * Si el usuario está autorizado (tiene un permiso igual al getNombrePermiso() que indica el servlet)
		 * puede ejecutar la lógica, en caso contrario, lo redirijo a la pagina de inicio con un mensaje
		 */
		
		if (estaAutorizado(request,response)){
			ejecutarLogica(request, response);
		} else {
			//mensaje("No tiene autorizacion para realizar esta operacion.", request, response);
			redirigir(request, response, "noAutorizado.jsp");
		}
		
		return;
	}
	
	private boolean estaAutorizado(HttpServletRequest request, HttpServletResponse response){
		
		try {
			HttpSession sesion = request.getSession();
			
			if (sesion == null) return false;
			
			UsuarioWeb usuarioWeb = null;
			usuarioWeb = (UsuarioWeb) sesion.getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);

			if (usuarioWeb == null) return false;
			
			/** 
			 * Si existen un conjunto de permisos y contienen el nombre del permiso que se
			 * indica, entonces se autoriza al usuario. En caso contrario, no.
			 */
			
			// TODO USAR ESTA VERSION
			// /*
			String operacion = "";
			if (request.getParameterMap().containsKey("accion")){
				operacion = request.getServletPath().toUpperCase() + "." + request.getParameter("accion").toUpperCase();
			} else {
				operacion = request.getServletPath().toUpperCase();
			}
			
			logger.debug("se requiere permiso '" + operacion + "'");
			
			if ( usuarioWeb.tienePermiso( operacion )) return true;
			else return false;
			// */

			// if ( usuarioWeb.tienePermiso(getNombrePermiso()) ) return true;
			// else return false;
			
		} catch (Exception ex){
			// Si ocurre cualquier error, no hay permisos
			return false;
		}
		
	}
	
	/**
	 * Método para redirigir el flujo de lógica a un pagina JSP
	 * @param request
	 * @param response
	 * @param pagina
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void redirigir(HttpServletRequest request, HttpServletResponse response, String pagina) 
			throws ServletException, IOException{

		RequestDispatcher rd = request.getRequestDispatcher( pagina );
		rd.forward(request, response);
	}
	
	/**
	 * Coloca un mensaje en una cookie "mensaje"
	 * @param mensaje
	 */
	protected void mensaje(String mensaje, HttpServletRequest request, HttpServletResponse response){
		
		String msg = URLEncoder.encode(mensaje).replaceAll("\\+","%20");
		response.addCookie(new Cookie("mensaje", msg));
	}
	
	protected String getUsuario(HttpServletRequest request){
		try {
			UsuarioWeb u = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
			return u.getNombreUsuario();
		} catch (Exception ex){
			
		}
		
		return "";
	}

	protected UsuarioWeb getUsuarioWeb(HttpServletRequest request){
		try {
			UsuarioWeb u = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
			return u;
		} catch (Exception ex){
			return null;
		}
		
	}

	/**
	 * Metodo en que se implementa la lógica de la aplicación
	 * @param request
	 * @param response
	 */
	protected abstract void ejecutarLogica(HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * Metodo que retorna un string con el nombre de un permiso requerido para ejecutar la logica
	 * @return Nombre del permiso requerido
	 */
	protected abstract String getNombrePermiso();

}
