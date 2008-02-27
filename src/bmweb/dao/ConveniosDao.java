package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.ConvenioDTO;
import bmweb.dto.ValconDTO;
import bmweb.util.Constantes;
import bmweb.util.QueryLogger;
import bmweb.util.QueryUtil;
import bmweb.util.ReflectionFiller;
import bmweb.util.UsuarioWeb;

public class ConveniosDao implements IConveniosDao {

	private static String[] mapaConvenios = new String[] {
		 "codigo", "cv_codigo",
		 "glosa", "cv_glosa",
		 "codigoPrestador", "pb_codigo", 
		 "codigoArancelFonasa", "af_codigo",
		 "fechaInicio", "cv_fecini",
		 "fechaTermino", "cv_fecter",
		 "tipoConvenio", "dom_tipcon",
		 "codigoConcurrencia", "cv_nrores",
		 "fechaConcurrencia", "cv_fecres", 
		 "moneda", "dom_moneda", 
		 "referenciaFonasa", "cv_reffon", 
		 "nivelReferenciaFonasa", "cv_refniv", 
		 "factorRefFonasa", "cv_reffac",
		 "estadoConvenio", "dom_estcvn",
	};

	private static String[] mapaValcon = new String[] {
		 "idConvenio", "cv_codigo",
		 "codigoPrestacion", "pr_codigo",
		 "valorCovenido", "vc_valor",
		 "valorLista", "vc_lispre",
		 "estado", "dom_estvlc",
		 "valorFonasa", "vf_valor",
	};

