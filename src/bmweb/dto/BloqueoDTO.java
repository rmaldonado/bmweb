package bmweb.dto;

import java.util.Date;

/**
 * @author denis
 *
 * Clase que mapea contra la tabla bmweb_bloqueo que registra los
 * bloqueos de filas que se requiere implementar.
 * 
 * Se bloquea una fila indicando el nombre de la tabla a bloquear
 * y la PK del registro que se quiere bloquear. La clase registra
 * internamente el timestamp. Se debe modificar el valor de la
 * constante que indica por cuanto tiempo es valido el bloqueo.
 */

public class BloqueoDTO {

	private Integer id;
	private String tabla;
	private Integer idFila;
	private Long timestamp;
	private String usuario;
	
	private final int TIMEOUT = 10 * 60 * 1000; // 10 minutos
	
	/**
	 * Revisa si el bloqueo está vencido porque ha pasado más de el
	 * tiempo permitido para un bloqueo
	 * @return
	 */
	public boolean estaVencido(){
		
		// bloqueo mal definido esta vencido
		if (timestamp == null) return true;
		
		// Creo un bloqueoDTO temporal  para obtener el timestamp actual
		BloqueoDTO b = new BloqueoDTO("", new Integer(0), "");
		
		// Si la expiración está en el futuro, todavia es un bloqueo valido
		if ( this.timestamp.longValue() + TIMEOUT >  b.timestamp.longValue() ) return false;
		else return true;
	}
	
	public BloqueoDTO(){ }
	
	/**
	 * @param tabla
	 * @param idFila
	 */
	public BloqueoDTO(String tabla, Integer idFila, String usuario) {
		super();
		this.tabla = tabla;
		this.idFila = idFila;
		this.usuario = usuario;
		
		this.timestamp = new Long(new Date().getTime());
	}
	
	
	
	/**
	 * @return Returns the usuario.
	 */
	public String getUsuario() {
		return usuario;
	}
	/**
	 * @param usuario The usuario to set.
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	/**
	 * @return Returns the id.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return Returns the idFila.
	 */
	public Integer getIdFila() {
		return idFila;
	}
	/**
	 * @param idFila The idFila to set.
	 */
	public void setIdFila(Integer idFila) {
		this.idFila = idFila;
	}
	/**
	 * @return Returns the tabla.
	 */
	public String getTabla() {
		return tabla;
	}
	/**
	 * @param tabla The tabla to set.
	 */
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	/**
	 * @return Returns the timestamp.
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp The timestamp to set.
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
