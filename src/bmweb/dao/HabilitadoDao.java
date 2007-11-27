/*
 * Created on 03-oct-2005
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.HabilitadoDTO;
import bmweb.util.Constantes;
import bmweb.util.QueryLogger;
import bmweb.util.QueryUtil;
import bmweb.util.ReflectionFiller;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 */
public class HabilitadoDao implements IHabilitadoDao {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }
	
	private static String[] mapaHabilitado = new String[] {
			"codigo", "ha_codigo",
			"nombre", "ha_nombre",
			"ubicacion", "ha_ubicacion",
			"dom_ciudad", "dom_ciudad",
			"direccion", "ha_direccion",
			"responsable", "ha_responsable",
			"activo", "ha_activo"
	};
	
	public List getHabilitados(Map params, UsuarioWeb uw) {
		try {
			/*
			HibernateTemplate ht = getHibernateTemplate();
			List lista = (List) ht.execute( new HabilitadoListadoHibernateCallback(params) );
			return lista;
			*/
			HabilitadoListadoMappingQuery buscaHabilitados = 
				new HabilitadoListadoMappingQuery(dataSource, params, uw);
			
			List lista = buscaHabilitados.execute();

			int inicio = 0;
			try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
			
			List resultado = lista.subList(inicio, lista.size());
			return resultado;
			
		} catch (Exception e) {
			return new ArrayList();
		}
	}

	public boolean activarDesactivarHabilitado(Map params, UsuarioWeb uw){

		try {
			Integer codigo = new Integer((String)params.get("codigo"));
			String codActivo;
			if ("activar".equals(params.get("accion"))){ codActivo = HabilitadoDTO.HABILITADO_ACTIVO; }
			else { codActivo =HabilitadoDTO.HABILITADO_NO_ACTIVO ; }
			
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			String query = "update bm_habilitado set ha_activo = '" + codActivo + "' where ha_codigo = " + codigo;
			QueryLogger.log(uw, query);
			
			template.execute(query);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean crearHabilitado(Map params, UsuarioWeb uw){

		// antes de crear, vuelvo a revisar si el habilitado existe
		if (revisarHabilitadoExiste(params, uw)){
			// existe, asi que no puedo crear el habilitado
			return false;
		}
		
		try {
			// recupero los parametros
			Integer codigo 		= new Integer((String)params.get("codigo"));
			String nombre 		= TextUtil.filtrar((String)params.get("nombre"));
			String ubicacion 	= TextUtil.filtrar((String)params.get("ubicacion"));
			Integer dom_ciudad	= new Integer( (String) params.get("dom_ciudad"));
			String direccion	= TextUtil.filtrar((String)params.get("direccion"));
			String responsable 	= TextUtil.filtrar((String)params.get("responsable"));
			String estado 		= TextUtil.filtrar((String)params.get("activo"));

			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			
			String query = "" +
					" insert into bm_habilitado (" +
					"ha_codigo, ha_nombre, ha_ubicacion, dom_ciudad, " +
					"ha_direccion, ha_responsable, ha_activo) values (" +
					codigo + ",'" + nombre + "','"+ubicacion + "'," + dom_ciudad +
					",'"+direccion+"','"+responsable + "','" + estado + "')";
			
			QueryLogger.log(uw, query);
			template.execute(query);
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	public boolean revisarHabilitadoExiste(Map params, UsuarioWeb uw){
		
		try {
			int codigo = Integer.parseInt( (String)params.get("codigo") );			

			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			String query = "select count(*) from bm_habilitado where ha_codigo = " + codigo;
			QueryLogger.log(uw, query);
			int encontrado = template.queryForInt(query);
			
			if (encontrado > 0) return true;
			else return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public HabilitadoDTO getHabilitadoPorCodigo(int codigo, UsuarioWeb uw){
		try {
			Map params = new HashMap();
			params.put("codigo", ""+codigo);
			
			// Uso el metodo para editar habilitado ya hecho
			return editarHabilitado( params, uw );
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public HabilitadoDTO editarHabilitado(Map params, UsuarioWeb uw){
		
		//Integer codigo = new Integer( (String)params.get("codigo") );
		params.put("inicio", "0");
		params.put("dpp", "0");
		params.put("opcodigo","eq");

		// pongo restricciones a la busqueda para que solo me traiga el que me interesa
		
		// busco el habilitado por su codigo
		HabilitadoListadoMappingQuery buscar = new HabilitadoListadoMappingQuery(dataSource, params, uw);
		List lista = buscar.execute();
		return (HabilitadoDTO) lista.get(0);

		/*
		HibernateTemplate ht = getHibernateTemplate();
		return (HabilitadoDTO) ht.execute( new HabilitadoEditarHibernateCallback(params) );
		*/
		
	}
	
	public boolean modificarHabilitado(Map params, UsuarioWeb uw){
		
		// antes de crear, vuelvo a revisar si el habilitado existe
		//if (revisarHabilitadoExiste(params, uw)){
			// existe, asi que no puedo crear el habilitado
			//return false;
		// }
		
		try {
			// recupero los parametros
			Integer codigo 		= new Integer((String)params.get("codigo"));
			String nombre 		= TextUtil.filtrar((String)params.get("nombre"));
			String ubicacion 	= TextUtil.filtrar((String)params.get("ubicacion"));
			Integer dom_ciudad	= new Integer( (String) params.get("dom_ciudad"));
			String direccion	= TextUtil.filtrar((String)params.get("direccion"));
			String responsable 	= TextUtil.filtrar((String)params.get("responsable"));
			String estado 		= TextUtil.filtrar((String)params.get("activo"));

			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			
			String query = "" +
					" update bm_habilitado set " +
					"ha_nombre = '" + nombre + "', " + 
					"ha_ubicacion = '" + ubicacion + "', " + 
					"dom_ciudad = " + dom_ciudad + ", " + 
					"ha_direccion = '" + direccion + "', " + 
					"ha_responsable = '" + responsable + "', " + 
					"ha_activo = '" + estado + "' " + 
					"where ha_codigo = " + codigo;
			
			QueryLogger.log(uw, query);
			template.update(query);
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean eliminarHabilitado(Map params, UsuarioWeb uw){
		/*
		HibernateTemplate ht = getHibernateTemplate();
		Boolean resultado = (Boolean) ht.execute( new HabilitadoEliminarHibernateCallback(params) );
		return resultado.booleanValue();
		*/
		
		try {
			Integer codigo = new Integer( (String)params.get("codigo") );			

			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			String query = "delete from bm_habilitado where ha_codigo = " + codigo;
			
			QueryLogger.log(uw, query);
			template.execute(query);
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	/**
	 * Esta función retorna un HabilitadoDTO asociado al usuario
	 * que recibe como parametro
	 * @param usuarioWeb (usualmente el usuario que está el la sesión)
	 * @return HabilitadoDTO asociado
	 */
	
	public HabilitadoDTO habilitadoPorUsuario(UsuarioWeb usuario){
		
		try {

			HabilitadoDTO h = null;
			
			if ("admin".equalsIgnoreCase(usuario.getNombreUsuario())){
				h = getHabilitadoPorCodigo(1, usuario); // uso un habilitado por defecto
			} else {
				h = getHabilitadoPorCodigo(Integer.parseInt(usuario.getNombreUsuario()), usuario);
			}
			
			return h;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 

	}

	/**
	 * Clase interior que efectivamente hace la query, usando los parametros
	 * que vienen del Map params, incluyendo el inicio del listado para la
	 * paginacion
	 * 
	 * @author denis.fuenzalida
	 *
	 */

	class HabilitadoListadoMappingQuery extends MappingSqlQuery {

		public HabilitadoListadoMappingQuery(DataSource ds, Map params, UsuarioWeb uw){
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

			ArrayList listaWhere = new ArrayList();
			
			String query = "select first " + (inicio+maxResults) + " * from bm_habilitado ";
			
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
			
			query = query + QueryUtil.getWhere(listaWhere) + " order by ha_codigo asc";
			
			QueryLogger.log(uw, query);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			HabilitadoDTO habilitado = new HabilitadoDTO();
			ReflectionFiller.fill( mapaHabilitado, rs, habilitado );
			return habilitado;
		}
		
	}


}
