package bmweb.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.fop.apps.Driver;
import org.springframework.context.ApplicationContext;
import org.xml.sax.InputSource;

import cl.e_sign.www.DocumentoParametro;
import cl.e_sign.www.EncabezadoRequest;
import cl.e_sign.www.EncabezadoResponse;
import cl.e_sign.www.WSIntercambiaDocSoapStub;

import bmweb.dao.IBeneficiariosDao;
import bmweb.dao.IBonoDao;
import bmweb.dao.ICiudadDao;
import bmweb.dao.IHabilitadoDao;
import bmweb.dao.IPrestacionesGenericasDao;
import bmweb.dao.IPrestadoresDao;
import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.BonoDTO;
import bmweb.dto.BonoWItemDTO;
import bmweb.dto.HabilitadoDTO;
import bmweb.dto.PrestacionGenericaDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.CodigoValidacion;
import bmweb.util.ParamsUtil;
import bmweb.util.PdfUtil;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;


/**
 * @author denis
 * 
 */

public class BonoPDFServlet extends ServletSeguro {

	private ApplicationContext appCtx;
	private IBonoDao bonoDao;
	private IPrestacionesGenericasDao prestacionesGenericasDao;
	private IHabilitadoDao habilitadoDao;
	private IPrestadoresDao prestadoresDao;
	private IBeneficiariosDao beneficiariosDao;
	private ICiudadDao ciudadDao;

	public void init() throws ServletException {
		super.init();
		appCtx = DBServlet.getApplicationContext();
		bonoDao = (IBonoDao) appCtx.getBean("bonoDao");
		prestacionesGenericasDao = (IPrestacionesGenericasDao) appCtx.getBean("prestacionesGenericasDao");
		habilitadoDao = (IHabilitadoDao) appCtx.getBean("habilitadoDao");
		prestadoresDao = (IPrestadoresDao) appCtx.getBean("prestadoresDao");
		beneficiariosDao = (IBeneficiariosDao) appCtx.getBean("beneficiariosDao");
		ciudadDao = (ICiudadDao) appCtx.getBean("ciudadDao");
		
	}
	
	protected String getNombrePermiso() { return "bonos"; }
	
