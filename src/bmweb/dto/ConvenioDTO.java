/*
 * Created on Jul 29, 2005
 *
 */
package bmweb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * cv_codigo            serial        = Identificador del convenio
 * cv_glosa             char(45)      = Glosa descriptiva
 * pb_codigo            integer       = Codigo del Prestador 
 * af_codigo            integer       = Codigo del arancel Fonasa asociado
 * cv_fecini            date          = Fecha Inicio del convenio
 * cv_fecter            date          = Fecha de Termino del Convenio
 * dom_tipcon           smallint      = Dominio de tipo de convenio
 * cv_nrores            char(10)      = Numero de Resolucion de concurrencia  (Aportes)
 * cv_fecres            date          = Fecha de la Resolucion de concurrencia 
 * dom_moneda           smallint      = Dominio de la moneda en que se expresa el convenio ( Siempre es $ chileno) 
 * cv_reffon            char(1)       = Indicador convenio hace referencia a Fonasa (S)i/(N)o 
 * cv_refniv            smallint      = Nivel de referencia Fonasa (Nivel 1, 2 o 3) 
 * cv_reffac            decimal(5,2)  = Factor de referencia Fonasa
 * dom_estcvn           smallint      = CAMPO NUEVO, indica estado del convenio
 *                                      debemos analizar que estados ponerles( Vigente, en Proceso, etc..)
 * 
 */
public class ConvenioDTO implements Serializable {

	public static int CONVENIO_NUEVO = 1;
	public static int CONVENIO_MODIFICADO = 2;
	public static int CONVENIO_ELIMINADO = 3;

	private int codigo;
	private String glosa;
	private int codigoPrestador;
	private int codigoArancelFonasa;
	private Date fechaInicio;
	private Date fechaTermino;
	private int tipoConvenio;
	private String codigoConcurrencia;
	private Date fechaConcurrencia;
	private int moneda;
	private String referenciaFonasa;
	private int nivelReferenciaFonasa;
	private float factorRefFonasa;
	private int estadoConvenio;
	
	public ConvenioDTO(){ }

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo.intValue();
	}

	public String getGlosa() {
		return glosa;
	}

	public void setGlosa(String glosa) {
		this.glosa = glosa;
	}

	public int getCodigoPrestador() {
		return codigoPrestador;
	}

	public void setCodigoPrestador(int codigoPrestador) {
		this.codigoPrestador = codigoPrestador;
	}

	public void setCodigoPrestador(Integer codigoPrestador) {
		this.codigoPrestador = codigoPrestador.intValue();
	}

	public int getCodigoArancelFonasa() {
		return codigoArancelFonasa;
	}

	public void setCodigoArancelFonasa(int codigoArancelFonasa) {
		this.codigoArancelFonasa = codigoArancelFonasa;
	}

	public void setCodigoArancelFonasa(Integer codigoArancelFonasa) {
		this.codigoArancelFonasa = codigoArancelFonasa.intValue();
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public void setFechaInicio(java.sql.Date fechaInicio) {
		this.fechaInicio = new Date(fechaInicio.getTime());
	}

	public Date getFechaTermino() {
		return fechaTermino;
	}

	public void setFechaTermino(java.sql.Date fechaTermino) {
		this.fechaTermino = new Date(fechaTermino.getTime());
	}

	public int getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(int tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public void setTipoConvenio(Short tipoConvenio) {
		this.tipoConvenio = tipoConvenio.intValue();
	}

	public String getCodigoConcurrencia() {
		return codigoConcurrencia;
	}

	public void setCodigoConcurrencia(String codigoConcurrencia) {
		this.codigoConcurrencia = codigoConcurrencia;
	}

	public Date getFechaConcurrencia() {
		return fechaConcurrencia;
	}

	public void setFechaConcurrencia(Date fechaConcurrencia) {
		this.fechaConcurrencia = fechaConcurrencia;
	}

	public void setFechaConcurrencia(java.sql.Date fechaConcurrencia) {
		this.fechaConcurrencia = new Date(fechaConcurrencia.getTime());
	}

	public int getMoneda() {
		return moneda;
	}

	public void setMoneda(int moneda) {
		this.moneda = moneda;
	}

	public void setMoneda(Short moneda) {
		this.moneda = moneda.intValue();
	}

	public String getReferenciaFonasa() {
		return referenciaFonasa;
	}

	public void setReferenciaFonasa(String referenciaFonasa) {
		this.referenciaFonasa = referenciaFonasa;
	}

	public float getFactorRefFonasa() {
		return factorRefFonasa;
	}

	public void setFactorRefFonasa(float factorRefFonasa) {
		this.factorRefFonasa = factorRefFonasa;
	}

	public void setFactorRefFonasa(BigDecimal factorRefFonasa) {
		this.factorRefFonasa = factorRefFonasa.floatValue();
	}

	public int getEstadoConvenio() {
		return estadoConvenio;
	}

	public void setEstadoConvenio(int estadoConvenio) {
		this.estadoConvenio = estadoConvenio;
	}

	public void setEstadoConvenio(Short estadoConvenio) {
		this.estadoConvenio = estadoConvenio.intValue();
	}

	public int getNivelReferenciaFonasa() {
		return nivelReferenciaFonasa;
	}

	public void setNivelReferenciaFonasa(int nivelReferenciaFonasa) {
		this.nivelReferenciaFonasa = nivelReferenciaFonasa;
	};
	
	public void setNivelReferenciaFonasa(Short nivelReferenciaFonasa) {
		this.nivelReferenciaFonasa = nivelReferenciaFonasa.intValue();
	};
	
	
	
}
