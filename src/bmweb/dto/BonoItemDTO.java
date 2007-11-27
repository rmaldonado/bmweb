/*
 * Created on 24-05-2005
 *
 */
package bmweb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author denis
 *
 * Clase que mapea contra la tabla bm_bonite
 */

public class BonoItemDTO implements Serializable {

	/*
        <id name="id" type="int" column="bi_serial" unsaved-value="null">
            <!-- generator class="native"/ -->
        </id>

        <property name="idBono" type="int" column="bo_serial" not-null="false"/>
        <property name="idFacturaPrestador" type="int" column="dp_serial" not-null="false"/>
        <property name="tipoBono" type="string" column="dom_tipbon" not-null="false"/>
        <property name="codPrestacion" type="int" column="pr_codigo" not-null="true"/>
        <property name="codTipoPrestacion" type="int" column="dom_tippre" not-null="false"/>
        <property name="fechaEfectivaAtencionMedica" type="date" column="bi_fecate" not-null="false"/>
        <property name="valorCobradoPrestador" type="int" column="vc_valor" not-null="false"/>
        <property name="valorAporteDipreca" type="int" column="bi_apodip" not-null="false"/>
        <property name="valorAporteSeguro" type="int" column="bi_aposeg" not-null="false"/>
        <property name="codigoPabellon" type="int" column="pa_pabellon" not-null="false"/>
        <property name="cantidadAtenciones" type="int" column="bi_cantidad" not-null="false"/>
        <property name="codEstadoAtencion" type="string" column="dom_estliq" not-null="false"/>

        <property name="valorConvenidoPrestacion" type="float" column="bi_valcon" not-null="false"/>
        <property name="valorEnBono" type="float" column="bi_valbon" not-null="false"/>

        <property name="codConvenio" type="int" column="cv_codigo" not-null="false"/>
        <property name="cuentaContableAsoc" type="string" column="pr_ctapre" not-null="false"/>
        <property name="cuentaContableAsocPabellon" type="string" column="pr_ctapab" not-null="false"/>

        <property name="valorPabellon" type="float" column="bi_valpab" not-null="false"/>
        <property name="valorCopago" type="float" column="bi_copago" not-null="false"/>

        <property name="rutCompaniaSeguro" type="int" column="bi_rutseg" not-null="false"/>
        <property name="fechaInicioHospital" type="date" column="bi_fedeho" not-null="false"/>
        <property name="fechaTerminoHospital" type="date" column="bi_fehaho" not-null="false"/>

        <property name="aporteDiprecaPabellon" type="float" column="bi_apopab" not-null="false"/>

        <property name="incluyePabellon" type="string" column="bi_incpab" not-null="false"/>
        <property name="pensionadoOSalaComun" type="string" column="bi_pencom" not-null="false"/>
        <property name="tipoPaciente" type="string" column="bi_paciente" not-null="false"/>
        <property name="codProfesional" type="string" column="bi_quien" not-null="false"/>
        <property name="codRazonesNoAporte" type="int" column="dom_ranodi" not-null="false"/>
	 */
	
	private Integer id;
	private Integer idBono;
	private Integer idFacturaPrestador;
	private String tipoBono;
	private Integer codPrestacion;
	private Integer codTipoPrestacion;
	private Date fechaEfectivaAtencionMedica;
	private Integer valorCobradoPrestador;
	private Integer valorAporteDipreca;
	private Integer valorAporteSeguro;
	private Integer codigoPabellon;
	private Integer cantidadAtenciones;
	private String codEstadoAtencion;
	private Float valorConvenidoPrestacion;
	private Float valorEnBono;
	private Integer codConvenio;
	private String cuentaContableAsoc;
	private String cuentaContableAsocPabellon;
	private Float valorPabellon;
	private Float valorCopago;
	private Integer rutCompaniaSeguro;
	private Date fechaInicioHospital;
	private Date fechaTerminoHospital;
	private Float aporteDiprecaPabellon;
	private String incluyePabellon;
	private String pensionadoOSalaComun;
	private String tipoPaciente;
	private String codProfesional;
	private Integer codRazonesNoAporte;
	
