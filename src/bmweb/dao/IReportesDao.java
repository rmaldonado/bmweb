package bmweb.dao;

import java.util.List;
import java.util.Map;

import bmweb.util.UsuarioWeb;

public interface IReportesDao {
	
	public List ejecutarReporte(String reporteId, Map params, UsuarioWeb uw);
	
}
