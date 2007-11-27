/*
 * Creado en 24-08-2005 por denis
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.QueryLogger;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 * 2006 02 01
 * 
 * Ahora esta clase solo una SQL Mappers de Spring,
 * Todos los metodos reciben un UsuarioWeb y hacen LOG del SQL para auditoria
 *
 * Clase utilitaria para consultas sobre Beneficiarios
 * 
 */
public class BeneficiariosDao implements IBeneficiariosDao {
	
	private DataSource dataSource;
	
	private DataSource getDataSource(){ return dataSource; }
	public void setDataSource(DataSource ds){ this.dataSource = ds; }


	public boolean validarCMC(String CMC, UsuarioWeb uw){
		
		/*
		HibernateTemplate template = getHibernateTemplate();
		Boolean resultado = (Boolean) template.execute( new ValidarCmcHibernateCallback(CMC) );
		return resultado.booleanValue();
		*/
		
		LeeRolbeneMappingQuery validador = new LeeRolbeneMappingQuery(getDataSource(), uw, CMC);
		List lista = validador.execute();
		
		boolean errorCMC = false;
        // Si encuentra un registro asociado en RolBene, reviso el rolbene
        if (lista != null && lista.size() > 0) {
        	RolbeneDTO rb = null;
        	rb = (RolbeneDTO) lista.get(0);
        	
        	// Si el estado es 1, esta ok
        	if (rb.getEstado() == 1){
        		errorCMC = false;
        	} else {
            	// Si el estado es distinto de 1, esta de baja, pero
            	//  si la fecha de termino es nula o es antes de hoy
            	//  --> la persona esta de baja -> CMC no valido
        		
        		Date hoy = new Date();
        		if (rb.getFecter() == null || hoy.after( rb.getFecter() )){
        			errorCMC = true; // No es valido
        		} else {
        			errorCMC = false; // fecha de termino no ha pasado -> CMC es valido
        		  }
        	  }
        }
        
        // La lista no retorna registros -> CMC no encontrado -> CMC no valido
        else {
        	errorCMC = true; //CMC No existe
        }
        
	
	return errorCMC;

	}

	public RolbeneDTO leeRolbene(String CMC, UsuarioWeb uw){

		LeeRolbeneMappingQuery validador = new LeeRolbeneMappingQuery(getDataSource(), uw, CMC);
		List lista = validador.execute();

		if (lista != null && lista.size() > 0) return (RolbeneDTO) lista.get(0);
		else return null;

	}

	public RolbeneDTO leeRolbenePorRut(int RUT, UsuarioWeb uw){

		/*
		HibernateTemplate template = getHibernateTemplate();
		RolbeneDTO resultado = (RolbeneDTO) template.execute( new LeeRolbenePorRUTHibernateCallback(RUT) );
		return resultado;
		*/

		LeeRolbenePorRutMappingQuery lector = new LeeRolbenePorRutMappingQuery(getDataSource(), uw, RUT);
		List lista = lector.execute();

		if (lista != null && lista.size() > 0) return (RolbeneDTO) lista.get(0);
		else return null;

	}

	

	public BeneficiarioDTO leeBeneficiario(int RUT, UsuarioWeb uw){

		LeeBeneficiarioMappingQuery lector = new LeeBeneficiarioMappingQuery(getDataSource(), uw, RUT);
		List lista = lector.execute();

		if (lista != null && lista.size() > 0) return (BeneficiarioDTO) lista.get(0);
		else return null;

	}	

	/*************************************************************************************************************/
	
	class LeeRolbeneMappingQuery extends MappingSqlQuery {

		public LeeRolbeneMappingQuery(DataSource ds, UsuarioWeb uw, String CMC ) {
			
			super();
			super.setDataSource(ds);
	
			String query = "" +
					" select nro_correl, cod_repart, nro_impo, rut_bene, rut_impo, " +
					" cod_contrato, cod_estado, fec_ini_est, fec_ter_est" +
					" from rolbene " +
					" where cod_repart = '" + CMC.substring(0,1).toUpperCase() + "'" +
					" and nro_impo = " + new Integer(CMC.substring(2,8)) + "" +
					" and nro_correl = " + new Integer(CMC.substring(9,11));

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, query);
			
