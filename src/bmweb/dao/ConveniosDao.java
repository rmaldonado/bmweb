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
	
	public void guardarNuevoConvenio(int rutPrestador, List listaValcon) throws Exception {
		
		try {
			
			// Inserto una nueva fila en bm_convenio
			// SIN FECHAS DE INICIO NI TERMINO
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			JdbcTemplate template = new JdbcTemplate(dataSource);
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
			
			// Recupero el cv_codigo del nuevo convenio recien creado
			
			int cv_codigo = template.queryForInt("" +
					" select max(cv_codigo)" +
					" from bm_convenio" +
					" where cv_fecini is null and cv_fecter is null" +
					" and pb_codigo = " + rutPrestador);
			Integer cvCodigo = new Integer(cv_codigo);
			
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
			
		} catch (Exception e) {
			throw new Exception("No se pudieron guardar los valores del convenio, intente nuevamente más tarde");
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
			
			String query = "select first " + (inicio+maxResults) + " * from bm_valcon ";
			
			if (params.containsKey("id")){
				Integer id = new Integer((String)params.get("id"));
				try { listaWhere.add("cv_codigo = " + id); } 
				catch (Exception ex){  }
			}

			query = query + QueryUtil.getWhere(listaWhere) + " order by cv_codigo asc";
			
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

			query = query + QueryUtil.getWhere(listaWhere) + " order by cv_codigo asc";
			
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