	/**
	 * Implementacion de la logica de este servlet
	 */
	protected void ejecutarLogica(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			
			generarPDF(request, response);
			
		} catch (Exception ex){ }
		
	}
	
	private void generarPDF(HttpServletRequest request, HttpServletResponse response) {
		
        Logger log = new ConsoleLogger(ConsoleLogger.LEVEL_WARN);
        
        // Lista que contiene los campos con los que se genera el codigo de seguridad
        ArrayList campos = new ArrayList();
        
        UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);

		try {

			int folio = -1;
			Map params = ParamsUtil.fixParams(request.getParameterMap());
			
			folio = Integer.parseInt( (String)params.get("folio") );
			BonoDTO bono = bonoDao.bonoWebPorFolio( folio, usuarioWeb );
			
			// si el usuario es de nivel "22" y no es el emisor del bono, no
			// puede imprimir
			
			if ("22".equals(usuarioWeb.getNivel()) 
				&& !(usuarioWeb.getNombreUsuario().equals(bono.getCodigoHabilitado().toString()))) {
				
				// Vuelvo a la pagina anterior a esta, que pueden ser varias
				mensaje("No tiene permiso para imprimir el bono", request, response);
				redirigir(request, response, "inicio.jsp");
				return;
			}
			
			// Busco el habilitado asociado
			//HabilitadoDTO habilitado = habilitadoDao.getHabilitadoPorCodigo( bono.getCodigoHabilitado().intValue(), usuarioWeb );

			// Datos del prestador asociado
            PrestadorDTO prestador = prestadoresDao.prestadorPorRut( bono.getRutPrestador(), usuarioWeb );
            String ciudadPrestador;
            
            boolean agregarComprobante = false;
            
            try {
				ciudadPrestador = (String) ciudadDao.mapa().get( new Integer(prestador.getCodCiudad()) );
				ciudadPrestador = ciudadPrestador.trim();
				
				// Ciudades para las que hay que emitir comprobante
				List ciudadesComprobante = Arrays.asList( new String[]{"ANTOFAGASTA", "PELOTILLEHUE"} );
				if (ciudadesComprobante.contains(ciudadPrestador.toUpperCase())){ agregarComprobante = true; }
				
			} catch (Exception e) {
				ciudadPrestador = "";
			}


            RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), usuarioWeb );
            
            BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), usuarioWeb );
            BeneficiarioDTO beneficiario2 = beneficiariosDao.leeBeneficiario( rolbene.getRimpo(), usuarioWeb );

			List listaDetalle = bonoDao.getDetalleBonoWeb( bono.getId().intValue(), usuarioWeb );
			
			StringBuffer bufferDetalle = new StringBuffer();
			for (int i=0; listaDetalle != null && i<listaDetalle.size();i++){
				BonoWItemDTO item = (BonoWItemDTO) listaDetalle.get(i);
				PrestacionGenericaDTO p = prestacionesGenericasDao.prestacionPorCodigo( item.getCodigoPrestacion() );
				campos.add( TextUtil.completarDerecha("00", item.getCodigoPrestacion()+"") );
				
				// Con la prestacion, genero el XML y lo agrego al buffer
				String XML = PdfUtil.getFilaDetalle(p.getCodigo()+"", p.getNombre(), "", "", "", "");
				bufferDetalle.append( XML );
			}
			
			/*
			 * De aqui en adelante, es solo generacion del PDF
			 * - Leer el XML con la plantilla para Apache FO
			 * - Reemplazos varios en el XML usando marcas (ej. __CAMPO__)
			 */
	        try {
	        	InputSource foFile;
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	            response.setContentType("application/pdf");
	            response.setCharacterEncoding("iso-8859-1");
	            
	            String path = getServletContext().getRealPath(".");
	            
	            // Convierto el path en path con slashes en Windows
	            String salida = "";
	            String slash = "";
	    		StringTokenizer tok = new StringTokenizer(path, "\\");
	    		while (tok.hasMoreTokens()){ salida += slash + tok.nextToken(); slash = "/"; }
	    		path = salida;
	            
	            String pathXML = path + "/WEB-INF/bono.fo.xml";
	            StringBuffer buffer = new StringBuffer();
	            BufferedReader reader = new BufferedReader(new FileReader( pathXML ));
	            
	            String linea = null;
	            while ( (linea=reader.readLine()) != null){ buffer.append(linea); }
	            String archivoFO = buffer.toString();
	            
	            // Calculo 30 dias corridos desde la fecha de emision del bono
	            Date fechaExpiracion = (Date)bono.getFechaEmision().clone();
	            fechaExpiracion.setDate( fechaExpiracion.getDate() + 30 );
	            
	            // Aplico los reemplazos para los textos del FOLIO, FECHA
	            
	            archivoFO = archivoFO.replaceAll("__PATH__", path);
	            //campos.add( path );
	            archivoFO = archivoFO.replaceAll("__FOLIO__", bono.getFolio().toString());
	            campos.add( TextUtil.completarDerecha("00000000", bono.getFolio().toString().trim()) );
	            
	            archivoFO = archivoFO.replaceAll("__NOMBRE_PRESTADOR__", prestador.getRazonSocial() );
	            //campos.add( prestador.getRazonSocial() );
	            String strRutPrestador = bono.getRutPrestador().trim();
	            int rutPrestador = (new Integer(strRutPrestador.substring(0, strRutPrestador.length()-2))).intValue();
	            archivoFO = archivoFO.replaceAll("__RUT_PRESTADOR__", TextUtil.getRutFormateado(rutPrestador) );
	            // archivoFO = archivoFO.replaceAll("__RUT_PRESTADOR__", bono.getRutPrestador() );
	            //campos.add( bono.getRutPrestador() );
	            archivoFO = archivoFO.replaceAll("__CIUDAD_PRESTADOR__", ciudadPrestador );

	            
	            String [] nombreCiudad = getNombreCiudadEmisor( bono.getCodigoHabilitado().toString() ); // 20060405

	            
	            //archivoFO = archivoFO.replaceAll("__NOMBRE_HABILITADO__", habilitado.getNombre());
	            //archivoFO = archivoFO.replaceAll("__NOMBRE_HABILITADO__", usuarioWeb.getNombreCompleto());
	            archivoFO = archivoFO.replaceAll("__NOMBRE_HABILITADO__", nombreCiudad[0] ); // 20060405

	            //campos.add( habilitado.getNombre() );
	            //archivoFO = archivoFO.replaceAll("__RUT_HABILITADO__", habilitado.getCodigo()+"");
	            //int rutEmisor = (new Integer(usuarioWeb.getRutEmisor().trim())).intValue();
	            int rutEmisor = bono.getCodigoHabilitado().intValue(); // 20060314
	            archivoFO = archivoFO.replaceAll("__RUT_HABILITADO__", TextUtil.getRutFormateado( rutEmisor ));
	            campos.add( TextUtil.completarDerecha("0000000", usuarioWeb.getRutEmisor().trim()) );

	            archivoFO = archivoFO.replaceAll("__CIUDAD_HABILITADO__", nombreCiudad[1] ); // 20060405
	            	            
	            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	            String fecha = "";
	            try { fecha = sdf.format(bono.getFechaEmision()); } catch (Exception ex){ ex.printStackTrace(); }
	            archivoFO = archivoFO.replaceAll("__FECHA__", fecha );
	            //campos.add( fecha );
	            
	            String fechaTermino = "";
	            try { fechaTermino = sdf.format( fechaExpiracion ); } catch (Exception ex){ ex.printStackTrace(); }
	            archivoFO = archivoFO.replaceAll("__FECHA_EXPIRACION__", fechaTermino );
	            //campos.add( fechaTermino );
	            
	            //LuisLatin
	            archivoFO = archivoFO.replaceAll("__RUTB__", TextUtil.getRutFormateado(rolbene.getRbene()) );
	            archivoFO = archivoFO.replaceAll("__RUTI__", TextUtil.getRutFormateado(rolbene.getRimpo()) );
	            //campos.add( rolbene.getRimpo()+"" );
	            archivoFO = archivoFO.replaceAll("__CMCB__", rolbene.getRepart()+"-"+rolbene.getImpo()+"-"+rolbene.getCorrel());
	            campos.add( (rolbene.getRepart() + rolbene.getImpo() + rolbene.getCorrel()).trim() );
                archivoFO = archivoFO.replaceAll("__NOMB__", beneficiario.getNombre()+" "+beneficiario.getPat()+" "+beneficiario.getMat());
                //campos.add( beneficiario.getNombre()+" "+beneficiario.getPat()+" "+beneficiario.getMat() );
                archivoFO = archivoFO.replaceAll("__CMCI__", rolbene.getRepart()+"-"+rolbene.getImpo()+"-0");
                //campos.add( rolbene.getRepart()+"-"+rolbene.getImpo()+"-0" );
                archivoFO = archivoFO.replaceAll("__NOMI__", beneficiario2.getNombre()+" "+beneficiario2.getPat()+" "+beneficiario2.getMat());
                //campos.add( (beneficiario2.getNombre()+" "+beneficiario2.getPat()+" "+beneficiario2.getMat()).trim() );
                
                String contrato="E";
                String dcontrato="ERRONEO";
                contrato = rolbene.getContrato();
                if (contrato.equals("I")){ dcontrato="INSTITUCIONAL"; } 
                if (contrato.equals("S")){ dcontrato="SUPREMO"; }
				archivoFO= archivoFO.replaceAll("__CONT__",  dcontrato );
				//campos.add( dcontrato );
				
	            
	            // Denis Fuenzalida - Detalle del Bono con Prestaciones Genericas
	            archivoFO = archivoFO.replaceAll("__DETALLE__", bufferDetalle.toString());
	            //campos.add( bufferDetalle.toString() );
	            
	            // Con el contenido de la lista de campos saco un string con el codigo
	            // de validacion y lo aplico
	            //String codigoValidacion = CodigoValidacion.calculaCodigo( campos );
	            //archivoFO = archivoFO.replaceAll("__CODIGO_VALIDACION__", codigoValidacion);

	            // CALCULO EL CODIGO DE VALIDACION CON EL SERIAL DEL BONO
	            String codigoCompleto = TextUtil.completarDerecha("00000000000", bono.getId().toString() );
	            String codigoValidacion = CodigoValidacion.calculaCodigo( codigoCompleto );
	            archivoFO = archivoFO.replaceAll("__CODIGO_VALIDACION__", codigoValidacion);

	            // En el bono no valorado no coloco los siguientes campos
	            archivoFO = archivoFO.replaceAll("__TOTAL__", " ");
	            archivoFO = archivoFO.replaceAll("__TOTAL_CARGO_DIPRECA__", " ");
	            archivoFO = archivoFO.replaceAll("__TOTAL_CARGO_SEGURO__", " ");
	            archivoFO = archivoFO.replaceAll("__TOTAL_COPAGO__", " ");
	            
	            // INICIO PARCHE COMPROBANTE PARA ANTOFAGASTA
	            // El ID del bono
	            String bonoId = TextUtil.completarDerecha("000000000000", bono.getId().toString());
	            archivoFO = archivoFO.replaceAll("__BONO_ID__", bonoId);
	            
	            // Si no tiene que ir un comprobante, comento el contenido
	            
	            // OJO: En el Bono no Valorado no hay comprobante! -- Denis 20060502 
	            if (true || !agregarComprobante){
		            archivoFO = archivoFO.replaceAll("__INICIO_COMPROBANTE__", "<!-- "); // Abro comentario XML
		            archivoFO = archivoFO.replaceAll("__FIN_COMPROBANTE__", " -->"); // Cierro comentario XML
	            } else {
		            archivoFO = archivoFO.replaceAll("__INICIO_COMPROBANTE__", " "); // Abro comentario XML
		            archivoFO = archivoFO.replaceAll("__FIN_COMPROBANTE__", " "); // Cierro comentario XML
	            }
	            // FIN	PARCHE COMPROBANTE ANTOFAGASTA
	            
	            // EL BONO NO VALORADO SIEMPRE DICE "Copago"
	            archivoFO = archivoFO.replaceAll("__TITULO_COPAGO__", "Copago");
	            
	            InputStream inputStream = (InputStream)(new ByteArrayInputStream(archivoFO.getBytes()));
	            InputSource inputSource = new InputSource(inputStream);
	            Driver driver = new Driver(inputSource, out);
	            driver.setLogger(log);
	            driver.setRenderer(Driver.RENDER_PDF);
	            driver.run();
	
	            byte[] content = out.toByteArray();
	            
	            if ("1".equals(getInitParameter("firma.digital.usar"))){
	            	
		            try {	            	
			            // Denis 20091011 - Firma digital usando web service de e-sign.cl
			            String origenBase64 = bmweb.util.Base64.encodeBytes(content);
			            
			    		URL url = new URL( getInitParameter("firma.digital.url") );
			    		WSIntercambiaDocSoapStub service = new WSIntercambiaDocSoapStub(url, null);
			    		
			    		EncabezadoRequest encabezado = new EncabezadoRequest();
			    		encabezado.setUser( getInitParameter("firma.digital.usuario") );
			    		encabezado.setPassword( getInitParameter("firma.digital.password") );
			    		encabezado.setTipoIntercambio("pdf");
			    		encabezado.setNombreConfiguracion( getInitParameter("firma.digital.configuracion") );
			    		encabezado.setFormatoDocumento("b64");
			    				            
			    		DocumentoParametro parametro = new DocumentoParametro();
			    		parametro.setNombreDocumento("BonoValorado.pdf");
			    		parametro.setDocumento(origenBase64);

			    		EncabezadoResponse respuestaWS = service.intercambiaDoc(encabezado, parametro);
			    		
			    		String documentoFirmado = respuestaWS.getDocumento();
			    		byte[] salidaWS = bmweb.util.Base64.decode(documentoFirmado);
			    		
			            response.setContentLength(salidaWS.length);
			            response.getOutputStream().write(salidaWS);
			            response.getOutputStream().flush();		    		
			            
		            } catch (Exception ex) {
		            	
		            	ex.printStackTrace();

		            	// Si hay algun error, env�o la version sin firmar
			            response.setContentLength(content.length);
			            response.getOutputStream().write(content);
			            response.getOutputStream().flush();	            	
		            }
		            
	            } else {
	            	// version sin firma digital
		            response.setContentLength(content.length);
		            response.getOutputStream().write(content);
		            response.getOutputStream().flush();	            	
	            }
	            
	            
	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
			}
	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	
	}
	
	/**
	 * retorna un String[] {nombreEmisor, ciudadEmisor}
	 * @param rutUsuario
	 * @return
	 */
	private String[] getNombreCiudadEmisor(String rutUsuario){

		// veo si es beneficiario
		try {
			UsuarioWeb usuarioWeb = new UsuarioWeb(rutUsuario); 
			usuarioWeb.setRutEmisor( rutUsuario );
			BeneficiarioDTO ben = beneficiariosDao.leeBeneficiario(Integer.parseInt(rutUsuario), usuarioWeb);
			String nombreEmisor = ben.getNombre() + " " + ben.getPat() + " " + ben.getMat();
			
			String ciudad = "";
			
			// si el usuario es beneficiario,
			// ademas veo si es habilitado para ver su ciudad
			try {
				HabilitadoDTO h = habilitadoDao.getHabilitadoPorCodigo( Integer.parseInt(rutUsuario), usuarioWeb );
				if (h.getDom_ciudad() != null){
					ciudad = (String) ciudadDao.mapa().get( h.getDom_ciudad() );
				}

			} catch (Exception e) { }

			return new String[]{ nombreEmisor, ciudad };

		} catch(Exception e){ }
		
		// veo si es habilitado
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			HabilitadoDTO h = habilitadoDao.getHabilitadoPorCodigo( Integer.parseInt(rutUsuario), uwTemp );
			String nombreEmisor = h.getNombre();
			
			String nombreCiudad = "";
			try { nombreCiudad = (String) ciudadDao.mapa().get( h.getDom_ciudad() ); }
			catch (Exception e) { nombreCiudad = "Habilitado en ciudad incorrecta."; }
			return new String[]{ nombreEmisor, nombreCiudad };

		} catch(Exception e){ }
		
		// veo si es prestador
		try {
			UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
			uwTemp.setNombreUsuario(rutUsuario);
			PrestadorDTO p = prestadoresDao.prestadorPorRutAux( rutUsuario, uwTemp );
			String nombreEmisor = p.getRazonSocial();
			
			String nombreCiudad = "";
			try { nombreCiudad = (String) ciudadDao.mapa().get( new Integer(p.getCodCiudad()) ); }
			catch (Exception e) { nombreCiudad = "Ciudad incorrecta en prestador"; }
			return new String[]{ nombreEmisor, nombreCiudad };
		} catch(Exception e){ }
		
		return new String[]{ rutUsuario, "" };
	}
	
}