	private String nombrePrestacion;
	
	private BonoDTO bonoDTO;
	
	public BonoItemDTO(){ }
	
	/**
	 * @return Returns the bonoDTO.
	 */
	public BonoDTO getBonoDTO() {
		return bonoDTO;
	}
	/**
	 * @param bonoDTO The bonoDTO to set.
	 */
	public void setBonoDTO(BonoDTO bonoDTO) {
		this.bonoDTO = bonoDTO;
	}
	/**
	 * @return Returns the aporteDiprecaPabellon.
	 */
	public Float getAporteDiprecaPabellon() {
		return aporteDiprecaPabellon;
	}
	/**
	 * @param aporteDiprecaPabellon The aporteDiprecaPabellon to set.
	 */
	public void setAporteDiprecaPabellon(Float aporteDiprecaPabellon) {
		this.aporteDiprecaPabellon = aporteDiprecaPabellon;
	}
	
	public void setAporteDiprecaPabellon(BigDecimal aporteDiprecaPabellon) {
		this.aporteDiprecaPabellon = new Float(aporteDiprecaPabellon.floatValue());
	}

	/**
	 * @return Returns the cantidadAtenciones.
	 */
	public Integer getCantidadAtenciones() {
		return cantidadAtenciones;
	}
	/**
	 * @param cantidadAtenciones The cantidadAtenciones to set.
	 */
	public void setCantidadAtenciones(Integer cantidadAtenciones) {
		this.cantidadAtenciones = cantidadAtenciones;
	}

	public void setCantidadAtenciones(Short cantidadAtenciones) {
		this.cantidadAtenciones = new Integer(cantidadAtenciones.intValue());
	}

	/**
	 * @return Returns the codConvenio.
	 */
	public Integer getCodConvenio() {
		return codConvenio;
	}
	/**
	 * @param codConvenio The codConvenio to set.
	 */
	public void setCodConvenio(Integer codConvenio) {
		this.codConvenio = codConvenio;
	}
	/**
	 * @return Returns the codEstadoAtencion.
	 */
	public String getCodEstadoAtencion() {
		return codEstadoAtencion;
	}
	/**
	 * @param codEstadoAtencion The codEstadoAtencion to set.
	 */
	public void setCodEstadoAtencion(String codEstadoAtencion) {
		this.codEstadoAtencion = codEstadoAtencion;
	}
	/**
	 * @return Returns the codigoPabellon.
	 */
	public Integer getCodigoPabellon() {
		return codigoPabellon;
	}
	/**
	 * @param codigoPabellon The codigoPabellon to set.
	 */
	public void setCodigoPabellon(Integer codigoPabellon) {
		this.codigoPabellon = codigoPabellon;
	}
	/**
	 * @return Returns the codPrestacion.
	 */
	public Integer getCodPrestacion() {
		return codPrestacion;
	}
	/**
	 * @param codPrestacion The codPrestacion to set.
	 */
	public void setCodPrestacion(Integer codPrestacion) {
		this.codPrestacion = codPrestacion;
	}
	/**
	 * @return Returns the codProfesional.
	 */
	public String getCodProfesional() {
		return codProfesional;
	}
	/**
	 * @param codProfesional The codProfesional to set.
	 */
	public void setCodProfesional(String codProfesional) {
		this.codProfesional = codProfesional;
	}
	/**
	 * @return Returns the codRazonesNoAporte.
	 */
	public Integer getCodRazonesNoAporte() {
		return codRazonesNoAporte;
	}
	/**
	 * @param codRazonesNoAporte The codRazonesNoAporte to set.
	 */
	public void setCodRazonesNoAporte(Integer codRazonesNoAporte) {
		this.codRazonesNoAporte = codRazonesNoAporte;
	}
	/**
	 * @return Returns the codTipoPrestacion.
	 */
	public Integer getCodTipoPrestacion() {
		return codTipoPrestacion;
	}
	/**
	 * @param codTipoPrestacion The codTipoPrestacion to set.
	 */
	public void setCodTipoPrestacion(Integer codTipoPrestacion) {
		this.codTipoPrestacion = codTipoPrestacion;
	}

