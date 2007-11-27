package bmweb.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bmweb.dao.IBonoDao;
import bmweb.dto.BonoDTO;
import bmweb.dto.DocumentoPagoDTO;
import bmweb.dto.FacturaItemDTO;
import bmweb.manager.IFacturaManager;
import bmweb.util.ParamsUtil;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 * 
 * Servlet que realiza operaciones sobre la tabla BM_DOCPAGO
 * 
 * Si no se pasan parametros, deberia mostrar un listado con las facturas
 * cuyo prestador sea el mismo usuario que esta conectado, permitiendo
 * crear, modificar y eliminar facturas
 *  
 */

public class FacturaServlet extends ServletSeguro {

	private IFacturaManager facturaManager;
	private IBonoDao bonoDao;
	
	public static String ATRIBUTO_FACTURA_EN_SESION = "factura";

	public void init() throws ServletException {
		super.init();
		facturaManager = (IFacturaManager) DBServlet.getApplicationContext().getBean("facturaManager");
		bonoDao = (IBonoDao) DBServlet.getApplicationContext().getBean("bonoDao");
	}

	protected String getNombrePermiso() {
		return "factura";
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

			if ("detalle".equals(request.getParameter("accion"))) {
				detalle(request, response);
				return;
			}

			if ("modificar".equals(request.getParameter("accion"))) {
				modificar(request, response);
				return;
			}

			if ("insertar".equals(request.getParameter("accion"))) {
				insertar(request, response);
				return;
			}

			if ("agregarBono".equals(request.getParameter("accion"))) {
				agregarBono(request, response);
				return;
			}


			if ("quitarBono".equals(request.getParameter("accion"))) {
				quitarBono(request, response);
				return;
			}

			if ("eliminar".equals(request.getParameter("accion"))) {
				eliminar(request, response);
				return;
			}


			/*
			if ("editar".equals(request.getParameter("accion"))) {
				editar(request, response);
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
			*/

			// Accion por defecto: recuperar el listado e ir a la vista del
			// listado
			listado(request, response);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void listado(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			List lista = facturaManager.listado(usuarioWeb, params);
			request.setAttribute("lista", lista);
			
			//if (params.containsKey("paginaSiguiente"))
			
			if (params.containsKey("inicio")){ request.setAttribute("inicio", params.get("inicio")); } 
			response.addCookie(new Cookie("update", "update")); // Cookie para evitar el "back"
			redirigir(request, response, "listadoFactura.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void crear(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			DocumentoPagoDTO factura = null;
			
			try {
				int numeroFactura = (new Integer((String)params.get("factura"))).intValue();
				factura = facturaManager.buscarPorNumero(usuarioWeb, numeroFactura);
			} catch (Exception e) { }
			
			if (factura != null){
				mensaje("Error: La factura #" + factura + " ya existe.", request, response);
			}
			
			redirigir(request, response, "crearFactura.jsp");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertar(HttpServletRequest request, HttpServletResponse response) {

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			Integer factura = new Integer((String)params.get("factura"));
			
			DocumentoPagoDTO facturaEncontrada = null;
			try {
				int numeroFactura = (new Integer((String)params.get("factura"))).intValue();
				facturaEncontrada = facturaManager.buscarPorNumero(usuarioWeb, numeroFactura);
			} catch (Exception e) { }
			
			if (facturaEncontrada != null){
				mensaje("Error: La factura #" + factura + " ya existe.", request, response);
			} else {
				//Integer bono = new Integer((String)params.get("bono"));
				// 20060628 - no guardo la factura directamente, espero hasta que cambie
				// la lista de bonos asociados
				
				//DocumentoPagoDTO documentoPagoDTO = facturaManager.crear(usuarioWeb, factura.intValue());
				
				// Creo el objeto documentoPagoDTO sin guardarlo
				DocumentoPagoDTO documentoPagoDTO = new DocumentoPagoDTO();
				String acreeRut = usuarioWeb.getNombreUsuario() + "-" + TextUtil.getDigitoVerificador(Integer.parseInt(usuarioWeb.getNombreUsuario()));
				String pbCodigo = usuarioWeb.getNombreUsuario();
				
				documentoPagoDTO.setRutAcreedor(acreeRut);
				documentoPagoDTO.setTipoDocumento("FA");
				documentoPagoDTO.setNumeroFactura(factura);
				documentoPagoDTO.setCodigoAcreedor(new Integer(pbCodigo));
				documentoPagoDTO.setOrigen(BonoDTO.TIPOBONO_WEB);
				documentoPagoDTO.setDetalle(new ArrayList());
				
				request.getSession().setAttribute(ATRIBUTO_FACTURA_EN_SESION, documentoPagoDTO);
	
				List listaBonos = facturaManager.listadoBonosSinFacturar(usuarioWeb);
				request.setAttribute("bonos", listaBonos);
	
				request.setAttribute("factura", documentoPagoDTO);			
			}

			redirigir(request, response, "crearFactura.jsp");

		} catch (Exception e) {
			// coloco el mensaje de error
			mensaje(e.getMessage(), request, response);
		}

		try { redirigir(request, response, "crearFactura.jsp"); } 
		catch (Exception e) { }
	}

	private void detalle(HttpServletRequest request, HttpServletResponse response) {

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			Integer factura = new Integer((String)params.get("factura"));
			
			DocumentoPagoDTO documentoPagoDTO = facturaManager.buscarPorNumero(usuarioWeb, factura.intValue());
			
			// Se esta imprimiendo la factura, no se le puede cambiar el detalle
			// desde este momento
		    if ( request.getParameter("imprimir") != null ){
		    	// intento cerrar la factura
		    	boolean ok = facturaManager.cerrarFactura( documentoPagoDTO.getId(), usuarioWeb);
		    	
		    	if (!ok){
		    		mensaje("No se pudo cerrar la factura. Intente imprimir nuevamente.", request, response);
		    	}
		    }
		    
		    if (facturaManager.esFacturaCerrada(documentoPagoDTO.getId(), usuarioWeb)){
		    	request.setAttribute("cerrada", "cerrada");
		    }

			List listaBonos = facturaManager.listadoBonosSinFacturar(usuarioWeb);
			request.setAttribute("bonos", listaBonos);

			request.setAttribute("factura", documentoPagoDTO);
			redirigir(request, response, "crearFactura.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void modificar(HttpServletRequest request, HttpServletResponse response) {
		detalle(request, response);
	}
	
	private void agregarBono(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			String[] foliosBono = request.getParameterValues("folioBono");

			Map params = ParamsUtil.fixParams(request.getParameterMap());

			Integer numFactura = new Integer((String)params.get("factura"));

			DocumentoPagoDTO documentoPagoDTO;

			// 20060628
			// Si la sesion contiene una factura, intento guardarla antes de que la pidan
			// -> solo guardo cuando le han agregado un bono
			
			if (request.getSession().getAttribute(ATRIBUTO_FACTURA_EN_SESION) != null){
				documentoPagoDTO = (DocumentoPagoDTO) request.getSession().getAttribute(ATRIBUTO_FACTURA_EN_SESION);
				facturaManager.crear(usuarioWeb, documentoPagoDTO.getNumeroFactura().intValue());
				request.getSession().removeAttribute(ATRIBUTO_FACTURA_EN_SESION);
			}
			
			int errores = 0;
			for (int i=0; foliosBono != null && i<foliosBono.length; i++){
				
				//Integer folioBono = new Integer((String)params.get("folioBono"));
				Integer folioBono = new Integer(foliosBono[i]);

				documentoPagoDTO = facturaManager.buscarPorNumero(usuarioWeb, numFactura.intValue());
				
				BonoDTO bono = bonoDao.bonoWebPorFolio(folioBono.intValue(), usuarioWeb);
				int valorTotal = bonoDao.getValorTotalBono( bono.getId().intValue(),bono.getRutPrestador(), usuarioWeb);
				bono.setValorTotal(valorTotal);

				// TODO Validar que el Usuario es el mismo proveedor que realiza las atenciones del bono
				// que se quiere asociar a la factura 
				
				try {
					facturaManager.agregarBono(usuarioWeb, numFactura.intValue(), bono.getId().intValue());
				} catch (Exception e) {
					e.printStackTrace();
					errores++;
				}
			}
			
			if (errores == 0){
				mensaje("Se han agregado los bonos a la factura", request, response);
			} else {
				mensaje("Han ocurrido errores al intentar agregar los bonos a la factura", request, response);
			}
			

			// luego de intentar agregar el bono a la factura, consulto la lista de los que quedaron sin asignar
			List listaBonos = facturaManager.listadoBonosSinFacturar(usuarioWeb);
			request.setAttribute("bonos", listaBonos);

			documentoPagoDTO = facturaManager.buscarPorNumero(usuarioWeb, numFactura.intValue());
			request.setAttribute("factura", documentoPagoDTO);
			
		} catch (Exception e) {
			mensaje(e.getMessage(), request, response);
		}
		
		try {
			redirigir(request, response, "crearFactura.jsp");
		} catch (Exception e) { }
		
	}

	
	private HashMap mapaBonosFactura(DocumentoPagoDTO factura, UsuarioWeb uw){
		
		HashMap mapa = new HashMap();
		
		Iterator i = factura.getDetalle().iterator();
		while (i.hasNext()){
			FacturaItemDTO item = (FacturaItemDTO) i.next();
			BonoDTO bono = bonoDao.bonoWebPorSerial(item.getBonoSerial().intValue(), uw);
			mapa.put(bono.getId().toString(), bono.getFolio().toString());
		}
		
		return mapa;
	}
	
	private void quitarBono(HttpServletRequest request, HttpServletResponse response) {
		try {

			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			Integer numFactura = new Integer((String)params.get("factura"));
			Integer folioBono = new Integer((String)params.get("folioBono"));
			BonoDTO bono = bonoDao.bonoWebPorFolio(folioBono.intValue(), usuarioWeb);

			DocumentoPagoDTO documentoPagoDTO;
			try {
				facturaManager.quitarBono(usuarioWeb, numFactura.intValue(), bono.getId().intValue());
				
				// Recupero la factura
				documentoPagoDTO = facturaManager.buscarPorNumero(usuarioWeb, numFactura.intValue());
				if ( documentoPagoDTO.getDetalle() == null || documentoPagoDTO.getDetalle().size() == 0){
					// factura vacia, se elimina
					facturaManager.borrar(usuarioWeb, documentoPagoDTO.getId().intValue());
					mensaje("Se ha quitado el bono de la factura y se ha eliminado la factura vacia.", request, response);
				} else {
					mensaje("Se ha quitado el bono de la factura", request, response);

					documentoPagoDTO = facturaManager.buscarPorNumero(usuarioWeb, numFactura.intValue());
					request.setAttribute("factura", documentoPagoDTO);

					// luego de intentar agregar el bono a la factura, consulto la lista de los que quedaron sin asignar
					List listaBonos = facturaManager.listadoBonosSinFacturar(usuarioWeb);
					request.setAttribute("bonos", listaBonos);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				mensaje("Ha ocurrido un error al intentar quitar el bono a la factura", request, response);
			}
			

		} catch (Exception e) {
			mensaje("Ha ocurrido un error al intentar quitar el bono a la factura", request, response);
		}
		
		try {
			redirigir(request, response, "crearFactura.jsp");
		} catch (Exception e) { }
	}
		
	private void eliminar(HttpServletRequest request, HttpServletResponse response) {
		try {

			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());


			Integer serial = new Integer((String)params.get("codigo"));
			// Integer folioBono = new Integer((String)params.get("folioBono"));
			// BonoDTO bono = bonoDao.bonoWebPorFolio(folioBono.intValue(), usuarioWeb);

			try {
				facturaManager.borrar(usuarioWeb, serial.intValue());
				mensaje("Se ha borrado la factura", request, response);
			} catch (Exception e) {
				e.printStackTrace();
				mensaje("Ha ocurrido un error al intentar borrar la factura", request, response);
			}
			
		} catch (Exception e) {
			mensaje("Ha ocurrido un error al intentar quitar el bono a la factura", request, response);
		}
		
		try {
			listado(request, response);
		} catch (Exception e) { }
	}

}