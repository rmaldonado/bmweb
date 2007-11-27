/*
 * Creado en 02-08-2005 por denis
 *
 */
package bmweb.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.BonoDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.dto.ReglaDTO;
import bmweb.util.QueryLogger;
import bmweb.util.ReflectionFiller;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis
 *
 * Clase que permite recuperar listados de prestadores por ciudad
 *
 */
public class PrestadoresDao implements IPrestadoresDao {


	private DataSource dataSource;
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }

	// funcion para calcular edad sacada del javaalmanac.com
	
	public int calcularEdadBeneficiario(BeneficiarioDTO benefiario){

		
		Date fn = benefiario.getFechaNacimiento();
		
		// Create a calendar object with the date of birth
	    Calendar dateOfBirth = new GregorianCalendar(); //fn, Calendar.JANUARY, 27);
	    dateOfBirth.setTime(fn);
	    //dateOfBirth.set(Calendar.YEAR, fn.getYear() + 1900);
	    //dateOfBirth.set(Calendar.MONTH, fn.getMonth() +1);
	    //dateOfBirth.set(Calendar.DATE, fn.getDate());
	    
	    // Create a calendar object with today's date
	    Calendar today = Calendar.getInstance();
	    
	    // Get age based on year
	    int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
	    
	    // Add the tentative age to the date of birth to get this year's birthday
	    dateOfBirth.add(Calendar.YEAR, age);
	    
	    // If this year's birthday has not happened yet, subtract one from age
	    if (today.before(dateOfBirth)) {
	        age--;
	    }
	    
	    return age;

	}

	public String buscarPrestacionesIncompatibles(int[] prestacionesBono, int prestacionNueva, UsuarioWeb uw){

		List listaReglas = new ArrayList();
		String nueva = prestacionNueva + "";

		for (int j=0; j<prestacionesBono.length; j++){

			ReglasMappingQuery rq = new ReglasMappingQuery(prestacionesBono[j], uw);
			listaReglas = rq.execute();

			for (int i=0; listaReglas != null && i<listaReglas.size(); i++){
				ReglaDTO regla = (ReglaDTO) listaReglas.get(i);

				// Si entre las restricciones a la j-esima prestacion viene una de prestaciones incompatibles
				if (regla.getRestriccion().intValue() == ReglaDTO.RESTRICCION_PRESTACIONES_INCOMPATIBLES){
					
					// veo si la prestacion nueva es incompatible con las de la j-esima
					if (regla.getTexto().indexOf(nueva) > 0){
						// la prestacion nueva es incompatible con la otra
						return regla.getMensaje();
					}
				} // fin tipo regla
			} // fin lista reglas

		} // fin prestaciones

		return null;
	}
	
	public String autorizarPrestacion(String CMC, Integer rutEmisor, BeneficiarioDTO beneficiario, int codPrestacion, int cantidad, UsuarioWeb uw) {
		
		// 20060703 - Restriccion de bonos por nivel
		// Reviso si un usuario perteneciente a un nivel puede emitir bonos
		
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		String queryRestriccion = "" +
				" select count(*) " +
				" from KEYWORD_DET " +
				" where KEY_SIST='BENMED'" +
				" and KEY_WORD = 'RESTNV'" +
				" and KEY_ID = " + uw.getNivel();
		
		if (1 == jt.queryForInt(queryRestriccion)){
			return "Los usuarios de este nivel no pueden emitir bonos.";
		}
		
		List listaReglas = new ArrayList();
		
		/*
		try { listaReglas = (List) (new ReglasHibernateCallback(codPrestacion).doInHibernate( getSession() )); }
		catch (Exception e) { e.printStackTrace(); }
		*/
		ReglasMappingQuery rq = new ReglasMappingQuery(codPrestacion, uw);
		listaReglas = rq.execute();
		
		
		for (int i=0; listaReglas != null && i<listaReglas.size(); i++){
			ReglaDTO regla = (ReglaDTO) listaReglas.get(i);
			
			// Ahora todas las reglas corresponden con la prestacion que quiero hacer
			
			// REGLA con restriccion por edad - OJO: Edad minima y maxima NO son excluyentes
			// --> Se pueden especificar rangos de edades para prestaciones
			
			if (regla.getRestriccion().intValue() == ReglaDTO.RESTRICCION_EDAD
					&& beneficiario.getFechaNacimiento() != null) {
				
				int edadBeneficiario = calcularEdadBeneficiario(beneficiario);
				
				// Si la regla tiene edad minima
				if (regla.getNumero1()!= null && edadBeneficiario < regla.getNumero1().intValue() ){
					// error, la edad del beneficiario es inferior a la minima requerida
					return regla.getMensaje();
				}
				
				// Si la regla tiene edad maxima
				if (regla.getNumero2()!= null && edadBeneficiario > regla.getNumero2().intValue() ){
					// error, la edad del beneficiario es mayor que la maxima permitida
					return regla.getMensaje();
				}

			}
			
			// si la regla impone un sexo para el beneficiario
			if (regla.getRestriccion().intValue() == ReglaDTO.RESTRICCION_SEXO ) {
				String sexoBeneficiario = beneficiario.getSexo();
				String textoRegla = regla.getTexto();
				
				if (sexoBeneficiario != null) sexoBeneficiario = sexoBeneficiario.trim();
				if (textoRegla != null) textoRegla = textoRegla.trim();

				// el sexo del beneficiario no esta en las opciones especificadas por la regla
				if (sexoBeneficiario != null && textoRegla != null && (textoRegla.indexOf(sexoBeneficiario)) < 0){
					//return "Error: No se puede autorizar prestacion porque no corresponde con el sexo del beneficiario.";
					return regla.getMensaje();
				}
				
			}

			// Revisamos las prestaciones de los ultimos N dias para ver si hay prestaciones
			if (regla.getRestriccion().intValue() == ReglaDTO.RESTRICCION_CANTIDAD_POR_PERIODO && regla.getNumero1() != null && regla.getNumero2() != null ){
				int maxPrestaciones = regla.getNumero1().intValue();
				int numDias = regla.getNumero2().intValue();
				
				/*llr aplicar la regla a la cantidad de la prestacion//
				try {
					
				} catch (Exception e) {
					return regla.getMensaje();
				}
				*/
				try {
					int numPrestaciones = numPrestacionesUltimosDias(CMC, codPrestacion, numDias, uw);
					
					// si el numero de prestaciones supera las admitidas por la regla, notifico el error
					if ( maxPrestaciones < numPrestaciones + cantidad){
						// return "Error: No se autoriza la prestacion. El beneficiario ha superado el maximo de atenciones por periodo.";
						return regla.getMensaje();
					}
					
				} catch (Exception e) {
					// return null;
					return regla.getMensaje();
				}
				
			}
			
			// Otras reglas futuras aqui
				

		}
		
		// Regla de cantidad de bonos por emisor por periodo - 20060531
		ReglasMappingQuery rq2 = new ReglasMappingQuery(rutEmisor.intValue(), uw);
		listaReglas = rq2.execute();

		for (int i=0; listaReglas != null && i<listaReglas.size(); i++){
			ReglaDTO regla = (ReglaDTO) listaReglas.get(i);
			
			// Ahora todas las reglas corresponden con la prestacion que quiero hacer
			
			// REGLA con restriccion por edad - OJO: Edad minima y maxima NO son excluyentes
			// --> Se pueden especificar rangos de edades para prestaciones
			
			if (regla.getRestriccion().intValue() == ReglaDTO.RESTRICCION_CANTIDAD_BONOS_EMISOR_POR_PERIODO) {
				// Consulto la cantidad de bonos emitidos en el periodo
				int maxBonos = regla.getNumero1().intValue();
				int dias = regla.getNumero2().intValue();
				
				JdbcTemplate t = new JdbcTemplate(dataSource);
				String query = "" +
						"select count(*) from bm_bono b" +
						" where b.ha_codigo = " + rutEmisor +
						" and b.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'" +
						" and b.bo_fecemi + " + dias + " >= TODAY";
				
				int bonosEmitidos = t.queryForInt(query);

				if ( bonosEmitidos >= maxBonos){
					// return "Error: No se autoriza la prestacion. El beneficiario ha superado el maximo de atenciones por periodo.";
					return regla.getMensaje();
				}

			}
		}

		
		return null;
	}
	
	
       /**
        * Recupero el valor de una prestacion, calculo el aporte dipreca y aporte seguro
        * y copago.
        * @param args
        * @return int[]{ totalAporteDipreca, totalAporteSeguro, totalCopago };
        */
       public int[] copagoYAportesPorPrestador(String CMC, String rutPrestador, List listaPrestaciones, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int codPabellon, String conValorCobrado, UsuarioWeb uw ){

               int totalAporteDipreca	= 0;
               int totalAporteSeguro	= 0;
               int totalCopago			= 0;
               int valorConvenido		= 0;
               int aportePabellon		= 0;
               int codigoConvenio       = 0;

               for (int i=0; listaPrestaciones != null && i<listaPrestaciones.size(); i++){
	            	int cantidad = 1;
	
	            	try {
	                	String[] fila = (String[]) listaPrestaciones.get(i);
	                	String codPrestacion = fila[0];
	                	cantidad = new Integer(fila[2]).intValue();
	                	
	                	int[] filaAportes = copagoYAportesPorPrestadorYPrestacion(CMC, rutPrestador, codPrestacion, fecha, tipoPrestacion, codProfesional, codContrato, salaComun, codPabellon, conValorCobrado, uw);
		
                        totalAporteDipreca += cantidad * filaAportes[0];
                        totalAporteSeguro  += cantidad * filaAportes[1];
                        totalCopago        += cantidad * filaAportes[2];
                        codigoConvenio      = filaAportes[5];
                        valorConvenido 		= filaAportes[7];
                        aportePabellon		= filaAportes[8];
					} catch (Exception e) {
						e.printStackTrace();
						return null;  
						// TEST
						//return new int[]{ cantidad , cantidad, 1000 - (2*cantidad) };
					}

               }

               return new int[]{ totalAporteDipreca, totalAporteSeguro, totalCopago, valorConvenido, aportePabellon,codigoConvenio };
       }

       //Parche para carga Masiva 31/07/2007 Luis Latin
       public int[] copagoYAportesPorPrestadorCM(String CMC, String rutPrestador, List listaPrestaciones, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int codPabellon, String conValorCobrado, UsuarioWeb uw ){

        int totalAporteDipreca	= 0;
        int totalAporteSeguro	= 0;
        int totalCopago			= 0;
        int valorConvenido		= 0;
        int aportePabellon		= 0;
        int codigoConvenio       = 0;

        for (int i=0; listaPrestaciones != null && i<listaPrestaciones.size(); i++){
         	int cantidad = 1;

         	try {
             	String[] fila = (String[]) listaPrestaciones.get(i);
             	String codPrestacion = fila[0];
             	cantidad = new Integer(fila[2]).intValue();
             	
             	int[] filaAportes = copagoYAportesPorPrestadorYPrestacionCM(CMC, rutPrestador, codPrestacion, fecha, tipoPrestacion, codProfesional, codContrato, salaComun, codPabellon, conValorCobrado, uw);
	
                 totalAporteDipreca += cantidad * filaAportes[0];
                 totalAporteSeguro  += cantidad * filaAportes[1];
                 totalCopago        += cantidad * filaAportes[2];
                 codigoConvenio      = filaAportes[5];
                 valorConvenido 	 = filaAportes[7];
                 aportePabellon		 = filaAportes[8];
				} catch (Exception e) {
					e.printStackTrace();
					return null;  
					// TEST
					//return new int[]{ cantidad , cantidad, 1000 - (2*cantidad) };
				}

        }

        return new int[]{ totalAporteDipreca, totalAporteSeguro, totalCopago, valorConvenido, aportePabellon,codigoConvenio };
       }
       //Fin Parche carga Masiva 31/07/2007
       private Object[] buscarFactorAporteDipreca(List listaAportesDipreca, String CMC, int grupo){

               if (listaAportesDipreca == null || listaAportesDipreca.size() == 0) return null;

               // Trozo el CMC para hacer la busqueda - 1-234567-09
               String[] div = TextUtil.dividirCMC(CMC);
               String reparticion = div[0];
               //int grupo = new Integer(div[2]).intValue();

               // Primero busco la misma reparticion y grupo en la lista
               for (int i=0;i<listaAportesDipreca.size(); i++){
                       Object[] fila = (Object[])listaAportesDipreca.get(i);
                       float factor = ((Float)fila[0]).floatValue();
                       String instit = (String)fila[1];
                       int sp_grupo = ((Integer)fila[2]).intValue();

                       // misma reparticion y grupo
                       if (instit.equals(reparticion) && (grupo == sp_grupo)){
                               return fila;
                       }
               }
               // reparticion = 'T' y mismo grupo
               for (int i=0;i<listaAportesDipreca.size(); i++){
                       Object[] fila = (Object[])listaAportesDipreca.get(i);
                       float factor = ((Float)fila[0]).floatValue();
                       String instit = (String)fila[1];
                       int sp_grupo = ((Integer)fila[2]).intValue();

                       // misma reparticion y grupo
                       if (instit.equals("T") && (grupo == sp_grupo)){
                               return fila;
                       }
               }
               
               // misma reparticion y grupo = 99
               for (int i=0;i<listaAportesDipreca.size(); i++){
                       Object[] fila = (Object[])listaAportesDipreca.get(i);
                       float factor = ((Float)fila[0]).floatValue();
                       String instit = (String)fila[1];
                       int sp_grupo = ((Integer)fila[2]).intValue();

                       // misma reparticion y grupo 99
                       if (instit.equals(reparticion) && (sp_grupo == 99) ){
                               return fila;
                       }
               }         

               // reparticion = 'T' y grupo = "99"
               for (int i=0;i<listaAportesDipreca.size(); i++){
                       Object[] fila = (Object[])listaAportesDipreca.get(i);
                       float factor = ((Float)fila[0]).floatValue();
                       String instit = (String)fila[1];
                       int sp_grupo = ((Integer)fila[2]).intValue();

                       // misma reparticion y grupo
                       if (instit.equals("T") && (sp_grupo == 99)){
                               return fila;
                       }
               }

               return null;
       }

	private Integer valorPrestacionPorPrestador(String rutPrestador, String codPrestacion, UsuarioWeb uw){
       	
		try {
			String sql = "" +
				" select v.vc_valor as vc_valor " +
				" from "+
				" acreedor a," +
				" bm_convenio c," +
				" bm_valcon v " +
				" where a.acree_rut = '" + rutPrestador.trim() + "' " +
				"   and c.pb_codigo = a.acree_rut_aux" +
				"   and c.cv_codigo = v.cv_codigo " +
				"   and v.pr_codigo = " + codPrestacion + " " +
				"   and today between c.cv_fecini and c.cv_fecter ";
		
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			QueryLogger.log(uw, sql);
			int valor = template.queryForInt(sql);
			return new Integer(valor);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
       	 
    }
	private Integer valorPrestacionPorPrestador(String rutPrestador, String codPrestacion, UsuarioWeb uw, Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");   
		try { 
			String sql = "" +
				" select v.vc_valor as vc_valor " +
				" from "+
				" acreedor a," +
				" bm_convenio c," +
				" bm_valcon v " +
				" where a.acree_rut = '" + rutPrestador.trim() + "' " +
				"   and c.pb_codigo = a.acree_rut_aux" +
				"   and c.cv_codigo = v.cv_codigo " +
				"   and v.pr_codigo = " + codPrestacion + " " +
				"   and '" + sdf.format(fecha) +"' "+ "between c.cv_fecini and c.cv_fecter ";
		
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			QueryLogger.log(uw, sql);
			int valor = template.queryForInt(sql);
			return new Integer(valor);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
       	 
       }

	public Integer convenioPorPrestadorYPrestacion(String rutPrestador, String codPrestacion, UsuarioWeb uw){
       	
		try {
			String sql = "" +
				" select v.cv_codigo as codigo " +
				" from "+
				" acreedor a," +
				" bm_convenio c," +
				" bm_valcon v " +
				" where a.acree_rut = '" + rutPrestador.trim() + "' " +
				"   and c.pb_codigo = a.acree_rut_aux" +
				"   and c.cv_codigo = v.cv_codigo " +
				"   and v.pr_codigo = " + codPrestacion + " " +
				"   and today between c.cv_fecini and c.cv_fecter ";
		
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			QueryLogger.log(uw, sql);
			int valor = template.queryForInt(sql);
			return new Integer(valor);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
       	 
       }	
       private int[] copagoYAportesPorPrestadorYPrestacion(String CMC, String rutPrestador, String codPrestacion, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int cobroPabellon, String conValorCobrado, UsuarioWeb uw){
       
               try {
               	
               		/*
	               return new int[]{ 
	               		aporteDipreca + (int) dAportePabellon, 
	               		aporteSeguroDipreca + aporteSeguroPabellon, 
						valorPrestacion.intValue() + valorPabellon.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon, 
						valorPabellonConvenido.intValue(), 
						valorPabellon.intValue(), 
						codConvenio, 
						codPabellon };
						*/

               	
               	
               	
	               // Busco la lista de aportes para ese prestador y esa prestacion
	               // lista de Object[] = {(Float)factor, (String)dom_instit, (Integer)sp_grupo, (String) dom_bascob, (Float)ap_porval}
               	
               	   // ANTES
	               // List listaAportesDipreca = (List) ht.execute( new AporteDiprecaPorPrestacionYPrestadorHibernateCallback(rutPrestador, codPrestacion) );
               	
               	   AporteDiprecaPorPrestacionYPrestadorMappingQuery aq 
				      = new AporteDiprecaPorPrestacionYPrestadorMappingQuery(rutPrestador, codPrestacion, fecha, tipoPrestacion, codContrato, salaComun, uw);
	               List listaAportesDipreca = aq.execute();
	
	               // Busco el factor mas apropiado segun el documento
	               // OJO: Grupo son el primero (o los 2) primeros digitos del codigo de la prestacion, por eso divido por 100000
	               int grupo = new Integer(codPrestacion).intValue() / 100000; // ej. 2101001/100000 = 21
	               Object[] tupla = buscarFactorAporteDipreca(listaAportesDipreca, CMC, grupo);

	               // return new Object[]{apFactor, domInstit, spGrupo, domBascob, apPorval, codigoConvenio, pabellon };

	               Integer valorPrestacion = null;
	               float factorAporteDipreca = ((Float)tupla[0]).floatValue();
	               String baseCobranzaAporteDipreca = (String) tupla[3];
	               float porcentaje = ((Float)tupla[4]).floatValue();

	               int codConvenio = ((Integer)tupla[5]).intValue();
	               int codPabellon = ((Integer)tupla[6]).intValue();
                   
	              
	               // Busco el valor de la prestacion
	               //valorPrestacion = (Integer) ht.execute( new ValorPrestacionPorPrestadorHibernateCallback(rutPrestador, codPrestacion) );
	               if(conValorCobrado == "0"){
	                 valorPrestacion = valorPrestacionPorPrestador(rutPrestador, codPrestacion, uw );
	               } else {
	               	   valorPrestacion = new Integer(conValorCobrado);
	               }
	               
	               
	               // Si el prestador no tiene vigente la prestacion, no debe aparecer en el listado
	               if (valorPrestacion == null){
	               	return null; // Ahora manejado en BonoValoradoServlet
	               }

	               double dAporteDipreca = 0.0;
	               double dAportePabellon = 0.0;
	               Integer valorPabellon = new Integer(0);
	               Integer valorPabellonConvenido = valorPabellonSinFonasa(codConvenio, codPabellon, uw);
	               
	               // Si debo calcular el aporte en base al arancel fonasa
	               if ("F ".equalsIgnoreCase(baseCobranzaAporteDipreca)){

	               		// Si el pabellon es no nulo, recupero el valor del pabellon para esa prestacion y pabellon
	               		valorPabellon = valorPabellonFonasa(codPrestacion, codPabellon, fecha, uw);
	               		
	               		Integer valorFonasa = valorPrestacionFonasa(codPrestacion, fecha, codProfesional, uw );
	                    dAporteDipreca = Math.round( (factorAporteDipreca * porcentaje * valorFonasa.intValue())/10000.0 );
	                    
	                    // calculo el aporte dipreca sobre el valor del pabellon
	                    dAportePabellon = Math.round( (factorAporteDipreca * porcentaje * valorPabellon.intValue())/10000.0 );
	                    
	               } else {
	               		//valorPrestacion = (Integer) ht.execute( new ValorPrestacionPorPrestadorHibernateCallback(rutPrestador, codPrestacion) );
	               	
	               		valorPabellon = valorPabellonConvenido;
	               		
	                    dAporteDipreca = Math.round( (factorAporteDipreca * porcentaje * valorPrestacion.intValue())/10000.0 );

	                    // calculo el aporte dipreca sobre el valor del pabellon convenido cuando no es fonasa
	                    dAportePabellon = Math.round( (factorAporteDipreca * porcentaje * valorPabellonConvenido.intValue())/10000.0 );
	               }

	               // obtengo el valor entero del aporte dipreca
	               int aporteDipreca = new Double(dAporteDipreca).intValue();
	
	               // *** BUSQUEDA DE APORTE DEL SEGURO ***

	               int aporteSeguroDipreca = 0;
	               int aporteSeguroPabellon = 0;
	               // Si el beneficiario es asegurado
	               String poliza = beneficiarioEsAsegurado(CMC, uw);
	               
	               if (poliza != null){
	               		// con la poliza busco el aporte 

	               		// ASUMO QUE ES UNA LISTA COMO LA DE LOS APORTES DIPRECA
	               	    FactorAporteSeguroMappingQuery fq = 
	               	    	new FactorAporteSeguroMappingQuery(rutPrestador, codPrestacion, poliza, tipoPrestacion, uw);
	               		// List listaAportesSeguro = (List) ht.execute(new FactorAporteSeguroHibernateCallback(rutPrestador, codPrestacion, poliza));
	               		List listaAportesSeguro = fq.execute();
	               		
	               		if (listaAportesSeguro == null || listaAportesSeguro.size() == 0){
	               			aporteSeguroDipreca = 0;
	               			aporteSeguroPabellon = 0;
	               		} else {

		               		// Reutilizo la busqueda de factor de aporte para buscar el factor del seguro que corresponde para esta prestacion y grupo
		               	   Object[] tuplaSeguro = buscarFactorAporteDipreca(listaAportesSeguro, CMC, grupo);
		               	   
		               	   // No existe una tupla en la tabla = no hay aporte seguro
		               	   if (tuplaSeguro == null){
		               	   	
		                    aporteSeguroDipreca = 0;
		                    aporteSeguroPabellon = 0;
		               	   	
		               	   } else {
		               	   	
			 	               float factorAporteSeguro = ((Float)tuplaSeguro[0]).floatValue();
				               String baseCobranzaSeguro = (String) tuplaSeguro[3];
				               float porcentajeSeguro = ((Float)tuplaSeguro[4]).floatValue();
				               
				               // Si debo calcular el aporte en base al arancel fonasa		               
				               if ("F ".equalsIgnoreCase(baseCobranzaSeguro)){
				               		Integer valorFonasa = valorPrestacionFonasa(codPrestacion, fecha, codProfesional, uw );

				                    double dAporteSeguroDipreca = Math.round( (factorAporteSeguro * porcentajeSeguro * valorFonasa.intValue())/10000.0 );
				                    double dAporteSeguroPabellon = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPabellon.intValue())/10000.0 );
				                    aporteSeguroDipreca = new Double(dAporteSeguroDipreca).intValue();
				                    aporteSeguroPabellon = new Double(dAporteSeguroPabellon).intValue();
				               } else {
				                    double dAporteSeguroDipreca = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPrestacion.intValue())/10000.0 );
				                    double dAporteSeguroPabellon = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPabellon.intValue())/10000.0 );
				                    aporteSeguroDipreca = new Double(dAporteSeguroDipreca).intValue();
				                    aporteSeguroPabellon = new Double(dAporteSeguroPabellon).intValue();
				               }		               	   	
		               	   }

	               		}
	               		
	               }
	               
	               // corrijo el valor del copago
	               int copago = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon;
	               if (copago < 0) copago = 0;

	               // si los aportes superan los valores, calculo el aporte dipreca minimo posible
                   if (aporteSeguroPabellon > valorPrestacion.intValue()){
                   	  aporteSeguroPabellon = valorPrestacion.intValue();
                   	  aporteSeguroDipreca = 0;
                   	  aporteDipreca = 0;
                   }
	               if (aporteSeguroDipreca > valorPrestacion.intValue()){
                   	  aporteSeguroDipreca = valorPrestacion.intValue();
                   	  aporteSeguroPabellon=0;
                   	  aporteDipreca = 0;
                   }
	               if (aporteDipreca + aporteSeguroDipreca + aporteSeguroPabellon > valorPrestacion.intValue() + valorPabellonConvenido.intValue()){
	               	if (aporteSeguroDipreca+aporteSeguroPabellon < valorPrestacion.intValue()+ valorPabellonConvenido.intValue())
	               	    aporteDipreca = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteSeguroDipreca - aporteSeguroPabellon;
	               	  if (aporteDipreca >valorPrestacion.intValue() + valorPabellonConvenido.intValue())
	               	     aporteDipreca=valorPrestacion.intValue() + valorPabellonConvenido.intValue();
	               	copago = 0;
	               }

	               // si se viene cobrando solo el pabellon, no hay aporte dipreca
	               if (cobroPabellon == 0){
	               		aporteDipreca = 0;
	               		aporteSeguroDipreca = 0;
	               		copago = valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon;
	               }

	               // si se viene cobrando solo la prestacion, no hay aporte pabellon
	               if (cobroPabellon == 1){
	               		dAportePabellon = 0;
	               		aporteSeguroPabellon = 0;
		                copago = valorPrestacion.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon; 
	               }
	               
	               // vienen cobrando ambos
	               if (copago != 0 && cobroPabellon > 1){
	                 copago = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon; 
	               }

               	   // Reviso si el prestador está en el listado de los prestadores 
	               // con pago directo Y el tipo de prestacion no es '2' (hospitalaria)
	               	               
	               if ( prestadorEsPagoDirecto(rutPrestador) && tipoPrestacion != 2){
	               ValorPagoDirectoMappingQuery vpd = new ValorPagoDirectoMappingQuery(rutPrestador, codPrestacion, fecha, uw);
	               List listaValores = vpd.execute();
	               	   	
	                   if (listaValores != null){
	                     Integer[] fila = (Integer[]) listaValores.get(0);
	               	     // codConvenio, codPrestacion, valorConvenio, valorListaPrecio
	                     int valorListaPrecio = fila[3].intValue();
	               	     copago = valorListaPrecio - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon;
	               	   }
	               }

	               
	               // Busqueda con ultima prioridad: bm_aporte.dom_instit='T' y bm_aporte.sp_grupo=99
	               return new int[]{ 
	               		aporteDipreca + (int) dAportePabellon,
						aporteSeguroDipreca + aporteSeguroPabellon, 
						copago, // Antes era: valorPrestacion.intValue() + valorPabellon.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon, 
						valorPabellonConvenido.intValue(), 
						valorPabellon.intValue(), 
						codConvenio, codPabellon, valorPrestacion.intValue(), (int) dAportePabellon };
               } catch (Exception e){
               	return null; // Ahora manejado en BonoValoradoServlet
               }
       }
       //Parche para calculo del valor de la Prestacion convenida para el Prestador
       // solo para carga Masiva 31/07/2007 Luis Latin, se le agrego el parametro fecha
       //al metodo valorPrestacionPorPrestador()
       private int[] copagoYAportesPorPrestadorYPrestacionCM(String CMC, String rutPrestador, String codPrestacion, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int cobroPabellon, String conValorCobrado, UsuarioWeb uw){
                     try { 
                     	
                     	
                       AporteDiprecaPorPrestacionYPrestadorMappingQuery aq 
      			       = new AporteDiprecaPorPrestacionYPrestadorMappingQuery(rutPrestador, codPrestacion, fecha, tipoPrestacion, codContrato, salaComun, uw);
      	               List listaAportesDipreca = aq.execute();
      	
      	               // Busco el factor mas apropiado segun el documento
      	               // OJO: Grupo son el primero (o los 2) primeros digitos del codigo de la prestacion, por eso divido por 100000
      	               int grupo = new Integer(codPrestacion).intValue() / 100000; // ej. 2101001/100000 = 21
      	               Object[] tupla = buscarFactorAporteDipreca(listaAportesDipreca, CMC, grupo);

      	               // return new Object[]{apFactor, domInstit, spGrupo, domBascob, apPorval, codigoConvenio, pabellon };

      	               Integer valorPrestacion = null;
      	               float factorAporteDipreca = ((Float)tupla[0]).floatValue();
      	               String baseCobranzaAporteDipreca = (String) tupla[3];
      	               float porcentaje = ((Float)tupla[4]).floatValue();

      	               int codConvenio = ((Integer)tupla[5]).intValue();
      	               int codPabellon = ((Integer)tupla[6]).intValue();
                         
      	              
      	               // Busco el valor de la prestacion
      	               //valorPrestacion = (Integer) ht.execute( new ValorPrestacionPorPrestadorHibernateCallback(rutPrestador, codPrestacion) );
      	               if(conValorCobrado == "0"){
      	                 valorPrestacion = valorPrestacionPorPrestador(rutPrestador, codPrestacion, uw, fecha );
      	               } else {
      	               	   valorPrestacion = new Integer(conValorCobrado);
      	               }
      	               
      	               
      	               // Si el prestador no tiene vigente la prestacion, no debe aparecer en el listado
      	               if (valorPrestacion == null){
      	               	return null; // Ahora manejado en BonoValoradoServlet
      	               }

      	               double dAporteDipreca = 0.0;
      	               double dAportePabellon = 0.0;
      	               Integer valorPabellon = new Integer(0);
      	               Integer valorPabellonConvenido = valorPabellonSinFonasa(codConvenio, codPabellon, uw);
      	               
      	               // Si debo calcular el aporte en base al arancel fonasa
      	               if ("F ".equalsIgnoreCase(baseCobranzaAporteDipreca)){

      	               		// Si el pabellon es no nulo, recupero el valor del pabellon para esa prestacion y pabellon
      	               		valorPabellon = valorPabellonFonasa(codPrestacion, codPabellon, fecha, uw);
      	               		
      	               		Integer valorFonasa = valorPrestacionFonasa(codPrestacion, fecha, codProfesional, uw );
      	                    dAporteDipreca = Math.round( (factorAporteDipreca * porcentaje * valorFonasa.intValue())/10000.0 );
      	                    
      	                    // calculo el aporte dipreca sobre el valor del pabellon
      	                    dAportePabellon = Math.round( (factorAporteDipreca * porcentaje * valorPabellon.intValue())/10000.0 );
      	                    
      	               } else {
      	               		//valorPrestacion = (Integer) ht.execute( new ValorPrestacionPorPrestadorHibernateCallback(rutPrestador, codPrestacion) );
      	               	
      	               		valorPabellon = valorPabellonConvenido;
      	               		
      	                    dAporteDipreca = Math.round( (factorAporteDipreca * porcentaje * valorPrestacion.intValue())/10000.0 );

      	                    // calculo el aporte dipreca sobre el valor del pabellon convenido cuando no es fonasa
      	                    dAportePabellon = Math.round( (factorAporteDipreca * porcentaje * valorPabellonConvenido.intValue())/10000.0 );
      	               }

      	               // obtengo el valor entero del aporte dipreca
      	               int aporteDipreca = new Double(dAporteDipreca).intValue();
      	
      	               // *** BUSQUEDA DE APORTE DEL SEGURO ***

      	               int aporteSeguroDipreca = 0;
      	               int aporteSeguroPabellon = 0;
      	               // Si el beneficiario es asegurado
      	               String poliza = beneficiarioEsAsegurado(CMC, uw);
      	               
      	               if (poliza != null){
      	               		// con la poliza busco el aporte 

      	               		// ASUMO QUE ES UNA LISTA COMO LA DE LOS APORTES DIPRECA
      	               	    FactorAporteSeguroMappingQuery fq = 
      	               	    	new FactorAporteSeguroMappingQuery(rutPrestador, codPrestacion, poliza, tipoPrestacion, uw);
      	               		// List listaAportesSeguro = (List) ht.execute(new FactorAporteSeguroHibernateCallback(rutPrestador, codPrestacion, poliza));
      	               		List listaAportesSeguro = fq.execute();
      	               		
      	               		if (listaAportesSeguro == null || listaAportesSeguro.size() == 0){
      	               			aporteSeguroDipreca = 0;
      	               			aporteSeguroPabellon = 0;
      	               		} else {

      		               		// Reutilizo la busqueda de factor de aporte para buscar el factor del seguro que corresponde para esta prestacion y grupo
      		               	   Object[] tuplaSeguro = buscarFactorAporteDipreca(listaAportesSeguro, CMC, grupo);
      		               	   
      		               	   // No existe una tupla en la tabla = no hay aporte seguro
      		               	   if (tuplaSeguro == null){
      		               	   	
      		                    aporteSeguroDipreca = 0;
      		                    aporteSeguroPabellon = 0;
      		               	   	
      		               	   } else {
      		               	   	
      			 	               float factorAporteSeguro = ((Float)tuplaSeguro[0]).floatValue();
      				               String baseCobranzaSeguro = (String) tuplaSeguro[3];
      				               float porcentajeSeguro = ((Float)tuplaSeguro[4]).floatValue();
      				               
      				               // Si debo calcular el aporte en base al arancel fonasa		               
      				               if ("F ".equalsIgnoreCase(baseCobranzaSeguro)){
      				               		Integer valorFonasa = valorPrestacionFonasa(codPrestacion, fecha, codProfesional, uw );

      				                    double dAporteSeguroDipreca = Math.round( (factorAporteSeguro * porcentajeSeguro * valorFonasa.intValue())/10000.0 );
      				                    double dAporteSeguroPabellon = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPabellon.intValue())/10000.0 );
      				                    aporteSeguroDipreca = new Double(dAporteSeguroDipreca).intValue();
      				                    aporteSeguroPabellon = new Double(dAporteSeguroPabellon).intValue();
      				               } else {
      				                    double dAporteSeguroDipreca = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPrestacion.intValue())/10000.0 );
      				                    double dAporteSeguroPabellon = Math.round( (factorAporteSeguro * porcentajeSeguro * valorPabellon.intValue())/10000.0 );
      				                    aporteSeguroDipreca = new Double(dAporteSeguroDipreca).intValue();
      				                    aporteSeguroPabellon = new Double(dAporteSeguroPabellon).intValue();
      				               }		               	   	
      		               	   }

      	               		}
      	               		
      	               }
      	               
      	               // corrijo el valor del copago
      	               int copago = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon;
      	               if (copago < 0) copago = 0;

      	               // si los aportes superan los valores, calculo el aporte dipreca minimo posible
                         if (aporteSeguroPabellon > valorPrestacion.intValue()){
                         	  aporteSeguroPabellon = valorPrestacion.intValue();
                         	  aporteSeguroDipreca = 0;
                         	  aporteDipreca = 0;
                         }
      	               if (aporteSeguroDipreca > valorPrestacion.intValue()){
                         	  aporteSeguroDipreca = valorPrestacion.intValue();
                         	  aporteSeguroPabellon=0;
                         	  aporteDipreca = 0;
                         }
      	               if (aporteDipreca + aporteSeguroDipreca + aporteSeguroPabellon > valorPrestacion.intValue() + valorPabellonConvenido.intValue()){
      	               	if (aporteSeguroDipreca+aporteSeguroPabellon < valorPrestacion.intValue()+ valorPabellonConvenido.intValue())
      	               	    aporteDipreca = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteSeguroDipreca - aporteSeguroPabellon;
      	               	  if (aporteDipreca >valorPrestacion.intValue() + valorPabellonConvenido.intValue())
      	               	     aporteDipreca=valorPrestacion.intValue() + valorPabellonConvenido.intValue();
      	               	copago = 0;
      	               }

      	               // si se viene cobrando solo el pabellon, no hay aporte dipreca
      	               if (cobroPabellon == 0){
      	               		aporteDipreca = 0;
      	               		aporteSeguroDipreca = 0;
      	               		copago = valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon;
      	               }

      	               // si se viene cobrando solo la prestacion, no hay aporte pabellon
      	               if (cobroPabellon == 1){
      	               		dAportePabellon = 0;
      	               		aporteSeguroPabellon = 0;
      		                copago = valorPrestacion.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon; 
      	               }
      	               
      	               // vienen cobrando ambos
      	               if (copago != 0 && cobroPabellon > 1){
      	                 copago = valorPrestacion.intValue() + valorPabellonConvenido.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon; 
      	                 if (copago<0)
      	                 {	aporteDipreca+=copago;
      	                    copago = 0;
      	                 } 
      	               }
                       
                     	   // Reviso si el prestador está en el listado de los prestadores 
      	               // con pago directo Y el tipo de prestacion no es '2' (hospitalaria)
      	               	               
      	               if ( prestadorEsPagoDirecto(rutPrestador) && tipoPrestacion != 2){
      	               ValorPagoDirectoMappingQuery vpd = new ValorPagoDirectoMappingQuery(rutPrestador, codPrestacion, fecha, uw);
      	               List listaValores = vpd.execute();
      	               	   	
      	                   if (listaValores != null){
      	                     Integer[] fila = (Integer[]) listaValores.get(0);
      	               	     // codConvenio, codPrestacion, valorConvenio, valorListaPrecio
      	                     int valorListaPrecio = fila[3].intValue();
      	               	     copago = valorListaPrecio - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon;
      	               	     valorPrestacion=new Integer(fila[3].intValue()+ valorPabellonConvenido.intValue());
      	                   }
      	               }

      	               
      	               // Busqueda con ultima prioridad: bm_aporte.dom_instit='T' y bm_aporte.sp_grupo=99
      	               return new int[]{ 
      	               	    aporteDipreca, //0
      	               	//	aporteDipreca + (int) dAportePabellon, //0
      						aporteSeguroDipreca + aporteSeguroPabellon, //1 
      						copago, // 2 Antes era: valorPrestacion.intValue() + valorPabellon.intValue() - aporteDipreca - aporteSeguroDipreca - aporteSeguroPabellon - (int)dAportePabellon, 
      						valorPabellonConvenido.intValue(), //3 
      						valorPabellon.intValue(), //4
      						codConvenio,  //5
							codPabellon,  //6
							valorPrestacion.intValue(), //7
							(int) dAportePabellon }; //8
                     } catch (Exception e){
                     	return null; // Ahora manejado en BonoValoradoServlet
                     }
             }