	public void setCodTipoPrestacion(Short codTipoPrestacion) {
		this.codTipoPrestacion = new Integer(codTipoPrestacion.intValue());
	}

	/**
	 * @return Returns the cuentaContableAsoc.
	 */
	public String getCuentaContableAsoc() {
		return cuentaContableAsoc;
	}
	/**
	 * @param cuentaContableAsoc The cuentaContableAsoc to set.
	 */
	public void setCuentaContableAsoc(String cuentaContableAsoc) {
		this.cuentaContableAsoc = cuentaContableAsoc;
	}
	/**
	 * @return Returns the cuentaContableAsocPabellon.
	 */
	public String getCuentaContableAsocPabellon() {
		return cuentaContableAsocPabellon;
	}
	/**
	 * @param cuentaContableAsocPabellon The cuentaContableAsocPabellon to set.
	 */
	public void setCuentaContableAsocPabellon(String cuentaContableAsocPabellon) {
		this.cuentaContableAsocPabellon = cuentaContableAsocPabellon;
	}
	/**
	 * @return Returns the fechaEfectivaAtencionMedica.
	 */
	public Date getFechaEfectivaAtencionMedica() {
		return fechaEfectivaAtencionMedica;
	}
	/**
	 * @param fechaEfectivaAtencionMedica The fechaEfectivaAtencionMedica to set.
	 */
	public void setFechaEfectivaAtencionMedica(Date fechaEfectivaAtencionMedica) {
		this.fechaEfectivaAtencionMedica = fechaEfectivaAtencionMedica;
	}
	
	public void setFechaEfectivaAtencionMedica(java.sql.Date fechaEfectivaAtencionMedica) {
		Date fecha = new Date();
		fecha.setTime( fecha.getTime() );
		this.fechaEfectivaAtencionMedica = fecha;
	}

