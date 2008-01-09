    package bmweb.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
  
/**
 * @author denis
 * 
 * Clase que mapea contra la tabla bm_bono
 */

public class BonoDTO implements Serializable {
	
	/** 
	 * Constantes definidas para reflejar codigos 	de la
	 * base de datos
	 */
	 
	public static final String TIPOBONO_SINDETALLE	= "B";
	public static final String TIPOBONO_DIGITADO	= "D";
	public static final String TIPOBONO_FACTURADO	= "F";
	public static final String TIPOBONO_ABIERTO		= "G";
	public static final String TIPOBONO_VALORADO	= "V";
	public static final String TIPOBONO_WEB			= "W"; // ojo!
 
	public static final Integer CAUSADERIVACION_NOHORASDISPONIBLES 	= new Integer(1);
	public static final Integer CAUSADERIVACION_EXAMENFUERAHOSPITAL	= new Integer(2);
	public static final Integer CAUSADERIVACION_EQUIPOFUERASERVICIO	= new Integer(3);
	public static final Integer CAUSADERIVACION_AUTORIZAMEDICOJEFE	= new Integer(4);
	public static final Integer CAUSADERIVACION_URGENCIASMEDICAS 	= new Integer(5);
	public static final Integer CAUSADERIVACION_CONTROLPOSTOPERATORIO = new Integer(6);
	public static final Integer CAUSADERIVACION_CASOESPECIAL 		= new Integer(7);
	public static final Integer CAUSADERIVACION_ARANCELELEVADO 		= new Integer(8);
	public static final Integer CAUSADERIVACION_BONOPROVINCIA 		= new Integer(9);
	
	public static final Integer PRESTADORDERIVADO_HOSPITALDIPRECA	= new Integer(1);
	public static final Integer PRESTADORDERIVADO_HOSCAR			= new Integer(2);
	public static final Integer PRESTADORDERIVADO_SERMED			= new Integer(3);
	public static final Integer PRESTADORDERIVADO_CENTROSPARTICULARES	= new Integer(4	);
	public static final Integer PRESTADORDERIVADO_OTROSCENTROS		= new Integer(5);

	public static final String TIPOPRESTADOR_PSIQUIATRICO	= "S";
	public static final String TIPOPRESTADOR_CASAREPOSO		= "R";
	public static final String TIPOPRESTADOR_NORMAL			= "N";
 
	public static final String ESTADOBONO_IMPRESO			= "P";
	public static final String ESTADOBONO_ANULADO			= "A";
	
	/*
        <property name="tipoBono" type="string" column="dom_tipbon" not-null="false"/>
        <property name="folio" type="int" column="bo_folio" not-null="true"/>
        <property name="rutPrestador" type="string" column="pb_rut" not-null="false"/>
        <property name="carneBeneficiario" type="string" column="be_carne" not-null="false"/>
        <property name="fechaEmision" type="date" column="bo_fecemi" not-null="false"/>
        <property name="idCiudad" type="int" column="dom_ciudad" not-null="false"/>
        <property name="codigoHabilitado" type="int" column="ha_codigo" not-null="false"/>
        <property name="rutImponente" type="string" column="bo_rutimp" not-null="false"/>
        <property name="numeroAtencion" type="int" column="bo_nroate" not-null="false"/>
        <property name="idFacturaPrestador" type="int" column="dp_serial" not-null="false"/>
        <property name="idLiquidacion" type="int" column="bm_liquida" not-null="false"/>
        <property name="codDerivacionCentroPrivado" type="int" column="dom_cauext" not-null="false"/>
        <property name="codPrestadorDerivado" type="int" column="dom_deriva" not-null="false"/>
        <property name="codTipoPrestador" type="string" column="dp_origen" not-null="false"/>
        <property name="codEstadoBono" type="string" column="dom_estbon" not-null="false"/>
	*/
	
	private Integer id;
	private String tipoBono;
	private Integer folio;
	private String rutPrestador;
	private String carneBeneficiario;
	private Date fechaEmision;
	private Integer idCiudad;
	private Integer codigoHabilitado;
	private String rutImponente;
	private Integer numeroAtencion;
	private Integer idFacturaPrestador;
	private String idLiquidacion;
	private Integer codDerivacionCentroPrivado;
	private Integer codPrestadorDerivado;
	private String codTipoPrestador;
	private String codEstadoBono;

	private int valorTotal = 0;
	
	private List items;
	
	public BonoDTO(){ }


