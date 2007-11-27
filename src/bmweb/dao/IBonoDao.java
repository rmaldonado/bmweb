/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bmweb.dto.BonoDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 */
public interface IBonoDao {
	public abstract int getFolio();

	public abstract BonoDTO bonoWebPorFolio(int folio, UsuarioWeb uw);
	public abstract BonoDTO bonoWebPorSerial(int serial, UsuarioWeb uw);

	public abstract List getBonos(Map params, UsuarioWeb uw);

	public abstract BonoDTO guardarBonoWeb(Map params, UsuarioWeb uw);
	public abstract BonoDTO guardarBonoValoradoWeb(Map params, UsuarioWeb uw);
	public abstract boolean anularBonoValorado(Map params , UsuarioWeb uw);
	public abstract List getDetalleBonoWeb(int folio, UsuarioWeb uw);
	public abstract List getDetalleBonoValoradoWeb(int folio, UsuarioWeb uw);
	
	public abstract List getPrestacionesPorCodigo(String codigo, Date fecha, String rutPrestador, UsuarioWeb uw);
	public abstract List getPrestacionesPorNombre(String nombre, Date fecha, String rutPrestador, UsuarioWeb uw);
	public abstract List buscarPrestaciones(Integer familia, String nombre, Date fecha, String rutPrestador, UsuarioWeb uw);
	
	public abstract boolean guardarDetalle(BonoDTO bono, List listaItems, UsuarioWeb uw);
	public abstract boolean guardardetalleCarMas(BonoDTO bono, List listaItems, UsuarioWeb uw);
	
	public abstract int getValorTotalBono(int bonoSerial, String rutPrestador, UsuarioWeb uw);
	public abstract Integer ValorConvenioPabellon(Integer convenio, Integer pabellon);
	
	public abstract boolean puedeEmitirBonosPorNivel(UsuarioWeb uw);
	public abstract boolean esBonoValorado(int folio);
}