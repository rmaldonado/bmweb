/*
 * Creado en 06-08-2005 por denis
 *
 */
package bmweb.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author denis
 * 
 * Clase con métodos estáticos utiles para las consultas a la base de datos
 * 
 */
public class QueryUtil {
	
	/**
	 * Convierte una lista de Strings en una condicion 'where' + N condiciones 'and'
	 * @param lista con Strings a convertir
	 * @return Un String con el contenido de la lista convertido en where + ands
	 */
	
	public static String getWhere(Collection lista){
		
		String separador = " where ";
		StringBuffer buffer = new StringBuffer();
		
		Iterator it = lista.iterator();

		while( it.hasNext()){
			buffer.append( separador );
			buffer.append( (String) it.next());
			separador = " and ";
		}
		
		return buffer.toString();
	}
	
}
