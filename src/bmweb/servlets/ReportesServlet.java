package bmweb.servlets;
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
			UsuarioWeb uw = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			List filas = reportesDao.ejecutarReporte("", params, uw);

			request.setAttribute("filasReporte", filas);

			request.setAttribute("ciudades", ciudadDao.mapa());
			request.setAttribute("listaCiudades", ciudadDao.lista());

			redirigir(request, response, "reportes.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
