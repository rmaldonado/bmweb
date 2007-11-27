/*
 * Creado en 27-12-2005 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis.fuenzalida
 *
 * DTO que asocia un bono con el detalle de una factura
 */
public class FacturaItemDTO implements Serializable {
	
	private Integer id;
	private Integer facturaSerial;
	private Integer bonoSerial;
	
	// todo item pertenece a una factura
	private FacturaDTO factura;
	
	// constructor vacio
	public FacturaItemDTO(){ }
	
	public Integer getFacturaSerial() {
		return facturaSerial;
	}
	
	public void setFacturaSerial(Integer facturaSerial) {
		this.facturaSerial = facturaSerial;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBonoSerial() {
		return bonoSerial;
	}
	
	public void setBonoSerial(Integer bonoSerial) {
		this.bonoSerial = bonoSerial;
	}

	public FacturaDTO getFactura() {
		return factura;
	}
	
	public void setFactura(FacturaDTO factura) {
		this.factura = factura;
	}

}
