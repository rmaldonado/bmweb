package bmweb.servlets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationContext;

import bmweb.dao.ICiudadDao;
import bmweb.dao.IConveniosDao;
import bmweb.dto.ConvenioDTO;
import bmweb.dto.ValconDTO;
import bmweb.util.Constantes;
import bmweb.util.CsvParser;
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
			
			if (FileUpload.isMultipartContent(request)){
				procesarArchivoValores(request, response);
				return;
			}

			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			// La primera vez que voy a crear un convenio no he subido archivos
			if ("crear".equals(params.get("accion"))){
				redirigir(request, response, "crearConvenio.jsp");
				return;
			}

			if ("detalle".equals(params.get("accion"))){
				detalle(request, response);
				return;
			}

			if ("detalleExcel".equals(params.get("accion"))){
				detalleExcel(request, response);
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
	 * Proceso de un formulario con subida de archivo con el detalle de valores de un nuevo convenio
	 * 
	 */
	
	private void procesarArchivoValores(HttpServletRequest request, HttpServletResponse response) throws Exception{

		UsuarioWeb uw = getUsuarioWeb(request);
		
		Map params = new HashMap();
		List datosArchivo = null;
		List resultado = new ArrayList();
		
		if (FileUpload.isMultipartContent(request)){
			
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(300*1024); // 300 kilobytes
			factory.setRepository(new File("/tmp")); // 300 kilobytes
			
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List requestItems = upload.parseRequest(request);
			
			// Process the uploaded items
			Iterator iter = requestItems.iterator();
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();

			    if (item.isFormField()) {
			        //processFormField(item);
			    	params.put(item.getFieldName(), item.getString());
			    } else {
			    	// processUploadedFile(item);
			    	String texto = new String(item.get()); // contenido en memoria
			    	datosArchivo =  CsvParser.leer(texto);
			    }
			}

			// Recorro la lista de datos leidos, creo un mapa usando el codigo de prestacion como llave
			Map datosLeidos = new HashMap();

			int idConvenio = 0;
			// DESCARTO LA PRIMERA FILA (CON LOS NOMBES DE LAS COLUMNAS)
			for (int i=1; i<datosArchivo.size(); i++){
				String[] campos = (String []) datosArchivo.get(i);
				
				ValconDTO valcon = new ValconDTO();
				
				valcon.setIdConvenio(Integer.parseInt(campos[0]));
				valcon.setCodigoPrestacion(Integer.parseInt(campos[1]));
				valcon.setValorCovenido(new Float(campos[2]).longValue());
				valcon.setValorLista(new Float(campos[3]).longValue());
				
				// Por omision, marco todos los valores como NUEVOS, luego el proceso
				// los deja en NUEVO, MODIFICADO o ELIMINADO
				// valcon.setEstado(Integer.parseInt(campos[4]));
				valcon.setEstado(ValconDTO.ESTADO_NUEVO);
				
				// resultado.add(valcon);
				datosLeidos.put(new Integer(valcon.getCodigoPrestacion()), valcon);
				
				// conservo el idConvenio
				idConvenio = valcon.getIdConvenio();
			}
			
			// Leo los valores actuales del convenio
			Map paramsValcon = new HashMap(); // parametros: inicio, id, dpp, como Strings
			paramsValcon.put("id", "" + idConvenio);
			paramsValcon.put("inicio", "0");
			paramsValcon.put("dpp", "1000000");
			List listaValcon = conveniosDao.getValcon(paramsValcon, uw);
			
			// Voy comparando los valores del convenio vigente con los subidos por el usuario.
			// * Si el nuevo valor viene en cero o no está en la planilla, está eliminado
			// * Si el nuevo valor es distinto, lo marco como modificado
			
			for (int i=0; i<listaValcon.size(); i++){
				ValconDTO valcon = (ValconDTO)listaValcon.get(i);
				int codigoPrestacion = valcon.getCodigoPrestacion();
				
				// Si el archivo contiene la prestacion presente en el convenio actual
				if (datosLeidos.containsKey(new Integer(codigoPrestacion))){
				  
					ValconDTO valconArchivo = (ValconDTO) datosLeidos.get(new Integer(codigoPrestacion));
					
					if (valconArchivo.getValorCovenido() == 0){
						valcon.setEstado(ValconDTO.ESTADO_ELIMINADO);
						datosLeidos.put(new Integer(codigoPrestacion), valcon);						
					} else {
						// Finalmente, veo si (precioNuevo - precioActual) <> 0 ==> modificado
						if (valconArchivo.getValorCovenido() - valcon.getValorCovenido() != 0){
							valconArchivo.setEstado(ValconDTO.ESTADO_MODIFICADO);
							datosLeidos.put(new Integer(codigoPrestacion), valconArchivo);
						}
					}
					
				} else {
					// La prestación no está en la planilla, la agrego pero en estado eliminada
					valcon.setEstado(ValconDTO.ESTADO_ELIMINADO);
					datosLeidos.put(new Integer(codigoPrestacion), valcon);
				}
			}

			// tomo todos los valores ya aumentados y modificados y creo una lista
			// que entrego como resultado
			
			TreeSet tsResultado = new TreeSet(datosLeidos.values());
			Iterator iResultado = tsResultado.iterator();
			
			resultado = new ArrayList();
			while (iResultado.hasNext()){ resultado.add((ValconDTO)iResultado.next()); }
			
			// "resultado" contiene la lista de ValconDTO del nuevo convenio
			int rutPrestador = Integer.parseInt(uw.getNombreUsuario());
			conveniosDao.guardarNuevoConvenio(rutPrestador, resultado);

		} // end if form == multipart
		
		request.setAttribute("convenios", resultado);
		
		redirigir(request, response, "crearConvenio.jsp");
		
	}
	
	
	/**
	 * Exportar el detalle de un convenio en formato MS-Excel
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void detalleExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UsuarioWeb uw = getUsuarioWeb(request);		
		Map params = ParamsUtil.fixParams(request.getParameterMap());
		String id = (String) params.get("id");

		Map paramsValcon = new HashMap();
		paramsValcon.put("id", id);
		// Hay que pasar un numero maximo de filas, de lo contrario una Constantes.DPP (10)
		paramsValcon.put("dpp", "1000000");
		paramsValcon.put("inicio", "0");

		// Recupero la lista con dichos parametros
		List listaValcon = conveniosDao.getValcon(paramsValcon, uw);
		
		request.setAttribute("resultado", listaValcon);
		
	    response.setContentType("application/vnd.ms-excel");
	    response.setHeader("Content-Disposition", "attachment; filename=\"convenios_" + id + ".xls\"");

	    redirigir(request, response, "detalleConveniosExcel.jsp");
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
		paramsDetalle.put("tipoConvenios", "todos");
		
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

		request.setAttribute("tiposConvenio", ciudadDao.listaTiposConvenio());

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
			
			// Si el usuario está en uno de estos niveles (23 o 24), agrego
			// el RUT del prestador para filtrar sólo los convenios de ese RUT
			if (23 == Integer.parseInt(uw.getNivel())
				|| 24 == Integer.parseInt(uw.getNivel()) ){

				// uw.getNombreUsuario() contiene el RUT del prestador
				params.put("rut", uw.getNombreUsuario());
			}
			
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
