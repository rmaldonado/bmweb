/*
 * Created on 27-05-2005
 *
 * Clase utilitaria para recuperar la lista de PrestacionesGenericasDTO
 */
package bmweb.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.PrestacionGenericaDTO;
import bmweb.util.ReflectionFiller;
/**
 * @author denis
 *
 */

public class PrestacionesGenericasDao implements IPrestacionesGenericasDao {
	
	private static List listaPrestaciones = null;
	
	private DataSource dataSource;
	
	private static String[] mapaColumnas = new String[] {
		"codigo", "key_id",
		"nombre", "key_descr",
		"sistema", "key_sist",
		"tabla", "key_word"
	};

	public void setDataSource(DataSource ds){ this.dataSource = ds; }

	public List lista(){
		
		// si la tengo en el cache la uso, en caso contrario busco
		if (listaPrestaciones != null) return listaPrestaciones;
	
		/*
		HibernateTemplate ht = getHibernateTemplate();
		
		// Le paso la clase interior que sabe usar los par�metros
		listaPrestaciones = (List) ht.execute( new PrestacionesGenericasHibernateCallback() );
		*/
		
		PrestacionesGenenicasMappingQuery p = new PrestacionesGenenicasMappingQuery(dataSource);
		List listaPrestaciones = p.execute();
		return listaPrestaciones;
	}

	
	/**
	 * devuelve una prestacion generica dada, segun un codigo
	 */
	public PrestacionGenericaDTO prestacionPorCodigo(int codigo){
		
		List prestaciones = lista();
		for (int i=0; prestaciones!=null && i<prestaciones.size();i++){
			PrestacionGenericaDTO p = (PrestacionGenericaDTO) prestaciones.get(i);
			if (p.getCodigo() == codigo) return p;
		}
		
		return null;
	}

	class PrestacionesGenenicasMappingQuery extends MappingSqlQuery {
		
		public PrestacionesGenenicasMappingQuery(DataSource ds) {
			
			super();
			String query = "select * from keyword_det where key_sist='BENMED' and key_word='PREGEN' order by key_descr asc";
			setDataSource(ds);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			PrestacionGenericaDTO pg = new PrestacionGenericaDTO();
			ReflectionFiller.fill(mapaColumnas, rs, pg);
			return pg;
		}
	}
	
}
