package bmweb.dao;

import java.util.List;
import java.util.Map;

import bmweb.util.UsuarioWeb;

public interface IConveniosDao {

	public List getConvenios(Map params, UsuarioWeb uw);
	
	public List getValcon(Map params, UsuarioWeb uw);
	
	public int guardarNuevoConvenio(int rutPrestador, List listaValcon) throws Exception;

	public void autorizarConvenio(Map params, UsuarioWeb uw) throws Exception;

}
