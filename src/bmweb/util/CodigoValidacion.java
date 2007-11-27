package bmweb.util;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author denis.fuenzalida
 * 
 * 2006 01 26
 * 
 * Cambio el algoritmo, ahora se parsean enteros desde la coleccion,
 * se requiere devolver un string solo con numeros para generar el
 * codigo de barras correcto
 * 
 * 2006 01 17
 * 
 * Se agrega un codigo de validacion interno, calculado de forma similar
 * al digito verificador del rut, se botan el prefijo y sufijo
 * 
 * 2006 01 11
 *
 * Se reemplaza el algoritmo MD5 por un encoder Base64 para que no haya
 * perdida de informacion. Se agrega un prefijo para que la decriptacion
 * no sea tan obvia.
 * 
 * El algoritmo Base64 incrementa en un 25% el tamano del texto que se
 * quiere codificar (3 caracteres se convierten en 4)
 *
 * 2005 09 01
 * Clase utilitaria que permite obtener un hash de 8 caracteres
 * sacado del MD5 de un string m�s largo. Tiene un meodo calculaCodigo()
 * que recibe un String o una coleccion de objetos a los que convierte
 * en strings, que concatena y que finalmente procesa para obtener el
 * codigo.
 * 
 */
public class CodigoValidacion {

	private static String PREFIJO = "!";
	private static String SUFIJO = "!";
	//private static String CHARS_VALIDACION = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static String CHARS_VALIDACION = "1234567890";
	
	public static void main(String[] args) {

		/*
		System.out.println( calculaCodigo(".."));
		System.out.println( calculaCodigo("habia una vez un pueblo llamado..."));
		System.out.println( calculaCodigo("HOLAMUNDO"));
		System.out.println( calculaCodigo(Arrays.asList( new String[]{"HOLA", "MUNDO"})));
		
		String mensaje = "12345614476269K01012";
		String mensajeEnc = calculaCodigo( mensaje );
		String mensajeDec = decriptaCodigo( mensajeEnc );
		
		System.out.println( mensaje );
		System.out.println( mensajeEnc );
		System.out.println( mensajeDec );

		System.out.println( decriptaCodigo( calculaCodigo("a")) );
		
		// validacion
		System.out.println("***");
		System.out.println( decriptaCodigo( "TDEyMzQ1NjE0NDc2MjY5SzAxMDEy") );
		*/
		String codigo = "789";
		
		System.out.println( codigo );
		System.out.println( decriptaCodigo( calculaCodigo(codigo)) );
		System.out.println( "***\n" + calculaCodigo(codigo) );

	}

	public static String calculaCodigo(Collection items){
		
		Iterator i = items.iterator();
		
		StringBuffer buffer = new StringBuffer(); 
		while (i.hasNext()){
			String s = i.next().toString();
			buffer.append( s.toUpperCase().trim() );
		}
		
		//System.out.println( buffer.toString());
		return calculaCodigo( buffer.toString() );
	}
	
	public static String calculaCodigo(String texto) {

		/*
		try {
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			md.update( texto.getBytes() );
			byte[] digest = md.digest();
			
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i<digest.length; i++){
				int valor = digest[i];
				if (valor<0){ valor=-valor; }
				String hex = "";
				if (valor<10){ hex = "0"; }
				hex += Integer.toHexString(valor);// + ",";
				buffer.append( hex );
			}
			
			// Uso s�lo los primeros 8 caracteres
			return buffer.toString().substring(0,8);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
		*/
		String dv = calcularLetraValidacion(texto);
		
		String textoPorEncriptar = dv + texto;
		
		StringBuffer buffer = new StringBuffer();
		
		for (int i=0; i<textoPorEncriptar.length(); i++){
			String iLetra = textoPorEncriptar.substring(i, i+1);
			int digito = Integer.parseInt(iLetra);
			digito = (digito+3)%10;
			buffer.append(digito+"");
		}
		
		//return Base64.encodeBytes( textoPorEncriptar.getBytes() );
		return buffer.toString();
	}
	
	public static String decriptaCodigo(String texto) {
		
		//String decriptado = new String(Base64.decode( texto ));
		
		StringBuffer buffer = new StringBuffer();
		
		for (int i=0; i<texto.length(); i++){
			String iLetra = texto.substring(i, i+1);
			int digito = Integer.parseInt(iLetra);
			digito = (digito+7)%10;
			buffer.append(digito+"");
		}
		
		String decriptado = buffer.toString();

		String dv = decriptado.substring(0,1);
		String textoOrig = decriptado.substring(1);
		
		if (!calcularLetraValidacion(textoOrig).equals(dv)){
			throw new RuntimeException("Codigo de validacion incorrecto.");
		}

		return decriptado.substring(1);
	}
	
	private static String calcularLetraValidacion(String texto){
		
		/*
		int intValidacion = 0;
		for(int i=0; texto!= null && i<texto.length(); i++){
			intValidacion += (i+7)*texto.charAt(i);
		}
		intValidacion = intValidacion % CHARS_VALIDACION.length();
		
		return CHARS_VALIDACION.substring(intValidacion, intValidacion+1);
		*/
		
		int valor = Integer.parseInt(texto);
		return (valor%7) + "";
		
	}
}