	private DataSource dataSource;
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }

	public List getConvenios(Map params, UsuarioWeb uw){

		try {
			ConveniosListadoMappingQuery buscaConvenios = 
				new ConveniosListadoMappingQuery(dataSource, params, uw);
			
			List lista = buscaConvenios.execute();
	
			int inicio = 0;
			try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }			
			
			List resultado = lista.subList(inicio, lista.size());
			return resultado;			
		} catch (Exception e) {
			return new ArrayList();
		}
		
	}

	public List getValcon(Map params, UsuarioWeb uw){

		try {
		ValconMappingQuery buscaValcon = 
			new ValconMappingQuery(dataSource, params, uw);
		
		List lista = buscaValcon.execute();

		int inicio = 0;
		try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
		
		List resultado = lista.subList(inicio, lista.size());
		return resultado;
			
		} catch (Exception e) {
			return new ArrayList();
		}
		
	}
	
	public int guardarNuevoConvenio(int rutPrestador, List listaValcon) throws Exception {
		
		try {
			JdbcTemplate template = new JdbcTemplate(dataSource);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			// Determino si hay un convenio nuevo sin aprobar 
			int numConveniosNuevos = template.queryForInt("" +
					" select count(*) from bm_convenio" +
					" where pb_codigo = " + rutPrestador + "" +
					" and cv_fecini is null " +
					" and cv_fecter is null");
			
			Integer cvCodigo = null;
			
			// No hay convenios nuevos
			if (numConveniosNuevos == 0){

				// Inserto una nueva fila en bm_convenio
				// SIN FECHAS DE INICIO NI TERMINO
				
				template.update("" +
						" insert into bm_convenio " +
						" (cv_glosa, pb_codigo, dom_moneda, dom_estcvn)" +
						" values" +
						" (?, ?, ?, ?)", 
						new Object[]{
						"Cargado en " + sdf.format(new Date()),
						new Integer(rutPrestador),
						new Integer(1),
						new Integer(ConvenioDTO.CONVENIO_NUEVO)
						});

			} 
			
		//	else {

				// Recupero el cv_codigo del nuevo convenio (existente o recien creado)
				int cv_codigo = template.queryForInt("" +
						" select max(cv_codigo)" +
						" from bm_convenio" +
						" where cv_fecini is null and cv_fecter is null" +
						" and pb_codigo = " + rutPrestador);
				
				cvCodigo = new Integer(cv_codigo);

				// Borro la última versión de los valores asociados al convenio antes
				// de insertar la lista de nuevos valores
				
				template.update("delete bm_valcon where cv_codigo = ?", new Object[]{ cvCodigo });
		//	}
			
			// Inserto en bm_valcon toda la lista de convenios
			
			String query = "" +
					" insert into bm_valcon " +
					" (cv_codigo, pr_codigo, vc_valor, vc_lispre, dom_estvlc)" +
					" values (?, ?, ?, ?, ?)"; 
			
			for (int i=0; listaValcon != null && i<listaValcon.size(); i++){
				
				ValconDTO valcon = (ValconDTO) listaValcon.get(i);
				
				template.update(query,
					new Object[]{
						cvCodigo,
						new Integer(valcon.getCodigoPrestacion()),
						new Float(valcon.getValorCovenido()),
						new Float(valcon.getValorLista()),
						new Integer(valcon.getEstado())
					});
			}
			
			// Dejo el convenio como nuevo
			template.update("update bm_convenio set dom_estcvn = ? where cv_codigo = ? ", 
					new Object[]{ new Integer(ConvenioDTO.CONVENIO_NUEVO), cvCodigo });
			
			return cvCodigo.intValue();
			
		} catch (Exception e) {
			throw new Exception("No se pudieron guardar los valores del convenio, intente nuevamente más tarde");
		}
		

		
	}

	/**
	 * Autorizar un convenio
	 */
	public void autorizarConvenio(Map params, UsuarioWeb uw) throws Exception {
		
		try {
			
			JdbcTemplate template = new JdbcTemplate(dataSource);			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			/**
			 * cv_codigo            serial        = Identificador del convenio
			 * cv_glosa             char(45)      = Glosa descriptiva
			 * pb_codigo            integer       = Codigo del Prestador 
			 * af_codigo            integer       = Codigo del arancel Fonasa asociado
			 * cv_fecini            date          = Fecha Inicio del convenio
			 * cv_fecter            date          = Fecha de Termino del Convenio
			 * dom_tipcon           smallint      = Dominio de tipo de convenio
			 * cv_nrores            char(10)      = Numero de Resolucion de concurrencia  (Aportes)
			 * cv_fecres            date          = Fecha de la Resolucion de concurrencia 
			 * dom_moneda           smallint      = Dominio de la moneda en que se expresa el convenio ( Siempre es $ chileno) 
			 * cv_reffon            char(1)       = Indicador convenio hace referencia a Fonasa (S)i/(N)o 
			 * cv_refniv            smallint      = Nivel de referencia Fonasa (Nivel 1, 2 o 3) 
			 * cv_reffac            decimal(5,2)  = Factor de referencia Fonasa
			 * dom_estcvn           smallint      = CAMPO NUEVO, indica estado del convenio
			 *                                      debemos analizar que estados ponerles( Vigente, en Proceso, etc..)
			 */
			
			String query = "" +
					" update bm_convenio set " +
					" cv_glosa = ? , af_codigo = ?, cv_fecini = ?,  cv_fecter = ?, " +
					" dom_tipcon = ?, cv_nrores = ?, cv_fecres = ?, dom_moneda = 1, " +
					" cv_reffon = ?, cv_refniv = ?, cv_reffac = ?, dom_estcvn = 0 " +
					" where cv_codigo = ?";

			String glosa = (String) params.get("glosa");
			// Integer pbCodigo = new Integer((String)params.get("pb_codigo"));
			Integer afCodigo = new Integer((String)params.get("codigoArancelFonasa"));			
			Date fecIni = sdf.parse((String) params.get("fechaInicio"));
			Date fecTer = sdf.parse((String) params.get("fechaTermino"));
			String domTipcon = (String) params.get("tipoConvenio");
			String cvNroRes = (String) params.get("resolucionConcurrencia");
			Date cvFecRes = sdf.parse((String) params.get("fechaResolucion"));
			String cvRefFon = (String) params.get("referenciaFonasa");
			Integer cvRefNiv = new Integer((String) params.get("nivelReferenciaFonasa"));
			Float cvRefFac = new Float((String) params.get("factorReferenciaFonasa"));
			
			Integer cvCodigo = new Integer((String)params.get("id"));
			
			QueryLogger.log(uw, query);
			
			template.update(query, new Object[]{
					glosa, afCodigo, fecIni, fecTer, domTipcon,
					cvNroRes, cvFecRes, cvRefFon, cvRefNiv, cvRefFac,
					cvCodigo
			});
			
		} catch (Exception e) {
			throw new Exception("Error: No se pudo autorizar correctamente el convenio.");
		}
		
	}
	
	/**
	 * Pongo en estado RECHAZADO un valcon y su convenio correspondiente
	 */
	public void rechazarConvenio(Map params, UsuarioWeb uw) throws Exception {

		try {

			JdbcTemplate template = new JdbcTemplate(dataSource);
			
			Integer cvCodigo = new Integer((String)params.get("convenioRechazado"));
			Integer prCodigo = new Integer((String)params.get("prestacionRechazada"));
			
			// Rechazo el valcon
			template.update("" +
					"update bm_valcon set dom_estvlc = ? where cv_codigo = ? and pr_codigo = ?" +
					"", new Object[] { new Integer(ValconDTO.ESTADO_RECHAZADO), cvCodigo, prCodigo } );
			
			// Rechazo el convenio asociado
			template.update("" +
					"update bm_convenio set dom_estcvn = ? where cv_codigo = ? " +
					"", new Object[] { new Integer(ConvenioDTO.CONVENIO_RECHAZADO), cvCodigo } );
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("No se pudo rechazar correctamente el convenio.");
		}
		
	}

	class ValconMappingQuery extends MappingSqlQuery {
		
		public ValconMappingQuery(DataSource ds, Map params, UsuarioWeb uw){
			super();
			setDataSource(ds);

			int inicio = 0;
			int dpp = Constantes.DATOS_POR_PAGINA;
			int maxResults = Constantes.DATOS_POR_PAGINA + 1;

			// Paginacion
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
			}

			if (params.containsKey("dpp")) {
				try { dpp = Integer.parseInt((String) params.get("dpp")); maxResults = dpp+1; } catch (Exception e) { }
			}

			ArrayList listaWhere = new ArrayList();
			
			//String query = "select first " + (inicio+maxResults) + " * from bm_valcon ";
			  String query = "select b.*,a.vf_valor from bm_valfon a, bm_valcon b ";
			  Integer id = new Integer((String)params.get("id"));
			if (params.containsKey("id")){
				//Integer id = new Integer((String)params.get("id"));
				//try { listaWhere.add("cv_codigo = " + id); }
				try { listaWhere.add("b.cv_codigo = " + id); }
				catch (Exception ex){  }
			}
            listaWhere.add("a.pr_codigo=b.pr_codigo");
            listaWhere.add("a.af_codigo in ( select max(af_codigo) from bm_valfon)");
            listaWhere.add("a.nf_nivel= 1");
			//query = query + QueryUtil.getWhere(listaWhere) + " order by cv_codigo, pr_codigo asc";
            query = query + QueryUtil.getWhere(listaWhere);
            query = query +" union all ";
            query = query +"select b.*,0 from bm_valcon b "
			              +"where cv_codigo= "+ id
						  +"and pr_codigo not in "
						  +"(select pr_codigo from bm_valfon where af_codigo in "
						  +"(select max(af_codigo) from bm_valfon))"
						  +" order by 1,2 ";
            
			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			ValconDTO valorConvenio = new ValconDTO();
			ReflectionFiller.fill( mapaValcon, rs, valorConvenio);
			return valorConvenio;
		}
	}
	
	class ConveniosListadoMappingQuery extends MappingSqlQuery {
		
		public ConveniosListadoMappingQuery(DataSource ds, Map params, UsuarioWeb uw){
			super();
			setDataSource(ds);

			int inicio = 0;
			int dpp = Constantes.DATOS_POR_PAGINA;
			int maxResults = Constantes.DATOS_POR_PAGINA + 1;

			// Paginacion
			if (params.containsKey("inicio")) {
				try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
			}

			if (params.containsKey("dpp")) {
				try { dpp = Integer.parseInt((String) params.get("dpp")); maxResults = dpp+1; } catch (Exception e) { }
			}

			ArrayList listaWhere = new ArrayList();
			
			String query = "select first " + (inicio+maxResults) + " * from bm_convenio ";
			
			/*
			// Manejo del filtro por codigo
			if ( params.containsKey("opcodigo") && params.containsKey("codigo") ){
				try {
					int codigo = Integer.parseInt((String)params.get("codigo"));
					if ("le".equals(params.get("opcodigo"))){
						listaWhere.add("ha_codigo <= " + codigo);
					}

					if ("eq".equals(params.get("opcodigo"))){
						listaWhere.add("ha_codigo = " + codigo);
					}

					if ("gt".equals(params.get("opcodigo"))){
						listaWhere.add("ha_codigo >= " + codigo);
					}

				} catch (Exception ex){  }
			}


			*/

			if (params.containsKey("id")){
				Integer id = new Integer((String)params.get("id"));
				try { listaWhere.add("cv_codigo = " + id); } 
				catch (Exception ex){  }
			}
			
			
			// Filtro por el RUT del prestador si es que 
			if (params.containsKey("rut")){
				try {
					Integer rut = new Integer((String)params.get("rut"));
					listaWhere.add("pb_codigo = " + rut);
				} catch (Exception e){ }
			}

			// Filtro del convenio más reciente por prestador de beneficios
			// por omision o opTipoConvenio == "recientes"
			if (!params.containsKey("tipoConvenios")
				|| ( params.containsKey("tipoConvenios")
						&& "vigentes".equals((String)params.get("tipoConvenios"))
						)){
				listaWhere.add("cv_codigo in ( select max(cv_codigo) from bm_convenio where cv_fecini is not null and cv_fecter is not null group by pb_codigo )");
			}
			
			// Nuevos, modificados o eliminados
			if ("nuevos".equals((String)params.get("tipoConvenios"))){ listaWhere.add("dom_estcvn = " + ConvenioDTO.CONVENIO_NUEVO); }
			if ("modificados".equals((String)params.get("tipoConvenios"))){ listaWhere.add("dom_estcvn = " + ConvenioDTO.CONVENIO_MODIFICADO); }
			if ("eliminados".equals((String)params.get("tipoConvenios"))){ listaWhere.add("dom_estcvn = " + ConvenioDTO.CONVENIO_ELIMINADO); }

			query = query + QueryUtil.getWhere(listaWhere) + " order by cv_codigo desc";
			
			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			ConvenioDTO convenio = new ConvenioDTO();
			ReflectionFiller.fill( mapaConvenios, rs, convenio);
			return convenio;
		}
	}
}
