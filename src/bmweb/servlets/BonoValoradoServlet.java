package bmweb.servlets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.springframework.context.ApplicationContext;

import bmweb.dao.IBeneficiariosDao;
import bmweb.dao.IBonoDao;
import bmweb.dao.ICiudadDao;
import bmweb.dao.IHabilitadoDao;
import bmweb.dao.IPrestacionesGenericasDao;
import bmweb.dao.IPrestadoresDao;
import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.BonoDTO;
import bmweb.dto.BonoWItemDTO;
import bmweb.dto.CiudadDTO;
import bmweb.dto.HabilitadoDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.CodigoValidacion;
import bmweb.util.Constantes;
import bmweb.util.OrdenarUtil;
import bmweb.util.ParamsUtil;
import bmweb.util.UsuarioWeb;


/**
 * @author denis
 * 
 * Servlet que realiza las operaciones para obtener un
 * bono valorado.
 * 
 * Uso la valoracion de CMC de BonoServlet
 * 
 */

public class BonoValoradoServlet extends ServletSeguro {
	
	private IBonoDao bonoDao;
	private ICiudadDao ciudadDao;
	private IPrestacionesGenericasDao prestacionesGenericasDao;
	private IPrestadoresDao prestadoresDao;
	private IBeneficiariosDao beneficiariosDao;
	private IHabilitadoDao habilitadoDao;

	private ApplicationContext appCtx;
	private int _codigoSantiago = -1;
	
	public void init() throws ServletException {
		super.init();
		appCtx = DBServlet.getApplicationContext();
		bonoDao = (IBonoDao) appCtx.getBean("bonoDao");
		ciudadDao = (ICiudadDao) appCtx.getBean("ciudadDao");
		prestacionesGenericasDao = (IPrestacionesGenericasDao) appCtx.getBean("prestacionesGenericasDao");
		beneficiariosDao = (IBeneficiariosDao) appCtx.getBean("beneficiariosDao");
		prestadoresDao = (IPrestadoresDao) appCtx.getBean("prestadoresDao");
		habilitadoDao = (IHabilitadoDao) appCtx.getBean("habilitadoDao");		
	}
	
	protected String getNombrePermiso() { return "bonoValorado"; } // TODO OJO - Uso el mismo permiso para los bonos valorados y no valorados
	
	/**
	 * Implementacion de la logica de este servlet
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			// Luis Lat�n de puro intruso no mas //
			if ("detalle".equals( request.getParameter("accion") )){
				detalle(request, response);
				return;
			}
			if ("anular".equals( request.getParameter("accion") )){
				anular(request, response);
				return;
			}
			// Acciones definidas por este servlet
			
			if ("crear".equals( request.getParameter("accion") )){
				crear(request, response);
				return;
			}
			
			// inserto objetos en la sesion, implementacion simulada
			if ("insertar".equals( request.getParameter("accion") )){
				insertar(request, response);
				return;
			}

			if ("cambiarPrestaciones".equals( request.getParameter("accion") )){
				cambiarPrestaciones(request, response);
				return;
			}

			if ("buscar".equals( request.getParameter("accion") )){
				buscar(request, response);
				return;
			}

			if ("agregar".equals( request.getParameter("accion") )){
				agregar(request, response);
				return;
			}

			if ("eliminar".equals( request.getParameter("accion") )){
				eliminar(request, response);
				return;
			}

			if ("cambiarCantidades".equals( request.getParameter("accion") )){
				cambiarCantidades(request, response);
				return;
			}

			if ("buscarPrestadores".equals( request.getParameter("accion") )){
				buscarPrestadoresYAportes(request, response);
				return;
			}
			
			if ("validarCodigoBono".equals( request.getParameter("accion") )){
				validarCodigoBono(request, response);
				return;
			}
			if ("listado".equals( request.getParameter("accion") )){
				listado(request, response);
				return;
			}
			
			// La acción por defecto es crear un bono valorado
			crear(request, response);

			
		} catch (Exception ex){ }
		
	}

	/**
	 * Metodo interno: parseo el request y retorno una lista de String[] donde
	 * cada String[] = codigo, nombre, cantidad, tipo de prestacion.
	 * 
	 * Depende de que el Map params contenga llaves "prestacion.N" y "cantidad.N"
	 * 
	 * @param request
	 * @return
	 */
	
