/*
 * Created on 4/10/2005
 *
 */
package bmweb.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author denis.fuenzalida
 *
 */
public class ParamsUtil {

	/**
	 * Corrige el Map que entrega HttpServletRequest.getParameterMap()
	 * @param params un Map con llave -> String[] valores (arreglo de tamaño 1)
	 * @return un Map con llave -> valor como uno espera
	 */
	public static Map fixParams(Map params){
		
		try {
			HashMap fix = new HashMap();
			Iterator it = params.keySet().iterator();
			while (it.hasNext()){
				String llave = (String) it.next();
				String valor = ((String[]) params.get(llave))[0];
				
				// No agrego los parametros nulos ni vacios
				if (valor != null && (!"".equals(valor))){
					
					// Filtro todos los valores ingresados a la aplicacion
					valor = TextUtil.filtrar(valor);
					fix.put(llave, valor);					
				}
				
			}
			return fix;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
