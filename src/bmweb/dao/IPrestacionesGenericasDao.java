/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.List;

import bmweb.dto.PrestacionGenericaDTO;

/**
 * @author denis.fuenzalida
 */
public interface IPrestacionesGenericasDao {

	public abstract List lista();
	public abstract PrestacionGenericaDTO prestacionPorCodigo(int codigo);

}