	// TODO MEJORAR IMPLEMENTACION 
	private List getListaPrestaciones(Map params, UsuarioWeb uw){
		
		List salida = new ArrayList();
		
		for (int i=1; i<100; i++){ // desde "prestacion.1" en adelante...
			if (params.containsKey("prestacion."+i)){
				String codigo = (String) params.get("prestacion."+i);
				String cantidad = (String) params.get("cantidad."+i);
				
				// retorna [codigo, nombre, tipoPrestacion]
				String[] tupla = (String[]) bonoDao.getPrestacionesPorCodigo(codigo, new Date(), null, uw).get(0);
				String nombre = tupla[1]; 
				String tipo = tupla[2]; 
				
				salida.add( new String[]{ codigo, nombre, cantidad, tipo } );
			} else {
				break; // si no esta la "prestacion.N", termino el loop
			}
		}
		
		return salida;
	}

	/**
	 * Cambio las cantidades de una o más prestaciones
	 */
	private void cambiarCantidades(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb uw = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			List prestacionesBono = getListaPrestaciones(params, uw);
			request.setAttribute("prestacionesBono", prestacionesBono);
			cambiarPrestaciones(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Elimino una prestacion del listado de prestaciones del bono
	 */
	private void eliminar(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));

			String codigoEliminar = (String) params.get("prestacionEliminar");
			for (int i=0; i<prestacionesBono.size(); i++){
				String[] fila = (String[]) prestacionesBono.get(i);
				if (fila[0].equals(codigoEliminar)){
					prestacionesBono.remove(i);
					break;
				}
			}

			request.setAttribute("prestacionesBono", prestacionesBono);
			cambiarPrestaciones(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Agrego una prestacion al bono que voy a crear
	 */
	private void agregar(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));
			String CMC = (String) params.get("cmc");
            
			String codigoNueva = (String) params.get("codPrestacionNueva");

			// Luis Latin *No permitir repetir la misma prestacion* //
			for (int i=0; i<prestacionesBono.size(); i++){
				String[] fila = (String[]) prestacionesBono.get(i);
				if (fila[0].equals(codigoNueva)){
					prestacionesBono.remove(i);
					mensaje("La prestacion ya existe en el bono; modifique la columna cantidad", request, response);
					break;
				}
			}
            // Luis Latin *No permitir repetir la misma prestacion* //
			
			// NUEVO - 2005.12.15 - Valido si el CMC puede realizarse la prestacion deseada
			
			RolbeneDTO rolbene = null;
			BeneficiarioDTO beneficiario = null;
			rolbene = beneficiariosDao.leeRolbene(CMC, usuarioWeb);
			beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );

			
			String errorAutorizacion = prestadoresDao.autorizarPrestacion(CMC, new Integer(usuarioWeb.getRutEmisor()), beneficiario, Integer.parseInt(codigoNueva), 1 , usuarioWeb);
			if ( errorAutorizacion != null){
				// Si hubo un error de autorizacion, agrego un mensaje y no agrego la prestacion nueva
				mensaje(errorAutorizacion, request, response);
			} else {
				
				// veo si hay prestaciones incompatibles: creo un int[] con las prestaciones
				int[] prestaciones = new int[prestacionesBono.size()];
				for (int i=0; i<prestacionesBono.size(); i++){
					String[] fila = (String[]) prestacionesBono.get(i);
					prestaciones[i] = new Integer(fila[0]).intValue();
				}
				
				// reviso si hay incompatibilidad con alguna (metodo distinto del dao)
				String errorIncompatible = prestadoresDao.buscarPrestacionesIncompatibles(prestaciones, new Integer(codigoNueva).intValue(), usuarioWeb);
				if (errorIncompatible != null){
					mensaje(errorIncompatible, request, response);
				} else {
					
					// si no hay incompatibilidad de ningun tipo, la agrego
					List lista = bonoDao.getPrestacionesPorCodigo(codigoNueva, new Date(), null, getUsuarioWeb(request));
					String[] nueva = (String[]) lista.get(0);
					prestacionesBono.add(new String[]{nueva[0], nueva[1], "1", nueva[2]});
				}
				
			}
			