			super.setSql(query);
			compile();
		}
		
		public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			RolbeneDTO rolbene = new RolbeneDTO();
			rolbene.setCorrel( ((Short)rs.getObject("nro_correl")).intValue() );
			rolbene.setRepart( (String)rs.getObject("cod_repart") );
			rolbene.setImpo(   ((Integer)rs.getObject("nro_impo")).intValue() );
			rolbene.setRbene(  ((Integer)rs.getObject("rut_bene")).intValue() );
			rolbene.setRimpo(  ((Integer)rs.getObject("rut_impo")).intValue() );
			rolbene.setContrato( (String)rs.getObject("cod_contrato") );
			rolbene.setEstado( ((Short)rs.getObject("cod_estado")).intValue() );
			rolbene.setFecini( (Date)rs.getObject("fec_ini_est") );
			rolbene.setFecter( (Date)rs.getObject("fec_ter_est") );
			return rolbene;
		}

	}
	
	class LeeBeneficiarioMappingQuery extends MappingSqlQuery {

		public LeeBeneficiarioMappingQuery(DataSource ds, UsuarioWeb uw, int RUT) {
			
			super();
			super.setDataSource(ds);
	
			String query = "" +
					" select rut_bene, dgv_bene, nombres, ape_pat, ape_mat, sexo, fec_nac, fec_fall " +
					" from beneficiario " +
			 		" where rut_bene = " + RUT;

			// AUDITORIA DE LA QUERY
			//logger.info(uw.getNombreUsuario() + "\t" + uw.getIP() + "\t" + query);
			QueryLogger.log(uw, query);
			
			super.setSql(query);
			compile();
		}
		
		public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			BeneficiarioDTO b = new BeneficiarioDTO();
			b.setBene( ((Integer)rs.getObject("rut_bene")).intValue() );
			b.setDgv( (String)rs.getObject("dgv_bene") );
			b.setNombre( (String)rs.getObject("nombres") );
			b.setPat( (String)rs.getObject("ape_pat") );
			b.setMat( (String)rs.getObject("ape_mat") );
			b.setSexo( (String)rs.getObject("sexo") );
			b.setFechaNacimiento( (Date)rs.getObject("fec_nac") );
			b.setFechaFallecimiento( (Date)rs.getObject("fec_fall") );
			return b;
		}

	}
	
	class LeeRolbenePorRutMappingQuery extends MappingSqlQuery {

		public LeeRolbenePorRutMappingQuery(DataSource ds, UsuarioWeb uw, int RUT ) {
			
			super();
			super.setDataSource(ds);
	
			String query = "" +
					" select nro_correl, cod_repart, nro_impo, rut_bene, rut_impo, " +
					" cod_contrato, cod_estado, fec_ini_est, fec_ter_est" +
					" from rolbene " +
					" where rut_impo = " + RUT + 
					" order by cod_estado desc";

			// AUDITORIA DE LA QUERY
			QueryLogger.log(uw, query);
			
			super.setSql(query);
			compile();
		}
		
		public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			RolbeneDTO rolbene = new RolbeneDTO();
			rolbene.setCorrel( ((Short)rs.getObject("nro_correl")).intValue() );
			rolbene.setRepart( (String)rs.getObject("cod_repart") );
			rolbene.setImpo(   ((Integer)rs.getObject("nro_impo")).intValue() );
			rolbene.setRbene(  ((Integer)rs.getObject("rut_bene")).intValue() );
			rolbene.setRimpo(  ((Integer)rs.getObject("rut_impo")).intValue() );
			rolbene.setContrato( (String)rs.getObject("cod_contrato") );
			rolbene.setEstado( ((Short)rs.getObject("cod_estado")).intValue() );
			rolbene.setFecini( (Date)rs.getObject("fec_ini_est") );
			rolbene.setFecter( (Date)rs.getObject("fec_ter_est") );
			return rolbene;
		}

	}
}
