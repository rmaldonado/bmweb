/*
 * Creado en 08-08-2005 por denis
 *
 */
package bmweb.dto;
import java.io.Serializable;
import java.util.Date;
/**
 * @author denis
 *
        <property name="repart" type="int" column="cod_repart" not-null="false"/>
        <property name="impo" type="int" column="nro_impo" not-null="false"/>
        <property name="correl" type="int" column="nro_correl" not-null="false"/>         
        <property name="rbene" type="int" column="rut_bene" not-null="false"/>
        <property name="rimpo" type="int" column="rut_impo" not-null="false"/>
 */
public class RolbeneDTO implements Serializable {

	private String repart = "";
	private int impo = 0;
	private int correl = 0;
	private int rbene = 0;
	private int rimpo = 0;
	private String contrato = "";
	private int estado = 0;
	private Date fecini = null;
	private Date fecter = null;
	
	public RolbeneDTO(){}
	
	
	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	public int getCorrel() {
		return correl;
	}
	public void setCorrel(int correl) {
		this.correl = correl;
	}
	public int getImpo() {
		return impo;
	}
	public void setImpo(int impo) {
		this.impo = impo;
	}
	public int getRbene() {
		return rbene;
	}
	public void setRbene(int rbene) {
		this.rbene = rbene;
	}
	public String getRepart() {
		return repart;
	}
	public void setRepart(String repart) {
		this.repart = repart;
	}
	public int getRimpo() {
		return rimpo;
	}
	public void setRimpo(int rimpo) {
		this.rimpo = rimpo;
	}
	/**
	 * @return Returns the estado.
	 */
	public int getEstado() {
		return estado;
	}
	/**
	 * @param estado The estado to set.
	 */
	public void setEstado(int estado) {
		this.estado = estado;
	}
	/**
	 * @return Returns the fecini.
	 */
	public Date getFecini() {
		return fecini;
	}
	/**
	 * @param fecini The fecini to set.
	 */
	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}
	/**
	 * @return Returns the fecter.
	 */
	public Date getFecter() {
		return fecter;
	}
	/**
	 * @param fecter The fecter to set.
	 */
	public void setFecter(Date fecter) {
		this.fecter = fecter;
	}
}
