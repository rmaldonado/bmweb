/*
 * Creado en 26-03-2006 por denis
 *
 */
package bmweb.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author denis.fuenzalida
 *
 * Clase que lee un archivo con lineas de datos separados por ';'
 * y los convierte en una lista de String[]
 * 
 */

public class CsvParser {
	
	public static String SEPARADOR = ",";

	public static List leer(String texto){

		List resultado = new ArrayList();
		
		StringReader reader = new StringReader(texto);
		BufferedReader br = new BufferedReader(reader);
		String linea = null;
		
		try {

			while ((linea = br.readLine()) != null){
				
				List fila = new ArrayList();
				StringTokenizer tokenizer = new StringTokenizer(linea);
				String elem = null;
				
				System.err.println(linea);
				
				while (tokenizer.hasMoreTokens()){
					elem = tokenizer.nextToken(SEPARADOR);
					fila.add(elem);
				}
				
				// convierto la fila de elementos separados por SEPARADOR 
				// en un arreglo de strings y los agrego a la lista de resultados
				
				String[] filaStrings = new String[fila.size()];
				for (int i=0; i<filaStrings.length; i++){
					filaStrings[i] = (String) fila.get(i);
				}
				
				resultado.add( filaStrings );
				
			}
			
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		
		
		String texto =	"codigo;valor convenio;copago\n" +
				"101010;12500;3500\n" +
				"101001;12501;3501\n" +
				"101002;12502;3502\n" +
				"101003;12503;3503";
		
		List lista = CsvParser.leer(texto);
		
		for (int i=0; lista != null && i<lista.size(); i++){
			String[] fila = (String[]) lista.get(i);
			
			for (int j=0; fila != null && j<fila.length; j++){
				System.out.print("(" + fila[j] + ")");
			}
			
			System.out.println("");
			
		}
		
	}
}
