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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.dto.BonoDTO;
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
		
		/*
		Map fila = new HashMap();
		fila.put("especialidad", "XXXXXXXXX");
		fila.put("reparticion", new Integer(1));
		fila.put("imp_carga", "01");
		fila.put("sexo", "F");
		fila.put("subtotal", new Integer(42));
		resultado.add(fila);
		*/
		
		ReporteGenericoMappingQuery rgmp = new ReporteGenericoMappingQuery(dataSource, params, uw);
		List res2 = rgmp.execute();
		
		// El gran cuadro con todos los datos del reporte
		Map cuadroReporte = new HashMap();

		// Para cada fila del reporte
		// (especialidad, reparticion[1-5], imp_carga[00-05], sexo[MF], subtotal)

		Iterator iFilaReporte = res2.iterator();
		while (iFilaReporte.hasNext()){
			Map filaQuery = (Map) iFilaReporte.next();
			
			String especialidad = (String) filaQuery.get("especialidad");
			
			// La fila del gran cuadro asociada a cada especialidad
			Map mapaReporte;
			if (cuadroReporte.containsKey(especialidad)){ mapaReporte = (Map) cuadroReporte.get(especialidad); }
			else { mapaReporte = new HashMap(); }
			
			// Acumulo el valor en la posicion
			// {reparticion}.{imp_carga}.{sexo}
			
			String impCarga = (String) filaQuery.get("imp_carga");
			int intImpCarga;
			if ("00".equals(impCarga)){ intImpCarga = 0; }
			else { intImpCarga = 1; }
			
			String llave = (Integer) filaQuery.get("reparticion") + "." +
							intImpCarga + "." +
							(String) filaQuery.get("sexo");
			
			// System.out.println("***" + llave);
			
			// Si ya había registrado un subtotal con ese nombre, lo aumento
			if (mapaReporte.containsKey(llave)){
				int valorAnterior = ((Integer)mapaReporte.get(llave)).intValue();				
				int subtotal = ((Integer) filaQuery.get("subtotal")).intValue();
				mapaReporte.put(llave, new Integer(valorAnterior+subtotal));
			} else {
				// Si no habia valor registrado, uso el del subtotal actual del reporte
				mapaReporte.put(llave, (Integer)filaQuery.get("subtotal"));
			}
			
			// Truco: En el mapaReporte, coloco la especialidad, asi solo utilizo los valores
			mapaReporte.put("especialidad", especialidad);
			
			// Coloco todo el mapa, que representa una fila, en el gran cuadro
			cuadroReporte.put(especialidad, mapaReporte);
			
		}
		
		// Recupero una Hash asociado a la especialidad
		
		resultado.addAll( cuadroReporte.values() );
		
		// resultado.addAll(res2);
		
		return resultado;
	}
	
	class ReporteGenericoMappingQuery extends MappingSqlQuery {
		
		public ReporteGenericoMappingQuery(DataSource ds, Map params, UsuarioWeb uw) {
			
			super();
			
			// Por omisión, se buscan los bonos emitidos en el dia de hoy 
			SimpleDateFormat sdf_ahora = new SimpleDateFormat("dd/MM/yyyy");
			Date ahora = new Date();
			
			String fechaDesde;
			if (!params.containsKey("fechaDesde")){ fechaDesde = sdf_ahora.format(ahora); }
			else { fechaDesde = (String) params.get("fechaDesde"); }
			
			String fechaHasta;
			if (!params.containsKey("fechaHasta")){ fechaHasta = sdf_ahora.format(ahora); }
			else { fechaHasta = (String) params.get("fechaHasta"); }
			
			String query = "" +
					" select pr_nombre[1,30] especialidad, " +
					" be_carne[1,1] reparticion, " +
					" be_carne[10,11] imp_carga, " +
					" sexo, " + 
					" count(b.bo_serial) subtotal " +
					// " key_descr[1,10] jurisdiccion" +
					" from bm_bonite a, bm_bono b, bm_prestacion c, rolbene d, beneficiario e " +
					// " bm_habilitado f, keyword_det k " +
					" where b.bo_serial = a.bo_serial " +
					"  and b.dom_tipbon='W' " +
					"  and c.pr_codigo = a.pr_codigo ";
			
			// parche Luis Latin para cuando no filtre por Jurisdiccion o Region o Ciudad o Agencia
			if ("C".equals((String)params.get("CJRA"))||
			    "J".equals((String)params.get("CJRA"))||
			    "R".equals((String)params.get("CJRA"))||
			    "A".equals((String)params.get("CJRA")))
			{
				query ="" +
				" select pr_nombre[1,30] especialidad, " +
				" be_carne[1,1] reparticion, " +
				" be_carne[10,11] imp_carga, " +
				" sexo, " + 
				" count(b.bo_serial) subtotal " +
				// " key_descr[1,10] jurisdiccion" +
				" from bm_bonite a, bm_bono b, bm_prestacion c, rolbene d, beneficiario e, " +
				" bm_habilitado f, keyword_det k " +
				" where b.bo_serial = a.bo_serial " +
				"  and b.dom_tipbon='W' " +
				"  and c.pr_codigo = a.pr_codigo ";
				
			}
			// fin parche Luis LAtin CJRA //
			
			// CJRA: ciudad - jurisdiccion - region - agencia
			if ("C".equals((String)params.get("CJRA"))){
				query +="  and key_sist='BENMED' and key_word ='CIUDAD' "
					  + "  and f.ha_codigo = b.ha_codigo and k.key_id = f.dom_ciudad "
				      + "  and f.dom_ciudad = " + params.get("dom_ciudad") + " ";
			}
		
			if ("J".equals((String)params.get("CJRA"))){
				query +="  and key_sist='BENMED' and key_word ='JURISD' "
					  + "  and f.ha_codigo = b.ha_codigo and k.key_id = f.ha_jurisd "
					  + "  and f.ha_jurisd = " + params.get("dom_jurisdiccion") + " ";
			}
		
			if ("R".equals((String)params.get("CJRA"))){
				query +="  and key_sist='BENMED' and key_word ='REGION' "
					  + "  and f.ha_codigo = b.ha_codigo and k.key_id = f.ha_region "
					  + "  and f.ha_region = " + params.get("dom_region") + " ";
			}
		
			if ("A".equals((String)params.get("CJRA"))){
				query +="  and key_sist='BENMED' and key_word ='AGENCIA' " 
					  + "  and f.ha_codigo = b.ha_codigo and k.key_id = f.ha_agencia "
					  + "  and f.ha_agencia = " + params.get("dom_agencia") + " ";
			}
			
			if ("si".equals((String)params.get("opPrestacion"))){
				query += "" +
					"  and c.pr_codigo = " + (String)params.get("prestacion") + " ";				
			} else {
				query += "" +
				"  and c.pr_codigo between 0101005 and 0101099 ";
			}
					
			query += "" +
					"  and d.rut_bene = e.rut_bene " +
					"  and d.cod_repart = b.be_carne[1,1] " +
					"  and d.nro_impo   = b.be_carne[3,8] " +
					"  and d.nro_correl = b.be_carne[10,11] " +
					"  and b.be_carne[2,2]='-' " +
					"  and b.be_carne[9,9]='-' " +
					"  and b.be_carne[3] in  ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[4] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[5] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[6] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[7] in ('0','1','2','3','4','5','6','7','8','9') " +
					"  and b.be_carne[8] in ('0','1','2','3','4','5','6','7','8','9') "; 
				//	"  and f.ha_codigo = b.ha_codigo and k.key_id = f.ha_jurisd " 
				//"  and key_sist='BENMED' and key_word ='JURISD' ";
			
			// Si viene la reparticion
			if (params.containsKey("reparticiones")){
				String[] reps = (String[]) params.get("reparticiones");
				String lasReps = "";
				String coma = "";
				for (int i=0; null != reps && i<reps.length; i++){
					lasReps = lasReps + coma + "'" + reps[i] + "'";
					coma = ", ";
				}
				
				if (null != reps){
					query += "  and d.cod_repart in (" + lasReps + ") "; 
				}

			}
		
		
			// Si la opfecha viene con 'entre'
			if ( "entre".equals((String)params.get("opfecha")) ){
				query += "" +
				"  and bo_fecemi between TO_DATE('" + fechaDesde + "', '%d/%m/%Y')" +
				"    and TO_DATE('" + fechaHasta + "', '%d/%m/%Y')";
			}
			
			// Si el estado del bono es uno de estos: 'A', 'P', lo incluyo en la query
			//if (BonoDTO.ESTADOBONO_ANULADO.equals((String)params.get("estadoBono")) ||
			//	BonoDTO.ESTADOBONO_IMPRESO.equals((String)params.get("estadoBono"))){
			if (BonoDTO.ESTADOBONO_ANULADO.equals((String)params.get("estadoBono"))){
				String paramEstadoBono = (String)params.get("estadoBono");
				query += "  and b.dom_estbon = '" + paramEstadoBono + "' ";
			}
			if (BonoDTO.ESTADOBONO_IMPRESO.equals((String)params.get("estadoBono"))){
				String paramEstadoBono = (String)params.get("estadoBono");
				query += "  and (b.dp_serial is not null and b.dp_serial > 0) ";
			}
			// Si viene el rut del prestador, filtro por el
			if ("si".equals((String)params.get("opPrestador"))){
					String paramRutPrestador = (String)params.get("prestador");
					query += "  and b.pb_rut = '" + paramRutPrestador + "'";
			}
		
			query += "" +
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
			
			try {
				fila.put("reparticion", new Integer(rs.getString("reparticion")));
			} catch (Exception e) {
				// 2007.12.27
				// A veces puede venir un 'X' o una 'Y' en la repartición, reemplazo por 10 y 11 respectivamente
				
				String reparticion = rs.getString("reparticion");
				if ("X".equalsIgnoreCase(reparticion)){ fila.put("reparticion", new Integer(10)); }
				if ("Y".equalsIgnoreCase(reparticion)){ fila.put("reparticion", new Integer(11)); }
			}
			
			fila.put("imp_carga", rs.getString("imp_carga"));
			fila.put("sexo", rs.getString("sexo"));
			fila.put("subtotal", new Integer(rs.getString("subtotal")));
			return fila;
		}
	}

}
