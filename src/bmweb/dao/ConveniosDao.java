package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.ConvenioDTO;
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

	private DataSource dataSource;
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }

	public List getConvenios(Map params, UsuarioWeb uw){

		try {
		List resultados = new ArrayList();
		
		ConvenioDTO c1 = new ConvenioDTO();
		c1.setCodigo(1);
		c1.setGlosa("Primer Convenio");
		
		resultados.add(c1);

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

			// Filtro por nombre
			if ( params.containsKey("opnombre") && params.containsKey("nombre") ){
				try {
					String nombre = ((String)params.get("nombre")).toUpperCase();
					nombre = TextUtil.filtrar(nombre);
					if ("comienza".equals(params.get("opnombre"))){
						listaWhere.add("upper(ha_nombre) like ('" + nombre + "%') ");
					}

					if ("contiene".equals(params.get("opnombre"))){
						listaWhere.add("upper(ha_nombre) like ('%" + nombre + "%') ");
					}

					if ("termina".equals(params.get("opnombre"))){
						listaWhere.add("upper(ha_nombre) like ('%" + nombre + "') ");
					}

				} catch (Exception ex){  }
			}
			
			// Filtro por ubicacion
			if (params.containsKey("opubicacion") && params.containsKey("ubicacion") ){
				try {
					String ubicacion = ((String)params.get("ubicacion")).toUpperCase();
					ubicacion = TextUtil.filtrar(ubicacion);
					if ("comienza".equals(params.get("opubicacion"))){
						listaWhere.add("upper(ha_ubicacion) like ('" + ubicacion + "%') ");
					}

					if ("contiene".equals(params.get("opubicacion"))){
						listaWhere.add("upper(ha_ubicacion) like ('%" + ubicacion + "%') ");
					}

					if ("termina".equals(params.get("opubicacion"))){
						listaWhere.add("upper(ha_ubicacion) like ('%" + ubicacion + "') ");
					}

				} catch (Exception ex){  }
			}

			// Filtro por ciudad
			if ( params.containsKey("dom_ciudad") ){
				try {
					String dom_ciudad  = TextUtil.filtrar((String)params.get("dom_ciudad"));
					listaWhere.add("dom_ciudad = " + dom_ciudad + " ");

				} catch (Exception ex){  }
			}

			// Filtro por direccion
			if (params.containsKey("opdireccion")){
				try {
					String direccion = TextUtil.filtrar(((String)params.get("direccion")).toUpperCase());
					if ("comienza".equals(params.get("opdireccion"))){
						listaWhere.add("upper(ha_direccion) like ('" + direccion + "%') ");
					}

					if ("contiene".equals(params.get("opdireccion"))){
						listaWhere.add("upper(ha_direccion) like ('%" + direccion + "%') ");
					}

					if ("termina".equals(params.get("opdireccion"))){
						listaWhere.add("upper(ha_direccion) like ('%" + direccion + "') ");
					}

				} catch (Exception ex){  }
			}

			// Filtro por responsable
			if (params.containsKey("opresponsable")){
				try {
					String responsable = TextUtil.filtrar(((String)params.get("responsable")).toUpperCase());
					if ("comienza".equals(params.get("opresponsable"))){
						listaWhere.add("upper(ha_responsable) like ('" + responsable + "%') ");
					}

					if ("contiene".equals(params.get("opresponsable"))){
						listaWhere.add("upper(ha_responsable) like ('%" + responsable + "%') ");
					}

					if ("termina".equals(params.get("opresponsable"))){
						listaWhere.add("upper(ha_responsable) like ('%" + responsable + "') ");
					}

				} catch (Exception ex){  }
			}

			// Filtro por estado activo/no activo
			if (params.containsKey("opactivo")){
				try {
					if ("activo".equals(params.get("opactivo"))){
						listaWhere.add("ha_activo = 'S'");
					}

					if ("noactivo".equals(params.get("opactivo"))){
						listaWhere.add("ha_activo = 'N'");
					}

				} catch (Exception ex){  }
			}
			*/
			
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
