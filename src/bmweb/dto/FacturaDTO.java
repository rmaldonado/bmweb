/*
 * Creado en 27-12-2005 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author denis.fuenzalida
 *
 * DTO que representa una factura, que tiene un detalle de bonos asociados
 * -> una FacturaDTO tiene un conjunto de FacturaItem
 */

public class FacturaDTO implements Serializable {

	private Integer id;
	private Integer numero;
	private String observaciones;
	
	private List detalle = new ArrayList(); 
	
	// Estado que indica que una factura se ha impreso y no
	// se puede modificar su detalle de bonos asociados
	public static int FACTURA_ESTADO_CERTIFICADO = 7;
	
	// constructor sin parametros
	public FacturaDTO(){ }
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public List getDetalle() {
		return detalle;
	}
	public void setDetalle(List detalle) {
		this.detalle = detalle;
	}
}
