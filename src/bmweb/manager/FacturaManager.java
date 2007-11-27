/*
 * Creado en 27-12-2005 por denis
 *
 */
package bmweb.manager;

import java.util.List;
import java.util.Map;

import bmweb.dao.IFacturaDao;
import bmweb.dto.DocumentoPagoDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 * Clase de negocio que realiza las operaciones sobre las Facturas.
 * 
 * La idea es que el servlet se entienda con el Manager, y solo el
 * Manager conoce al Dao. 
 * 
 * El IFacturaDao se puede inyectar para realizar pruebas unitarias.
 * 
 */

public class FacturaManager implements IFacturaManager {

	private IFacturaDao facturaDao;

	public boolean esFacturaCerrada(Long serialFactura, UsuarioWeb uw){
		return facturaDao.esFacturaCerrada(serialFactura, uw);
	}
	
	public boolean cerrarFactura(Long serialFactura, UsuarioWeb uw){
		return facturaDao.cerrarFactura(serialFactura, uw);
	}
	
	public DocumentoPagoDTO buscarPorNumero(UsuarioWeb uw, int numeroFactura){
		return facturaDao.buscarPorNumero(uw, numeroFactura);
	}

	public DocumentoPagoDTO crear(UsuarioWeb uw, int numeroFactura){
		return facturaDao.crear(uw, numeroFactura);
	}

	public void borrar(UsuarioWeb uw, int numeroFactura){
		facturaDao.borrar(uw, numeroFactura);
	}

	public void agregarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial){
		facturaDao.agregarBono(uw, numeroFactura, bonoSerial);
	}

	public void quitarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial){
		facturaDao.quitarBono(uw, numeroFactura, bonoSerial);
	}

	public DocumentoPagoDTO buscarPorBonoSerial(UsuarioWeb uw, int serialBono){
		return facturaDao.buscarPorBonoSerial(uw, serialBono);
	}
	
	public List listado(UsuarioWeb uw, Map params){
		
		int inicio = 0;
		try { inicio = Integer.parseInt((String) params.get("inicio")); } catch (Exception e) { }
		
		List lista = facturaDao.listado(uw, params);
		List resultado = lista.subList(inicio, lista.size());
		return resultado;

	}

	public List listadoBonosSinFacturar(UsuarioWeb uw){
		return facturaDao.listadoBonosSinFacturar(uw);
	}

	public IFacturaDao getFacturaDao() {
		return facturaDao;
	}
	public void setFacturaDao(IFacturaDao _facturaDao) {
		this.facturaDao = _facturaDao;
	}
}
