/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.List;
import java.util.Map;

/**
 * @author denis.fuenzalida
 */
public interface ICiudadDao {
	public abstract List lista();
	public abstract Map mapa();
	
	public abstract List listaJurisdicciones();	
	public abstract Map mapaJurisdicciones();
	
	public abstract List listaRegiones();	
	public abstract Map mapaRegiones();
	
	public abstract List listaAgencias();	
	public abstract Map mapaAgencias();
	
	
}