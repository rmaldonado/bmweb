package bmweb.servlets;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationContext;

import bmweb.dao.IBeneficiariosDao;
import bmweb.dao.IBonoDao;
import bmweb.dao.ICiudadDao;
import bmweb.dao.IHabilitadoDao;
import bmweb.dao.IPrestacionesGenericasDao;
import bmweb.dao.IPrestadoresDao;
import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.BonoDTO;
import bmweb.dto.BonoItemDTO;
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
 * Servlet que permite a los prestadores el agregar 
 * prestaciones valoradas a los bonos abiertos
 * que les presentaron
 * 
 */

public class DetallarBonosMasivoServlet extends ServletSeguro {
	
	private IBonoDao bonoDao;
	private ICiudadDao ciudadDao;
	private IPrestacionesGenericasDao prestacionesGenericasDao;
	private IPrestadoresDao prestadoresDao;
	private IBeneficiariosDao beneficiariosDao;
	private IHabilitadoDao habilitadoDao;

	private ApplicationContext appCtx;
	
	private int _codigoSantiago = -1;
	// private String folpas ="0";
	
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
			
			if (FileUpload.isMultipartContent(request)){
				procesarArchivo(request,response);
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

			if ("detalle".equals( request.getParameter("accion") )){
				detalle(request, response);
				return;
			}
			
			if ("insertar".equals( request.getParameter("accion") )){
				insertar(request, response);
				return;
			}
			
			if ("procesarArchivo".equals( request.getParameter("accion") )){
				procesarArchivo(request, response);
				return;
			}
			
			if ("guardar".equals( request.getParameter("accion") )){
				guardar(request, response);
				return;
			}
			
			// La accion por defecto es solo los filtros
			detalle(request, response);

			
		} catch (Exception ex){ }
		
	}
	
	private void guardar (HttpServletRequest request, HttpServletResponse response) {
		try {
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			List items = (List) request.getSession().getAttribute("items");
			for (int i=0; items != null && i<items.size(); i++){
				BonoItemDTO item = (BonoItemDTO) items.get(i);
				String folio = item.getBonoDTO().getFolio().toString();
				BonoDTO bono = bonoDao.bonoWebPorFolio( Integer.parseInt(folio), usuarioWeb);
				
				// creo una nueva lista con solo un item
				List listaUnItem = new ArrayList();
				listaUnItem.add( item );

				bonoDao.guardardetalleCarMas(bono, listaUnItem, usuarioWeb);
			}
			
			request.getSession().removeAttribute("items");
			
			mensaje("El listado de prestaciones fue grabado exitosamente", request, response);
			
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"
			request.setAttribute("listadoSoloFiltro", "listadoSoloFiltro");
			redirigir(request, response, "inicio.jsp");
			
		} catch (Exception e) {

			mensaje("Ocurrio un error al guardar el listado", request, response);
			detalle(request, response);

		}
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

			BonoDTO bono = bonoDao.guardarBonoWeb(params, uw);
			
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
			// Creo uno nuevo para re-utilizar el jsp de modificacion
			BonoDTO b = new BonoDTO();
			request.setAttribute("bono", b);
			request.setAttribute("nuevo", "nuevo");
			request.setAttribute("ciudades", ciudadDao.lista());
			request.setAttribute("prestaciones", prestacionesGenericasDao.lista());

			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			// Si viene el parametro "ciudad", buscar los prestadores de una ciudad
			try {
				int domCiudad = Integer.parseInt(request.getParameter("ciudad"));
				
				// RESTRICCION:
				// Si la ciudad es Santiago, reviso si el usuario puede emitir bonos
				// en santiago
				
				if (domCiudad == getCodigoSantiago()){
					
					if (usuarioWeb.puedeHacerBonosSantiago()){

						int codPrestacionGenerica = Integer.parseInt(request.getParameter("prestacionGenerica"));
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

					int codPrestacionGenerica = Integer.parseInt(request.getParameter("prestacionGenerica"));
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

		try {
			Map params = ParamsUtil.fixParams(request.getParameterMap());
	    	
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			
			HttpSession sesion = request.getSession();
			if (("nuevo".equals((String)params.get("op"))) && sesion != null && sesion.getAttribute("items") != null){ sesion.removeAttribute("items"); }
			
			int numeroFolio = -1;
			try { numeroFolio = Integer.parseInt((String)params.get("folio")); } catch (Exception e) { }
			BonoDTO bono = bonoDao.bonoWebPorFolio( numeroFolio, usuarioWeb );
			
			List bonoItems = new ArrayList();
			try {  bono.getItems(); } catch (Exception ex){ }
	
			List listaDetalle = null;
			try { listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb ); }
			catch (Exception e) { }
			
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
				String rutPrestador = bono.getRutPrestador();
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date fecha = sdf.parse( (String) params.get("fecha") );
				
				if ("codigo".equals(opbuscar)){
					prestacionesEncontradas = bonoDao.getPrestacionesPorCodigo(buscar, new Date(), rutPrestador, usuarioWeb);
				} else {
					int familiaPrestaciones = ((PrestacionGenericaDTO)(listaPrestaciones.get(0))).getCodigo(); 
					prestacionesEncontradas = bonoDao.buscarPrestaciones(new Integer(familiaPrestaciones), buscar, fecha, rutPrestador, usuarioWeb);
				}
	
			}
			
			// hago el forward al jsp
			response.addCookie(new Cookie("update","update")); // Cookie para evitar el "back"				    	
	    	/**********************************************************************************/
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// Voy a la pagina de validacion de bono
		try { redirigir(request, response, "detalleBonosMasivosArchivo.jsp"); }
		catch (Exception e) { e.printStackTrace(); }

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
					mensaje("Error: El CMC ingresado no es valido o esta de baja.", request, response);
					//crear(request, response);
					redirigir(request, response, "crearBonoVerBeneficiario.jsp");
				  }
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//redirigir(request, response, "crearBonoVerBeneficiario.jsp");
		} catch (Exception e) {e.printStackTrace();
		}
		
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
	 * 
	 */
	
	private void validarCodigoBono(HttpServletRequest request, HttpServletResponse response) {
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

	private void procesarArchivo(HttpServletRequest request, HttpServletResponse response) {
		
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy"); // formato del archivo CSV
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			HttpSession sesion = request.getSession();
			
			// borro la lista de prestaciones que pueda estar presente 
			// en la sesion del usuario
			sesion.setAttribute("items", new ArrayList());
			
			Map params = new HashMap();
			List datosArchivo = new ArrayList();
			boolean hayp = false;
			// si se sube un archivo
			
			BonoDTO bono = new BonoDTO();
			
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
				    	datosArchivo =  CsvParser.leer(texto); // parseo el CSV como lista de strings
				    }
				}
				
				// Tengo el archivo en "texto" y los parametros en "params"
				


				  // veo si viene una lista de Bonitems en el request
	              List items;
				  if (sesion.getAttribute("items") != null){ items = (List) sesion.getAttribute("items"); }
				  else { items = new ArrayList(); }
				
				  // Itero sobre la lista de prestaciones que viene en el archivo
				  // codigoPrestacion,tipoPrestacion,fechaPrestacion,pabellon,cantidad,esCirugia,salaComun,quienCobra
				
				  boolean huboErrores = false;
				  boolean procesarArchivo = true;
				  
				  // BonoDTO bono;
				  
				  // me salto la primera fila donde viene el formato de las columnas
				  // Corregidollr( debe leer de la 1ra fila) 20 Nov 2006
				  for (int i=0; procesarArchivo && i<datosArchivo.size(); i++){

				  	String[] fila = (String[])datosArchivo.get(i);
				  	
					String folio = fila[0]; // (String) params.get("folio");
					bono = bonoDao.bonoWebPorFolio( Integer.parseInt(folio), usuarioWeb);
					
					// Valido que el Rut del prestador asociado al bono es el mismo
					// del usuario logueado
					
					String dv = TextUtil.getDigitoVerificador(Integer.parseInt( usuarioWeb.getRutEmisor() ));
					String rutDV = usuarioWeb.getRutEmisor() + "-" + dv;
					String llr= bono.getRutPrestador();
					if ( !bono.getRutPrestador().trim().equals( rutDV ) ){
						mensaje("El listado contiene el bono #" + bono.getFolio() + " que no fue emitido para el prestador.", request, response);
						procesarArchivo = false;
						break;
					}
					if (bonoDao.esBonoValorado(new Integer(folio).intValue())){
					   mensaje ("El listado contiene el bono #" + bono.getFolio() + " que es un Bono Valorado.", request, response);
					   procesarArchivo = false;
					   break;
					}
					// Agregar validacion: el bono no debe ser un bono valorado
					
					List listaBonoAbierto = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
					BonoWItemDTO itemllr = (BonoWItemDTO) listaBonoAbierto.get(0);
					int hosAmb = itemllr.getCodigoPrestacion(); //si generico es Hospitalario o no
					
					PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
		            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
		            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );

		            String CMC = TextUtil.formarCMC(rolbene.getRepart().trim(), rolbene.getImpo(), rolbene.getCorrel());
		            String codContrato = rolbene.getContrato();
				  	
					String codigo = fila[1]; // era 0
					int grupo = ((Integer.parseInt(codigo))/100000);
					int cantidad = Integer.parseInt(fila[4]); // antes era 4
					String conValorCobrado = "0";
					if (grupo==76 || grupo ==77 || grupo==87 || grupo==88 || grupo == 85){ 
						conValorCobrado = fila[8];
						cantidad = 1; //Para Medicamentos o Insumos
					} else {
						conValorCobrado = "0";
					}
					//Si la prestacion generica es 2 todas la prestaciones son tipo 2
					Integer codigoPabellon= new Integer("0");
					int cobroPabellon = 1;
					int codProfesional = 6;
					  
					if(prestadoresDao.EsCirugia(codigo)){
						codigoPabellon = prestadoresDao.pabellonPorCodigo( ( new Integer(codigo) ).intValue() , usuarioWeb);
						cobroPabellon = Integer.parseInt(fila[5]);
						codProfesional = Integer.parseInt(fila[7]);
					}
					  
					int tipoPrestacion = 0;
					
                    int salaComun = 0;
					if (grupo==2) salaComun = Integer.parseInt(fila[6]); // antes era 6
					 
					tipoPrestacion = Integer.parseInt(fila[2]);
					   if(hosAmb != 2) {
						   tipoPrestacion = Integer.parseInt(fila[2]);
					       if(tipoPrestacion != 7) tipoPrestacion = 1;
					       if (grupo == 76){ 
					    	   codigo = "7777777";
					    	   grupo = 77;
					       }
					       if (grupo == 87){
					    	  codigo = "8888888";
					    	  grupo = 88;
					       }
					   } 
				     
					// Creo una lista de prestaciones con la nueva prestacion a realizar
					List listaPrestaciones = new ArrayList();
					// Si la prestacion generica no es 2 y el grupo es 80 u 85 debe pasar //
					if((hosAmb!= 2 && grupo == hosAmb)||(hosAmb!=2 && grupo==80)||(hosAmb!=2 && grupo==85)||(hosAmb!=2 && grupo==77)||(hosAmb!=2 && grupo==88)|| hosAmb==2) {
					listaPrestaciones.add(new String[]{codigo, "nombre", "1"});
					
					// int[]{ totalAporteDipreca, totalAporteSeguro, totalCopago };
					
					Date fechaAtencion = sdf.parse( TextUtil.completarDerecha( "00000000", fila[3]) );
					
					// REVISO Y AUTORIZO LA PRESTACION QUE VIENE EN CADA LINEA
					// DEL ARCHIVO CSV - 20060717
					  
					String errorAutorizacion = prestadoresDao.autorizarPrestacion(CMC, new Integer(usuarioWeb.getRutEmisor()), beneficiario, Integer.parseInt(codigo), cantidad , usuarioWeb);
					if ( errorAutorizacion != null){
						// Si hubo un error de autorizacion, agrego un mensaje y no agrego la prestacion nueva
						mensaje(errorAutorizacion, request, response);
						procesarArchivo = false;
						break;
							
					} else {
						
						// veo si hay prestaciones incompatibles: creo un int[] con las prestaciones
						int[] prestaciones = new int[items.size()];
						for (int j=0; items.size() > 0 && j<items.size(); j++){
							BonoItemDTO item = (BonoItemDTO) items.get(j);
							prestaciones[j] = item.getCodPrestacion().intValue();
						}
						
						// reviso si hay incompatibilidad con alguna (metodo distinto del dao)
						String errorIncompatible = prestadoresDao.buscarPrestacionesIncompatibles(prestaciones, new Integer(codigo).intValue(), usuarioWeb);
						if (errorIncompatible != null){
							mensaje(errorIncompatible, request, response);
							procesarArchivo = false;
							break;
						}
						
					}
					  
					// int[]{ totalAporteDipreca, totalAporteSeguro, totalCopago, valorConvenido, aportePabellon };
					int[] valores = prestadoresDao.copagoYAportesPorPrestadorCM(CMC, prestador.getRutAcreedor().trim(), listaPrestaciones, fechaAtencion, tipoPrestacion, codProfesional, codContrato, salaComun, cobroPabellon, conValorCobrado, usuarioWeb);
					BonoItemDTO nuevoItem = new BonoItemDTO();
					
					// TODO Agregar el caso de codigos que se usa el valor cobrado desde
					// la planilla excel
					
					nuevoItem.setBonoDTO( bono );
					nuevoItem.setCodPrestacion( new Integer(codigo) );

					if (grupo==2) nuevoItem.setPensionadoOSalaComun("" + salaComun );

					try { 
						nuevoItem.setValorAporteDipreca( new Integer(cantidad * valores[0]) );
						nuevoItem.setValorAporteSeguro( new Integer(cantidad * valores[1]) );
						nuevoItem.setValorCopago( new Float(cantidad * valores[2]) );
						
						nuevoItem.setValorConvenidoPrestacion( new Float( valores[3] ));
						nuevoItem.setAporteDiprecaPabellon( new Float(cantidad * valores[4]) );
						nuevoItem.setCodConvenio(new Integer (valores[5]));
						// datos extra que vienen en la planilla CSV
						// TODO Reforzar
						//llr	nuevoItem.setCodTipoPrestacion(new Integer(fila[1]));
						nuevoItem.setCodTipoPrestacion(new Integer(tipoPrestacion));
						//fllr
						nuevoItem.setFechaEfectivaAtencionMedica( fechaAtencion );
						
						// nuevoItem.setCodigoPabellon(new Integer(fila[3])); // cambiar
						nuevoItem.setCodigoPabellon( codigoPabellon );
						
//						nuevoItem.setCantidadAtenciones(new Integer(fila[4])); // era 4
						nuevoItem.setCantidadAtenciones(new Integer(cantidad)); // era 4
						if(fila[4]!= null){nuevoItem.setIncluyePabellon(fila[5]);} // era 5
						if((Integer.parseInt(codigo))/10000 == 2) {nuevoItem.setPensionadoOSalaComun(fila[6]);} // era 6

						// si la prestacion es cirugia, uso el valor que viene el archivo
						if(prestadoresDao.EsCirugia(codigo)) {
							nuevoItem.setCodProfesional(fila[7]); // era 7
						    nuevoItem.setIncluyePabellon("" + cobroPabellon);
						} else {
							nuevoItem.setCodProfesional( "" + codProfesional );
							nuevoItem.setIncluyePabellon("" + cobroPabellon);
						}

						String[] codigoNombrePrestacion = (String[]) bonoDao.getPrestacionesPorCodigo(codigo, fechaAtencion, bono.getRutPrestador(), usuarioWeb).get(0);
						nuevoItem.setNombrePrestacion( codigoNombrePrestacion[1] );
						
						items.add( nuevoItem );
					  	} catch (Exception e) {
						  e.printStackTrace();
						  huboErrores = true;
					  	}
					  } //llr ojo//
				  }
				
				  // Si el procesar Archivo salio ok, valido si hubo otro tipo de errores
				  if (procesarArchivo){
				  	
					  if (huboErrores){
						  mensaje("Hay prestaciones en el archivo que no son validas para el prestador", request, response);
					  } else {
						  mensaje("Las prestaciones fueron leidas exitosamente", request, response);
					  }
				  	
				  }
				  
			        
				sesion.setAttribute("items", items);
				
				// llr fin if agregado 
			} else {
				// si es un formulario normal
				params = ParamsUtil.fixParams(request.getParameterMap());
			}
			
				// una vez que se han procesado todos los campos del formulario
		    	/**********************************************************************************/
				
				// int numeroFolio = bonoDao.
				//try { numeroFolio = Integer.parseInt((String)params.get("folio")); } catch (Exception e) { }
				//try { numeroFolio = Integer.parseInt((String)request.getAttribute("folio")); } catch (Exception e) { }
				//numeroFolio= Integer.parseInt(folpas);
				//BonoDTO bono = bonoDao.bonoWebPorFolio( numeroFolio, usuarioWeb );
				List bonoItems = bono.getItems();

				List listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
				
				ArrayList listaPrestaciones = new ArrayList();
				for (int i=0; listaDetalle != null && i<listaDetalle.size();i++){
					BonoWItemDTO item = (BonoWItemDTO) listaDetalle.get(i);
					PrestacionGenericaDTO p = prestacionesGenericasDao.prestacionPorCodigo( item.getCodigoPrestacion() );
					listaPrestaciones.add(p);
				}
				
				List prestacionesEncontradas = null;
				
				// Si viene un codigo o una descripcion de una prestacion para buscar, busco
				if (params.get("buscar") != null && params.get("opbuscar") != null){
					String buscar = (String) params.get("buscar");
					String opbuscar = (String) params.get("opbuscar");
					
					SimpleDateFormat ddmmyyyy = new SimpleDateFormat("dd/MM/yyyy");
					Date fecha = ddmmyyyy.parse( (String) params.get("fecha"));
					
					if ("codigo".equals(opbuscar)){
						try { 
							Integer.parseInt(buscar.trim()); 
							prestacionesEncontradas = bonoDao.getPrestacionesPorCodigo(buscar, fecha, bono.getRutPrestador(), usuarioWeb);
							
							// a la lista de prestacionesEncontradas, 
							// le quito las que no corresponden por codigo de prestacion generica
							
							PrestacionGenericaDTO p = (PrestacionGenericaDTO) listaPrestaciones.get(0);
							String codigoGenerico = p.getCodigo() + "";
							
							// Si el codigo de prestacion generica es distinto de "2", debo restringir
							// las prestaciones que se obtienen. No se pueden obtener prestaciones
							// que no pertenezcan a la misma familia.
							
							if (!"2".equals(codigoGenerico)){

								for (int i=0; prestacionesEncontradas != null && i < prestacionesEncontradas.size(); i++){
									String[] fila = (String[]) prestacionesEncontradas.get(i);
									
									int familiaPrestacion = Integer.parseInt(fila[0]) / 100000;
									int familiaGenerica = Integer.parseInt(codigoGenerico);
									
									// si el codigo de la prestacion no coincide 
									// con el codigo generico de la familia, entonces borro la fila
									
									if (familiaPrestacion != familiaGenerica){
										prestacionesEncontradas.remove(i);
									}
								}
								
							} 
							
							
						} catch (Exception ex){
							prestacionesEncontradas = new ArrayList();
						}
					} else {
						int familiaPrestaciones = ((PrestacionGenericaDTO)(listaPrestaciones.get(0))).getCodigo(); 
						prestacionesEncontradas = bonoDao.buscarPrestaciones(new Integer(familiaPrestaciones), buscar, fecha, bono.getRutPrestador(), usuarioWeb);
					}

				}
				
				// cuento con una lista de prestaciones encontradas, ahora
				// voy a ver cuales de ellas son cirugia y si tienen pabellon
				
				Map mapaCirugias = new HashMap();
				for (int i=0; prestacionesEncontradas != null && i<prestacionesEncontradas.size(); i++){
					// new String[] { codigo, nombre, "Ambulatoria" };
					String [] fila = (String []) prestacionesEncontradas.get(i);
					int codigoPrestacion = Integer.parseInt(fila[0]);
					
					Integer pabellon = prestadoresDao.pabellonPorCodigo(codigoPrestacion, usuarioWeb);
					
					if (pabellon != null){
						mapaCirugias.put(""+codigoPrestacion, pabellon);
					}
					
				}

				request.setAttribute("mapaCirugias", mapaCirugias);

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
		    	/**********************************************************************************/
				
			
		} catch (Exception e)  {
			e.printStackTrace();
		}
		
		// Voy a la pagina de validacion de bono
		try { redirigir(request, response, "detalleBonosMasivosArchivo.jsp"); }
		catch (Exception e) { e.printStackTrace(); }
		
	}
	
	private void agregar(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // El formato del formulario
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			HttpSession sesion = request.getSession(true);
			String codigo = (String) params.get("codigo");
			String folio = (String) params.get("folio");
			//llr
			BonoDTO bono = bonoDao.bonoWebPorFolio( Integer.parseInt(folio), usuarioWeb);
			List listaBonoAbierto = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
			BonoWItemDTO itemllr = (BonoWItemDTO) listaBonoAbierto.get(0);
			int hosAmb = itemllr.getCodigoPrestacion();
			//fllr
			//llatin
			int tipoPrestacion = 0;
			  int grupo= ((Integer.parseInt(codigo))/100000);
            int pensionadoOSalaComun=0;
			if (grupo==2)pensionadoOSalaComun = Integer.parseInt((String)params.get("salaComun")); // antes era 6
			  
			if(hosAmb != 2){
               tipoPrestacion = Integer.parseInt((String) params.get("tipoPrestacion"));
			   if(tipoPrestacion != 7)tipoPrestacion = 1;
			}else{
			   tipoPrestacion = 2;
			}
			  //Si la prestacion generica es 2 todas la prestaciones son tipo 2
			int cantidad  = Integer.parseInt((String) params.get("cantidad"));
			Integer codigoPabellon= new Integer("0");
			int cobroPabellon = 1;
			int codProfesional=6;
			  
			if(prestadoresDao.EsCirugia(codigo)){
			   codigoPabellon = prestadoresDao.pabellonPorCodigo( ( new Integer(codigo) ).intValue() , usuarioWeb);
               codProfesional = Integer.parseInt((String) params.get("codProfesional"));
               cobroPabellon  = Integer.parseInt((String) params.get("cobroPabellon"));
			}
			//fllatin
			

			
			Date fechaAtencion = sdf.parse((String)params.get("fecha"));

			
			
			// si la prestacion no es cirugia, el codigo del profesional queda en cero
			

            PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );

            String CMC = TextUtil.formarCMC(rolbene.getRepart().trim(), rolbene.getImpo(), rolbene.getCorrel());
            String codContrato = rolbene.getContrato();
            String rutPrestador = bono.getRutPrestador();
            
			// veo si viene una lista de Bonitems en el request
            List items;
			if (sesion.getAttribute("items") != null){ items = (List) sesion.getAttribute("items"); }
			else { items = new ArrayList(); }
			
			// Creo una lista de prestaciones con la nueva prestacion a realizar
			List listaPrestaciones = new ArrayList();
			listaPrestaciones.add(new String[]{codigo, "nombre", "1"});
			
			// *** inicio - basado en procesarArchivo *********************************************
			
			boolean huboErrores = false;
			String conValorCobrado = "0";
			int[] valores = prestadoresDao.copagoYAportesPorPrestador(CMC, prestador.getRutAcreedor().trim(), listaPrestaciones, fechaAtencion, tipoPrestacion, codProfesional, codContrato, pensionadoOSalaComun, cobroPabellon, conValorCobrado, usuarioWeb);
			BonoItemDTO nuevoItem = new BonoItemDTO();
			
			Integer pabellon = prestadoresDao.pabellonPorCodigo((new Integer(codigo)).intValue(), usuarioWeb);
			
			nuevoItem.setBonoDTO( bono );
			nuevoItem.setCodPrestacion( new Integer(codigo) );
			
			try {
				nuevoItem.setValorAporteDipreca( new Integer(cantidad * valores[0]) );
				nuevoItem.setValorAporteSeguro( new Integer(cantidad * valores[1]) );
				nuevoItem.setValorCopago( new Float(cantidad * valores[2]) );
				
				nuevoItem.setValorConvenidoPrestacion( new Float( valores[3] ));
				nuevoItem.setAporteDiprecaPabellon( new Float(cantidad * valores[4]) );

				// datos extra que vienen en la planilla CSV
				// TODO Reforzar
				//nuevoItem.setCodTipoPrestacion(new Integer((String)params.get("tipoPrestacion")));
				nuevoItem.setCodTipoPrestacion(new Integer(tipoPrestacion));
				nuevoItem.setFechaEfectivaAtencionMedica( fechaAtencion );

				//nuevoItem.setCodigoPabellon(new Integer((String)params.get("pabellon")));
				nuevoItem.setCodigoPabellon( pabellon );
				
				nuevoItem.setCantidadAtenciones(new Integer((String)params.get("cantidad")));
				
				//nuevoItem.setIncluyePabellon((String)params.get("cobroPabellon"));
				nuevoItem.setIncluyePabellon(""+cobroPabellon);
				//nuevoItem.setPensionadoOSalaComun((String)params.get("salaComun"));
				nuevoItem.setPensionadoOSalaComun(""+pensionadoOSalaComun);
				//nuevoItem.setCodProfesional((String)params.get("codProfesional"));
				nuevoItem.setCodProfesional(""+codProfesional);

				String[] codigoNombrePrestacion = (String[]) bonoDao.getPrestacionesPorCodigo(codigo, fechaAtencion, rutPrestador, usuarioWeb).get(0);
				nuevoItem.setNombrePrestacion( codigoNombrePrestacion[1] );
				
				boolean errorAgregandoPrestacion = false;
				
				  // REVISO Y AUTORIZO LA PRESTACION QUE VIENE EN CADA LINEA
				  // DEL ARCHIVO CSV - 20060717
				  
					String errorAutorizacion = prestadoresDao.autorizarPrestacion(CMC, new Integer(usuarioWeb.getRutEmisor()), beneficiario, Integer.parseInt(codigo), nuevoItem.getCantidadAtenciones().intValue() , usuarioWeb);
					if ( errorAutorizacion != null){
						// Si hubo un error de autorizacion, agrego un mensaje y no agrego la prestacion nueva
						mensaje(errorAutorizacion, request, response);
						errorAgregandoPrestacion = true;
						
					} else {
						
						// veo si hay prestaciones incompatibles: creo un int[] con las prestaciones
						int[] prestaciones = new int[items.size()];
						for (int j=0; items.size() > 0 && j<items.size(); j++){
							BonoItemDTO item = (BonoItemDTO) items.get(j);
							prestaciones[j] = item.getCodPrestacion().intValue();
						}
						
						// reviso si hay incompatibilidad con alguna (metodo distinto del dao)
						String errorIncompatible = prestadoresDao.buscarPrestacionesIncompatibles(prestaciones, new Integer(codigo).intValue(), usuarioWeb);
						if (errorIncompatible != null){
							mensaje(errorIncompatible, request, response);
							errorAgregandoPrestacion = true;
						}
						
					}

				if (!errorAgregandoPrestacion){
					items.add( nuevoItem );
				}
					
			} catch (Exception e) {
				e.printStackTrace();
				huboErrores = true;
				mensaje("No se puede agregar prestacion porque no esta en ningun convenio vigente", request, response);
			}
			
			// *** fin - basado en procesarArchivo *********************************************

			sesion.setAttribute("items", items);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		detalle(request, response);

	}

	private void eliminar(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		
		try {
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			UsuarioWeb usuarioWeb = getUsuarioWeb(request);
			HttpSession sesion = request.getSession(true);
			String codigo = (String) params.get("codigo");
			String folio = (String) params.get("folio");
			
			List items = (List) request.getSession().getAttribute("items");
			
			for (int i=0; items != null && i<items.size(); i++){
				BonoItemDTO item = (BonoItemDTO) items.get(i);

				// Si uno de los items pertenece a un bono que tiene el mismo folio
				if (item.getBonoDTO().getFolio().toString().equals( folio )){

					// y ademas es el mismo codigo de prestacion
					if (codigo.equals(item.getCodPrestacion().toString())){
						items.remove(i); // lo saco y termino
						break;
					}

				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		detalle(request, response);

	}

	
}
