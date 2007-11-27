/*
 * Creado en 15-08-2005 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis
 *
 * Clase que mapea un item generico del bono
 */
public class BonoWItemDTO implements Serializable {

	private int id;
	private int bonoSerial;
	private int codigoPrestacion;
	
	public BonoWItemDTO(){ }

	public int getBonoSerial() {
		return bonoSerial;
	}
	public void setBonoSerial(int bonoSerial) {
		this.bonoSerial = bonoSerial;
	}
	public int getCodigoPrestacion() {
		return codigoPrestacion;
	}
	public void setCodigoPrestacion(int codigoPrestacion) {
		this.codigoPrestacion = codigoPrestacion;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
