package bmweb.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author denis
 *
 */
public class InicioServlet extends ServletSeguro {

	/**
	 * El inicio no tiene mucha logica. Simplemente redirige a la página "inicio.jsp"
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
		
		try { 
			// mensaje("funciona ok", request, response);
			redirigir(request, response, "inicio.jsp");
		} catch (Exception ex){ }

	}

	/**
	 * No se requiere permiso especial para ver la pagina de inicio
	 */
	protected String getNombrePermiso() { return ""; }

}
