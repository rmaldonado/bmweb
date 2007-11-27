/*
 * Created on 04-06-2005
 *
 * Clase que mapea la tabla "bmweb_ciudad" que contiene
 * el codigo y nombre de las ciudades en la aplicación
 */
package bmweb.dto;

import java.io.Serializable;

/**
 * @author denis
 * 
 */
public class CiudadDTO implements Serializable {

	private int codigo;
	private String nombre;
	private String sistema;
	private String tabla;
	
	
	public CiudadDTO(){ }
		
	/**
	 * @param codigo
	 * @param nombre
	 */
	public CiudadDTO(int codigo, String nombre) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
	}
	
	public String getSistema() {
		return sistema;
	}
	public void setSistema(String sistema) {
		this.sistema = sistema;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	/**
	 * @return Returns the codigo.
	 */
	public int getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo The codigo to set.
	 */
	public void setCodigo(int codigo) {
		this.codigo = codigo;
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
	
	// metodos ReflectionFiller
	public void setCodigo(Integer cod){
		this.codigo = cod.intValue();
	}
	
	public void setCodigo(String codigo){
		this.codigo = Integer.parseInt(codigo.trim());
	}
}
