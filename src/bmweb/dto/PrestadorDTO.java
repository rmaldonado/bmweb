/*
 * Creado en 02-08-2005 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis
 *
 */
public class PrestadorDTO implements Serializable {

	private String rutAcreedor;
	private String razonSocial;
	private int codCiudad; 
	private int rut;
	
	public PrestadorDTO(){ }
	
	public int getRut() {
		return rut;
	}
	public void setRut(int rut) {
		this.rut = rut;
	}
	
	// ReflectionFiller
	public void setRut(Integer r){
		this.rut = r.intValue();
	}
	
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getRutAcreedor() {
		return rutAcreedor;
	}
	public void setRutAcreedor(String rutAcreedor) {
		this.rutAcreedor = rutAcreedor;
	}
	public int getCodCiudad() {
		return codCiudad;
	}

	public void setCodCiudad(int codCiudad) {
		this.codCiudad = codCiudad;
	}
	
	public void setCodCiudad(Short codCiudad) {
		this.codCiudad = codCiudad.shortValue();
	}

	public void setCodCiudad(Integer codCiudad) {
		this.codCiudad = codCiudad.intValue();
	}
	

}
