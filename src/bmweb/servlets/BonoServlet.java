package bmweb.servlets;

import java.io.File;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import bmweb.dto.PrestacionGenericaDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.CodigoValidacion;
import bmweb.util.Constantes;
import bmweb.util.CsvParser;
import bmweb.util.ParamsUtil;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;


/**
 * @author denis.fuenzalida
 * 
 * Servlet que realiza operaciones sobre la tabla Bonos.
 * 
 * La accion por defecto es mostrar el formulario para busqueda
 * de bonos
 * 
 */

public class BonoServlet extends ServletSeguro {
	
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
		
		// busco el codigo de santiago para la validacion
		getCodigoSantiago();
	}
	
	protected String getNombrePermiso() { return "bonos"; }
	
	/**
	 * Implementacion de la logica de este servlet
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {

			// Acciones definidas por este servlet
			
			//if (FileUpload.isMultipartContent(request)){
			//	procesarArchivo(request,response);
			// }


			if ("detalle".equals( request.getParameter("accion") )){
				detalle(request, response);
				return;
			}
			
			if ("crear".equals( request.getParameter("accion") )){
				crear(request, response);
				return;
			}

			/*
			// Accion de ayuda para validar *dinamicamente* un CMC dado
			if ("revisarCMC".equals( request.getParameter("accion") )){
				revisarCMC(request, response);
				return;
			}
			*/
			
			if ("insertar".equals( request.getParameter("accion") )){
				insertar(request, response);
				return;
			}
			
			// En el caso del bono, no hay acción por defecto. No busco en el listado
			// a menos que se haya hecho una busqueda
			if ("listado".equals( request.getParameter("accion") )){
				listado(request, response);
				return;
			}
			
			if ("buscarBeneficiario".equals( request.getParameter("accion") )){
				buscarBeneficiario(request, response);
				return;
			}
			
			if ("validarCodigoBono".equals( request.getParameter("accion") )){
				validarCodigoBono(request, response);
				return;
			}
			
			if ("anular".equals( request.getParameter("accion") )){
				anular(request, response);
				return;
			}
			
			//if ("procesarArchivo".equals( request.getParameter("accion") )){
			//	procesarArchivo(request, response);
			//	return;
			//}
			
			
			// La accion por defecto es solo los filtros
			listadoSoloFiltro(request, response);

			
		} catch (Exception ex){ }
		
	}

	private void anular(HttpServletRequest request, HttpServletResponse response) {
		UsuarioWeb uw = getUsuarioWeb(request);

		Map params = ParamsUtil.fixParams(request.getParameterMap());
		boolean ok = bonoDao.anularBonoValorado(params, uw);

		if (ok) {
			mensaje("El Bono fue anulado exitosamente", request,
					response);
		} else {
			mensaje("No se pudo anular el Bono", request, response);
		}

		listado(request, response);
		
				
	}

	private void listadoSoloFiltro(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			request.setAttribute("listadoSoloFiltro", "listadoSoloFiltro");
			redirigir(request, response, "listadoBonos.jsp");
		} catch (Exception e) { }
	}
	
	// listado con paginacion - con DAOs
	private void listado(HttpServletRequest request, HttpServletResponse response) {

		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			int dpp = Constantes.DATOS_POR_PAGINA;
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			params.put("dpp", new Integer(dpp + 1).toString());
			params.put("NOVALORADOS", "NOVALORADOS");
			
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
			redirigir(request, response, "listadoBonos.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}

	private void insertar(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		
		try {
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			UsuarioWeb uw = getUsuarioWeb(request);
			
			// TODO Si el usuario es imponente, no hace falta buscar un habilitado
			// porque es �l mismo

			/*
			Integer codigoUsuario = new Integer(1);
			if ("22".equals(uw.getNivel())){
				codigoUsuario = habilitadoDao.habilitadoPorUsuario(uw).getCodigo();
			}
			
			params.put( UsuarioWeb.ATRIBUTO_USUARIO_WEB, codigoUsuario );
			*/
			
			BonoDTO bono = bonoDao.guardarBonoWeb(params, uw);
			
            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
			request.setAttribute("nombreCiudad", nombreCiudad);

			if ( bono != null ){
				  response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
				  request.setAttribute("bono", bono);
					mensaje("El registro fue agregado exitosamente.", request, response);
					request.setAttribute("folio", bono.getFolio().toString());
					
					detalle(request, response);

			} else {
				mensaje("Error: No se pudo crear el bono", request, response);
				crear(request, response);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void crear(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
//llr
			String CMC="";
			String codigoNueva="1";
			BeneficiarioDTO beneficiario=null;
			String errorAutorizacion = prestadoresDao.autorizarPrestacion(CMC, new Integer(usuarioWeb.getRutEmisor()), beneficiario, Integer.parseInt(codigoNueva), 1 , usuarioWeb);
			if ( errorAutorizacion != null){
				// Si hubo un error de autorizacion, agrego un mensaje y no agrego la prestacion nueva
				mensaje(errorAutorizacion, request, response);
                redirigir(request, response, "irInicio.jsp");
                return;
			}
//llrf
			if (!bonoDao.puedeEmitirBonosPorNivel(usuarioWeb)){
				mensaje("Este usuario no puede emitir bonos", request, response);
				redirigir(request, response, "irInicio.jsp");
				return;
			}
			
			// Creo uno nuevo para re-utilizar el jsp de modificacion
			BonoDTO b = new BonoDTO();
			request.setAttribute("bono", b);
			request.setAttribute("nuevo", "nuevo");
			request.setAttribute("ciudades", ciudadDao.lista());
			request.setAttribute("prestaciones", prestacionesGenericasDao.lista());

			
			// Si viene el parametro "ciudad", buscar los prestadores de una ciudad
			try {
				int domCiudad = Integer.parseInt((String)params.get("ciudad"));
				
				// RESTRICCION:
				// Si la ciudad es Santiago, reviso si el usuario puede emitir bonos
				// en santiago
				
				if (domCiudad == getCodigoSantiago()){
					
					if (usuarioWeb.puedeHacerBonosSantiago()){

						int codPrestacionGenerica = Integer.parseInt((String)params.get("prestacionGenerica"));
						List listaPrestadores = prestadoresDao.prestadoresPorCiudadYPrestacionGenerica(domCiudad, codPrestacionGenerica, usuarioWeb);
						ArrayList prestadores = new ArrayList();
						for (int i=0; i<listaPrestadores.size(); i++){ prestadores.add(listaPrestadores.get(i)); }
						request.setAttribute("listaPrestadores", prestadores);
						
					} else {
						// Este usuario no puede emitir bonos para prestadores en Santiago
						mensaje("Este usuario no puede emitir bonos para prestadores en Santiago.", request, response);
						//request.setAttribute("listaPrestadores", new ArrayList());
					}
				} else {

					int codPrestacionGenerica = Integer.parseInt((String)params.get("prestacionGenerica"));
					List listaPrestadores = prestadoresDao.prestadoresPorCiudadYPrestacionGenerica(domCiudad, codPrestacionGenerica, usuarioWeb );
					ArrayList prestadores = new ArrayList();
					for (int i=0; i<listaPrestadores.size(); i++){ prestadores.add(listaPrestadores.get(i)); }
					request.setAttribute("listaPrestadores", prestadores);
					
				}
				

				
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			// hago el forward al jsp
			redirigir(request, response, "crearBono.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}

	private void detalle(HttpServletRequest request, HttpServletResponse response) {
		
		Session session = null;

		try {

			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			int numeroFolio = -1;
			try { numeroFolio = Integer.parseInt((String)params.get("folio")); } catch (Exception e) { }
			try { numeroFolio = Integer.parseInt((String)request.getAttribute("folio")); } catch (Exception e) { }
			BonoDTO bono = bonoDao.bonoWebPorFolio( numeroFolio, usuarioWeb );
			List bonoItems = bono.getItems();

			List listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
			
			ArrayList listaPrestaciones = new ArrayList();
			for (int i=0; listaDetalle != null && i<listaDetalle.size();i++){
				BonoWItemDTO item = (BonoWItemDTO) listaDetalle.get(i);
				PrestacionGenericaDTO p = prestacionesGenericasDao.prestacionPorCodigo( item.getCodigoPrestacion() );
				listaPrestaciones.add(p);
			}

			request.setAttribute("bono", bono);
			request.setAttribute("bonoItems", bonoItems);
			request.setAttribute("prestacionesGenericas", listaPrestaciones);

            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
			request.setAttribute("nombreCiudad", nombreCiudad);
			
			request.setAttribute("ciudades", ciudadDao.mapa()); // Cargo el HashMap de las ciudaddes
			request.setAttribute("listaCiudades", ciudadDao.lista()); // Cargo el HashMap de las ciudaddes
			
			// Recupero el prestador con el rut que estaba en el bono
			PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
			request.setAttribute("prestador", prestador);

			// Recupero el Imponente con el CMC del bono
			RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), getUsuarioWeb(request) );
			request.setAttribute("rolbene", rolbene);

			// Recupero el Beneficiario con el CMC del bono
			BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
			request.setAttribute("beneficiario", beneficiario);
			
			// Recupero el Imponente con el CMC del bono
			BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );
			request.setAttribute("beneficiario2", beneficiario2);			
			
			// Nombre del emisor para el detalle
			request.setAttribute("emisor", getNombreEmisor( bono.getCodigoHabilitado().toString() ));
			
			// hago el forward al jsp
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			redirigir(request, response, "detalleBonos.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO mover esta logica a un DAO
	private String getNombreEmisor(String rutUsuario){

		// veo si es beneficiario
		try {
			UsuarioWeb usuarioWeb = new UsuarioWeb(rutUsuario); 
			usuarioWeb.setNombreUsuario( rutUsuario );
			usuarioWeb.setRutEmisor( rutUsuario );
			BeneficiarioDTO ben = beneficiariosDao.leeBeneficiario(Integer.parseInt(rutUsuario), usuarioWeb);
			String nombreEmisor = ben.getNombre() + " " + ben.getPat() + " " + ben.getMat();
			return nombreEmisor;
		} catch(Exception e){ }
		
		// veo si es habilitado
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			uwTemp.setRutEmisor(rutUsuario);
			HabilitadoDTO h = habilitadoDao.getHabilitadoPorCodigo( Integer.parseInt(rutUsuario), uwTemp );
			String nombreEmisor = h.getNombre();
			return nombreEmisor;
		} catch(Exception e){ }
		
		// veo si es prestador
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			PrestadorDTO p = prestadoresDao.prestadorPorRutAux( rutUsuario, uwTemp );
			String nombreEmisor = p.getRazonSocial();
			return nombreEmisor;
		} catch(Exception e){ }
		
		return rutUsuario;
	}
	
	private void buscarBeneficiario(HttpServletRequest request, HttpServletResponse response) {
		boolean errorCMC = true;
		
		// VALIDACION DE LOS CMCs PARA LOS QUE EL USUARIO WEB PUEDE EMITIR BONOS 
		
		try {
			//String CMC = request.getParameter("CMC");
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());

			try {
				String CMC = (String)params.get("CMC");
				
				try { errorCMC = beneficiariosDao.validarCMC(CMC, usuarioWeb); }
				catch (Exception e) { errorCMC = true; }
								
				if (errorCMC == false){
					//RolbeneDTO rolbene = Beneficiarios.leeRolbene(request.getParameter("CMC"));
					RolbeneDTO rolbene = null;
					BeneficiarioDTO beneficiario = null;
					rolbene = beneficiariosDao.leeRolbene(CMC, usuarioWeb);
					beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
					if (beneficiario != null){
						
						// Si el usuario en la sesion tiene un CMC, veo si es compatible con el CMC
						// al que va a sacar un bono
						
						if (usuarioWeb.getCMC() != null){
							
							String[] cmcNuevo = TextUtil.dividirCMC(CMC);
							String[] cmcUsuario = TextUtil.dividirCMC(usuarioWeb.getCMC());

							// SOLO Si tienen la misma reparticion, y mismo imponente se agrega el beneficiario
							// al request para que se apruebe el CMC que quiere sacar el bono
							
							if (cmcNuevo[0].equals(cmcUsuario[0]) && cmcNuevo[1].equals(cmcUsuario[1])){
								request.setAttribute("beneficiario", beneficiario );
							} else {
								mensaje("Este usuario no puede emitir bonos para el CMC ingresado", request, response);
							}
							
						} else {
							request.setAttribute("beneficiario", beneficiario );
						}
						
						redirigir(request, response, "crearBonoVerBeneficiario.jsp");
					} 
				}else {
					mensaje("Error: El Carnet de Medicina Curativa ingresado no es valido o esta de baja.", request, response);
					//crear(request, response);
					redirigir(request, response, "crearBonoVerBeneficiario.jsp");
				  }
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//redirigir(request, response, "crearBonoVerBeneficiario.jsp");
		} catch (Exception e) { e.printStackTrace(); }
		
	}
	
	private int getCodigoSantiago(){
		
		if (_codigoSantiago > -1) return _codigoSantiago;
		
		List listaCiudades = ciudadDao.lista();

		for (int i=0; listaCiudades != null && i<listaCiudades.size(); i++){
			CiudadDTO ciudadDto = (CiudadDTO) listaCiudades.get(i);
			
			if ("SANTIAGO".equals(ciudadDto.getNombre().trim())){
				_codigoSantiago = ciudadDto.getCodigo();
				return ciudadDto.getCodigo();
			}
		}
		
		return -1;
	}
	
	/*
	 * El metodo validarCodigoBono determina si el bono es abierto
	 * o valorado y delega a otros m�todos la obtencion de los datos
	 * para el caso del bono abierto o valorado
	 */
	
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
					
					
					// Si no es el admin, reviso si el prestador del bono es el mismo usuario
					if (!"1".equals(usuarioWeb.getRutEmisor())){
						String rutPrestadorSinDV = bono.getRutPrestador().substring(0, bono.getRutPrestador().indexOf("-"));
						
						// si el usuario conectado es prestador, 
						// pero no es el prestador del bono
						
						if (("23".equals(usuarioWeb.getNivel()) ) &&
							!rutPrestadorSinDV.equals(usuarioWeb.getNombreUsuario().trim())){
							mensaje("Error: No puede validar bonos de otros prestadores.", request, response);
							try { redirigir(request, response, "validarCodigoBono.jsp"); }
							catch (Exception e) { e.printStackTrace(); }
						}
						
						if (( "24".equals(usuarioWeb.getNivel()) ) &&
								!rutPrestadorSinDV.equals(usuarioWeb.getNombreUsuario().trim())){
							mensaje("Error: No puede validar bonos de otros prestadores.", request, response);
							try { redirigir(request, response, "validarCodigoBono.jsp"); }
							catch (Exception e) { e.printStackTrace(); }
						}
						// llr 20 Nov 2006
						if (( "25".equals(usuarioWeb.getNivel()) ) &&
								!rutPrestadorSinDV.equals(usuarioWeb.getNombreUsuario().trim())){
							mensaje("Error: No puede validar bonos de otros prestadores.", request, response);
							try { redirigir(request, response, "validarCodigoBono.jsp"); }
							catch (Exception e) { e.printStackTrace(); }
						}
					}
					
					List datosDetalle = bonoDao.getDetalleBonoValoradoWeb(bono.getId().intValue(), usuarioWeb); // El detalle de las prestaciones del bono
					
					// Si busco el detalle de bono valorado pero no lo encuentro
					// entonces considero que es un bono abierto
					if (datosDetalle == null || datosDetalle.size() == 0){
						validarCodigoBonoAbierto(request, response);
						return;
					} else {
						validarCodigoBonoValorado(request, response);
						return;
					}
					
				} catch (Exception ex){
					mensaje("Error: El codigo de validacion no es correcto.", request, response);
					try { redirigir(request, response, "validarCodigoBono.jsp"); }
					catch (Exception e) { e.printStackTrace(); }
				}
			} else {
				try { redirigir(request, response, "validarCodigoBono.jsp"); }
				catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception ex){
			try { redirigir(request, response, "validarCodigoBono.jsp"); }
			catch (Exception e) { e.printStackTrace(); }
		}

		
	}
	
	private void validarCodigoBonoAbierto(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			// si viene el parametro codigo, intento validarlo
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			String codigo = (String) params.get("codigo");

			if (codigo != null){

				try {
					
					// quito el ultimo caracter al codigo y obtengo decripto el serial del bono
					String serialEncriptado = codigo.substring(0, codigo.length()-1);
					String decriptado = CodigoValidacion.decriptaCodigo(serialEncriptado);
					int serial = Integer.parseInt(decriptado);
					
					BonoDTO bono = bonoDao.bonoWebPorSerial(serial, usuarioWeb);
					List listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );

					ArrayList listaPrestaciones = new ArrayList();
					for (int i=0; listaDetalle != null && i<listaDetalle.size();i++){
						BonoWItemDTO item = (BonoWItemDTO) listaDetalle.get(i);
						PrestacionGenericaDTO p = prestacionesGenericasDao.prestacionPorCodigo( item.getCodigoPrestacion() );
						listaPrestaciones.add(p);
					}

					request.setAttribute("bono", bono);
					request.setAttribute("prestacionesGenericas", listaPrestaciones);
					
					
					request.setAttribute("ciudades", ciudadDao.mapa()); // Cargo el HashMap de las ciudaddes
					request.setAttribute("listaCiudades", ciudadDao.lista()); // Cargo el HashMap de las ciudaddes
					
					// Recupero el prestador con el rut que estaba en el bono
					PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
					request.setAttribute("prestador", prestador);

					// Recupero el Imponente con el CMC del bono
					RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
					request.setAttribute("rolbene", rolbene);

					// Recupero el Beneficiario con el CMC del bono
					BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
					request.setAttribute("beneficiario", beneficiario);
					
					// Recupero el Imponente con el CMC del bono
					BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );
					request.setAttribute("beneficiario2", beneficiario2);			

		            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405
					request.setAttribute("nombreCiudad", nombreCiudad);

					// hago el forward al jsp
					response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
					redirigir(request, response, "detalleBonos.jsp");					
					//request.setAttribute("resultado", new String[]{codPrestacion, folioBono, rutHabilitado, cmcBeneficiario});
					
				} catch (Exception ex){
					mensaje("Error: El codigo de validacion no es correcto.", request, response);
					redirigir(request, response, "validarCodigoBono.jsp");
				}
			} else {
				// Voy a la pagina de validacion de bono
				try { redirigir(request, response, "validarCodigoBono.jsp"); }
				catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception ex){
			ex.printStackTrace();

			// Voy a la pagina de validacion de bono
			try { redirigir(request, response, "validarCodigoBono.jsp"); }
			catch (Exception e) { e.printStackTrace(); }
		}
		
		
	}

	
	private void validarCodigoBonoValorado(HttpServletRequest request, HttpServletResponse response) {
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
					redirigir(request, response, "validarCodigoBono.jsp");
				}
			} else {
				// Voy a la pagina de validacion de bono
				try { redirigir(request, response, "validarCodigoBono.jsp"); }
				catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception ex){
			ex.printStackTrace();

			// Voy a la pagina de validacion de bono
			try { redirigir(request, response, "validarCodigoBono.jsp"); }
			catch (Exception e) { e.printStackTrace(); }
		}
		
		
	}

	
	/*
	private void procesarArchivo(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			Map params = new HashMap();

			// si se sube un archivo
			if (FileUpload.isMultipartContent(request)){
				
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(300*1024); // 300 kilobytes
				factory.setRepository(new File("/tmp")); // 300 kilobytes
				
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);

				// Parse the request
				List items = upload.parseRequest(request);
				
				// Process the uploaded items
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
				    FileItem item = (FileItem) iter.next();

				    if (item.isFormField()) {
				        //processFormField(item);
				    	params.put(item.getFieldName(), item.getString());
				    } else {
				    	// processUploadedFile(item);
				    	String texto = new String(item.get()); // contenido en memoria
				    	List datos =  CsvParser.leer(texto);   // parseo el CSV como lista de strings
				    	
				    	// Coloco los datos de las prestaciones ya cargadas en la sesion
				    	request.getSession().setAttribute("prestaciones", datos);
				    }
				}
				
			} else {
				// si es un formulario normal
				params = ParamsUtil.fixParams(request.getParameterMap());
			}
			
				// una vez que se han procesado todos los campos del formulario
		    	
				UsuarioWeb usuarioWeb = getUsuarioWeb(request);
				
				int numeroFolio = -1;
				try { numeroFolio = Integer.parseInt((String)params.get("folio")); } catch (Exception e) { }
				try { numeroFolio = Integer.parseInt((String)request.getAttribute("folio")); } catch (Exception e) { }
				BonoDTO bono = bonoDao.bonoWebPorFolio( numeroFolio, usuarioWeb );
				List bonoItems = bono.getItems();

				List listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
				
				ArrayList listaPrestaciones = new ArrayList();
				for (int i=0; listaDetalle != null && i<listaDetalle.size();i++){
					BonoWItemDTO item = (BonoWItemDTO) listaDetalle.get(i);
					PrestacionGenericaDTO p = prestacionesGenericasDao.prestacionPorCodigo( item.getCodigoPrestacion() );
					listaPrestaciones.add(p);
				}
				
				List prestacionesEncontradas = null;
				// Si viene un código o una descripcion de una prestacion para buscar, busco
				if (params.get("buscar") != null && params.get("opbuscar") != null){
					String buscar = (String) params.get("buscar");
					String opbuscar = (String) params.get("opbuscar");
					
					if ("codigo".equals(opbuscar)){
						prestacionesEncontradas = bonoDao.getPrestacionesPorCodigo(buscar, new Date(), usuarioWeb);
					} else {
						int familiaPrestaciones = ((PrestacionGenericaDTO)(listaPrestaciones.get(0))).getCodigo(); 
						prestacionesEncontradas = bonoDao.buscarPrestaciones(new Integer(familiaPrestaciones), buscar, usuarioWeb);
					}

				}

				request.setAttribute("bono", bono);
				request.setAttribute("bonoItems", bonoItems);
				request.setAttribute("prestacionesGenericas", listaPrestaciones);
				request.setAttribute("prestacionesEncontradas", prestacionesEncontradas);
				
				
				request.setAttribute("ciudades", ciudadDao.mapa()); // Cargo el HashMap de las ciudaddes
				request.setAttribute("listaCiudades", ciudadDao.lista()); // Cargo el HashMap de las ciudaddes
				
				// Recupero el prestador con el rut que estaba en el bono
				PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
				request.setAttribute("prestador", prestador);

				// Recupero el Imponente con el CMC del bono
				RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), getUsuarioWeb(request) );
				request.setAttribute("rolbene", rolbene);

				// Recupero el Beneficiario con el CMC del bono
				BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
				request.setAttribute("beneficiario", beneficiario);
				
				// Recupero el Imponente con el CMC del bono
				BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );
				request.setAttribute("beneficiario2", beneficiario2);			
				
				// Nombre del emisor para el detalle
				request.setAttribute("emisor", getNombreEmisor( bono.getCodigoHabilitado().toString() ));
				
				// hago el forward al jsp
				response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"				    	
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Voy a la pagina de validacion de bono
		try { redirigir(request, response, "detalleBonosArchivo.jsp"); }
		catch (Exception e) { e.printStackTrace(); }
		
	}
	*/

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
