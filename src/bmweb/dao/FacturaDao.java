/*
 * Creado en 27-12-2005 por denis
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.BonoDTO;
import bmweb.dto.DocumentoPagoDTO;
import bmweb.dto.FacturaDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.Constantes;
import bmweb.util.QueryLogger;
import bmweb.util.QueryUtil;
import bmweb.util.ReflectionFiller;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 * DAO que realiza las operaciones para agregar, quitar, modificar y buscar
 * facturas (FacturaDTO con sus FacturaItemDTO)
 * 
 */
public class FacturaDao implements IFacturaDao {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	// Usado por el dao para llenar un FacturaDTO
	String [] mapaFactura = new String[]{
		"id", "fa_serial",
		"numero", "fa_numero",
		"observaciones", "fa_observaciones"
	};
	
	String [] mapaFacturaItem = new String[]{
		"id", "fi_serial",
		"facturaSerial", "fa_serial",
		"bonoSerial", "bo_serial"
	};
	
	String [] mapaDocumentoPago = new String[]{
			"id", "dp_serial",
			"rutAcreedor", "acree_rut",
			"tipoDocumento", "dom_tipdoc",
			"codigoAcreedor","pb_codigo",
			"origen","dp_origen",
			"numeroFactura","dp_numero",
			"fechaLiquidacion","dp_fecha"
			};

