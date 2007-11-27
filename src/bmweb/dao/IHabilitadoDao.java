/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.List;
import java.util.Map;

import bmweb.dto.HabilitadoDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 */
public interface IHabilitadoDao {
	public abstract List getHabilitados(Map params, UsuarioWeb uw);

	public abstract boolean activarDesactivarHabilitado(Map params, UsuarioWeb uw);

	public abstract boolean crearHabilitado(Map params, UsuarioWeb uw);

	public abstract boolean revisarHabilitadoExiste(Map params, UsuarioWeb uw);

	public abstract HabilitadoDTO getHabilitadoPorCodigo(int codigo, UsuarioWeb uw);

	public abstract HabilitadoDTO editarHabilitado(Map params, UsuarioWeb uw);

	public abstract boolean modificarHabilitado(Map params, UsuarioWeb uw);

	public abstract boolean eliminarHabilitado(Map params, UsuarioWeb uw);

	/**
	 * Esta función retorna un HabilitadoDTO asociado al usuario
	 * que recibe como parametro
	 * @param usuarioWeb (usualmente el usuario que está el la sesión)
	 * @return HabilitadoDTO asociado
	 */
	public abstract HabilitadoDTO habilitadoPorUsuario(UsuarioWeb usuario);
}