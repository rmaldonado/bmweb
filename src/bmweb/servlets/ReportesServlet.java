package bmweb.servlets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import bmweb.dao.ICiudadDao;
import bmweb.dao.IReportesDao;
import bmweb.util.ParamsUtil;
import bmweb.util.UsuarioWeb;


/**
 * @author denis
 * 
 * Servlet que realiza los reportes: 
 * 
 * 	Básicamente ejecuta consultas y dibuja los resultados en tablas 
 *  HTML o documentos XML (PO) para generar PDFs.
 * 
 * 
 */

public class ReportesServlet extends ServletSeguro {

	private ApplicationContext appCtx;
	
	private IReportesDao reportesDao;
	private ICiudadDao ciudadDao;
	
	public void init() throws ServletException {
		super.init();
		appCtx = DBServlet.getApplicationContext();
		
		ciudadDao = (ICiudadDao) appCtx.getBean("ciudadDao");
		reportesDao = (IReportesDao) appCtx.getBean("reportesDao");
	}
	
	protected String getNombrePermiso() { return "reportes"; }
	
	/**
	 * Implementacion de la logica de este servlet
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
			
		
			// Ejecuto un reporte
			reporte(request, response);

	}
		

	/**
	 * Cambio las cantidades de una o más prestaciones
	 */
	private void reporte(HttpServletRequest request, HttpServletResponse response) {
		try {
			String[] reparticiones = (String[]) request.getParameterMap().get("reparticiones");
			UsuarioWeb uw = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			// Convierto las reparticiones a una lista separada por comas
			String lasReparticiones = "";
			for(int i=0; null != reparticiones && i < reparticiones.length; i++){
				lasReparticiones = lasReparticiones + reparticiones[i] + ",";
			}
			
			params.put("reparticiones", reparticiones);
			 
			
			// Conservo la lista de reparticiones
			request.setAttribute("lasReparticiones", lasReparticiones);
			
			List filas;
			
			if ("listado".equals(request.getParameter("accion"))){
				filas = reportesDao.ejecutarReporte("", params, uw);
			} else {
				filas = new ArrayList();
			}

			request.setAttribute("filasReporte", filas);

			request.setAttribute("listaCiudades", ciudadDao.lista());
			request.setAttribute("ciudades", ciudadDao.mapa());

			request.setAttribute("listaJurisdicciones", ciudadDao.listaJurisdicciones());
			request.setAttribute("jurisdicciones", ciudadDao.mapaJurisdicciones());
			
			request.setAttribute("listaRegiones", ciudadDao.listaRegiones());
			request.setAttribute("regiones", ciudadDao.mapaRegiones());
			
			request.setAttribute("listaAgencias", ciudadDao.listaAgencias());
			request.setAttribute("agencias", ciudadDao.mapaAgencias());
			
			request.setAttribute("listaReparticiones", ciudadDao.listaReparticiones());
			request.setAttribute("reparticiones", ciudadDao.mapaReparticiones());
			
			// Si me indican que la salida sera en formato excel, agrego estos headers a la salida
			if ("excel".equals(request.getParameter("salida"))){
		        response.setContentType("application/vnd.ms-excel");
		        response.setHeader("Content-Disposition", "attachment; filename=\"reporte.xls\"");
			}

			redirigir(request, response, "reportes.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
