/*
 * Created on 17-05-2005
 *
 * Clase que mapea la tabla "bm_habilitado"
 * 
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis
 *
 */
public class HabilitadoDTO implements Serializable {

	public static final String NOMBRE_TABLA = "bm_habilitado";
	public static final String HABILITADO_ACTIVO = "S";
	public static final String HABILITADO_NO_ACTIVO = "N";

	private Integer codigo = new Integer(0);
	private String nombre = "";
	private String ubicacion = "";
	private Integer dom_ciudad = new Integer(0);
	private String direccion = "";
	private String responsable = "";
	private String activo = HABILITADO_NO_ACTIVO;
	
	
	/**
	 * Constructor sin argumentos requerido por Hibernate
	 */
	public HabilitadoDTO(){ }

	/**
	 * autogenerado :-)
	 * 
	 * @param codigo
	 * @param nombre
	 * @param ubicacion
	 * @param dom_ciudad
	 * @param direccion
	 * @param responsable
	 */
	
	public HabilitadoDTO(Integer codigo, String nombre, String ubicacion,
			Integer dom_ciudad, String direccion, String responsable) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
		this.ubicacion = ubicacion;
		this.dom_ciudad = dom_ciudad;
		this.direccion = direccion;
		this.responsable = responsable;
	}
	/**
	 * @return Returns the codigo.
	 */
	public Integer getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo The codigo to set.
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return Returns the direccion.
	 */
	public String getDireccion() {
		return direccion;
	}
	/**
	 * @param direccion The direccion to set.
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	/**
	 * @return Returns the dom_ciudad.
	 */
	public Integer getDom_ciudad() {
		return dom_ciudad;
	}
	/**
	 * @param dom_ciudad The dom_ciudad to set.
	 */
	public void setDom_ciudad(Integer dom_ciudad) {
		this.dom_ciudad = dom_ciudad;
	}
	/**
	 * @return Returns the nombre.
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre The nombre to set.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return Returns the responsable.
	 */
	public String getResponsable() {
		return responsable;
	}
	/**
	 * @param responsable The responsable to set.
	 */
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	/**
	 * @return Returns the ubicacion.
	 */
	public String getUbicacion() {
		return ubicacion;
	}
	/**
	 * @param ubicacion The ubicacion to set.
	 */
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getActivo() {
		return activo;
	}
	public void setActivo(String activo) {
		this.activo = activo;
	}
	
	// para el Reflectionfiller
	public void setDom_ciudad(Short ciudad){
		this.dom_ciudad = new Integer(ciudad.intValue());
	}
}
