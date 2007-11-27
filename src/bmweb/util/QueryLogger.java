/*
 * Creado en 02-02-2006 por denis
 *
 */
package bmweb.util;

import org.apache.log4j.Logger;

/**
 * @author denis.fuenzalida
 *
 * Clase estatica para hacer logging de las consultas
 * sql para auditoria.
 * 
 * SOLO ESTA CLASE DEBE USARSE PARA ESE PROPOSITO, PARA
 * CENTRALIZAR LA CONFIGURACION DE LOG4J
 * 
 */
public class QueryLogger {

	private static Logger logger = Logger.getLogger(QueryLogger.class);
	
	public static void log(UsuarioWeb uw, String mensaje){
		logger.info(uw.getNombreUsuario() + "\t" + uw.getIP() + "\t" + mensaje);
	}
	
}