	/**
	 * @return Returns the items.
	 */
	public List getItems() {
		return items;
	}
	/**
	 * @param items The items to set.
	 */
	public void setItems(List items) {
		this.items = items;
	}
	/**
	 * @return Returns the carneBeneficiario.
	 */
	public String getCarneBeneficiario() {
		return carneBeneficiario;
	}
	/**
	 * @param carneBeneficiario The carneBeneficiario to set.
	 */
	public void setCarneBeneficiario(String carneBeneficiario) {
		this.carneBeneficiario = carneBeneficiario;
	}
	/**
	 * @return Returns the codDerivacionCentroPrivado.
	 */
	public Integer getCodDerivacionCentroPrivado() {
		return codDerivacionCentroPrivado;
	}
	/**
	 * @param codDerivacionCentroPrivado The codDerivacionCentroPrivado to set.
	 */
	public void setCodDerivacionCentroPrivado(Integer codDerivacionCentroPrivado) {
		this.codDerivacionCentroPrivado = codDerivacionCentroPrivado;
	}
	/**
	 * @return Returns the codEstadoBono.
	 */
	public String getCodEstadoBono() {
		return codEstadoBono;
	}
	/**
	 * @param codEstadoBono The codEstadoBono to set.
	 */
	public void setCodEstadoBono(String codEstadoBono) {
		this.codEstadoBono = codEstadoBono;
	}
	/**
	 * @return Returns the codigoHabilitado.
	 */
	public Integer getCodigoHabilitado() {
		return codigoHabilitado;
	}
	/**
	 * @param codigoHabilitado The codigoHabilitado to set.
	 */
	public void setCodigoHabilitado(Integer codigoHabilitado) {
		this.codigoHabilitado = codigoHabilitado;
	}
	/**
	 * @return Returns the codPrestadorDerivado.
	 */
	public Integer getCodPrestadorDerivado() {
		return codPrestadorDerivado;
	}
	/**
	 * @param codPrestadorDerivado The codPrestadorDerivado to set.
	 */
	public void setCodPrestadorDerivado(Integer codPrestadorDerivado) {
		this.codPrestadorDerivado = codPrestadorDerivado;
	}
	/**
	 * @return Returns the codTipoPrestador.
	 */
	public String getCodTipoPrestador() {
		return codTipoPrestador;
	}
	/**
	 * @param codTipoPrestador The codTipoPrestador to set.
	 */
	public void setCodTipoPrestador(String codTipoPrestador) {
		this.codTipoPrestador = codTipoPrestador;
	}
	/**
	 * @return Returns the fechaEmision.
	 */
	public Date getFechaEmision() {
		return fechaEmision;
	}
	/**
	 * @param fechaEmision The fechaEmision to set.
	 */
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	
	public void setFechaEmision(java.sql.Date fecha){
		this.fechaEmision = new Date(fecha.getTime()); 
	}
	/**
	 * @return Returns the folio.
	 */
	public Integer getFolio() {
		return folio;
	}
	/**
	 * @param folio The folio to set.
	 */
	public void setFolio(Integer folio) {
		this.folio = folio;
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
	 * @return Returns the idCiudad.
	 */
	public Integer getIdCiudad() {
		return idCiudad;
	}
	/**
	 * @param idCiudad The idCiudad to set.
	 */
	public void setIdCiudad(Integer idCiudad) {
		this.idCiudad = idCiudad;
	}
	public void setIdCiudad(Short id){
		this.idCiudad = new Integer(id.intValue());
	}
	/**
	 * @return Returns the idFacturaPrestador.
	 */
	public Integer getIdFacturaPrestador() {
		return idFacturaPrestador;
	}
	/**
	 * @param idFacturaPrestador The idFacturaPrestador to set.
	 */
	public void setIdFacturaPrestador(Integer idFacturaPrestador) {
		this.idFacturaPrestador = idFacturaPrestador;
	}
	/**
	 * @return Returns the idLiquidacion.
	 */
	public String  getIdLiquidacion() {
		return idLiquidacion;
	}
	/**
	 * @param idLiquidacion The idLiquidacion to set.
	 */
	public void setIdLiquidacion(String idLiquidacion) {
		this.idLiquidacion = idLiquidacion;
	}
	/**
	 * @return Returns the numeroAtencion.
	 */
	public Integer getNumeroAtencion() {
		return numeroAtencion;
	}
	/**
	 * @param numeroAtencion The numeroAtencion to set.
	 */
	public void setNumeroAtencion(Integer numeroAtencion) {
		this.numeroAtencion = numeroAtencion;
	}

	public void setNumeroAtencion(Short numeroAtencion) {
		this.numeroAtencion = new Integer(numeroAtencion.intValue());
	}

	/**
	 * @return Returns the rutImponente.
	 */
	public String getRutImponente() {
		return rutImponente;
	}
	/**
	 * @param rutImponente The rutImponente to set.
	 */
	public void setRutImponente(String rutImponente) {
		this.rutImponente = rutImponente;
	}
	/**
	 * @return Returns the rutPrestador.
	 */
	public String getRutPrestador() {
		return rutPrestador;
	}
	/**
	 * @param rutPrestador The rutPrestador to set.
	 */
	public void setRutPrestador(String rutPrestador) {
		this.rutPrestador = rutPrestador;
	}
	/**
	 * @return Returns the tipoBono.
	 */
	public String getTipoBono() {
		return tipoBono;
	}
	/**
	 * @param tipoBono The tipoBono to set.
	 */
	public void setTipoBono(String tipoBono) {
		this.tipoBono = tipoBono;
	}
	
	/**
	 * @return Returns the valorTotal.
	 */
	public int getValorTotal() {
		return valorTotal;
	}
	/**
	 * @param valorTotal The valorTotal to set.
	 */
	public void setValorTotal(int valorTotal) {
		this.valorTotal = valorTotal;
	}
}
