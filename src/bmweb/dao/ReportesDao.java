/*
 * Creado en 24-08-2005 por denis
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.util.QueryLogger;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 *
 * 2007 12 07
 * 
 */
public class ReportesDao implements IReportesDao {
	
	private DataSource dataSource;
	
	private DataSource getDataSource(){ return dataSource; }
	public void setDataSource(DataSource ds){ this.dataSource = ds; }


	/**
	 * TODO Implementar!
	 * @param reporteId
	 * @return
	 */
	public List ejecutarReporte(String reporteId, Map params, UsuarioWeb uw){
		
		List resultado = new ArrayList();
				
		Map fila = new HashMap();
		fila.put("especialidad", "XXXXXXXXX");
		fila.put("reparticion", new Integer(1));
		fila.put("imp_carga", "01");
		fila.put("sexo", "F");
		fila.put("subtotal", new Integer(42));
		resultado.add(fila);
		
		ReporteGenericoMappingQuery rgmp = new ReporteGenericoMappingQuery(dataSource, params, uw);
		List res2 = rgmp.execute();
		resultado.addAll(res2);
		
		return resultado;
	}
	
	/*

	select pr_nombre[1,30] especialidad
	      ,be_carne[1,1] reparticion
	      ,be_carne[10,11] imp_carga
	      ,sexo
	      ,count(b.bo_serial)
	  from bm_bonite a
	      ,bm_bono b
	      ,bm_prestacion c
	      ,rolbene d
	      ,beneficiario e
	where b.bo_serial=a.bo_serial
	  and c.pr_codigo = a.pr_codigo
	  and c.pr_codigo between 0101005 and 0101099
	  and d.rut_bene=e.rut_bene
	  and d.cod_repart =b.be_carne[1,1]
	  and d.nro_impo   =b.be_carne[3,8]
	  and d.nro_correl =b.be_carne[10,11]
	  and b.be_carne[2,2]="-"
	  and b.be_carne[9,9]="-"
	  and b.be_carne[3] in ("0","1","2","3","4","5","6","7","8","9")
	  and b.be_carne[4] in ("0","1","2","3","4","5","6","7","8","9")
	  and b.be_carne[5] in ("0","1","2","3","4","5","6","7","8","9")
	  and b.be_carne[6] in ("0","1","2","3","4","5","6","7","8","9")
	  and b.be_carne[7] in ("0","1","2","3","4","5","6","7","8","9")
	  and b.be_carne[8] in ("0","1","2","3","4","5","6","7","8","9")
	--  and bo_fecemi between "01/01/2007" and "12/31/2007"
	group by 1,2,3,4
	order by 1,2,3,4

			 */
	class ReporteGenericoMappingQuery extends MappingSqlQuery {
		
		public ReporteGenericoMappingQuery(DataSource ds, Map params, UsuarioWeb uw) {
			
			super();
			
			SimpleDateFormat sdf_yyyy = new SimpleDateFormat("yyyy");
			Date ahora = new Date();
			
			String fechaDesde;
			if (!params.containsKey("fechaDesde")){ fechaDesde = "01/01/" + sdf_yyyy.format(ahora); }
			else { fechaDesde = (String) params.get("fechaDesde"); }
			
			String fechaHasta;
			if (!params.containsKey("fechaHasta")){ fechaHasta = "31/12/" + sdf_yyyy.format(ahora); }
			else { fechaHasta = (String) params.get("fechaHasta"); }
			
			String query = "" +
					" select pr_nombre[1,30] especialidad, " +
					" be_carne[1,1] reparticion, " +
					" be_carne[10,11] imp_carga, " +
					" sexo, " + 
					" count(b.bo_serial) subtotal " +
					" from bm_bonite a, bm_bono b, bm_prestacion c, rolbene d, beneficiario e " +
					" where b.bo_serial = a.bo_serial " +
					"  and c.pr_codigo = a.pr_codigo " +
					"  and c.pr_codigo between 0101005 and 0101099 " +
					"  and d.rut_bene = e.rut_bene " +
					"  and d.cod_repart = b.be_carne[1,1] " +
					"  and d.nro_impo   = b.be_carne[3,8] " +
					"  and d.nro_correl = b.be_carne[10,11] " +
					"  and b.be_carne[2,2]='-' " +
					"  and b.be_carne[9,9]='-' " +
					" and b.be_carne[3] in  ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[4] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[5] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[6] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[7] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[8] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and bo_fecemi between TO_DATE('" + fechaDesde + "', '%d/%m/%Y')" +
					"    and TO_DATE('" + fechaHasta + "', '%d/%m/%Y')" +
					"group by 1,2,3,4 " +
					"order by 1,2,3,4 ";

			// Conversión de fechas usando
			// TO_DATE ('2002-12-31 23:59:59' , '%Y-%m-%d %H:%M:%S' )

			setDataSource(ds);
			setSql(query);
			
			QueryLogger.log(uw, query);
			
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			HashMap fila = new HashMap();
			fila.put("especialidad", rs.getString("especialidad"));
			fila.put("reparticion", new Integer(rs.getString("reparticion")));
			fila.put("imp_carga", rs.getString("imp_carga"));
			fila.put("sexo", rs.getString("sexo"));
			fila.put("subtotal", new Integer(rs.getString("subtotal")));
			return fila;
		}
	}

}
