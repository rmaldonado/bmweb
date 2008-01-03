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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static List listaRegiones = null;
	private static List listaAgencias = null;
	private static List listaReparticiones = null;
	
	private static Map mapaCiudades = null;
	private static Map mapaJurisdicciones = null;
	private static Map mapaRegiones = null;
	private static Map mapaAgencias = null;
	private static Map mapaReparticiones = null;
	
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

		CiudadesMappingQuery buscador = new CiudadesMappingQuery(dataSource, "CIUDAD");
		List listaCiudades = buscador.execute();
		
		return listaCiudades;
	}

	public Map mapa(){
		
		if (mapaCiudades != null) return mapaCiudades;		
		else {
			mapaCiudades = listaToMapa(lista());
			return mapaCiudades;
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
	
	public Map mapaJurisdicciones(){
		
		if (mapaJurisdicciones != null) return mapaJurisdicciones;
		else {
			mapaJurisdicciones = listaToMapa(listaJurisdicciones());
			return mapaJurisdicciones;
		}
		
	}

	
	public List listaRegiones(){
		
		if (listaRegiones != null) return listaRegiones;
		else {			
			CiudadesMappingQuery buscador = new CiudadesMappingQuery(dataSource, "REGION");
			listaRegiones = buscador.execute();
			return listaRegiones;
		}
		
	}
	
	public Map mapaRegiones(){
		
		if (mapaRegiones != null) return mapaRegiones;
		else {
			mapaRegiones = listaToMapa(listaRegiones());
			return mapaRegiones;
		}
		
	}
	
	public List listaAgencias(){
		
		if (listaAgencias != null) return listaAgencias;
		else {			
			CiudadesMappingQuery buscador = new CiudadesMappingQuery(dataSource, "AGENCIA");
			listaAgencias = buscador.execute();
			return listaAgencias;
		}
		
	}
	
	public Map mapaAgencias(){
		
		if (mapaAgencias != null) return mapaAgencias;
		else {
			mapaAgencias = listaToMapa(listaAgencias());
			return mapaAgencias;
		}
		
	}
	
	
	public List listaReparticiones(){
		
		if ( listaReparticiones != null ) return listaReparticiones;
		else {
			listaReparticiones = new ArrayList();
			listaReparticiones.add(new CiudadDTO(1,  "Carabineros"));
			listaReparticiones.add(new CiudadDTO(2,  "Gendarmería"));
			listaReparticiones.add(new CiudadDTO(3,  "Investigaciones"));
			listaReparticiones.add(new CiudadDTO(4,  "Mutualidad"));
			listaReparticiones.add(new CiudadDTO(5,  "Funcionarios Dipreca"));
			listaReparticiones.add(new CiudadDTO(7,  "Pensionados"));
			listaReparticiones.add(new CiudadDTO(8,  "Montepiados"));
			listaReparticiones.add(new CiudadDTO(9,  "Investigaciones-2"));
			listaReparticiones.add(new CiudadDTO(10, "Exonerados-X"));
			listaReparticiones.add(new CiudadDTO(11, "Exonerados-Y"));
			
			return listaReparticiones;
		}
		
	}
	
	public Map mapaReparticiones(){

		if (mapaReparticiones!= null) return mapaReparticiones;
		else {
			mapaReparticiones = listaToMapa(listaReparticiones());
			return mapaReparticiones;
		}
		
	}
	
	
	private Map listaToMapa(List lista){
		
		
		try {
			Map res = new HashMap();
			
			for (int i = 0; null != lista && i<lista.size(); i++){
			    CiudadDTO j = (CiudadDTO) lista.get(i);
			    res.put( new Integer(j.getCodigo()) , j.getNombre() );
			}
	
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
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