	/**
	 * @return Returns the fechaInicioHospital.
	 */
	public Date getFechaInicioHospital() {
		return fechaInicioHospital;
	}
	/**
	 * @param fechaInicioHospital The fechaInicioHospital to set.
	 */
	public void setFechaInicioHospital(Date fechaInicioHospital) {
		this.fechaInicioHospital = fechaInicioHospital;
	}
	/**
	 * @return Returns the fechaTerminoHospital.
	 */
	public Date getFechaTerminoHospital() {
		return fechaTerminoHospital;
	}
	/**
	 * @param fechaTerminoHospital The fechaTerminoHospital to set.
	 */
	public void setFechaTerminoHospital(Date fechaTerminoHospital) {
		this.fechaTerminoHospital = fechaTerminoHospital;
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
	 * @return Returns the idBono.
	 */
	public Integer getIdBono() {
		return idBono;
	}
	/**
	 * @param idBono The idBono to set.
	 */
	public void setIdBono(Integer idBono) {
		this.idBono = idBono;
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
	 * @return Returns the incluyePabellon.
	 */
	public String getIncluyePabellon() {
		return incluyePabellon;
	}
	/**
	 * @param incluyePabellon The incluyePabellon to set.
	 */
	public void setIncluyePabellon(String incluyePabellon) {
		this.incluyePabellon = incluyePabellon;
	}
	/**
	 * @return Returns the pensionadoOSalaComun.
	 */
	public String getPensionadoOSalaComun() {
		return pensionadoOSalaComun;
	}
	/**
	 * @param pensionadoOSalaComun The pensionadoOSalaComun to set.
	 */
	public void setPensionadoOSalaComun(String pensionadoOSalaComun) {
		this.pensionadoOSalaComun = pensionadoOSalaComun;
	}
	/**
	 * @return Returns the rutCompaniaSeguro.
	 */
	public Integer getRutCompaniaSeguro() {
		return rutCompaniaSeguro;
	}
	/**
	 * @param rutCompaniaSeguro The rutCompaniaSeguro to set.
	 */
	public void setRutCompaniaSeguro(Integer rutCompaniaSeguro) {
		this.rutCompaniaSeguro = rutCompaniaSeguro;
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
	 * @return Returns the tipoPaciente.
	 */
	public String getTipoPaciente() {
		return tipoPaciente;
	}
	/**
	 * @param tipoPaciente The tipoPaciente to set.
	 */
	public void setTipoPaciente(String tipoPaciente) {
		this.tipoPaciente = tipoPaciente;
	}
	/**
	 * @return Returns the valorAporteDipreca.
	 */
	public Integer getValorAporteDipreca() {
		return valorAporteDipreca;
	}
	/**
	 * @param valorAporteDipreca The valorAporteDipreca to set.
	 */
	public void setValorAporteDipreca(Integer valorAporteDipreca) {
		this.valorAporteDipreca = valorAporteDipreca;
	}

	public void setValorAporteDipreca(BigDecimal valor) {
		this.valorAporteDipreca = new Integer(valor.intValue());
	}
	/**
	 * @return Returns the valorAporteSeguro.
	 */
	public Integer getValorAporteSeguro() {
		return valorAporteSeguro;
	}
	/**
	 * @param valorAporteSeguro The valorAporteSeguro to set.
	 */
	public void setValorAporteSeguro(Integer valorAporteSeguro) {
		this.valorAporteSeguro = valorAporteSeguro;
	}

	public void setValorAporteSeguro(BigDecimal valor) {
		this.valorAporteSeguro = new Integer(valor.intValue());
	}
	
	/**
	 * @return Returns the valorCobradoPrestador.
	 */
	public Integer getValorCobradoPrestador() {
		return valorCobradoPrestador;
	}
	/**
	 * @param valorCobradoPrestador The valorCobradoPrestador to set.
	 */
	public void setValorCobradoPrestador(Integer valorCobradoPrestador) {
		this.valorCobradoPrestador = valorCobradoPrestador;
	}

	public void setValorCobradoPrestador(BigDecimal valor) {
		this.valorCobradoPrestador = new Integer(valor.intValue());
	}

	/**
	 * @return Returns the valorConvenidoPrestacion.
	 */
	public Float getValorConvenidoPrestacion() {
		return valorConvenidoPrestacion;
	}
	/**
	 * @param valorConvenidoPrestacion The valorConvenidoPrestacion to set.
	 */
	public void setValorConvenidoPrestacion(Float valorConvenidoPrestacion) {
		this.valorConvenidoPrestacion = valorConvenidoPrestacion;
	}

	public void setValorConvenidoPrestacion(BigDecimal valor) {
		this.valorConvenidoPrestacion = new Float(valor.intValue());
	}

	/**
	 * @return Returns the valorCopago.
	 */
	public Float getValorCopago() {
		return valorCopago;
	}
	/**
	 * @param valorCopago The valorCopago to set.
	 */
	public void setValorCopago(Float valorCopago) {
		this.valorCopago = valorCopago;
	}

	public void setValorCopago(BigDecimal valor) {
		this.valorCopago = new Float(valor.intValue());
	}
	/**
	 * @return Returns the valorEnBono.
	 */
	public Float getValorEnBono() {
		return valorEnBono;
	}
	/**
	 * @param valorEnBono The valorEnBono to set.
	 */
	public void setValorEnBono(Float valorEnBono) {
		this.valorEnBono = valorEnBono;
	}
	/**
	 * @return Returns the valorPabellon.
	 */
	public Float getValorPabellon() {
		return valorPabellon;
	}
	/**
	 * @param valorPabellon The valorPabellon to set.
	 */
	public void setValorPabellon(Float valorPabellon) {
		this.valorPabellon = valorPabellon;
	}
	
	public void setValorPabellon(BigDecimal valor) {
		this.valorPabellon = new Float(valor.intValue());
	}
	
	
	public String getNombrePrestacion() {
		return nombrePrestacion;
	}
	public void setNombrePrestacion(String nombrePrestacion) {
		this.nombrePrestacion = nombrePrestacion;
	}
}
