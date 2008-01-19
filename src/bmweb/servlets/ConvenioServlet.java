package bmweb.servlets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import bmweb.dao.ICiudadDao;
import bmweb.dao.IConveniosDao;
import bmweb.dto.ConvenioDTO;
import bmweb.util.Constantes;
import bmweb.util.ParamsUtil;
import bmweb.util.UsuarioWeb;

public class ConvenioServlet extends ServletSeguro {

	private ApplicationContext appCtx;
	
	private ICiudadDao ciudadDao;
	private IConveniosDao conveniosDao;
	
	public void init() throws ServletException {
		super.init();
		appCtx = DBServlet.getApplicationContext();
		ciudadDao = (ICiudadDao) appCtx.getBean("ciudadDao");
		conveniosDao = (IConveniosDao) appCtx.getBean("conveniosDao");
	}
	
	protected String getNombrePermiso() { return "convenios"; }
	
	
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			if ("detalle".equals(params.get("accion"))){
				detalle(request, response);
				return;
			}

			// Acción por omisión
			listaConvenios(request, response);

		} catch (Exception e) {
			
			try {
				mensaje(e.getMessage(), request, response);
				redirigir(request, response, "inicio.jsp");				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}
		

	}

	/**
	 * Ver detalle de un convenio
	 */
	
	private void detalle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UsuarioWeb uw = getUsuarioWeb(request);		
		Map params = ParamsUtil.fixParams(request.getParameterMap());
		String id = (String) params.get("id");
		
		Map paramsDetalle = new HashMap();
		paramsDetalle.put("id", id);
		
		// La lista de convenios solo debería entregar uno 
		List listaConvenios = conveniosDao.getConvenios(paramsDetalle, uw);
		
		try {
			ConvenioDTO convenio = (ConvenioDTO) listaConvenios.get(0);		
			request.setAttribute("convenio", convenio);						
		} catch (Exception e) {
			throw new Exception("Error: No se pudo encontrar convenio");
		}
		
		// Detalle de los valores del convenio por el Id del convenio
		Map paramsValcon = new HashMap();
		int dpp = Constantes.DATOS_POR_PAGINA;
		paramsValcon.put("id", id);
		paramsValcon.put("dpp", new Integer(dpp));
		paramsValcon.put("inicio", (String) params.get("inicio"));
		
		List listaValcon = conveniosDao.getValcon(paramsValcon, uw);
		request.setAttribute("valcon", listaValcon);
		
		request.setAttribute("ciudades", ciudadDao.mapa());
		request.setAttribute("listaCiudades", ciudadDao.lista());

		// Calculo si hay pagina anterior y siguiente
		int inicio = 0;
		if (params.containsKey("inicio")) {
			try { inicio = Integer.parseInt((String) params.get("inicio")); } 
			catch (Exception e) { e.printStackTrace(); }
		}

		int total = listaValcon.size();
		request.setAttribute("dpp", new Integer(dpp));
		if (inicio > 0) {
			request.setAttribute("inicio", new Integer(inicio));
		}

		// Si hay mas que DPP datos, hay pagina siguiente y hay que quitar
		// lo sobrante
		if (total > dpp) {
			request.setAttribute("pagSiguiente", "pagSiguiente");
			listaValcon = listaValcon.subList(0, dpp);
		}

		redirigir(request, response, "detalleConvenio.jsp");

	}

	
	/**
	 * Cargo la lista de convenios, con paginación, etc
	 * 
	 * @param request
	 * @param response
	 */
	private void listaConvenios(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			UsuarioWeb uw = getUsuarioWeb(request);
			
			int dpp = Constantes.DATOS_POR_PAGINA;
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			params.put("dpp", new Integer(dpp + 1).toString());
			List listaConvenios = conveniosDao.getConvenios(params, uw);

			// Calculo si hay pagina anterior y siguiente
			int inicio = 0;
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } 
				catch (Exception e) { e.printStackTrace(); }
			}

			int total = listaConvenios.size();
			request.setAttribute("dpp", new Integer(dpp));
			if (inicio > 0) {
				request.setAttribute("inicio", new Integer(inicio));
			}

			// Si hay mas que DPP datos, hay p�gina siguiente y hay que quitar
			// lo sobrante
			if (total > dpp) {
				request.setAttribute("pagSiguiente", "pagSiguiente");
				listaConvenios = listaConvenios.subList(0, dpp);
			}

			request.setAttribute("listaConvenios", listaConvenios);			
			request.setAttribute("ciudades", ciudadDao.mapa());
			request.setAttribute("listaCiudades", ciudadDao.lista());
			
			redirigir(request, response, "listadoConvenios.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
