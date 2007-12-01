/*
 * Created on 03-oct-2005
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.BonoDTO;
import bmweb.dto.BonoItemDTO;
import bmweb.dto.BonoWItemDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.servlets.DBServlet;
import bmweb.util.Constantes;
import bmweb.util.QueryLogger;
import bmweb.util.QueryUtil;
import bmweb.util.ReflectionFiller;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 * 
 * 2006 02 11
 * 
 * Incorporado ReflectionFiller en las consultas por DAOs
 * 
 * Mucho esfuerzo, se agregan metodos setter a los BonoDTO y BonoItemDTO
 * para que reciban argumentos BigDecimal, java.sql.Date, etc
 * 
 * 2006 02 09
 * 
 * Ahora esta clase solo una SQL Mappers de Spring,
 * Todos los metodos reciben un UsuarioWeb y hacen LOG del SQL para auditoria
 * 
 */
public class BonoDao implements IBonoDao {

	private DataSource dataSource;

	private static boolean init = false;

	private static int _folio = 0;

	private List prestacionesEncontradas;

	private List listaPrestadores;

	private IPrestadoresDao prestadoresDao = null;
	private IBeneficiariosDao beneficiariosDao = null;

	// "propiedad", "columna"
	private String[] mapaColumnasBono = new String[]{
		"id", "bo_serial",
		"tipoBono", "dom_tipbon",
		"folio", "bo_folio",
		"rutPrestador", "pb_rut",
		"carneBeneficiario", "be_carne",
		"fechaEmision", "bo_fecemi",
		"idCiudad", "dom_ciudad",
		"codigoHabilitado", "ha_codigo",
		"rutImponente", "bo_rutimp",
		"numeroAtencion", "bo_nroate",
		"idFacturaPrestador", "dp_serial",
		"idLiquidacion", "bm_liquida",
		"codDerivacionCentroPrivado", "dom_cauext",
		"codPrestadorDerivado", "dom_deriva",
		"codTipoPrestador", "dp_origen",
		"codEstadoBono", "dom_estbon"
		};
	
	private String[] mapaItemsBonoValoradoWeb = new String[]{
		"id", "bi_serial",
		"idBono", "bo_serial",
		"idFacturaPrestador", "dp_serial",
		"tipoBono", "dom_tipbon",
		"codPrestacion", "pr_codigo",
		"codTipoPrestacion", "dom_tippre",
		"fechaEfectivaAtencionMedica", "bi_fecate",
		"valorCobradoPrestador", "vc_valor",
		"valorAporteDipreca", "bi_apodip",
		"valorAporteSeguro", "bi_aposeg",
		"codigoPabellon", "pa_pabellon",
		"cantidadAtenciones", "bi_cantidad",
		"codEstadoAtencion", "dom_estliq",
		"valorConvenidoPrestacion", "bi_valcon",
		"valorEnBono", "bi_valbon",
		"codConvenio", "cv_codigo",
		"cuentaContableAsoc", "pr_ctapre",
		"cuentaContableAsocPabellon", "pr_ctapab",
		"valorPabellon", "bi_valpab",
		"valorCopago", "bi_copago",
		"rutCompaniaSeguro", "bi_rutseg",
		"fechaInicioHospital", "bi_fedeho",
		"fechaTerminoHospital", "bi_fehaho",
		"aporteDiprecaPabellon", "bi_apopab",
		"incluyePabellon", "bi_incpab",
		"pensionadoOSalaComun", "bi_pencom",
		"tipoPaciente", "bi_paciente",
		"codProfesional", "bi_quien",
		"codRazonesNoAporte", "dom_ranodi",
	};

	// setters para IOC (Spring)
	public void setPrestadoresDao(IPrestadoresDao ip) { this.prestadoresDao = ip; }
	public void setBeneficiariosDao(IBeneficiariosDao ib) { this.beneficiariosDao = ib; }

	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	public BonoDao() {
	}

	public synchronized int getFolio() {

		// Si esta iniciado ok, incremento y retorno el valor
		if (init) {
			_folio += DBServlet.getTotalServers();
			return _folio;
		}

		try {
			/*
			 * max = 125
			 * totalservers = 10
			 * servernumber = 3
			 * 
			 * 10 * int (125 / 10) = 120
			 * 120 + totalservers = 130
			 * folios = 133, 143, 153 ... 120 + 10 + (10*n)
			 */
			
			JdbcTemplate jt = new JdbcTemplate(dataSource);
			int max = jt.queryForInt("select max(bo_folio) from bm_bono where dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'");
			_folio = DBServlet.getTotalServers()*((int)(max*1.0/DBServlet.getTotalServers())) 
					 + DBServlet.getTotalServers()
					 + DBServlet.getServerNumber();
			init = true;
			return _folio;
		} catch (Exception e) {
			e.printStackTrace();
			init = true;
			_folio = DBServlet.getServerNumber();
			return _folio;
		}

	}

	public BonoDTO bonoWebPorSerial(int serial, UsuarioWeb uw) {

		// Creo un Mapa con parametros, coloco el serial como parametro y hago
		// la query
		HashMap params = new HashMap();
		params.put("serial", "" + serial);
		BonoListadoMappingQuery listado = new BonoListadoMappingQuery(
				dataSource, uw, params);
		List lista = listado.execute();

		if (lista != null && lista.size() > 0) {
			return (BonoDTO) lista.get(0);
		} else {
			return null;
		}

	}

