/*
 * Creado en 27-12-2005 por denis
 *
 */
package bmweb.manager;

import java.util.List;
import java.util.Map;

import bmweb.dto.DocumentoPagoDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 */

public interface IFacturaManager {
	public abstract DocumentoPagoDTO buscarPorNumero(UsuarioWeb uw, int numeroFactura);

	public abstract DocumentoPagoDTO crear(UsuarioWeb uw, int numeroFactura);

	public abstract void borrar(UsuarioWeb uw, int numeroFactura);

	public abstract void agregarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial);

	public abstract void quitarBono(UsuarioWeb uw, int numeroFactura, int bonoSerial);

	public abstract DocumentoPagoDTO buscarPorBonoSerial(UsuarioWeb uw, int serialBono);
	
	public abstract List listado(UsuarioWeb uw, Map params);
	
	public abstract List listadoBonosSinFacturar(UsuarioWeb uw); // lista de String[] {folioBono, bonoSerial }

	public abstract boolean cerrarFactura(Long serialFactura, UsuarioWeb uw);
	
	public abstract boolean esFacturaCerrada(Long serialFactura, UsuarioWeb uw);

}