			request.setAttribute("prestacionesBono", prestacionesBono);			
			cambiarPrestaciones(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void buscar(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			List prestacionesEncontradas = null;
			
			if (params.containsKey("busqueda") && "porCodigo".equals(params.get("tipoBusqueda")) ){
				prestacionesEncontradas = bonoDao.getPrestacionesPorCodigo( (String)params.get("busqueda"), new Date(), null, getUsuarioWeb(request));
			}

			if (params.containsKey("busqueda") && "porNombre".equals(params.get("tipoBusqueda")) ){
				prestacionesEncontradas = bonoDao.getPrestacionesPorNombre( (String)params.get("busqueda"), new Date(), null, getUsuarioWeb(request));
			}

			// Cargo las prestaciones actuales del bono
			List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));
			request.setAttribute("prestacionesBono", prestacionesBono);

			// Cargo las prestaciones encontradas
			request.setAttribute("prestacionesEncontradas", prestacionesEncontradas);

			cambiarPrestaciones(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void cambiarPrestaciones(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			// La ciudad por su codigo
			String codCiudad = (String) params.get("ciudad");
			String nombreCiudad = (String) ciudadDao.mapa().get(new Integer(codCiudad));
			request.setAttribute("nombreCiudad", nombreCiudad);
			request.setAttribute("ciudades", ciudadDao.lista());
			
			// Validacion: Si el usuario quiere bonos para santiago pero no puede
			// sacarlos, le envio un mensaje de error -- 2006.03.31
			
			if (codCiudad.equals( getCodigoSantiago() ) && usuarioWeb.puedeHacerBonosSantiago() == false){
				mensaje("El usuario no puede emitir bonos en Santiago", request, response);
				response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
				redirigir(request, response, "crearBonoValorado1.jsp");			
				return;
			}

			String errores = validarPrestacionesBono(request, response);

			if (errores != null){
				mensaje(errores, request, response);
			}
			
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			redirigir(request, response, "crearBonoValorado2.jsp");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void buscarPrestadoresYAportes(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));
			request.setAttribute("prestacionesBono", prestacionesBono);
			String RutDirecto = (String) params.get("rutdirecto");
			System.out.println(RutDirecto);
			// La ciudad por su codigo
			String codCiudad = (String) params.get("ciudad");
			String nombreCiudad = (String) ciudadDao.mapa().get(new Integer(codCiudad));
			request.setAttribute("nombreCiudad", nombreCiudad);

			// El beneficiario por su CMC
			String CMC = (String) params.get("cmc");
			RolbeneDTO rolbene = beneficiariosDao.leeRolbene(CMC, usuarioWeb);
			String codContrato = rolbene.getContrato();
			
			BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
			request.setAttribute("beneficiario", beneficiario);
			
			String errores = validarPrestacionesBono(request, response);

			// si hay errores, no creo el bono valorado y vuelvo a la pantalla
			// de agregar prestaciones
			if (errores != null){
				
				request.setAttribute("prestacionesBono", prestacionesBono);
				request.setAttribute("NOVALORIZAR", "NOVALORIZAR");
				
				mensaje(errores, request, response);
				response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
				redirigir(request, response, "crearBonoValorado2.jsp");
				return;
			}

			
			
			// TODO MOVER ESTA LOGICA A UN DAO O UN PrestadoresNegocio
			// Si el bono contiene prestaciones, busco los prestadores que pueden realizarlas todas
			if (request.getAttribute("prestacionesBono") != null){
				prestacionesBono = (List) request.getAttribute("prestacionesBono");
				
				// para calcular el numero de prestaciones ofrecidas por el prestador (por su rut)
				HashMap numPrestacionesOfrecidas = new HashMap();
				
				// Guardo los nombres de los prestadores por su rut
				HashMap mapaNombresPrestadores = new HashMap();
				
				// Primero recorro la lista con los codigos de las prestaciones
				for (int i=0; i<prestacionesBono.size(); i++){
					String[] fila = (String[]) prestacionesBono.get(i);
					int codPrestacion = new Integer(fila[0]).intValue();
					int intCiudad = new Integer(codCiudad).intValue();
					
					// busco los prestadores para cada prestacion y la ciudad
					List prestadores = prestadoresDao.prestadoresPorCiudadYPrestacion( intCiudad, codPrestacion, usuarioWeb, RutDirecto );
					// obtengo una Lista de String[] con el {rutPrestador, nombrePrestador }
					
					for (int j=0; prestadores != null && j < prestadores.size(); j++){
						String[] prestador = (String[])prestadores.get(j);
						String rutPrestador = prestador[0];
						String nombrePrestador = prestador[1];
						
						mapaNombresPrestadores.put(rutPrestador, nombrePrestador);
						
						// Para cada {rutPrestador, nombrePrestador} coloco la cantidad de prestaciones
						// que realiza en el mapaPrestadores
						if (numPrestacionesOfrecidas.containsKey(rutPrestador)){
							Integer valor = (Integer) numPrestacionesOfrecidas.get(rutPrestador);
							numPrestacionesOfrecidas.put(rutPrestador, new Integer(valor.intValue()+1));
						} else {
							numPrestacionesOfrecidas.put(rutPrestador, new Integer(1));
						}
					}
					
				}
				
				// Calculo el numero de prestaciones distintas
				int numeroPrestaciones = prestacionesBono.size();

				List listaPrestadores = new ArrayList();

				// Ahora, recorro el mapaPrestadores para ver los prestadores que hacen la
				// misma cantidad de prestaciones que tiene el bono
				
				Iterator iPrestadores = numPrestacionesOfrecidas.keySet().iterator();
				while (iPrestadores.hasNext()){
					String rutPrestador = (String) iPrestadores.next();
					String nombrePrestador = (String) mapaNombresPrestadores.get(rutPrestador);
					Integer numPrestaciones = (Integer) numPrestacionesOfrecidas.get(rutPrestador);
					
					// Si el prestador hace todas las prestaciones del bono,
					// lo agrego a la lista de prestadores validos para el bono

					if (numPrestaciones.intValue() == numeroPrestaciones){
						
						// Calculo los aportes y copago por prestador y lista de prestaciones
						// TODO 20060423 REVISAR - Asumo que la prestacion es ambulatoria
						// TODO 20060425 corregir '6' por un dominio del profesional que viene cobrando
						int[] aportes = prestadoresDao.copagoYAportesPorPrestador(CMC, rutPrestador, prestacionesBono, new Date(), 1, 6, codContrato, 1, 2, "0", usuarioWeb );
					
						// si la lista de aportes es valida, agrego al prestador. Si es nula, hay algun problema
						// con el prestador (convenio vencido, etc).
						if (aportes != null){
							String totalAporteDipreca = new Integer(aportes[0]).toString();
							String totalAporteSeguro  = new Integer(aportes[1]).toString();
							String totalCopago        = new Integer(aportes[2]).toString();
							
							// Veo si el prestador es arancel diferenciado o no
							String aradif = null;
							int intRutPrestador = Integer.parseInt( rutPrestador.substring(0, rutPrestador.indexOf("-")));
							if (prestadoresDao.prestadorEsArancelDiferenciado( intRutPrestador )){
								aradif = "S";
							}
							
							String [] fila = new String[]{ rutPrestador, nombrePrestador, totalAporteDipreca, totalAporteSeguro, totalCopago, aradif };
							listaPrestadores.add(fila);
						}

					}
					
				}
				
				// Ordeno la lista de prestadores usando el OrdenarUtil
				listaPrestadores = OrdenarUtil.ordenarCopagoAscendente(listaPrestadores);
				
				request.setAttribute("listaPrestadores", listaPrestadores);
			} // if bono contiene prestaciones
			
			cambiarPrestaciones(request, response);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// TODO IMPLEMENTACION SIMULADA
	private void insertar(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			String errores = validarPrestacionesBono(request, response);

			// si hay errores, no creo el bono valorado y vuelvo a la pantalla
			// de agregar prestaciones
			if (errores != null){
				
				List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));
				request.setAttribute("prestacionesBono", prestacionesBono);

				// La ciudad por su codigo
				String codCiudad = (String) params.get("ciudad");
				String nombreCiudad = (String) ciudadDao.mapa().get(new Integer(codCiudad));
				request.setAttribute("nombreCiudad", nombreCiudad);

				// El beneficiario por su CMC
				String CMC = (String) params.get("cmc");
				RolbeneDTO rolbene = beneficiariosDao.leeRolbene(CMC, usuarioWeb);
				BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
				request.setAttribute("beneficiario", beneficiario);
				
				mensaje(errores, request, response);
				response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
				redirigir(request, response, "crearBonoValorado2.jsp");
				return;
			}


