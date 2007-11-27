/*
 * Created on Aug 5, 2005
 */
package bmweb.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author denis.fuenzalida
 */
public class BeneficiarioDTO implements Serializable {
   private int bene;
   private String dgv;
   private String nombre;
   private String pat;
   private String mat;
   
   private Date fechaNacimiento;
   private Date fechaFallecimiento;
   
   private String sexo;
  
   public BeneficiarioDTO(){ }
   
	/**
	 * @return Returns the bene.
	 */
	public int getBene() {
		return bene;
	}
	/**
	 * @param bene The bene to set.
	 */
	public void setBene(int bene) {
		this.bene = bene;
	}
	/**
	 * @return Returns the dgv.
	 */
	public String getDgv() {
		return dgv;
	}
	/**
	 * @param dgv The dgv to set.
	 */
	public void setDgv(String dgv) {
		this.dgv = dgv;
	}
	/**
	 * @return Returns the mat.
	 */
	public String getMat() {
		return mat;
	}
	/**
	 * @param mat The mat to set.
	 */
	public void setMat(String mat) {
		this.mat = mat;
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
	 * @return Returns the pat.
	 */
	public String getPat() {
		return pat;
	}
	/**
	 * @param pat The pat to set.
	 */
	public void setPat(String pat) {
		this.pat = pat;
	}
	
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	/**
	 * @return Returns the fechaFallecimiento.
	 */
	public Date getFechaFallecimiento() {
		return fechaFallecimiento;
	}
	/**
	 * @param fechaFallecimiento The fechaFallecimiento to set.
	 */
	public void setFechaFallecimiento(Date fechaFallecimiento) {
		this.fechaFallecimiento = fechaFallecimiento;
	}
	/**
	 * @return Returns the fechaNacimiento.
	 */
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	/**
	 * @param fechaNacimiento The fechaNacimiento to set.
	 */
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
}