	private DataSource dataSource;
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }
	
	private IBonoDao bonoDao;
	public void setBonoDao(IBonoDao bd){ this.bonoDao = bd; }
	
	private IBeneficiariosDao beneficiariosDao;
	public void setBeneficiariosDao(IBeneficiariosDao bd){ this.beneficiariosDao = bd; }
	
	// Busco una factura por su numero
	public DocumentoPagoDTO buscarPorNumero(UsuarioWeb uw, int numeroFactura){
		return buscarFactura(uw, null, new Integer(numeroFactura));
	}
	
	public DocumentoPagoDTO buscarPorSerial(UsuarioWeb uw, int serialFactura){
		return buscarFactura(uw, new Integer(serialFactura), null);
	}
	
	public DocumentoPagoDTO crear(UsuarioWeb uw, int numeroFactura){
		
		// veo si la factura existe. Si existe, tiro un RuntimeException
		if (buscarPorNumero(uw, numeroFactura) != null){
			throw new RuntimeException("Factura numero #" + numeroFactura + " ya existia");
		}

		String acreeRut;
		String pbCodigo;
		try {
			acreeRut = uw.getNombreUsuario() + "-" + TextUtil.getDigitoVerificador(Integer.parseInt(uw.getNombreUsuario()));
			pbCodigo = uw.getNombreUsuario();
		} catch (Exception e) {
			acreeRut = "1-9";
			pbCodigo = "1";
		}
		
		String query =	" insert into bm_docpago " +
						" (acree_rut, dom_tipdoc, dp_numero, pb_codigo, dp_origen) values " +
						" ( '" + acreeRut + "', 'FA', " + numeroFactura + "," + pbCodigo + ",'" + BonoDTO.TIPOBONO_WEB + "')";
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		QueryLogger.log(uw, query);
		template.execute(query);
		
		DocumentoPagoDTO nuevaFactura = buscarPorNumero(uw, numeroFactura);

		return nuevaFactura;	
		
	}
	
	public void borrar(UsuarioWeb uw, int facturaSerial){
		try {
			DocumentoPagoDTO factura = buscarPorSerial(uw, facturaSerial);
			if (factura == null){ throw new RuntimeException("Se intenta borrar factura no existente!");}
			if (factura.getFechaLiquidacion() != null){ throw new RuntimeException("Se intenta borrar factura liquidada");}
			
			JdbcTemplate template = new JdbcTemplate(dataSource);
			
			// quito el vinculo entre todos los bonos y esta factura
			
			// TODO 20060424 agregar transaccionalidad aqui

			String query1 = "update bm_bono set dp_serial = null where dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "' and dp_serial = " + facturaSerial;
			QueryLogger.log(uw, query1);
			template.execute(query1);
			
			String query2 = "update bm_bonite set dp_serial = null where dom_tipbon = '" +  BonoDTO.TIPOBONO_WEB + "' and dp_serial = " + facturaSerial;
			QueryLogger.log(uw, query1);
			template.execute(query1);
			
			String query3 = "delete from bm_docpago where dp_serial = " + facturaSerial;
			QueryLogger.log(uw, query3);
			template.execute(query3);
			
			// getSession().delete(factura);			
		} catch (Exception ex){
			logger.warn("Excepcion al intentar borrar factura con serial #" + facturaSerial);
			logger.warn(ex.getMessage());
			ex.printStackTrace();
		}
	}
		
	public void agregarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial){
		// si la factura no existe - RuntimeException
		
		DocumentoPagoDTO factura = buscarPorNumero(uw, numeroFactura);
		if (factura == null){ throw new RuntimeException("Se intenta asociar factura inexistente");}
		if (factura.getFechaLiquidacion() != null){ throw new RuntimeException("Se intenta asociar a una factura ya liquidada");}
		
		// TODO buscar si hay alguna factura que tenga el mismo bono ya asociado
		
		List detalle = factura.getDetalle(); // lista de String[]{ bo_serial, bo_folio }
		
		// veo si en el detalle viene el numero de bono
		boolean contieneBono = false;
		for (int i=0; detalle != null && i<detalle.size(); i++){
			String[] fila = (String[]) detalle.get(i);
			int bonoSerialFila = Integer.parseInt(fila[0]);
			if (bonoSerial == bonoSerialFila){ contieneBono = true; break; }
		}
		
		// validacion - recupero el bono. Si el bono ya esta asociado a otra factura, es un error
		// intentar agregarlo a una nueva factura
		JdbcTemplate templateValidacion = new JdbcTemplate(dataSource);
		String queryValidacion = "select dp_serial from bm_bono where dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "' and bo_serial = " + bonoSerial;
		QueryLogger.log(uw, queryValidacion);
		List listaSeriales = templateValidacion.queryForList(queryValidacion);
		if (listaSeriales.size() > 0){
			
			if (null != ((Map)listaSeriales.get(0)).get("dp_serial")) {
				String dpSerial = ((Map)listaSeriales.get(0)).get("dp_serial").toString();
				DocumentoPagoDTO facturaAntigua = buscarFactura(uw, new Integer(dpSerial), null);
				throw new RuntimeException("El bono web ya estaba asociado a la factura Nro. " + facturaAntigua.getNumeroFactura());				
			} else {
				contieneBono = false;
			}
		}
		
		if (!contieneBono){

			// TODO 20060424 agregar transaccionalidad aqui

			JdbcTemplate template = new JdbcTemplate(dataSource);
			String query1 = "update bm_bono set dp_serial = " + factura.getId() + " where bo_serial = " + bonoSerial;
			String query2 = "update bm_bonite set dp_serial = " + factura.getId() + " where bo_serial = " + bonoSerial;
			QueryLogger.log(uw, query1);
			
			try {
				template.execute(query1);
				template.execute(query2);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error interno al agregar bono a la factura");
			}
			
		}
	}

		public void quitarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial){
			// si la factura no existe - RuntimeException
			
			DocumentoPagoDTO factura = buscarPorNumero(uw, numeroFactura);
			if (factura == null){ throw new RuntimeException("Se intenta asociar factura inexistente");}
			if (factura.getFechaLiquidacion() != null){ throw new RuntimeException("Se intenta usar una factura liquidada");}
			
			// TODO buscar si hay alguna factura que tenga el mismo bono ya asociado
			
			List detalle = new ArrayList(factura.getDetalle());
			
			// veo si en el detalle viene el numero de bono
			String encontrado = null;
			for (int i=0; detalle != null && i<detalle.size(); i++){
				String[] fila = (String[]) detalle.get(i);
				int bonoSerialFila = Integer.parseInt(fila[0]);

				if (bonoSerial == bonoSerialFila){ 
					encontrado = fila[0];
					break;
				}
			}
			
			if (encontrado != null){
				JdbcTemplate template = new JdbcTemplate(dataSource);
				
				//String query = "delete from bmw_factitem where fi_serial = " + encontrado.getId();
				
				// TODO 20060424 agregar transaccionalidad aqui
				
				String query1 = "update bm_bono set dp_serial = null where bo_serial = " + bonoSerial;
				QueryLogger.log(uw, query1);
				String query2 = "update bm_bonite set dp_serial = null where bo_serial = " + bonoSerial;
				QueryLogger.log(uw, query2);
				
				try { 
					template.execute(query1);
					template.execute(query2);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error al borrar bono de factura: " + e.getMessage());
				}
				
			}

	}
		
	public DocumentoPagoDTO buscarPorBonoSerial(UsuarioWeb uw, int serialBono){
		
		return buscarFactura(uw, new Integer(serialBono), null);

	}
	
	
	private DocumentoPagoDTO buscarFactura(UsuarioWeb uw, Integer serial, Integer numero){
	
		FacturaMappingQuery buscador = new FacturaMappingQuery(dataSource, uw, serial, numero);
		List lista = buscador.execute();
		
			if (lista == null || lista.size() == 0) return null;
			
			else {
				DocumentoPagoDTO documentoPago = (DocumentoPagoDTO) lista.get(0);
				
				// intento recuperar el detalle de items de una factura
				try {
					FacturaItemMappingQuery buscaItems = new FacturaItemMappingQuery(dataSource, uw, documentoPago.getId());
					List items = buscaItems.execute();
					List itemsConValor = new ArrayList();
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					// Para cada item de la lista, agrego el valor del bono
					for (int i=0; i<items.size(); i++){
						String[] fila = (String []) items.get(i);
						int bonoSerial = Integer.parseInt(fila[0]);
						String rutPrestador = fila[2];
						int valorTotal = bonoDao.getValorTotalBono(bonoSerial, rutPrestador, uw);
						
						// recupero el bono
						BonoDTO bono = bonoDao.bonoWebPorSerial( bonoSerial, uw );

						// Recupero el Beneficiario con el CMC del bono
						RolbeneDTO rolbene = beneficiariosDao.leeRolbene( bono.getCarneBeneficiario(), uw );
						BeneficiarioDTO beneficiario = beneficiariosDao.leeBeneficiario( rolbene.getRbene(), uw );
						
						itemsConValor.add( new String[]{
								fila[0], 
								fila[1], 
								valorTotal+"", 
								sdf.format( bono.getFechaEmision()),
								beneficiario.getNombre() + " " + beneficiario.getPat() + " " + beneficiario.getMat()
								});
						
					}
					
					// lista de String[bonoSerial, bonoFolio]
					documentoPago.setDetalle( itemsConValor );
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return documentoPago;
			}
		
	}
	
	public List listado(UsuarioWeb uw, Map params){
		ListadoFacturaMappingQuery listador = new ListadoFacturaMappingQuery(dataSource, uw, params);
		return listador.execute();
	}	

	/**
	 * retorno una lista de String[]{folioBono, bonoSerial}
	 * para los bonos de este proveedor que no tengan una
	 * factura asociada 
	 */
	public List listadoBonosSinFacturar(UsuarioWeb uw) {
		
		ListadoBonosSinFacturarQuery listador = new ListadoBonosSinFacturarQuery(dataSource, uw);
		return listador.execute();
	}

	public boolean esFacturaCerrada(Long serialFactura, UsuarioWeb uw){
		
		try {
			JdbcTemplate template = new JdbcTemplate(dataSource);
			
			String query =	"select count(*) from bm_docpago " +
							" where dp_serial = " + serialFactura +
							" and dom_estdp = " + FacturaDTO.FACTURA_ESTADO_CERTIFICADO;
			
			QueryLogger.log(uw, query);
			int i = template.queryForInt(query);
			
			if (i == 1) return true;
			else return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	
	public boolean cerrarFactura(Long serialFactura, UsuarioWeb uw){
		
		try {
			JdbcTemplate template = new JdbcTemplate(dataSource);
			
			String query =	"update bm_docpago set dom_estdp = " + FacturaDTO.FACTURA_ESTADO_CERTIFICADO +
							" where dp_serial = " + serialFactura;
			
			QueryLogger.log(uw, query);
			template.update(query);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	///////////////////////////////////////////////////////////////
	//  Clases Interiores para buscar Facturas, FacturaItems, etc
	
	class ListadoBonosSinFacturarQuery extends MappingSqlQuery {
		
		public ListadoBonosSinFacturarQuery(DataSource ds, UsuarioWeb uw) {
			super();
			setDataSource(ds);
			
			String query =	" select distinct b.bo_folio as bo_folio, b.bo_serial as bo_serial" +
							" from bm_bono b, bm_bonite bi" +
							" where b.dp_serial is null " +
							" and bi.bo_serial = b.bo_serial " +
							" and b.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'" +
							" and (b.dom_estbon <> 'A' or b.dom_estbon is null)" +
							" and b.pb_rut = '" + uw.getRutEmisor() + 
							"-" + TextUtil.getDigitoVerificador(Integer.parseInt(uw.getRutEmisor())) + "'";

			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new String[]{ rs.getObject("bo_folio").toString(), rs.getObject("bo_serial").toString() }; 
		}
	}

	
	class FacturaMappingQuery extends MappingSqlQuery{


		public FacturaMappingQuery(DataSource ds, UsuarioWeb uw, Integer serial, Integer numero) {
			super();
			setDataSource(ds);
			
			String query = "select * from bm_docpago ";
			List listaWhere = new ArrayList();
			listaWhere.add("pb_codigo = " + uw.getRutEmisor() );
			if (serial != null){ listaWhere.add("dp_serial = " + serial); }
			if (numero != null){ listaWhere.add("dp_numero = " + numero); }
			query += QueryUtil.getWhere(listaWhere);
			
			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			DocumentoPagoDTO dp = new DocumentoPagoDTO();
			ReflectionFiller.fill(mapaDocumentoPago, rs, dp);
			return dp;
		}
	}
	

	class FacturaItemMappingQuery extends MappingSqlQuery {
		
		public FacturaItemMappingQuery(DataSource ds, UsuarioWeb uw, Long facturaSerial) {
			super();
			setDataSource(ds);
			String query = "select bo_serial, bo_folio, pb_rut from bm_bono where dp_serial = " + facturaSerial + " and (dom_estbon <> 'A' or dom_estbon is null)";
			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			String serial = ((Integer) rs.getObject("bo_serial")).toString();
			String folio  = ((Integer) rs.getObject("bo_folio")).toString();
			String rutPrestador = ((String) rs.getObject("pb_rut"));
			return new String[]{serial, folio, rutPrestador};
		}
		
	}


	class ListadoFacturaMappingQuery extends MappingSqlQuery {

		public ListadoFacturaMappingQuery(DataSource ds, UsuarioWeb uw, Map params){
			super();
			setDataSource(ds);

			int inicio = 0;  
			int dpp = Constantes.DATOS_POR_PAGINA;
			int maxResults = Constantes.DATOS_POR_PAGINA + 1;

			// Si me indican el inicio del listado, lo coloco
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
			}

			if (params.containsKey("dpp")) {
				try { dpp = Integer.parseInt((String) params.get("dpp")); maxResults = dpp+1; } catch (Exception e) { }
			}
/*
	String [] mapaDocumentoPago = new String[]{
			"id", "dp_serial",
			"rutAcreedor", "acree_rut",
			"tipoDocumento", "dom_tipdoc",
			"codigoAcreedor","pb_codigo",
			"origen","dp_origen",
			"numeroFactura","dp_numero",
			"fechaLiquidacion","dp_fecha"
			};

 */
			ArrayList listaWhere = new ArrayList();
			String query = "select first " + (inicio+maxResults) + " distinct " +
					" d.dp_serial, d.acree_rut, d.dom_tipdoc, d.pb_codigo, d.dp_origen, " +
					" d.dp_numero, d.dp_fecha " +
					" from bm_docpago d, bm_bono b";
			
			try { 
				// funciona para todos los usuarios prestadores, pero no restringe al admin
				String pbCodigo = (new Long(uw.getNombreUsuario())).toString();
				listaWhere.add("d.pb_codigo = " + pbCodigo);
			} catch (Exception e){ }

			listaWhere.add("b.dp_serial = d.dp_serial"); // join entre bm_docpago y bm_bono
			listaWhere.add("b.dom_tipbon = '" + BonoDTO.TIPOBONO_WEB + "'");		 // solo facturas con bonos tipo web
			
			query = query + QueryUtil.getWhere(listaWhere) + " order by d.dp_numero, d.dp_serial asc";
			QueryLogger.log(uw, query);
			setSql(query);
			compile();

		}
		
		protected Object mapRow(ResultSet rs, int rownumber) throws SQLException {
			DocumentoPagoDTO dp = new DocumentoPagoDTO();
			ReflectionFiller.fill(mapaDocumentoPago, rs, dp);
			return dp;
		}
		
	}
}
