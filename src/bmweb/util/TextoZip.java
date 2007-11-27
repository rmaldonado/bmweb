package bmweb.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class TextoZip {

	public static void main(String[] args) {
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(os);
			out.putNextEntry(new ZipEntry("bono"));
			
			String texto = "" +
			"Fecha de Emision 05/09/2005\n" +
			"Numero de Bono 911\n" +
			"Emisor C.TAY RIVERA-S.ALARCON\n" +
			"C.ARICA\n" +
			"Rut Emisor 1\n" +
			"Codigo Validacion 49196627\n" +
			"Prestador DIPRECA FONDO HOSPITAL RUT Prestador 61513003-6\n" +
			"Beneficiario THOMAS ANTONIO ARAVENA FUENTES\n" +
			"Numero de C.M.C. 5-2068-1\n" +
			"Imponente JUANA MARLENE FUENTES PARADA\n" +
			"Numero de C.M.C. 5-2068-0\n" +
			"Nombramiento SUPREMO"
			;

			System.out.println("* Largo texto: " + texto.length() + " caracteres");
			out.write(texto.getBytes());
			out.flush();
			out.close();
			
			// Salida
			String salida = Base64.encodeBytes( os.toByteArray() );
			System.out.println( "* Salida B64: (" + salida.length() + " caracteres)\n" + salida );
			System.out.flush();
			
			// Decode
			byte[] entradaBytes = Base64.decode(salida);
			ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(entradaBytes));
			in.getNextEntry();
			StringBuffer buffer = new StringBuffer();
			byte[] bufferBytes = new byte[1024];
            int len;
            
            while ((len = in.read(bufferBytes)) > 0) {
                buffer.append( new String(bufferBytes, 0, len));
            }
            
			System.out.println( "* Decodificacion B64:" );
			System.out.println( buffer.toString() );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