	public synchronized BonoDTO bonoWebPorFolio(int folio, UsuarioWeb uw) {

		// Creo un Mapa con parametros, coloco el serial como parametro y hago
		// la query
		HashMap params = new HashMap();
		params.put("opfolio", "eq");
		params.put("folio", "" + folio);
		BonoListadoMappingQuery listado = new BonoListadoMappingQuery(
				dataSource, uw, params);
		List lista = listado.execute();

		if (lista != null && lista.size() > 0) {
			BonoDTO bono = (BonoDTO) lista.get(0);
			
			// Aqui le estaba pasando el getFolio y yo lo cambie a getId para corregir.LLR//
			List listaItems = getDetalleBonoValoradoWeb(bono.getId().intValue(), uw);
			bono.setItems(listaItems);
			return bono;
		} else {
			return null;
		}

	}

	public List getBonos(Map params, UsuarioWeb uw) {
		try {

			BonoListadoMappingQuery listado = new BonoListadoMappingQuery(
					dataSource, uw, params);
			List filas = listado.execute();

			int inicio = 0;
			try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }

			List resultado = filas.subList(inicio, filas.size());
			return resultado;

		} catch (Exception e) {
			return new ArrayList();
		}
	}

	public BonoDTO guardarBonoWeb(Map params, UsuarioWeb uw) {

		try {
			JdbcTemplate template = new JdbcTemplate(dataSource);

			int nuevoFolio = getFolio();
			
			String insertBono;
			// if ("22".equals(uw.getNivel())) {
			
			insertBono = "insert into bm_bono ("
				+ "bo_folio, be_carne, " + "dom_ciudad, dom_tipbon, "
				+ "pb_rut, bo_fecemi, ha_codigo) values (" + nuevoFolio
				+ ", '" + 
				TextUtil.filtrar((String) params.get("cmc")) 
				+ "', " + 
				TextUtil.filtrar((String) params.get("ciudad"))
				+ 
				", '" + BonoDTO.TIPOBONO_WEB + "', " + "'" +
				TextUtil.filtrar((String) params.get("idPrestador"))
				+ "', TODAY, "
				+ uw.getRutEmisor()
				+ ")";
			/*
			} else {
				insertBono = "insert into bm_bono ("
					+ "bo_folio, be_carne, " + "dom_ciudad, dom_tipbon, "
					+ "pb_rut, bo_fecemi) values (" + nuevoFolio
					+ ", '" + (String) params.get("cmc") + "', "
					+ (String) params.get("ciudad") + ", '"
					+ BonoDTO.TIPOBONO_WEB + "', " + "'"
					+ (String) params.get("idPrestador") + "', TODAY)";
			}
			*/

			QueryLogger.log(uw, insertBono);

			template.update(insertBono); // bonoDTO

			// Busco el bono recien guardado para obtener su serial
			BonoDTO nuevoBono = bonoWebPorFolio(nuevoFolio, uw);

			String insertBonoWItem = "insert into bmw_bonite (bo_serial, wi_codigo) values ("
					+ nuevoBono.getId()
					+ ", "
					+ 
					TextUtil.filtrar((String) params.get("prestacionGenerica"))
					+ ")";

			QueryLogger.log(uw, insertBonoWItem);

			template.update(insertBonoWItem); // bonoWItem

			return nuevoBono;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public BonoDTO guardarBonoValoradoWeb(Map params, UsuarioWeb uw) {
		
		/*
		HibernateTemplate ht = getHibernateTemplate();
		BonoDTO resultado = (BonoDTO) ht
				.execute(new GuardarBonoValoradoWebHibernateCallback(params));
		return resultado;
		*/
		
		GuardarBonoValoradoWeb guardador = new GuardarBonoValoradoWeb();
		BonoDTO resultado = guardador.execute(dataSource, params, uw);
		return resultado;
		
	}
	
	public Integer ValorConvenioPabellon(Integer convenio,Integer pabellon){
		String palor = "0";
		if (pabellon != null){
           JdbcTemplate t = new JdbcTemplate(dataSource);
           String query = "select pc_valor from bm_pabcon where cv_codigo = "+ convenio + " and pa_serial = 1 and pa_pabellon = "+ pabellon;
        	   
           // Si la query encuentra la prestacion, es cirugia
           try {
           	palor = String.valueOf(t.queryForInt(query));	
           } catch (Exception e) {
			palor = "0";
		}
           

		}
        return (new Integer(palor));
        
	}
	
	public List getDetalleBonoValoradoWeb(int idBono, UsuarioWeb uw) {
		try {
			/*
			 * HibernateTemplate ht = getHibernateTemplate(); List lista =
			 * (List) ht.execute( new ItemsBonoValoradoWeb(idBono) );
			 */

			List resultado = new ArrayList();

			ItemsBonoValoradoWebMappingQuery buscaItems = new ItemsBonoValoradoWebMappingQuery(
					dataSource, uw, idBono);

			List lista = buscaItems.execute();

			// Recorro la lista para formar las tuplas
			/*
			 * String codigo = fila[0]; String nombre = fila[1]; String valor =
			 * fila[2]; String copagoDipreca = fila[3]; String copagoSeguro =
			 * fila[4]; String copago = fila[5];
			 */
			for (int i = 0; lista != null && i < lista.size(); i++) {
				BonoItemDTO item = (BonoItemDTO) lista.get(i);

				String codigo = item.getCodPrestacion().toString();
				String[] porCodigo = (String[]) (getPrestacionesPorCodigo(codigo, new Date(), null, uw).get(0));
				String nombre = porCodigo[1];
				
				String valor = "0";
				String copagoDipreca = "0";
				String copagoSeguro = "0";
				String copago = "0";
			 	String aportePabellon ="0"; 
				String llrvalorPabellon = "0";
				
			 	try {aportePabellon = item.getAporteDiprecaPabellon().intValue()+""; }
			 	catch (Exception e){aportePabellon = "0"; }
				try { llrvalorPabellon =item.getValorPabellon().intValue()+""; 	}
				catch (Exception e){ llrvalorPabellon = "0"; }
				
				
				try { valor = ((item.getValorConvenidoPrestacion().intValue()*item.getCantidadAtenciones().intValue())+ Integer.valueOf(llrvalorPabellon).intValue())+ "" ;} 
				catch (Exception e){e.printStackTrace();}
				
				try { copagoDipreca = (item.getValorAporteDipreca().intValue() + Integer.valueOf(aportePabellon).intValue())+ "";}
				catch (Exception e){e.printStackTrace();}
				
				try { copagoSeguro = item.getValorAporteSeguro().intValue() + "";}
				catch (Exception e){e.printStackTrace();}
				
				try { copago = item.getValorCopago().intValue() + "";}
				catch (Exception e){e.printStackTrace();}

				String[] fila = new String[] { codigo, nombre, valor, copagoDipreca, copagoSeguro, copago };
				resultado.add(fila);
			}

			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	public List getDetalleBonoWeb(int folio, UsuarioWeb uw) {
		try {
			ItemsBonoWebMappingQuery buscaItems = new ItemsBonoWebMappingQuery(
					dataSource, uw, folio);
			List lista = buscaItems.execute();
			return lista;
		} catch (Exception e) {
			return new ArrayList();
		}
	}

	public List buscarPrestaciones(Integer familia, String nombre, Date fecha, String rutPrestador, UsuarioWeb uw) {

		int minimo = 101001;
		int maximo = 9999999;
		
		// Solo la familia '2' - hospitalizacion 
		// admite buscar prestaciones fuera de esa familia
		if (2 != familia.intValue()){
			minimo = familia.intValue() * 100000;
			maximo = familia.intValue() * 100000 + 99999;
		}
		
		PrestacionesMappingQuery buscadorPrestaciones = new PrestacionesMappingQuery(dataSource, uw, new Integer(minimo), new Integer(maximo), nombre, fecha, rutPrestador);
		List resultado = buscadorPrestaciones.execute();
		return resultado;

	}

	public List getPrestacionesPorCodigo(String codigo, Date fecha, String rutPrestador, UsuarioWeb uw) {

		PrestacionesMappingQuery buscadorPrestaciones = new PrestacionesMappingQuery(dataSource, uw, new Integer(codigo), new Integer(codigo), null, fecha, rutPrestador);
		List resultado = buscadorPrestaciones.execute();
		return resultado;

	}

	public List getPrestacionesPorNombre(String nombre, Date fecha, String rutPrestador, UsuarioWeb uw) {

		if (nombre == null || nombre.length() == 0) {
			return new ArrayList();
		}

		PrestacionesMappingQuery buscadorPrestaciones = new PrestacionesMappingQuery(dataSource, uw, null, null, nombre, fecha, rutPrestador);
		List resultado = buscadorPrestaciones.execute();
		return resultado;

	}

	public boolean guardarDetalle(BonoDTO bono, List listaItems, UsuarioWeb uw){
		
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(dataSource);
		
		// TODO 20060424 Agregar transaccionalidad aqui
		
		//SimpleDateFormat sdf = new SimpleDateFormat("(MM,dd,yyyy)");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		for (int i=0; listaItems != null && i<listaItems.size(); i++){
			BonoItemDTO item = (BonoItemDTO) listaItems.get(i);
			
			// busco el convenio para el prestador y prestacion
			String rutPrestador = bono.getRutPrestador();
			Integer convenio = prestadoresDao.convenioPorPrestadorYPrestacion(rutPrestador, item.getCodPrestacion().toString(), uw);
			Integer valpab   = ValorConvenioPabellon(convenio,item.getCodigoPabellon());           
			// busco la ctaPre y ctaGas para la prestacion
			int codPrestacion = item.getCodPrestacion().intValue();
			CuentaPresupuestoCuentaGastoMappingQuery ctaQuery 
				= new CuentaPresupuestoCuentaGastoMappingQuery(codPrestacion);
			
			List listaCuentas = ctaQuery.execute();
			String[] cuentas = (String[]) listaCuentas.get(0);
			String cuentaPre = cuentas[0];
			String cuentaGas = cuentas[1];
			
			int rutCiaSeguros = getRutCiaSeguros(bono.getCarneBeneficiario(), item.getFechaEfectivaAtencionMedica());
			//aqui
			String insertItem = "" +
			" insert into bm_bonite (" +
			" bo_serial, bi_apodip, " +
			" bi_aposeg, bi_copago, " +
			" bi_valcon, " +
			" dom_tipbon, " +
			" pr_codigo, cv_codigo, " +
			" dom_tippre, bi_fecate, " +
			" vc_valor, " + 
			" pr_ctapre, pr_ctapab, dom_estliq, " +
			" bi_incpab, bi_pencom, bi_quien, bi_cantidad, " +
			" bi_rutseg, " +
			" pa_pabellon, bi_apopab, bi_valpab " +
			" ) values (" +
			bono.getId() + "," + item.getValorAporteDipreca() + ", " + 
			item.getValorAporteSeguro() + "," + item.getValorCopago() + "," +
			item.getValorConvenidoPrestacion().intValue() + ", " + 
			"'" + BonoDTO.TIPOBONO_WEB + "', " +
			item.getCodPrestacion() + "," + convenio + ", " +
			item.getCodTipoPrestacion() + ", ? , " + 
			// item.getValorConvenidoPrestacion() + "," + item.getCodigoPabellon() + ", " + 
			// "'" + item.getCuentaContableAsoc() + "', '" + item.getCuentaContableAsocPabellon() + "', " +
			// item.getAporteDiprecaPabellon() +
			(item.getValorConvenidoPrestacion().intValue() * item.getCantidadAtenciones().intValue()) + "," +
			"'" + cuentaPre + "', '" + cuentaGas + "', '00', " +
			"'" + item.getIncluyePabellon() + "', '" + item.getPensionadoOSalaComun() + "','" + item.getCodProfesional() + "', " +
			item.getCantidadAtenciones() + ", " + rutCiaSeguros + ", " +
			item.getCodigoPabellon() + ", " + item.getAporteDiprecaPabellon() + ", " +
			valpab +
			")";

			QueryLogger.log(uw, insertItem);
			
			template.update(insertItem, new Object[]{ item.getFechaEfectivaAtencionMedica() } );

		}
		
		
		
		return true;
	}
	public boolean guardardetalleCarMas(BonoDTO bono, List listaItems, UsuarioWeb uw){
		
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(dataSource);
		
		// TODO 20060424 Agregar transaccionalidad aqui
		
		//SimpleDateFormat sdf = new SimpleDateFormat("(MM,dd,yyyy)");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		for (int i=0; listaItems != null && i<listaItems.size(); i++){
			BonoItemDTO item = (BonoItemDTO) listaItems.get(i);
			
			// busco el convenio para el prestador y prestacion
			String rutPrestador = bono.getRutPrestador();
			Integer convenio = item.getCodConvenio();
			Integer valpab   = ValorConvenioPabellon(convenio,item.getCodigoPabellon());           
			// busco la ctaPre y ctaGas para la prestacion
			int codPrestacion = item.getCodPrestacion().intValue();
			CuentaPresupuestoCuentaGastoMappingQuery ctaQuery 
				= new CuentaPresupuestoCuentaGastoMappingQuery(codPrestacion);
			
			List listaCuentas = ctaQuery.execute();
			String[] cuentas = (String[]) listaCuentas.get(0);
			String cuentaPre = cuentas[0];
			String cuentaGas = cuentas[1];
			
			int rutCiaSeguros = getRutCiaSeguros(bono.getCarneBeneficiario(), item.getFechaEfectivaAtencionMedica());
			//aqui
			String insertItem = "" +
			" insert into bm_bonite (" +
			" bo_serial, bi_apodip, " +
			" bi_aposeg, bi_copago, " +
			" bi_valcon, " +
			" dom_tipbon, " +
			" pr_codigo, cv_codigo, " +
			" dom_tippre, bi_fecate, " +
			" vc_valor, " + 
			" pr_ctapre, pr_ctapab, dom_estliq, " +
			" bi_incpab, bi_pencom, bi_quien, bi_cantidad, " +
			" bi_rutseg, " +
			" pa_pabellon, bi_apopab, bi_valpab " +
			" ) values (" +
			bono.getId() + "," + item.getValorAporteDipreca() + ", " + 
			item.getValorAporteSeguro() + "," + item.getValorCopago() + "," +
			item.getValorConvenidoPrestacion().intValue() + ", " + 
			"'" + BonoDTO.TIPOBONO_WEB + "', " +
			item.getCodPrestacion() + "," + convenio + ", " +
			item.getCodTipoPrestacion() + ", ? , " + 
			(item.getValorConvenidoPrestacion().intValue() * item.getCantidadAtenciones().intValue()) + "," +
			"'" + cuentaPre + "', '" + cuentaGas + "', '00', " +
			"'" + item.getIncluyePabellon() + "', '" + item.getPensionadoOSalaComun() + "','" + item.getCodProfesional() + "', " +
			item.getCantidadAtenciones() + ", " + rutCiaSeguros + ", " +
			item.getCodigoPabellon() + ", " + item.getAporteDiprecaPabellon() + ", " +
			valpab +
			")";

			QueryLogger.log(uw, insertItem);
			
			template.update(insertItem, new Object[]{ item.getFechaEfectivaAtencionMedica() } );

		}
		
		
		
		return true;
	}

	//llr hoy
	public boolean anularBonoValorado(Map params, UsuarioWeb uw){

		try {
			int hay = 0;
			Integer folio = new Integer((String)params.get("folio"));
						
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			String query = "select count(*) " + 
			                " from bm_docpago where " +
			"dp_serial in (select dp_serial from bm_bono "+ 
			               "where dom_tipbon = '"+BonoDTO.TIPOBONO_WEB+"' "+
			                 "and bo_folio = "+folio+" )";
			hay = template.queryForInt(query);
			if (hay == 0) 
			{
			  try
			  {
			    query = "update bm_bono set dom_estbon = 'A' where " +
				    	"dom_tipbon = '"+BonoDTO.TIPOBONO_WEB+"' "+
					    "and bo_folio = "+folio+" "; 
					//agregar las validaciones que faltan llr;
			
			    QueryLogger.log(uw, query);
			
			    template.execute(query);
			    return true;
            			
		      } catch (Exception e) {
			    e.printStackTrace();
			    return false;
		        }
			}else return false;
			
		}catch (Exception e) {
			  e.printStackTrace();
			  return false;
		 }
		
	}
    //	fin llr hoy
	
	public int getValorTotalBono(int bonoSerial, String rutPrestador,UsuarioWeb uw){
		String query ="";
		try {
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			if ( prestadoresDao.prestadorEsPagoDirecto(rutPrestador.trim()))
			{
			     query = query +
					" select sum (nvl(bi_apodip,0) + nvl(bi_apopab,0)" +
					" + nvl(bi_aposeg,0)) " +
					" from bm_bonite where bo_serial = " + bonoSerial;
			}else{
				 query = query +
				" select sum (nvl(bi_apodip,0) + nvl(bi_apopab,0)" +
				" + nvl(bi_aposeg,0) + nvl(bi_copago,0)) " +
				" from bm_bonite where bo_serial = " + bonoSerial;
			}
			
			QueryLogger.log(uw, query);
			
			int valor = template.queryForInt(query);
			return valor;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}

	public boolean puedeEmitirBonosPorNivel(UsuarioWeb uw){
		
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
			return false;
		} else {
			return true;
		}

	}
	public boolean esBonoValorado(int folio){
		
		// 20060724 - Revisa si el Bono es Valorado
		
		
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		String queryBonoValorado = "" +
				" select count(*) " +
				" from bm_bono b, bm_bonite i " +
				" where b.bo_folio = " + folio +
				" and b.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB+"'" + 
				" and i.bo_serial = b.bo_serial";
		
		if (0 == jt.queryForInt(queryBonoValorado)){
			return false;
		} else {
			return true;
		}

	}
	
	private int getRutCiaSeguros(String CMC, Date fecha){
		
		try {
			String[] cmcParte = TextUtil.dividirCMC(CMC);
			SimpleDateFormat sdf = new SimpleDateFormat("(MM,dd,yyyy)");

			Date fechaTermino = (Date)fecha.clone();
			fechaTermino.setMonth( fechaTermino.getMonth() - 2); // 2 meses de gracia
			
			String query = "" +
					" select c.rut_ciaseg from ciaseg c, reparseg r, asegurados a" +
					" where a.cod_repart = '" + cmcParte[0] + "'" +
					" and a.nro_impo = " + cmcParte[1] +
					" and a.nro_correl = " + cmcParte[2] +
					" and a.fec_ini_cober <= mdy" + sdf.format(fecha) +
					" and a.fec_ter_cober >= mdy" + sdf.format(fechaTermino) + 
					" and a.nro_poliza = r.nro_poliza" +
					" and c.cod_ciaseg = r.cod_ciaseg";
			
			JdbcTemplate jt = new JdbcTemplate(dataSource);
			int rut = jt.queryForInt(query);
			return rut;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	/** * CLASES INTERIORES ** */

	class BonoListadoMappingQuery extends MappingSqlQuery {

		int inicio = 0;
		int dpp = Constantes.DATOS_POR_PAGINA;
		int maxResults = Constantes.DATOS_POR_PAGINA + 1;
		
		public BonoListadoMappingQuery(DataSource ds, UsuarioWeb uw, Map params) {
			super();
			setDataSource(ds);

			// Si me indican el inicio del listado, lo coloco
			if (params.containsKey("inicio")) {
				try {
					inicio = Integer.parseInt((String) params.get("inicio"));
				} catch (Exception e) {
				}
			}

			List listaWhere = new ArrayList(); // para guardar las condiciones del filtro
			
			// Parche - Enviar "VALORADOS" como parametro para buscar entre los
			// bonos valorados
			
			String queryString;
			
			if (params.containsKey("VALORADOS")){
				
				queryString = "select first " + (inicio + maxResults) 
							+ " distinct a.* from bm_bono a, bm_bonite b ";
				
				listaWhere.add("a.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'");
	            listaWhere.add("b.bo_serial=a.bo_serial");
	            listaWhere.add("(a.dom_estbon <> 'A' or a.dom_estbon is null)");
	            
	            // Restriccion del listado de bonos para todos los usuarios excepto 'admin'
			    // si el nivel es distinto de 23 (prestadores)
			    if (!"23".equals(uw.getNivel())&& !"24".equals(uw.getNivel())){
			    	
	            	// solo veo los bonos que he emitido yo salvo para el admin
				    if (!"1".equals(uw.getRutEmisor())){
		            	listaWhere.add("a.ha_codigo = " + uw.getRutEmisor());
		            }
			    	
			    }
			    
				
			} else if (params.containsKey("NOVALORADOS")) {

				queryString = "select first " + (inicio + maxResults) 
							+ " distinct a.* from bm_bono a, bmw_bonite b ";
	
				listaWhere.add("a.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'");
			    listaWhere.add("b.bo_serial=a.bo_serial");
	            listaWhere.add("(a.dom_estbon <> 'A' or a.dom_estbon is null)");
			    // si el nivel es distinto de 23 (prestadores)
			    if (!"23".equals(uw.getNivel()) && !"24".equals(uw.getNivel()) ){
			    	
	            	// solo veo los bonos que he emitido yo salvo para el admin
				    if (!"1".equals(uw.getRutEmisor())){
		            	listaWhere.add("a.ha_codigo = " + uw.getRutEmisor());
		            }
			    	
			    }
			    
			    
			    // bonos que tienen items en bmw_bonite y TAMBIEN en bm_bonite no deben aparecer -- 20060412
			    listaWhere.add("b.bo_serial = a.bo_serial");
			    listaWhere.add("a.bo_serial not in ( select i.bo_serial from bm_bonite i, bm_bono h where i.bo_serial = h.bo_serial and h.dom_tipbon ='" + BonoDTO.TIPOBONO_WEB +"'"+")");
			} else {
				
				// query para bonos no valorados (abiertos)
				queryString = "select first " + (inicio + maxResults) + " * from bm_bono ";
				listaWhere.add("dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'");
				
	            if (!"1".equals(uw.getRutEmisor())){
	            	// solo veo los bonos que he emitido yo 
	            	// llr hoy
	            	if (!uw.getNivel().equals("23") && !uw.getNivel().equals("24")&& !uw.getNivel().equals("25")){
	            	listaWhere.add("bm_bono.ha_codigo = " + uw.getRutEmisor());
	            	}
	            }

			}


            
			// Busqueda por serial
			if (params.containsKey("serial")) {
				int serial = Integer.parseInt((String) params.get("serial"));
				listaWhere.add("bo_serial = " + serial);
			}

			// Manejo del filtro por folio
			if (params.containsKey("opfolio") && params.containsKey("folio")) {
				try {
					int folio = Integer.parseInt((String) params.get("folio"));
					if ("le".equals(params.get("opfolio"))) {
						listaWhere.add("bo_folio <= " + folio);
					}

					if ("eq".equals(params.get("opfolio"))) {
						listaWhere.add("bo_folio = " + folio);
					}

					if ("gt".equals(params.get("opfolio"))) {
						listaWhere.add("bo_folio >= " + folio);
					}

				} catch (Exception ex) {
				}
			}

			// Manejo del filtro por fecha
			if (params.get("opfecha") != null ) {
				try {
					// Usando funciones nativas de informix para fechas: day,
					// month, year
					String fechaDesde = (String) params.get("fechaDesde");
					String fechaHasta = (String) params.get("fechaHasta");
					
					String dd1 = fechaDesde.substring(0, 2);
					String mm1 = fechaDesde.substring(3, 5);
					String yyyy1 = fechaDesde.substring(6, 10);

					String dd2 = fechaHasta.substring(0, 2);
					String mm2 = fechaHasta.substring(3, 5);
					String yyyy2 = fechaHasta.substring(6, 10);

					if ("entre".equals(params.get("opfecha"))) {
						listaWhere
						.add("10000*year(bo_fecemi)+100*month(bo_fecemi)+day(bo_fecemi) <= "
										+ yyyy2 + mm2 + dd2 + " ");
						listaWhere
						.add("10000*year(bo_fecemi)+100*month(bo_fecemi)+day(bo_fecemi) >= "
										+ yyyy1 + mm1 + dd1 + " ");
					}

				} catch (Exception ex) {
				}
			}

			// Manejo del filtro por tipo de Bono
			if (params.get("paramTipoBono") != null
					&& !"".equals(params.get("paramTipoBono"))) {
				try {
					listaWhere.add("a.dom_tipbon = '" + params.get("paramTipoBono") + "'");
				} catch (Exception ex) {
				}
			}
			
			// Manejo del codigo del emisor
			
			
			if (params.get("opemisor") != null && !"".equals(params.get("opemisor"))) {
			   try {
			    int emisor = Integer.parseInt((String)params.get("emisor"));
			    listaWhere.add("a.ha_codigo = " + emisor);
			   } catch (Exception ex) {
			     }
			}

			// si estoy haciendo un listado, debo filtrar algunos bonos
			if (params.containsKey("VALORADOS") || params.containsKey("NOVALORADOS")){

				// si el usuario esta en el codigo 22 (habilitados)
				// solo puedo ver los que han sido emitidos por el mismo habilitado
	
				if (uw.getNivel().equals("22")){
					listaWhere.add("ha_codigo = " + uw.getRutEmisor());
				}
				
				// si el usuario esta en el codigo 23 (prestadores)
				// solo puedo ver los que han sido emitidos por el mismo prestador
	
				if (uw.getNivel().equals("23") || uw.getNivel().equals("24")){
					listaWhere.add("pb_rut = '" + uw.getRutEmisor() + "-" + TextUtil.getDigitoVerificador(Integer.parseInt(uw.getRutEmisor())).toUpperCase() + "'" );
				}
			}
			
			// filtro por rut del prestador - 2006 07 05
			if (params.get("oprutprestador") != null && !"".equals(params.get("oprutprestador"))) {
			   try {
				   String rutPrestador = (String)params.get("rutprestador");
				   rutPrestador = TextUtil.filtrar(rutPrestador); // evito sql inyection
				   listaWhere.add("a.pb_rut = '" + rutPrestador + "'");
			   } catch (Exception ex) { ex.printStackTrace(); }
			}

			// filtro por cmc del beneficiario
			if (params.get("opcmcbeneficiario") != null && !"".equals(params.get("opcmcbeneficiario"))) {
			   try {
				   String cmcBeneficiario = (String)params.get("cmcbeneficiario");
				   cmcBeneficiario = TextUtil.filtrar(cmcBeneficiario); // evito sql inyection
				   listaWhere.add("a.be_carne = '" + cmcBeneficiario + "'");
			   } catch (Exception ex) { ex.printStackTrace(); }
			}

			// Hago la query final
			queryString = queryString + QueryUtil.getWhere(listaWhere);// + "order by {bono.folio} asc";

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, queryString);

			super.setSql(queryString);
			compile();

		}

		protected Object mapRow(ResultSet rs, int rownumber)
				throws SQLException {

			// Mapeo la clase bono completa
			BonoDTO bono = new BonoDTO();

			// Lleno el bono con los datos del resultSet
			ReflectionFiller.fill(mapaColumnasBono, rs, bono);

			return bono;
		}

	}


	class GuardarBonoValoradoWeb  {

		public BonoDTO execute(DataSource ds, Map params, UsuarioWeb uw) {

			try {
				ArrayList bonoItems = new ArrayList();

				int folio = getFolio();

				String cmc = (String) params.get("cmc");
				String rutPrestador = (String) params.get("rutPrestador");
				
				RolbeneDTO rolbene = beneficiariosDao.leeRolbene(cmc, uw);
				String codContrato = rolbene.getContrato();

				// si es un habilitado, creo el bono con el codigo de habilitado incluido
				//if ("22".equals(uw.getNivel())){
				
				String insertBono = ""
					+ " insert into bm_bono ("
					+ " bo_folio, be_carne, dom_ciudad, dom_tipbon, "
					+ " pb_rut, bo_fecemi, ha_codigo ) values (" + folio
					+ ", '" + cmc + "', " + 
					TextUtil.filtrar((String) params.get("ciudad"))
					+ ",'" + BonoDTO.TIPOBONO_WEB + "'," + "'"
					+ rutPrestador + "', TODAY, "
					+ uw.getRutEmisor()
					+ ")";
				
				/*
				} else {
					insertBono = ""
						+ " insert into bm_bono (bo_folio, be_carne, dom_ciudad, dom_tipbon, "
						+ " pb_rut, bo_fecemi ) values (" + folio
						+ ", '" + cmc + "', " + (String) params.get("ciudad")
						+ ",'" + BonoDTO.TIPOBONO_WEB + "'," + "'"
						+ rutPrestador + "', TODAY )";
				}
				*/
				
				JdbcTemplate template = new JdbcTemplate();
				template.setDataSource(ds);
				QueryLogger.log(uw, insertBono);
				template.update(insertBono);

				// Recupero el serial del bono

				BonoDTO bono = bonoWebPorFolio(folio, uw);

				// guardar el detalle (prestaciones valoradas)
				// Asumo un maximo de 100 prestaciones por bono

				for (int i = 1; i < 100; i++) {
					String prestacion = (String) params.get("prestacion." + i);
					if (prestacion == null)
						break;

					String strCantidad = (String) params.get("cantidad." + i);
					int cantidad = Integer.parseInt(strCantidad);

					int[] aportes = null;
					for (int j = 1; j <= cantidad; j++) {
						BonoItemDTO item = new BonoItemDTO();
						item.setCodPrestacion(new Integer(prestacion));
						item.setIdBono(bono.getId());
						// item.setBonoDTO(bono);

						// agrego los valores y copago para el CMC

						List listaPrestaciones = new ArrayList();
						listaPrestaciones.add(new String[] { prestacion, "", "1" }); // hago "j" veces cada prestacion

						if (aportes == null) {
							// optimizacion - no pido los valores de aportes mas
							// de una vez a la base de datos
							
							// TODO corregir - agregando solo prestaciones ambulatorias
							// TODO 20060425 corregir - cambiar '6' por un dominio 
							// TODO 20060427 corregir - cambiar '0' - solo se cobra la prestacion por un dominio
							String conValorCobrado ="0";
							aportes = prestadoresDao.copagoYAportesPorPrestador(cmc,rutPrestador, listaPrestaciones, new Date(), 1, 6, codContrato, 1, 1, conValorCobrado, uw);
						}

						/*
						 * totalAporteDipreca += cantidad * filaAportes[0];
						 * totalAporteSeguro += cantidad * filaAportes[1];
						 * totalCopago += cantidad * filaAportes[2];
						 */
						
						item.setValorAporteDipreca(new Integer(aportes[0]));
						item.setValorAporteSeguro(new Integer(aportes[1]));
						item.setValorCopago(new Float(aportes[2]));
						item.setValorConvenidoPrestacion(new Float(aportes[0] + aportes[1] + aportes[2]));
						item.setTipoBono(BonoDTO.TIPOBONO_WEB);

						// busco el convenio para el prestador y prestacion
						Integer convenio = prestadoresDao.convenioPorPrestadorYPrestacion(rutPrestador, item.getCodPrestacion().toString(), uw);
						
						// busco la ctaPre y ctaGas para la prestacion
						int codPrestacion = new Integer(prestacion).intValue();
						CuentaPresupuestoCuentaGastoMappingQuery ctaQuery 
							= new CuentaPresupuestoCuentaGastoMappingQuery(codPrestacion);
						
						List listaCuentas = ctaQuery.execute();
						String[] cuentas = (String[]) listaCuentas.get(0);
						String cuentaPre = cuentas[0];
						String cuentaGas = cuentas[1];
						
						// 2006 06 15 - en el caso de un bono valorado ambulatorio,
						// el tipo de prestacion es '1' y la fecha de atencion
						// es la fecha en que se emite el bono "hoy"
						
						int rutCiaSeguros = 0;

					    rutCiaSeguros = getRutCiaSeguros(cmc, new Date());
						String insertItem = "" +
							" insert into bm_bonite (" +
							" bo_serial, bi_apodip, bi_aposeg, bi_copago," +
							" bi_valcon, dom_tipbon, pr_codigo, cv_codigo," +
							" dom_tippre, bi_fecate, bi_cantidad, vc_valor, " +
							" pr_ctapre, pr_ctapab, " +
							" dom_estliq, bi_rutseg " +
							" ) values (" +
							bono.getId() + "," + item.getValorAporteDipreca() + ", " + item.getValorAporteSeguro() +
							"," + item.getValorCopago() + "," + item.getValorConvenidoPrestacion() + ",'" + BonoDTO.TIPOBONO_WEB + "', " +
							item.getCodPrestacion() + "," + convenio + ", 1, TODAY, 1," +
							item.getValorConvenidoPrestacion() + "," +
							"'" + cuentaPre + "', '" + cuentaGas + "'," +
							"'00', " + rutCiaSeguros +  
							")";

						QueryLogger.log(uw, insertItem);
						template.execute(insertItem);

						bonoItems.add(item);

						aportes = null;
					}
					
				}
				
				bono.setItems( bonoItems );
				return bono;

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;

			}
		}
		
	}

	class ItemsBonoWebMappingQuery extends MappingSqlQuery {

		public ItemsBonoWebMappingQuery(DataSource ds, UsuarioWeb uw, int folio) {
			super();
			setDataSource(ds);

			String queryString = "select wi_serial, bo_serial, wi_codigo from bmw_bonite where bo_serial = " + folio;

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, queryString);

			super.setSql(queryString);
			compile();

		}

		protected Object mapRow(ResultSet rs, int rowNumber)
				throws SQLException {
			BonoWItemDTO b = new BonoWItemDTO();
			try { b.setId(((Integer) rs.getObject("wi_serial")).intValue()); } catch (Exception e) { }
			try { b.setBonoSerial(((Integer) rs.getObject("bo_serial")) .intValue()); } catch (Exception e) { }
			try { b.setCodigoPrestacion(((Integer) rs.getObject("wi_codigo")).intValue()); } catch (Exception e) { }
			return b;
		}

	}

	class ItemsBonoValoradoWebMappingQuery extends MappingSqlQuery {

		public ItemsBonoValoradoWebMappingQuery(DataSource ds, UsuarioWeb uw, int folio) {
			super();
			setDataSource(ds);

			String queryString = "select * from bm_bonite where bo_serial = " + folio + " and dom_tipbon='" + BonoDTO.TIPOBONO_WEB + "'";

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, queryString);

			super.setSql(queryString);
			compile();

		}

		protected Object mapRow(ResultSet rs, int rowNumber)
				throws SQLException {
			BonoItemDTO b = new BonoItemDTO();
			
			// TODO Usar ReflectionFiller aqui
			ReflectionFiller.fill(mapaItemsBonoValoradoWeb, rs, b);

			return b;
		}
	}

	// recuperacion de prestaciones por codigo o nombre
	class PrestacionesMappingQuery extends MappingSqlQuery {

		public PrestacionesMappingQuery(DataSource ds, UsuarioWeb uw,
				Integer codigoMin, Integer codigoMax, String nombre, 
				Date fecha, String rutPrestador) {
			super();
			setDataSource(ds);
			
			String queryString = "";

			if (fecha != null && rutPrestador != null){

				SimpleDateFormat sdf = new SimpleDateFormat("(MM,dd,yyyy)");
				String mmddyyyy = sdf.format(fecha);
				
				queryString = "select unique" +
						" p.pr_codigo, p.pr_nombre " +
						" from bm_prestacion p, bm_valcon v, bm_convenio c ";
				List listaWhere = new ArrayList(); 

				String rutSinDV = rutPrestador.substring(0, rutPrestador.length()-3);
				
				listaWhere.add("c.pb_codigo = " + rutSinDV);
				listaWhere.add("c.cv_codigo = v.cv_codigo");
				listaWhere.add("mdy" + mmddyyyy + " >= c.cv_fecini");
				listaWhere.add("mdy" + mmddyyyy + " <= c.cv_fecter");
				listaWhere.add("p.pr_codigo = v.pr_codigo");
				
				if (codigoMin != null && codigoMax != null) {
					listaWhere.add("p.pr_codigo between " + codigoMin + " and " + codigoMax);
				}
				
				if (nombre != null) {
					listaWhere.add("upper(p.pr_nombre) like '%" + TextUtil.filtrar(nombre.toUpperCase()) + "%'");
				}
				
				queryString = queryString + QueryUtil.getWhere(listaWhere);
				
			} else {

				queryString = "select pr_codigo, pr_nombre from bm_prestacion ";
				List listaWhere = new ArrayList(); 

				if (codigoMin != null && codigoMax != null) {
					listaWhere.add("pr_codigo between " + codigoMin + " and " + codigoMax);
				}
				
				if (nombre != null) {
					listaWhere.add("upper(pr_nombre) like '%" + TextUtil.filtrar(nombre.toUpperCase()) + "%'");
				}
				
				queryString = queryString + QueryUtil.getWhere(listaWhere);

			}
			

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, queryString);

			super.setSql(queryString);
			compile();
		}

		protected Object mapRow(ResultSet rs, int nowNumber)
				throws SQLException {

			String codigo = ((Integer) rs.getObject("pr_codigo")).toString();
			String nombre = (String) rs.getObject("pr_nombre");
			return new String[] { codigo, nombre, "Ambulatoria" };
		}

	}

	// consulta de cuenta de presupuesto y de gasto para grabar los items del bono valorado
   	class CuentaPresupuestoCuentaGastoMappingQuery extends MappingSqlQuery {

		public CuentaPresupuestoCuentaGastoMappingQuery(int codigoPrestacion) {
			
			String query = "" +
					" select pr_ctapre, pr_ctagas " +
					" from bm_prestacion where pr_codigo = " + codigoPrestacion;
			
			setDataSource(dataSource);
			setSql(query);
		}
   		
		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {

			String ctaPre = "0";
			String ctaGas = "0";
			
			try { ctaPre = rs.getString("pr_ctapre").trim(); }
			catch (Exception e) { }
			
			try { ctaGas = rs.getString("pr_ctagas").trim(); }
			catch (Exception e) { }
			
			if ("".equals(ctaPre)){ ctaPre = "0"; }
			if ("".equals(ctaGas)){ ctaGas = "0"; }
			
			return new String[]{ctaPre, ctaGas};
		}
   		
   	}

   	
}