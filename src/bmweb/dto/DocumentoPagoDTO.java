/*
 * Creado en 26-02-2006 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author denis.fuenzalida
 * 
 * Esta clase representa una fila de la tabla bm_docpago
 * donde se almacena la informacion de las facturas.
 * 
 * Se guardan las columnas:
 * 
 *  dp_serial					# AUTOGENERADO
 *  acree_rut char(13),			# RUT DEL PRESTADOR
 *  dom_tipdoc char(2),			# 'FA'
 *  dp_numero decimal(10,0),	# Numero de la factura que indica el usuario
 *  pb_codigo integer,			# RUT DEL PRESTADOR SIN DV
 *  dp_origen char(1),			# 'W'
 *
 */
public class DocumentoPagoDTO implements Serializable {

	private Long id;
	private String rutAcreedor;
	private String tipoDocumento;
	private Long numeroFactura;
	private Long codigoAcreedor;
	private String origen;
	private Date fechaLiquidacion;
	
	private List detalle;
	
	public DocumentoPagoDTO(){ }
		
	public Long getCodigoAcreedor() {
		return codigoAcreedor;
	}
	public void setCodigoAcreedor(Long codigoAcreedor) {
		this.codigoAcreedor = codigoAcreedor;
	}

	public void setCodigoAcreedor(Integer codigoAcreedor) {
		this.codigoAcreedor = new Long(codigoAcreedor.intValue());
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setId(Integer id) {
		this.id = new Long(id.intValue());
	}
	
	public Long getNumeroFactura() {
		return numeroFactura;
	}
	public void setNumeroFactura(Long numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	
	public void setNumeroFactura(Integer numeroFactura) {
		this.numeroFactura = new Long(numeroFactura.intValue());
	}
	
	public void setNumeroFactura(BigDecimal numeroFactura) {
		this.numeroFactura = new Long(numeroFactura.intValue());
	}
	
	
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getRutAcreedor() {
		return rutAcreedor;
	}
	public void setRutAcreedor(String rutAcreedor) {
		this.rutAcreedor = rutAcreedor;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public Date getFechaLiquidacion() {
		return fechaLiquidacion;
	}
	public void setFechaLiquidacion(Date fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}
	
	public void setFechaLiquidacion(java.sql.Date fl) {
		Date d = new Date(fl.getDate(), fl.getMonth(), fl.getYear());
		this.fechaLiquidacion = d;
	}
	
	
	
	public List getDetalle() {
		return detalle;
	}
	public void setDetalle(List detalle) {
		this.detalle = detalle;
	}
	
	
}