// fin nuevo
       public List prestadoresPorCiudadYPrestacion(int codCiudad, int codPrestacion, UsuarioWeb uw, String rutDirecto){

       	/*
               HibernateTemplate ht = getHibernateTemplate();

               // Le paso la clase interior que sabe usar los parametros
               List listaPrestadores = (List) ht.execute( new PrestadoresPorCiudadYPrestacionHibernateCallback(codCiudad, codPrestacion) );
               return listaPrestadores;
               */
       	
       	PrestadoresPorCiudadYPrestacionMappingQuery pq = new PrestadoresPorCiudadYPrestacionMappingQuery(codCiudad, codPrestacion, uw, rutDirecto);
       	List lista = pq.execute();
       	return lista;
       }


       /**
        * Devuelve un conjunto de prestadores para una ciudad y codigo de prestacion generica
        * (para la prestacion generica "01" devuelve prestadores que hagan la prestacion "0101001" entre otras)
        * @param codCiudad codigo de la ciudad de la tabla de dominios
        * @param codPrestacionGenerica codigo de la prestacion generica (como numero)
        * @return una lista de prestadores
        */

	public List prestadoresPorCiudadYPrestacionGenerica(int codCiudad, int codPrestacionGenerica, UsuarioWeb uw){

       		/*
               HibernateTemplate ht = getHibernateTemplate();

               // Le paso la clase interior que sabe usar los par?metros
               List listaPrestadores = (List) ht.execute( new PrestadoresPorCiudadYPrestacionGenericaHibernateCallback(codCiudad, codPrestacionGenerica) );
               return listaPrestadores;
               */
    
		PrestadoresPorCiudadYPrestacionGenericaMappingQuery ppc = 
			new PrestadoresPorCiudadYPrestacionGenericaMappingQuery(dataSource, codCiudad, codPrestacionGenerica, uw);
		List lista = ppc.execute();
		return lista;
		
	}

       public PrestadorDTO prestadorPorRut(String rut, UsuarioWeb uw){

       	/*
        HibernateTemplate ht = getHibernateTemplate();
        PrestadorDTO p  = (PrestadorDTO) ht.execute( new PrestadorPorRutHibernateCallback(rut) );
        return p;
        */
       	PrestadorPorRutMappingQuery ppr = new PrestadorPorRutMappingQuery(dataSource, rut, uw);
       	List resultado = ppr.execute();
       	try { return (PrestadorDTO) resultado.get(0); } 
       	catch (Exception e) {
			e.printStackTrace();
			return null;
		}

}

       public PrestadorDTO prestadorPorRutAux(String rut, UsuarioWeb uw){

       	/*
        HibernateTemplate ht = getHibernateTemplate();
        PrestadorDTO p  = (PrestadorDTO) ht.execute( new PrestadorPorRutAuxHibernateCallback(rut) );
        return p;
        */
       	
       	PrestadorPorRutAuxMappingQuery ppr = new PrestadorPorRutAuxMappingQuery(dataSource, rut, uw);
       	List resultado = ppr.execute();
       	try { return (PrestadorDTO) resultado.get(0); } 
       	catch (Exception e) {
			e.printStackTrace();
			return null;
		}

       }
       
       public Integer pabellonPorCodigo(int codigoPrestacion, UsuarioWeb uw){
       	
       	
       	try {
           	JdbcTemplate jt = new JdbcTemplate(dataSource);
           	String query = "" +
           			"select p.pa_pabellon" +
           			" from keyword_det k, bm_prestacion p" +
           			" where k.key_sist = 'BENMED'" +
           			" and k.key_word = 'CIRUJ'" +
           			" and k.key_id = p.pr_codigo " + 
           			" and p.pa_pabellon <> 0" +
           			" and p.pr_codigo = " + codigoPrestacion;
           	
           	int codPabellon = jt.queryForInt(query);
           	return new Integer(codPabellon);
			
		} catch (Exception e) {
			return null;
		}
       	
       	
       }

       private Integer valorPabellonSinFonasa(int codConvenio, int codPabellon, UsuarioWeb uw){
       	
       	try {
            String sql = "" +
	        " select first 1 p.pc_valor" +
			" from bm_pabcon p" +
			" where p.cv_codigo = " + codConvenio + 
			" and p.pa_pabellon = " + codPabellon;

	        JdbcTemplate template = new JdbcTemplate();
	        template.setDataSource(dataSource);
	        QueryLogger.log(uw, sql);
	        int valor = template.queryForInt(sql);
	        return new Integer(valor);
		} catch (Exception e) {
			// e.printStackTrace();
			return new Integer(0);
		}
       }

       
       private Integer valorPabellonFonasa(String codPrestacion, int codPabellon, Date fecha, UsuarioWeb uw){
       	
       	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	       	try {
	            String sql = "" +
		        " select p.pa_valor " +
				" from bm_arafon a, bm_valfon v, bm_pabellon p " +
				// " where today between a.af_fecini and a.af_fecter " +
		        " where 10000*year(a.af_fecini)+100*month(a.af_fecini)+day(a.af_fecini) <= " + sdf.format(fecha) + " " + 
		        " and " + sdf.format(fecha) + " <= 10000*year(a.af_fecter)+100*month(a.af_fecter)+day(a.af_fecter) " +
				" and a.af_codigo = v.af_codigo " +
				" and v.pr_codigo = " + codPrestacion + 
				" and v.nf_nivel = 1" +
				" and p.af_codigo = a.af_codigo" +
				" and p.pa_pabellon = " + codPabellon;
		        
		        JdbcTemplate template = new JdbcTemplate();
		        template.setDataSource(dataSource);
		        QueryLogger.log(uw, sql);
		        int valor = template.queryForInt(sql);
		        return new Integer(valor);
				
			} catch (Exception e) {
				//e.printStackTrace();
				return new Integer(0);
			}

       }
       
       private Integer valorPrestacionFonasa(String codPrestacion, Date fecha, int codProfesional, UsuarioWeb uw){
       	
       	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
       	
       	String[] columnas = {"", "v.vf_valor1", "v.vf_valor2", "v.vf_valor3", "v.vf_valor4", "v.vf_valor5", "v.vf_valor" };
       	String columna = columnas[6];
       	try { columna = columnas[codProfesional]; } catch (Exception e) { }
        //llrint grupo = new Integer(codPrestacion).intValue() / 100000;
       	if (!EsCirugia(codPrestacion))
       	   columna = columnas[6];
        String sql = "" +
        " select " + columna + " " +
		" from bm_arafon a, bm_valfon v " +
		// " where today between a.af_fecini and a.af_fecter " +
        " where 10000*year(a.af_fecini)+100*month(a.af_fecini)+day(a.af_fecini) <= " + sdf.format(fecha) + " " + 
        " and " + sdf.format(fecha) + " <= 10000*year(a.af_fecter)+100*month(a.af_fecter)+day(a.af_fecter) " +
		" and a.af_codigo = v.af_codigo " +
		" and v.pr_codigo = " + codPrestacion + 
		" and v.nf_nivel = 1";
        
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(dataSource);
        QueryLogger.log(uw, sql);
        int valor = template.queryForInt(sql);
        return new Integer(valor);
       }
       
       private String beneficiarioEsAsegurado(String CMC, UsuarioWeb uw){

       	String[] cmcParte = TextUtil.dividirCMC(CMC);
    	
            String sql = "" +
            " select r.poliza as poliza" +
			" from asegurados a, reparseg r " +
			" where a.cod_repart = '" + cmcParte[0] + "'" +
			" and a.nro_impo = " + cmcParte[1] +
			" and a.nro_correl = " + cmcParte[2] +
			" and a.nro_poliza = r.nro_poliza " +
			" and today between a.fec_ini_cober and a.fec_ter_cober ";
            
            JdbcTemplate template = new JdbcTemplate();
            template.setDataSource(dataSource);
            QueryLogger.log(uw, sql);
            List lista = template.queryForList(sql);
            
            try {
            	Map mapa = (Map) lista.get(0);
            	return (String) mapa.get("poliza");
            } catch (Exception e) {
				return null;
			}

       }

       private int numPrestacionesUltimosDias(String CMC, int codigoPrestacion, int numeroDias, UsuarioWeb uw){

       	String SQL = "" +
			" select count(bi.bi_serial) as total" +
			" from bm_bono bo, bm_bonite bi " +
			" where bo.be_carne = '" + CMC + "'" +
			" and bi.pr_codigo = " + codigoPrestacion +
			" and bi.bo_serial = bo.bo_serial" + 
			" and bo.bo_fecemi >= today - " + numeroDias;
			
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			QueryLogger.log(uw, SQL);
			int total = template.queryForInt(SQL);
			
			return total;

       }
       
       public boolean prestadorEsPagoDirecto(String rutPrestador){
        JdbcTemplate t = new JdbcTemplate(dataSource);
        String rutSinDV = rutPrestador.trim().substring(0, rutPrestador.length() - 2);
        String query = "select count(*) from KEYWORD_DET where key_sist = 'BENMED' and key_word='PAGDIR' and key_id = '" + rutSinDV + "'";
        	   
        // Si la query encuentra el rut, es pago directo
        if (1 == t.queryForInt(query)) return true;
        else return false;

       }
       
       public boolean EsCirugia(String codigoPrestacion){
        JdbcTemplate t = new JdbcTemplate(dataSource);
        String query = "select count(*) from KEYWORD_DET where key_sist = 'BENMED' and key_word='CIRUJ' and key_id = "+ codigoPrestacion;
        	   
        // Si la query encuentra la prestacion, es cirugia
        if (1 == t.queryForInt(query)) return true;
        else return false;

       }
       
       public boolean prestadorEsArancelDiferenciado(int rutPrestador){
        JdbcTemplate t = new JdbcTemplate(dataSource);
        String query = "select count(*) from KEYWORD_DET where key_sist = 'BENMED' and key_word='ARADIF' and key_id = "+ rutPrestador;
        	   
        // Si la query encuentra la prestacion, es cirugia
        if (1 == t.queryForInt(query)) return true;
        else return false;

       }
       
       public boolean prestadorEsPagoEnAgencia(String rutPrestador){
        JdbcTemplate t = new JdbcTemplate(dataSource);
        String rutSinDV = rutPrestador.trim().substring(0, rutPrestador.length() - 2);
        String query = "select count(*) from KEYWORD_DET where key_sist = 'BENMED' and key_word='PAGAGE' and key_id = '" + rutSinDV + "'";
        	   
        // Si la query encuentra el rut, es pago directo
        if (1 == t.queryForInt(query)) return true;
        else return false;

       }
       /* ***************************************************************************************************** */


       
       
   	class PrestadoresPorCiudadYPrestacionGenericaMappingQuery extends MappingSqlQuery {
       	
		PrestadoresPorCiudadYPrestacionGenericaMappingQuery(DataSource ds, int codCiudad, int codPrestacionGenerica, UsuarioWeb uw){
			super();
			setDataSource(ds);

			String sql = "" +
	            " select unique q.acree_rut as acree_rut, q.acree_rsocial as acree_rsocial from bm_preben p,"+
	            //" select q.acree_rut, q.acree_rsocial from bm_preben p,"+
	            " acreedor q," +
	            " bm_convenio r," +
	            " bm_valcon v " +
	            " where q.acree_rut = p.pb_rut " +
	            "   and r.pb_codigo = q.acree_rut_aux" +
	            "   and ( p.dom_ciudad = " + codCiudad + " " +
				"    or " + codCiudad + " in " +
				"(select key_id" +
						"    from keyword_det" +
						"   where key_sist ='BENMED'" +
						"     and key_word ='PRECIU'" +
						"     and key_descr = q.acree_rut ))" +
	            "   and r.cv_codigo = v.cv_codigo " +
	            "   and trunc(v.pr_codigo/100000) = " + codPrestacionGenerica + " " +
			//"	and q.acree_rut_aux not in (61513003, 60505723)" +
				"	and q.acree_rut_aux not in" +
				" (select key_descr" +
				"    from keyword_det" +
				"   where key_sist ='BENMED'" +
				"     and key_word ='PRESTAD')" +
	            "     and today between cv_fecini and cv_fecter " +
				"   order by 2 ";
	            // Uso trunc(v.pr_codigo/100000) para eliminar los ultimos 5 digitos de la prestacion
			
			QueryLogger.log(uw, sql);
			setSql(sql);
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			try {
				String acreeRut = (String) rs.getObject("acree_rut");
				String acreeRazonSocial = (String) rs.getObject("acree_rsocial");
				return new String[]{ acreeRut, acreeRazonSocial};

			} catch (Exception e) {
				e.printStackTrace();
				return new String[]{"0","???"};
			}
		}
	}

   	class PrestadorPorRutMappingQuery extends MappingSqlQuery {


		public PrestadorPorRutMappingQuery(DataSource ds, String RUT, UsuarioWeb uw) {

			super();
			setDataSource(ds);
			
            String query = "" +
                    " select a.acree_rut, a.acree_rsocial, a.acree_rut_aux, b.dom_ciudad" +
                    " from acreedor a, bm_preben b" +
                    " where acree_rut_aux in " +
                    " (select pb_codigo from bm_convenio)" +
                    " and acree_rut = '" + RUT + "'" + 
					" and a.acree_rut = b.pb_rut";

            QueryLogger.log(uw, query);
            setSql(query);
            compile();

			
		}

		protected Object mapRow(ResultSet rs, int rownumber) throws SQLException {
			String[] mapaPrestadorDTO = new String[]{
				"rutAcreedor", "acree_rut",
				"razonSocial", "acree_rsocial",
				"rut","acree_rut_aux",
				"codCiudad", "dom_ciudad"
				};
			
			PrestadorDTO p = new PrestadorDTO();
			ReflectionFiller.fill(mapaPrestadorDTO, rs, p);
			return p;
		}
		
   	}
   	
   	class PrestadorPorRutAuxMappingQuery extends MappingSqlQuery {


		public PrestadorPorRutAuxMappingQuery(DataSource ds, String RUT, UsuarioWeb uw) {

			super();
			setDataSource(ds);
			
            String query = "" +
                    " select a.acree_rut, a.acree_rsocial, a.acree_rut_aux, b.dom_ciudad" +
                    " from acreedor a, bm_preben b" +
                    " where a.acree_rut_aux = " + RUT +
					" and a.acree_rut = b.pb_rut";
            
            QueryLogger.log(uw, query);
            setSql(query);
            compile();

			
		}

		protected Object mapRow(ResultSet rs, int rownumber) throws SQLException {
			String[] mapaPrestadorDTO = new String[]{
				"rutAcreedor", "acree_rut",
				"razonSocial", "acree_rsocial",
				"rut","acree_rut_aux",
				"codCiudad", "dom_ciudad"
				};
			
			PrestadorDTO p = new PrestadorDTO();
			ReflectionFiller.fill(mapaPrestadorDTO, rs, p);
			return p;
		}
		
   	}

   	class ValorPagoDirectoMappingQuery extends MappingSqlQuery {
   		

		public ValorPagoDirectoMappingQuery(String rutPrestador, String codPrestacion, Date fecha, UsuarioWeb uw) {

			SimpleDateFormat sdfIngles = new SimpleDateFormat("yyyyMMdd");
			
			String sql = "" +
				"select v.vc_valor, v.vc_lispre, v.cv_codigo, v.pr_codigo " +
				" from bm_valcon v, bm_convenio c, acreedor ac " +
				" where ac.acree_rut = '" + rutPrestador + "'" + 
				" and c.pb_codigo = ac.acree_rut_aux " +
				" and c.pb_codigo = ac.acree_rut_aux " +
				" and c.cv_codigo = v.cv_codigo " +
				" and v.pr_codigo = " + codPrestacion +
				// " and c.cv_codigo = 7889 " +
	            " and 10000*year(c.cv_fecini)+100*month(c.cv_fecini)+day(c.cv_fecini) <= " + sdfIngles.format(fecha) + " " + 
	            " and " + sdfIngles.format(fecha) + " <= 10000*year(c.cv_fecter)+100*month(c.cv_fecter)+day(c.cv_fecter) ";

            setDataSource(dataSource);
            QueryLogger.log(uw, sql);
            setSql(sql);
            compile();
            
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {

			Integer codConvenio = new Integer(0);
			Integer codPrestacion = new Integer(0);
			Float valorConvenio = new Float(0);
			Float valorListaPrecio = new Float(0);
			
			try{ codConvenio = (Integer)rs.getObject("cv_codigo"); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ codPrestacion = (Integer)rs.getObject("pr_codigo"); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ valorConvenio = new Float(((BigDecimal)rs.getObject("vc_valor")).floatValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ valorListaPrecio = new Float(((BigDecimal)rs.getObject("vc_lispre")).floatValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			return new Integer[]{ codConvenio, codPrestacion, new Integer(valorConvenio.intValue()), new Integer(valorListaPrecio.intValue()) };
			}
		}

   	
   	
   	class AporteDiprecaPorPrestacionYPrestadorMappingQuery extends MappingSqlQuery {
   		

		public AporteDiprecaPorPrestacionYPrestadorMappingQuery(String rutPrestador, String codPrestacion, Date fecha, int tipoPrestacion, String codContrato, int salaComun, UsuarioWeb uw) {

			SimpleDateFormat sdfIngles = new SimpleDateFormat("yyyyMMdd");
			
			// Agrego esta parte de la query solo para atenciones hospitalarias
			// tipoPrestacion == 2
			
			String whereHospitalaria = "";
			if (tipoPrestacion == 2){
				
				String penCom = (salaComun==0)?"C":"P";
				
				whereHospitalaria = "" +
	            "   and ap.cod_contrato = '" + codContrato + "'" + 
	            "   and ap.ap_pencom = '" + penCom + "'";
			}
			
            String sql = "" +
            " select ap.ap_factor, ap.dom_instit, ap.sp_grupo, dom_bascob, " +
			" ap.ap_porval, c.cv_codigo, p.pa_pabellon " +
            " from "+
            " acreedor ac," +
            " bm_aporte ap," +
            " bm_convenio c," +
            " bm_valcon v, " +
			" bm_prestacion p " +
            " where ac.acree_rut = '" + rutPrestador.trim() + "' " +
            "   and c.cv_codigo = ap.cv_codigo" +
            "   and c.pb_codigo = ap.pb_codigo" +
            "   and c.pb_codigo = ac.acree_rut_aux" +
            "   and c.cv_codigo = v.cv_codigo " +
            "   and v.pr_codigo = " + codPrestacion + " " +
			"	and p.pr_codigo = " + codPrestacion + 
            // "   and today between c.cv_fecini and c.cv_fecter" +
            // "   and '" + sdfIngles.format(fecha) + "' between c.cv_fecini and c.cv_fecter" + // Fecha de uso de la prestacion - 2006.04.19
            "	and 10000*year(c.cv_fecini)+100*month(c.cv_fecini)+day(c.cv_fecini) <= " + sdfIngles.format(fecha) + " " + 
            "	and " + sdfIngles.format(fecha) + " <= 10000*year(c.cv_fecter)+100*month(c.cv_fecter)+day(c.cv_fecter) " +
            "   and ap.dom_tippre = " + tipoPrestacion +
			
            //"   and ap.cod_contrato = '" + codContrato + "'" + 
            //"   and ap.ap_pencom = '" + salaComun + "'" +
			whereHospitalaria +
			
            // "   and ap.dom_tippre = 1 " +
            // "   and ap.ap_nroate = 1" +
            " order by ap.sp_grupo, ap.dom_instit";
            
            setDataSource(dataSource);
            QueryLogger.log(uw, sql);
            setSql(sql);
            compile();
            
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {

			Float apFactor = new Float(0);
			String domInstit = "";
			Integer spGrupo = new Integer(0);
			String domBascob = "";
			Float apPorval = new Float(0);
			Integer codigoConvenio = new Integer(0);
			Integer pabellon = new Integer(0);
			
			try{ apFactor = new Float(((BigDecimal)rs.getObject("ap_factor")).floatValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ domInstit = (String)rs.getObject("dom_instit"); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ spGrupo = new Integer(((Short)rs.getObject("sp_grupo")).intValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ domBascob = (String)rs.getObject("dom_bascob"); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ apPorval = new Float(((BigDecimal)rs.getObject("ap_porval")).floatValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ codigoConvenio = new Integer(((Integer)rs.getObject("cv_codigo")).intValue()); }
			catch(Exception e){ e.printStackTrace(); }			
			
			try{ pabellon = new Integer(((Integer)rs.getObject("pa_pabellon")).intValue()); }
			catch(Exception e){ 
				// e.printStackTrace();
			}			
			
			return new Object[]{apFactor, domInstit, spGrupo, domBascob, apPorval, codigoConvenio, pabellon };
			}
		}

   	class FactorAporteSeguroMappingQuery extends MappingSqlQuery {

		public FactorAporteSeguroMappingQuery(String rutPrestador, String codPrestacion, String poliza, int tipoPrestacion, UsuarioWeb uw){

			super();
			setDataSource(dataSource);

			String sql = "" +
            " select ap.as_factor, ap.dom_instit, ap.sp_grupo, dom_bascob, ap.as_porval" +
            " from "+
            " acreedor ac," +
            " bm_aporseg ap," +
            " bm_convenio c," +
            " bm_valcon v " +
            " where ap.se_nropol = '" + poliza.trim() + "'" +
            "   and ac.acree_rut = '" + rutPrestador.trim() + "' " +
            "   and ap.pb_codigo = ac.acree_rut_aux" +
            "   and ap.cv_codigo = c.cv_codigo " +
            "   and ap.dom_tippre = " + tipoPrestacion + 
			"   and today between c.cv_fecini and c.cv_fecter" +
			"   and c.cv_codigo= v.cv_codigo" +
            "   and v.pr_codigo = " + codPrestacion.trim() + "" +
            " order by ap.sp_grupo, ap.dom_instit";
			
			QueryLogger.log(uw, sql);
			setSql(sql);
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Float asFactor = new Float(0);
			String domInstit = "";
			Integer spGrupo = new Integer(0);
			String domBascob = "";
			Float asPorval = new Float(0);
			
			try { asFactor = new Float(((BigDecimal)rs.getObject("as_factor")).floatValue()); } catch (Exception e){ e.printStackTrace(); }
			try { domInstit = (String)rs.getObject("dom_instit"); } catch (Exception e){ e.printStackTrace(); }
			try { spGrupo = new Integer(((Short)rs.getObject("sp_grupo")).intValue()); } catch (Exception e){ e.printStackTrace(); }
			try { domBascob = (String)rs.getObject("dom_bascob"); } catch (Exception e){ e.printStackTrace(); }
			try { asPorval = new Float(((BigDecimal)rs.getObject("as_porval")).floatValue()); } catch (Exception e){ e.printStackTrace(); }
			
			return new Object[]{ asFactor, domInstit, spGrupo, domBascob, asPorval };
		}
   		

   	}
   	
   	class ReglasMappingQuery extends MappingSqlQuery {
   		
		public ReglasMappingQuery(int codPrestacion, UsuarioWeb uw) {

			super();
			setDataSource(dataSource);
			String sql = "select * from bmw_regla where cod_prestacion = " + codPrestacion;
			QueryLogger.log(uw, sql);
			setSql(sql);
			compile();
		}
   		
		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String[] mapaReglas = new String[]{
				"id", "regla_id",
				"codPrestacion", "cod_prestacion",
				"restriccion", "restriccion",
				"numero1", "numero1",
				"numero2", "numero2",
				"texto", "texto",
				"mensaje", "mensaje"
			};
			
			ReglaDTO r = new ReglaDTO();
			ReflectionFiller.fill(mapaReglas, rs, r);
			return r;
		}
   	}
   	
   	class PrestadoresPorCiudadYPrestacionMappingQuery extends MappingSqlQuery {
   		
		public PrestadoresPorCiudadYPrestacionMappingQuery(int codCiudad, int codPrestacion, UsuarioWeb uw, String rutDirecto) {
            String qrd ="";
            System.out.println(rutDirecto);
			if(rutDirecto != null){
				qrd="where q.acree_rut='"+rutDirecto+"'" + " and q.acree_rut= p.pb_rut";
			}
			else
			{
            	qrd="where q.acree_rut= p.pb_rut";	 
            }
             
			
        
			String sql = "" +
            " select unique q.acree_rut as acree_rut, q.acree_rsocial as acree_rsocial from bm_preben p,"+
            // " select q.acree_rut, q.acree_rsocial from bm_preben p,"+
            " acreedor q," +
            " bm_convenio r," +
            " bm_valcon v " + qrd +
            //" where q.acree_rut = p.pb_rut " +
            "   and r.pb_codigo = q.acree_rut_aux" +
            "   and ( p.dom_ciudad = " + codCiudad + " " +
			"    or " + codCiudad + " in (select key_id from keyword_det " +
			"                             where key_sist = 'BENMED' " +
			"                               and key_word = 'PRECIU' " +
			"                               and key_descr= q.acree_rut))" +
            "   and r.cv_codigo = v.cv_codigo " +
            "   and v.pr_codigo = " + codPrestacion + " " +
            "   and today between cv_fecini and cv_fecter " +

			// "	and q.acree_rut_aux not in (61513003, 60505723)" +
			// cambiado por:
			"	and q.acree_rut_aux not in" +
			" (select key_descr" +
			"    from keyword_det" +
			"   where key_sist ='BENMED'" +
			"     and key_word ='PRESTAD')" +

            "   order by q.acree_rut asc ";

            setDataSource(dataSource);
            QueryLogger.log(uw, sql);
            setSql(sql);
            compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			String rut = "";
			String rsocial = "";
			
			try { rut = (String)rs.getObject("acree_rut"); } catch (Exception ex){ }			
			try { rsocial = (String)rs.getObject("acree_rsocial"); } catch (Exception ex){ }
			
			return new String[]{ rut.trim(), rsocial.trim()};
		}
   	}

}

