package bmweb.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/*
cv_codigo            integer       = Identificador del convenio (referencia)
pr_codigo            integer       = Codigo de la prestacion
vc_valor             decimal(16,0) = Valor convenido de la prestacion
vc_lispre            decimal(16,0) = Valor lista del prestador, para diferenciar a los Convenios con PAGO DIRECTO
dom_estvlc           smallint      = CAMPO NUEVO, indica el estado de una prestacion del convenio, posibles estados
                                     (NUEVO, MODIFICADO,ELIMINADO,etc.) 

 */
public class ValconDTO implements Serializable, Comparable {

	public static int ESTADO_NUEVO = 1;
	public static int ESTADO_MODIFICADO = 2;
	public static int ESTADO_ELIMINADO = 3;
	public static int ESTADO_RECHAZADO = 4;
	
	private int idConvenio;
	private int codigoPrestacion;
	private long valorCovenido;
	private long valorLista;
	private int estado;
	
	public ValconDTO(){ }
	
	public int compareTo(Object o) {
		if (ValconDTO.class.isAssignableFrom(o.getClass())){
			ValconDTO oV = (ValconDTO) o;
			return this.codigoPrestacion - oV.getCodigoPrestacion();
		} else {
			return -1;
		}
	}
	
	public int getIdConvenio() {
		return idConvenio;
	}
	public void setIdConvenio(int idConvenio) {
		this.idConvenio = idConvenio;
	}
	public void setIdConvenio(Integer idConvenio) {
		this.idConvenio = idConvenio.intValue();
	}
	public int getCodigoPrestacion() {
		return codigoPrestacion;
	}
	public void setCodigoPrestacion(int codigoPrestacion) {
		this.codigoPrestacion = codigoPrestacion;
	}
	public void setCodigoPrestacion(Integer codigoPrestacion) {
		this.codigoPrestacion = codigoPrestacion.intValue();
	}
	public float getValorCovenido() {
		return valorCovenido;
	}
	public void setValorCovenido(long valorCovenido) {
		this.valorCovenido = valorCovenido;
	}
	public void setValorCovenido(BigDecimal valorCovenido) {
		this.valorCovenido = valorCovenido.longValue();
	}
	public float getValorLista() {
		return valorLista;
	}
	public void setValorLista(long valorLista) {
		this.valorLista = valorLista;
	}
	public void setValorLista(BigDecimal valorLista) {
		this.valorLista = valorLista.longValue();
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	public void setEstado(Short estado) {
		this.estado = estado.intValue();
	}
	
	
	
}
