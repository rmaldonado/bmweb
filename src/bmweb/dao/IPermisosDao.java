/*
 * Creado en 23-11-2005 por denis
 *
 */
package bmweb.dao;

import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 */

public interface IPermisosDao {
	public abstract UsuarioWeb getUsuarioWeb(String username, String nivel);
}