			// Paso el rut del habilitado para el DAO
			UsuarioWeb uw = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
			//Integer codigoUsuario = habilitadoDao.habilitadoPorUsuario(uw).getCodigo();
			//params.put( UsuarioWeb.ATRIBUTO_USUARIO_WEB, codigoUsuario );
			
			BonoDTO bono = bonoDao.guardarBonoValoradoWeb(params, uw);
			List datosDetalle = bonoDao.getDetalleBonoValoradoWeb(bono.getId().intValue(), getUsuarioWeb(request)); // El detalle de las prestaciones del bono

			HabilitadoDTO habilitado = new HabilitadoDTO(); // habilitadoDao.getHabilitadoPorCodigo( bono.getCodigoHabilitado().intValue(), usuarioWeb );
            PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
            //RolbeneDTO rolbene = beneficiariosDao.leeRolbenePorCarne( bono.getCarneBeneficiario(), usuarioWeb );
            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
            BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );

			request.setAttribute("bono", bono);
			request.setAttribute("datosDetalle", datosDetalle);
			request.setAttribute("habilitado", habilitado);
			request.setAttribute("prestador", prestador);
			request.setAttribute("rolbene", rolbene);
			request.setAttribute("beneficiario", beneficiario);
			request.setAttribute("imponente", beneficiario2);

            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
			request.setAttribute("nombreCiudad", nombreCiudad);

			// Pago directo en el detalle del bono valorado
			//llr 11.06.2006
			boolean espagoDirecto=true;
            List listallr = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
            try{
               BonoWItemDTO itemw = (BonoWItemDTO) listallr.get(0);
               int codgen = itemw.getCodigoPrestacion();
               if(codgen==2)espagoDirecto=false;
            }catch (Exception ex){
            	 	ex.printStackTrace() ;} //fin llr 11.06.2006
			if (prestadoresDao.prestadorEsPagoDirecto(bono.getRutPrestador().trim())&& espagoDirecto){
				request.setAttribute("pagoDirecto", "pagoDirecto");
			}

			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			redirigir(request, response, "detalleBonoValorado.jsp");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

	private void crear(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			if (!bonoDao.puedeEmitirBonosPorNivel(usuarioWeb)){
				mensaje("Este usuario no puede emitir bonos", request, response);
				redirigir(request, response, "irInicio.jsp");
				return;
			}

			
			// Creo uno nuevo para re-utilizar el jsp de modificacion
			request.setAttribute("ciudades", ciudadDao.lista());

			// hago el forward al jsp
			redirigir(request, response, "crearBonoValorado1.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}

	private void anular(HttpServletRequest request, HttpServletResponse response) {
		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = bonoDao.anularBonoValorado(params, uw);

		if (ok) {
			mensaje("El Bono Valorado fue anulado exitosamente", request,
					response);
		} else {
			mensaje("No se pudo anular el Bono Valorado", request, response);
		}

		listado(request, response);
		
				
	}
	
	private void detalle(HttpServletRequest request, HttpServletResponse response) {
		
		Session session = null;

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb( request );
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			int numeroFolio = -1;
			int id_bono = -1;
			try { numeroFolio = Integer.parseInt((String)params.get("folio")); } catch (Exception e) { }
			try { numeroFolio = Integer.parseInt((String)request.getAttribute("folio")); } catch (Exception e) { }
			//BonoDTO bono = bonoDao.bonoWebPorFolio( numeroFolio, usuarioWeb );
			try { id_bono = Integer.parseInt((String)params.get("id_bono")); } catch (Exception e) { }
			try { id_bono = Integer.parseInt((String)request.getAttribute("id_bono")); } catch (Exception e) { }
			BonoDTO bono = bonoDao.bonoWebPorSerial(id_bono, usuarioWeb);
			

			List datosDetalle = bonoDao.getDetalleBonoValoradoWeb(bono.getId().intValue(), usuarioWeb); // El detalle de las prestaciones del bono

			HabilitadoDTO habilitado = habilitadoDao.getHabilitadoPorCodigo( bono.getCodigoHabilitado().intValue(), usuarioWeb );
            PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
            // RolbeneDTO rolbene = beneficiariosDao.leeRolbenePorCarne( bono.getCarneBeneficiario() );
            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
            BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );

			request.setAttribute("bono", bono);
			request.setAttribute("datosDetalle", datosDetalle);
			request.setAttribute("habilitado", habilitado);
			request.setAttribute("prestador", prestador);
			request.setAttribute("rolbene", rolbene);
			request.setAttribute("beneficiario", beneficiario);
			request.setAttribute("imponente", beneficiario2);

            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
			request.setAttribute("nombreCiudad", nombreCiudad);

			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			
			// Pago directo en el detalle del bono valorado
//			llr 11.06.2006
			boolean espagoDirecto=true;
            List listallr = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
            try{
               BonoWItemDTO itemw = (BonoWItemDTO) listallr.get(0);
               int codgen = itemw.getCodigoPrestacion();
               if(codgen==2)espagoDirecto=false;
            }catch (Exception ex){
            	 	ex.printStackTrace();
            } //fin llr 11.06.2006
            
			if (prestadoresDao.prestadorEsPagoDirecto(bono.getRutPrestador().trim())&& espagoDirecto){
				request.setAttribute("pagoDirecto", "pagoDirecto");
			}
			
			redirigir(request, response, "detalleBonoValorado.jsp");			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void listado(HttpServletRequest request, HttpServletResponse response) {

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			int dpp = Constantes.DATOS_POR_PAGINA;
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			params.put("dpp", new Integer(dpp + 1).toString());
			params.put("VALORADOS", "VALORADOS");
			
			List listaBonos = bonoDao.getBonos(params, usuarioWeb);

			// Calculo si hay pagina anterior y siguiente

			int inicio = 0;
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } 
				catch (Exception e) { }
			}

			int total = listaBonos.size();
			request.setAttribute("dpp", new Integer(dpp));
			if (inicio > 0) {
				request.setAttribute("inicio", new Integer(inicio));
			}

			// Si hay mas que DPP datos, hay p�gina siguiente y hay que quitar lo sobrante
			if (total > dpp) {
				request.setAttribute("pagSiguiente", "pagSiguiente");
				listaBonos = listaBonos.subList(0, dpp);
			}
			
			request.setAttribute("listaBonos", listaBonos);
			request.setAttribute("ciudades", ciudadDao.mapa()); // Cargo el HashMap de las ciudaddes
			request.setAttribute("listaCiudades", ciudadDao.lista()); // Cargo el HashMap de las ciudaddes
			
			// hago el forward al jsp
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			redirigir(request, response, "listadoBonoValorado.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	private void validarCodigoBono(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			// si viene el parametro codigo, intento validarlo
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			String codigo = (String) params.get("codigo");

			if (codigo != null){

				try {
					UsuarioWeb usuarioWeb = getUsuarioWeb(request);

					// quito el ultimo caracter al codigo y obtengo decripto el serial del bono
					String serialEncriptado = codigo.substring(0, codigo.length()-1);
					String decriptado = CodigoValidacion.decriptaCodigo(serialEncriptado);
					int serial = Integer.parseInt(decriptado);
					
					BonoDTO bono = bonoDao.bonoWebPorSerial(serial, usuarioWeb);
					List datosDetalle = bonoDao.getDetalleBonoValoradoWeb(bono.getId().intValue(), usuarioWeb); // El detalle de las prestaciones del bono

					HabilitadoDTO habilitado = habilitadoDao.getHabilitadoPorCodigo( bono.getCodigoHabilitado().intValue(), usuarioWeb );
		            PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
		            // RolbeneDTO rolbene = beneficiariosDao.leeRolbenePorCarne( bono.getCarneBeneficiario() );
		            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
		            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
		            BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );

		            
		            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
					request.setAttribute("nombreCiudad", nombreCiudad);

		            
					request.setAttribute("bono", bono);
					request.setAttribute("datosDetalle", datosDetalle);
					request.setAttribute("habilitado", habilitado);
					request.setAttribute("prestador", prestador);
					request.setAttribute("rolbene", rolbene);
					request.setAttribute("beneficiario", beneficiario);
					request.setAttribute("imponente", beneficiario2);
					
					response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
					redirigir(request, response, "detalleBonoValorado.jsp");			


					//request.setAttribute("resultado", new String[]{codPrestacion, folioBono, rutHabilitado, cmcBeneficiario});
					
				} catch (Exception ex){
					mensaje("Error: El codigo de validacion no es correcto.", request, response);
					redirigir(request, response, "validarCodigoBonoValorado.jsp");
				}
			} else {
				// Voy a la pagina de validacion de bono
				try { redirigir(request, response, "validarCodigoBonoValorado.jsp"); }
				catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception ex){
			ex.printStackTrace();

			// Voy a la pagina de validacion de bono
			try { redirigir(request, response, "validarCodigoBonoValorado.jsp"); }
			catch (Exception e) { e.printStackTrace(); }
		}
		
		
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	private String validarPrestacionesBono(HttpServletRequest request, HttpServletResponse response) {
		
		Map params = ParamsUtil.fixParams(request.getParameterMap());
		UsuarioWeb usuarioWeb = getUsuarioWeb(request);
		
		// El beneficiario por su CMC
		String CMC = (String) params.get("cmc");
		RolbeneDTO rolbene = beneficiariosDao.leeRolbene(CMC, usuarioWeb);
		BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
		request.setAttribute("beneficiario", beneficiario);
		
		// reviso las prestaciones que tinen m�s de una atencion para autorizarlas por cantidad
		List prestacionesBono = getListaPrestaciones(params, getUsuarioWeb(request));
		
		String nuevoMensaje = "";
		String mensajeError = "";
		int numeroErrores = 0;
		for (int i=0; i<prestacionesBono.size(); i++){
			// new String[]{ codigo, nombre, cantidad, tipo }
			String[] fila = (String[]) prestacionesBono.get(i);
			int codigo = Integer.parseInt(fila[0]);
			int cantidad = 0;
			try { cantidad = Integer.parseInt(fila[2]); } catch (Exception ex){ }
			
			if (cantidad > 0){
				
				nuevoMensaje = prestadoresDao.autorizarPrestacion(CMC, new Integer(usuarioWeb.getRutEmisor()), beneficiario, codigo, cantidad, usuarioWeb);

				// si nuevoMensaje NO es null es porque no se autorizo la prestacion
				if (nuevoMensaje != null){
					mensajeError = nuevoMensaje;
					numeroErrores++;
				}

				// reviso prestaciones incompatibles
				int[] prestaciones = new int[i+1];
				for (int j=0; j<i; j++){
					String[] filaj = (String[]) prestacionesBono.get(j);
					prestaciones[i] = new Integer(filaj[0]).intValue();
				}
				
				// reviso si hay incompatibilidad con alguna (metodo distinto del dao)
				String errorIncompatible = prestadoresDao.buscarPrestacionesIncompatibles(prestaciones, codigo, usuarioWeb);
				if (errorIncompatible != null){
					mensajeError = errorIncompatible;
					numeroErrores++;
				}

				
			}
		}
		
		// reviso si se produjeron errores
		if (numeroErrores == 0) return null;
		else {
			// cuando hay un error coloco una marca en el request
			request.setAttribute("NOVALORIZAR", "NOVALORIZAR");
			
			if (numeroErrores == 1) return mensajeError;
			// si hay m�s de 1 error, retorno "se produjeron varios errores"
			else return "No se pudieron autorizar algunas prestaciones de este bono.";
		}

	}

	private String getCodigoSantiago(){
		
		if (_codigoSantiago > -1) return _codigoSantiago + "";
		
		List listaCiudades = ciudadDao.lista();

		for (int i=0; listaCiudades != null && i<listaCiudades.size(); i++){
			CiudadDTO ciudadDto = (CiudadDTO) listaCiudades.get(i);
			
			if ("SANTIAGO".equals(ciudadDto.getNombre().trim())){
				_codigoSantiago = ciudadDto.getCodigo();
				return ciudadDto.getCodigo() + "";
			}
		}
		
		return "";
	}

	private String[] getNombreCiudadEmisor(String rutUsuario){

		// veo si es beneficiario
		try {
			UsuarioWeb usuarioWeb = new UsuarioWeb(rutUsuario); 
			usuarioWeb.setRutEmisor( rutUsuario );
			BeneficiarioDTO ben = beneficiariosDao.leeBeneficiario(Integer.parseInt(rutUsuario), usuarioWeb);
			String nombreEmisor = ben.getNombre() + " " + ben.getPat() + " " + ben.getMat();

			return new String[]{ nombreEmisor, "" };

		} catch(Exception e){ }
		
		// veo si es habilitado
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			HabilitadoDTO h = habilitadoDao.getHabilitadoPorCodigo( Integer.parseInt(rutUsuario), uwTemp );
			String nombreEmisor = h.getNombre();
			
			String nombreCiudad = "";
			try { nombreCiudad = (String) ciudadDao.mapa().get( h.getDom_ciudad() ); } catch (Exception e) { }
			return new String[]{ nombreEmisor, nombreCiudad };

		} catch(Exception e){ }
		
		// veo si es prestador
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			PrestadorDTO p = prestadoresDao.prestadorPorRutAux( rutUsuario, uwTemp );
			String nombreEmisor = p.getRazonSocial();
			
			String nombreCiudad = "";
			try { nombreCiudad = (String) ciudadDao.mapa().get( new Integer(p.getCodCiudad()) ); } catch (Exception e) { }
			return new String[]{ nombreEmisor, nombreCiudad };
		} catch(Exception e){ }
		
		return new String[]{ rutUsuario, "" };
	}


}