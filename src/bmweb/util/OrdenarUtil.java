/*
 * Creado en 28-11-2005 por denis
 *
 */
package bmweb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author denis.fuenzalida
 *
 * Clase utilitaria, permite ordenar de menor a mayor las filas
 * String [] fila = new String[]{ rutPrestador, nombrePrestador, totalAporteDipreca, totalAporteSeguro, totalCopago };
 * 
 *
 */
public class OrdenarUtil {

	public static List ordenarNumericoAsc(List filas, int numColumna){

		// recorro las filas, que tienen String[] fila
		// creo el Integer valor
		// creo una nueva lista de Object[] con "valor" , "fila"
		// hago sort a nuevaLista.toArray
		
		// recorro el Object[] recuperando las String[] fila en orden
		
		ArrayList filasOrdenadas = new ArrayList();
		
		TreeMap treeMap = new TreeMap();
		ArrayList temp = new ArrayList();
			
		try {

			for (int i=0; filas != null && i<filas.size(); i++){
			
				// leo el valor del entero en la columna "numColumna"
				String[] fila = (String []) filas.get(i);
				int valor = Integer.parseInt(fila[numColumna]);
				
				int j;
				for (j=0; j<filasOrdenadas.size(); j++){
					// voy a dejar en "j" la posicion para insertar el nuevo elemento
					
					if (filasOrdenadas.size() == 0) break;
					
					String[] filaPos = (String[]) filasOrdenadas.get(j);
					int valorPos = Integer.parseInt(filaPos[numColumna]);
					if (valor < valorPos) break;
				}
				
				// inserto la fila en la posicion j
				filasOrdenadas.add(j, fila);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filasOrdenadas;
	}
	
	public static List ordenarCopagoAscendente(List filas){
		return ordenarNumericoAsc(filas, 4); // ordeno por la quinta columna
	}
	
	public static void main(String[] args) {
		
		List listaPrueba = new ArrayList();
		
		// String[]{ rutPrestador, nombrePrestador, totalAporteDipreca, totalAporteSeguro, totalCopago };
		
		listaPrueba.add(new String[]{"aaa","naaa","0","1","100" });
		listaPrueba.add(new String[]{"bbb","nbbb","0","2","300" });
		listaPrueba.add(new String[]{"ddd","nddd","0","4","220" });
		listaPrueba.add(new String[]{"ccc","nccc","0","3","220" });
		
		List resultado = OrdenarUtil.ordenarCopagoAscendente( listaPrueba );
		
		for(int i=0; i<listaPrueba.size(); i++){
			String[] f = (String[]) resultado.get(i);
			System.out.println(f[0] + " " + f[1] + " " + f[2] + " " + f[3] + " " + f[4]);
		}
		
	}
}
