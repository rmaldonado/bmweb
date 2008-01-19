package bmweb.dao;

import java.util.List;
import java.util.Map;

import bmweb.util.UsuarioWeb;

public interface IConveniosDao {

	public List getConvenios(Map params, UsuarioWeb uw);
	
}
