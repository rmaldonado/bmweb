/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.HashMap;
import java.util.List;

/**
 * @author denis.fuenzalida
 */
public interface ICiudadDao {
	public abstract List lista();

	public abstract HashMap mapa();
	
	public abstract List listaJurisdicciones();
}