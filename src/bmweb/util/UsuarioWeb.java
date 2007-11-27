/*
 * Created on 29-05-2005
 *
 */
package bmweb.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author denis
 *
 */
public class UsuarioWeb {

	private String nombreUsuario = null;
	private String nombreCompleto = null;
	private String IP = null;
	private String nivel = null;
	
	// El RUT Emisor (para el bono Valorado y No Valorado)
	private String rutEmisor = null;
	
	// Si tiene CMC, se deben restringir los CMCs a los que puede hacer bonos
	private String CMC = null;
	private boolean puedeHacerBonosSantiago = true;
	
	public static final String ATRIBUTO_USUARIO_WEB = "usuarioweb";
	
	private Set permisos = new HashSet();
	
	
	public UsuarioWeb(String nombreUsuario){
		super();
		this.setNombreUsuario(nombreUsuario);
	}
		
	/**
	 * @return Returns the nombreUsuario.
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	/**
	 * @param nombreUsuario The nombreUsuario to set.
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	
	/**
	 * @return Returns the nombreCompleto.
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	/**
	 * @param nombreCompleto The nombreCompleto to set.
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public boolean tienePermiso(String nombrePermiso){
		
		// Para el permiso "" siempre hay permiso, si hay una sesion
		if ("".equals(nombrePermiso)) return true;
		
		if (permisos.contains(nombrePermiso)) return true;
		else return false;
		
	}
	
	public void agregarPermiso(String nombrePermiso){
		permisos.add(nombrePermiso);
	}
	
	public void quitarPermiso(String nombrePermiso){
		permisos.remove(nombrePermiso);
	}
	
	public String getCMC() {
		return CMC;
	}
	public void setCMC(String cmc) {
		CMC = cmc;
	}
	
	public boolean puedeHacerBonosSantiago() {
		return puedeHacerBonosSantiago;
	}
	public void setPuedeHacerBonosSantiago(boolean puedeHacerBonosSantiago) {
		this.puedeHacerBonosSantiago = puedeHacerBonosSantiago;
	}
	public String getRutEmisor() {
		return rutEmisor;
	}
	public void setRutEmisor(String rutEmisor) {
		this.rutEmisor = rutEmisor;
	}
	
	public String getIP() {
		return IP;
	}
	public void setIP(String ip) {
		IP = ip;
	}

	/**
	 * @return Returns the nivel.
	 */
	public String getNivel() {
		return nivel;
	}

	/**
	 * @param nivel The nivel to set.
	 */
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
}
