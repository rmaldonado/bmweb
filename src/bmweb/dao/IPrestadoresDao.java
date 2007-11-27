/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.Date;
import java.util.List;

import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 */
public interface IPrestadoresDao {

	public abstract List prestadoresPorCiudadYPrestacion(int codCiudad, int codPrestacion, UsuarioWeb uw, String rutDirecto);
	public abstract List prestadoresPorCiudadYPrestacionGenerica(int codCiudad, int codPrestacionGenerica, UsuarioWeb uw);

	public abstract PrestadorDTO prestadorPorRut(String rut, UsuarioWeb uw);
	public abstract PrestadorDTO prestadorPorRutAux(String rut, UsuarioWeb uw);
	
	public abstract int[] copagoYAportesPorPrestador(String CMC, String rutPrestador, List listaPrestaciones, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int codPabellon, String conValorCobrado, UsuarioWeb uw );
	public abstract int[] copagoYAportesPorPrestadorCM(String CMC, String rutPrestador, List listaPrestaciones, Date fecha, int tipoPrestacion, int codProfesional, String codContrato, int salaComun, int codPabellon, String conValorCobrado, UsuarioWeb uw );
	
	public abstract String autorizarPrestacion(String CMC, Integer rutEmisor, BeneficiarioDTO beneficiario, int codPrestacion, int cantidad, UsuarioWeb uw);
	public abstract String buscarPrestacionesIncompatibles(int[] prestacionesBono, int prestacionNueva, UsuarioWeb uw);
	
	public abstract Integer convenioPorPrestadorYPrestacion(String rutPrestador, String codPrestacion, UsuarioWeb uw);
	
	public abstract Integer pabellonPorCodigo(int codigoPrestacion, UsuarioWeb uw);
	
	public abstract boolean prestadorEsPagoDirecto(String rutPrestador);
	public abstract boolean EsCirugia(String codigoPrestacion);
	public abstract boolean prestadorEsPagoEnAgencia(String rutPrestador);
	
	public abstract boolean prestadorEsArancelDiferenciado(int rutPrestador);
}