/*
 * Creado en 15-01-2006 por denis
 *
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis.fuenzalida
 *
 * DTO que permite recuperar las reglas de restricciones para emitir
 * bonos.
 *
 */
public class ReglaDTO implements Serializable {
	
	/*
    <class name="ReglaDTO" table="bmw_regla">
	    <id name="id" type="int" column="regla_id" unsaved-value="0">
			<generator class="identity"/>
		</id>

	 */

	public static int RESTRICCION_EDAD = 1;
	public static int RESTRICCION_SEXO = 2;
	public static int RESTRICCION_CANTIDAD_POR_PERIODO = 3;
	public static int RESTRICCION_CANTIDAD_BONOS_EMISOR_POR_PERIODO = 4;
	public static int RESTRICCION_PRESTACIONES_INCOMPATIBLES = 5;
	
	private Integer id;
	private Integer codPrestacion;
	private Integer restriccion;
	private Integer numero1;
	private Integer numero2;
	private String texto;
	private String mensaje;
	
	public ReglaDTO(){ }
	
	public Integer getCodPrestacion() {
		return codPrestacion;
	}
	public void setCodPrestacion(Integer codPrestacion) {
		this.codPrestacion = codPrestacion;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Integer getNumero1() {
		return numero1;
	}
	public void setNumero1(Integer numero1) {
		this.numero1 = numero1;
	}
	public Integer getNumero2() {
		return numero2;
	}
	public void setNumero2(Integer numero2) {
		this.numero2 = numero2;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public Integer getRestriccion() {
		return restriccion;
	}
	public void setRestriccion(Integer restriccion) {
		this.restriccion = restriccion;
	}
}
