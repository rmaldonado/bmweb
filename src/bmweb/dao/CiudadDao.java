/**
 * 
 * @author denis.fuenzalida
 * 
 * 2006 02 11
 * 
 * Cambio para no utilizar hibernate, solo MappingSqlQuery de Spring
 * 
 * 2005 05 27
 *
 * Clase utilitaria para recuperar el listado de ciudades de la tabla "keyword_det"
 * como un ArrayList de CiudadDTOs
 */
package bmweb.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.CiudadDTO;
import bmweb.util.ReflectionFiller;
/**
 * @author denis.fuenzalida
 *
 * DAO para acceder al listado de Ciudades
 */

public class CiudadDao implements ICiudadDao {
	
	private static List listaCiudades = null;
	private static List listaJurisdicciones = null;
	
	private static HashMap mapaCiudades = null;
	private static HashMap mapaJurisdicciones = null;
	private DataSource dataSource;
	
	private static String[] mapaColumnas = new String[] {
		"codigo", "key_id",
		"nombre", "key_descr",
		"sistema", "key_sist",
		"tabla", "key_word"
	};
	
	public void setDataSource(DataSource ds){ this.dataSource = ds; }
	      
	public List lista(){
		
		// si la tengo en el cache la uso, en caso contrario busco
		if (listaCiudades != null) return listaCiudades;

		/*
		HibernateTemplate ht = getHibernateTemplate();
		listaCiudades = (List) ht.execute( new CiudadesHibernateCallback() );
		*/
		
		CiudadesMappingQuery buscador = new CiudadesMappingQuery(dataSource, "CIUDAD");
		List listaCiudades = buscador.execute();
		
		return listaCiudades;
	}

	public HashMap mapa(){
		
		if (mapaCiudades != null) return mapaCiudades;
		
		try {
			mapaCiudades = new HashMap();
			
			// Obtengo la lista de las ciudades y con ella lleno el HashMap
			List lasCiudades = lista();
			
			for (int i = 0; lasCiudades != null && i<lasCiudades.size(); i++){
			    CiudadDTO c = (CiudadDTO) lasCiudades.get(i);
			    mapaCiudades.put( new Integer(c.getCodigo()) , c.getNombre() );
			}
	
			return mapaCiudades;

		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	public HashMap mapaJurisdicciones(){
		
		if (mapaJurisdicciones != null) return mapaJurisdicciones;
		
		try {
			mapaJurisdicciones = new HashMap();
			
			// Obtengo la lista de las ciudades y con ella lleno el HashMap
			List lasJurisdicciones = listaJurisdicciones();
			
			for (int i = 0; lasJurisdicciones != null && i<lasJurisdicciones.size(); i++){
			    CiudadDTO j = (CiudadDTO) lasJurisdicciones.get(i);
			    mapaJurisdicciones.put( new Integer(j.getCodigo()) , j.getNombre() );
			}
	
			return mapaJurisdicciones;

		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	/**
	 * Lista de jurisdicciones
	 */
	
	public List listaJurisdicciones(){
	
		if (listaJurisdicciones != null) return listaJurisdicciones;
		else {			
			CiudadesMappingQuery buscador = new CiudadesMappingQuery(dataSource, "JURISD");
			listaJurisdicciones = buscador.execute();
			return listaJurisdicciones;
		}

	}
	
	
	/**
	 * Clase interior
	 * @author denis.fuenzalida
	 *
	 */
	
	class CiudadesMappingQuery extends MappingSqlQuery {
		
		public CiudadesMappingQuery(DataSource ds, String dominio) {
			
			super();
			String query = "select distinct * from keyword_det where key_sist='BENMED' and key_word='" + dominio + "' order by key_descr";
			setDataSource(ds);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			CiudadDTO ciudad = new CiudadDTO();
			ReflectionFiller.fill(mapaColumnas, rs, ciudad);
			return ciudad;
		}
	}

}
