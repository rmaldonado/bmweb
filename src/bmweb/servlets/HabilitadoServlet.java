package bmweb.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bmweb.dao.ICiudadDao;
import bmweb.dao.IHabilitadoDao;
import bmweb.dto.HabilitadoDTO;
import bmweb.util.Constantes;
import bmweb.util.ParamsUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis
 * 
 * Servlet que realiza operaciones sobre la tabla Habilitados. Si no se le pasan
 * parametros en el request, debera llenar un arraylist con tantas instancias de
 * la clase Habilitado como filas tenga la tabla asociada (ie. por defecto,
 * mostrar el listado).
 *  
 */

public class HabilitadoServlet extends ServletSeguro {

	private IHabilitadoDao habilitadoDao;
	private ICiudadDao ciudadDao;

	public void init() throws ServletException {
		super.init();
		habilitadoDao = (IHabilitadoDao) DBServlet.getApplicationContext().getBean("habilitadoDao");
		ciudadDao = (ICiudadDao) DBServlet.getApplicationContext().getBean("ciudadDao");
	}

	protected String getNombrePermiso() {
		return "habilitados";
	}

	/**
	 * Implementacion de la logica de este servlet
	 * 
	 * TODO Reemplazar con un controller de Spring
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {

		try {

			// Acciones definidas por este servlet
			if ("crear".equals(request.getParameter("accion"))) {
				crear(request, response);
				return;
			}

			if ("editar".equals(request.getParameter("accion"))) {
				editar(request, response);
				return;
			}

			if ("insertar".equals(request.getParameter("accion"))) {
				insertar(request, response);
				return;
			}

			if ("modificar".equals(request.getParameter("accion"))) {
				modificar(request, response);
				return;
			}

			if ("eliminar".equals(request.getParameter("accion"))) {
				eliminar(request, response);
				return;
			}

			// revisa si un codigo ya estaba en la tabla
			if ("revisar".equals(request.getParameter("accion"))) {
				revisar(request, response);
				return;
			}

			// activar y desactivar habilitados
			if ("activar".equals(request.getParameter("accion"))
					|| "desactivar".equals(request.getParameter("accion"))) {
				activarYDesactivar(request, response);
				return;
			}

			// Accion por defecto: recuperar el listado e ir a la vista del
			// listado
			listado(request, response);

		} catch (Exception ex) {
		}

	}

	// listado con paginacion
	// metodo convertido con DAOs - 2005.10.12
	private void listado(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			UsuarioWeb uw = getUsuarioWeb(request);
			
			int dpp = Constantes.DATOS_POR_PAGINA;
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			params.put("dpp", new Integer(dpp + 1).toString());
			List listaHabilitados = habilitadoDao.getHabilitados(params, uw);

			// Calculo si hay pagina anterior y siguiente

			int inicio = 0;
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } 
				catch (Exception e) { }
			}

			int total = listaHabilitados.size();
			request.setAttribute("dpp", new Integer(dpp));
			if (inicio > 0) {
				request.setAttribute("inicio", new Integer(inicio));
			}

			// Si hay mas que DPP datos, hay p�gina siguiente y hay que quitar
			// lo sobrante
			if (total > dpp) {
				request.setAttribute("pagSiguiente", "pagSiguiente");
				listaHabilitados = listaHabilitados.subList(0, dpp);
			}

			request.setAttribute("listaHabilitados", listaHabilitados);
			request.setAttribute("ciudades", ciudadDao.mapa());
			request.setAttribute("listaCiudades", ciudadDao.lista());

			// hago el forward al jsp
			response.addCookie(new Cookie("update", "update")); // Cookie para evitar el "back"
			redirigir(request, response, "listadoHabilitados.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// metodo convertido con DAOs - 2005.10.12
	private void activarYDesactivar(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = habilitadoDao.activarDesactivarHabilitado(params, uw);

		if (ok) {
			mensaje("El registro fue modificado exitosamente", request,
					response);
		} else {
			mensaje("No se pudo modificar el registro", request, response);
		}

		listado(request, response);

	}

	// metodo convertido con DAOs - 2005.10.12
	private void insertar(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = habilitadoDao.crearHabilitado(params, uw);

		if (ok) {
			mensaje("El registro fue agregado exitosamente.", request, response);
		} else {
			mensaje("Hubo un error y no se pudo agregar el registro.", request, response);
		}

		// recupero el listado
		listado(request, response);

	}

	private void crear(HttpServletRequest request, HttpServletResponse response) {

		try {
			// Creo uno nuevo para re-utilizar el jsp de modificacion
			HabilitadoDTO h = new HabilitadoDTO();
			request.setAttribute("habilitado", h);
			request.setAttribute("nuevo", "nuevo");
			request.setAttribute("ciudades", ciudadDao.lista());
			// hago el forward al jsp
			redirigir(request, response, "editarHabilitado.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void editar(HttpServletRequest request, HttpServletResponse response) {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		HabilitadoDTO habilitado = habilitadoDao.editarHabilitado(params, uw);

		if (habilitado == null) {
			mensaje("No se puede determinar un codigo de Habilitado valido", request, response);
			listado(request, response);
			return;
		} else {

			// cargo los datos para la vista
			request.setAttribute("habilitado", habilitado);
			request.setAttribute("ciudades", ciudadDao.lista());

			// hago el forward al jsp
			try { redirigir(request, response, "editarHabilitado.jsp"); } 
			catch (Exception e) { }
		}

	}

	private void modificar(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = habilitadoDao.modificarHabilitado(params, uw);

		if (ok) {
			mensaje("El registro fue modificado exitosamente.", request,
					response);
		} else {
			mensaje("Hubo un error y no se pudo modificar el registro.",
					request, response);
		}

		// recupero el listado
		listado(request, response);

	}

	private void eliminar(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = habilitadoDao.eliminarHabilitado(params, uw);

		if (ok) {
			mensaje("El registro fue elminado exitosamente.", request,
					response);
		} else {
			mensaje("Hubo un error y no se pudo eliminar el registro.",
					request, response);
		}

		// Despues de borrar, actualizo el listado nuevamente
		listado(request, response);

	}

	private void revisar(HttpServletRequest request,
			HttpServletResponse response) {

		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean existe = habilitadoDao.revisarHabilitadoExiste(params, uw);

		// Si existe un habilitado con el mismo codigo,
		// coloco una marca en el request para que lo sepa la vista
		if (existe) {
			request.setAttribute("existeCodigo","1");
		}
		
		try { redirigir(request, response, "revisarHabilitado.jsp"); } 
		catch (Exception e) { }

	}

}