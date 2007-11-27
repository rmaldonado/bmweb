/*
 * Creado en 15-11-2005 por denis
 *
 */
package bmweb.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author denis.fuenzalida
 *
 */
public class TextUtil {
	
	// Caracteres censurados de los insert en el SQL
	// TODO 20060425 agregar '-' a los caracteres prohibidos
	public static String caracteresFiltrados = "'\"@#$%&\\";
	
	public static String[] dividirCMC(String CMC){
		
		if (CMC == null || CMC.length()<11){
			throw new RuntimeException("CMC nulo o no valido");
		}
		
		String institucion = CMC.substring(0,1);
		String reparticion = new Integer(CMC.substring(2,8)).toString(); // evitar sql-injection
		String correlativo = new Integer(CMC.substring(9,11)).toString(); // idem
		
		return new String[]{institucion, reparticion, correlativo};
	}
	
	public static String formarCMC(String reparticion, int impo, int correl ){
		
		String strRepar = reparticion.trim();
		String strImpo = impo + "";
		String strCorrel = correl + "";
		
		strRepar = completarDerecha("  ", strRepar);
		strImpo = completarDerecha("000000", strImpo);
		strCorrel = completarDerecha("00", strCorrel);
		
		return reparticion + "-" + strImpo + "-" + strCorrel;
	}
	
	// toma una mascara "000000" y un valor "12" y retorna "000012"
	// usa los primeros caracteres de la mascara y agrega el valor
	// para formar un string del mismo largo que la mascara
	
	public static String completarDerecha(String mascara, String valor){
	
		
		// si el valor es más largo que la mascara
		if (valor.length() >= mascara.length()) return valor;
		
		String prefijo = mascara.substring(0, mascara.length() - valor.length());
		
		return prefijo+valor;
	}
	
	// los alemanes formatean los numeros igual que nosotros
	public static String formatearNumero(int numero){
		Locale locale = Locale.GERMAN;
		String salida = NumberFormat.getNumberInstance(locale).format(numero);
		return salida;
	}

	public static String formatearNumero(float numero){
		Locale locale = Locale.GERMAN;
		String salida = NumberFormat.getNumberInstance(locale).format(numero);
		return salida;
	}
	
	public static String formatearNumero(double numero){
		Locale locale = Locale.GERMAN;
		String salida = NumberFormat.getNumberInstance(locale).format(numero);
		return salida;
	}
	
	// rutina para sacar el DV de un rut sacada de la pagina
	// de manungo (plop.cl)
	public static String getDigitoVerificador(int rut){
		
		int M=0;
		int S=1;
		int T=rut;
		for( ;T!=0; T/=10) { 
			S = (S+T%10*(9-M++%6))%11;
		}
		String[] dv = {"K","0","1","2","3","4","5","6","7","8","9"};
		
		return dv[S];
	}
	
	public static String getRutFormateado(int rut){
		return formatearNumero(rut) + "-" + getDigitoVerificador(rut);
	}

	public static String filtrar(String texto){
		
		if (texto == null || texto.length() == 0) return "";
		
		StringBuffer buffer = new StringBuffer("");
		for(int i=0; i<texto.length();i++){
			char c = texto.charAt(i);

			// Si el caracter NO esta entre los filtrados, lo agrego
			if (!(caracteresFiltrados.indexOf(c)>-1)){
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
	public static void main(String[] args){

		String[] c = dividirCMC("1-234567-09");
		System.out.println(c[0]);
		System.out.println(c[1]);
		System.out.println(c[2]);
		
		System.out.println(completarDerecha("abcdef", "123"));
		
		System.out.println("-12.345.678,89 == " + formatearNumero(-12345678.89));
		System.out.println(14476269 + getDigitoVerificador(14476269));
		System.out.println(13053410 + getDigitoVerificador(13053410));
		System.out.println(20533849 + getDigitoVerificador(20533849));
		
		System.out.println(getRutFormateado(14476269));
		
	}

}
