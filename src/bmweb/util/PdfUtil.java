/*
 * Creado en 15-08-2005 por denis
 *
 */
package bmweb.util;

/**
 * @author denis
 *
 * Clase con metodos utilitarios para la generacion de PDFs
 */
public class PdfUtil {

	/**
	 * Recibe los parámetros y retorna un string XML-FO con una fila que representa el detalle. 
	 * 
	 * @param codigoPrestacion
	 * @param nombrePrestacion
	 * @param valor
	 * @param cargoDipreca
	 * @param cargoSeguro
	 * @param copago
	 * @return elemento xml fo:table-row como un String
	 */
	public static String getFilaDetalle(String codigoPrestacion, String nombrePrestacion, String valor,
										String cargoDipreca, String cargoSeguro, String copago){

		String grupo ="00";
		
		  
		if (codigoPrestacion == null || "".equals(codigoPrestacion)){
			codigoPrestacion = "<fo:block color=\"white\"> . </fo:block>";
		}
		/* if (grupo.equals("76") || grupo.equals("77")
		 || grupo.equals("85") || grupo.equals("86")
		 || grupo.equals("87") || grupo.equals("88")){
         */		
		 int Ivalor=0;
		 int IcargoDipreca=0;
		 int IcargoSeguro=0;
		 int Icopago=0;
		 if (cargoDipreca != null && !"".equals(cargoDipreca))
		 IcargoDipreca=Integer.parseInt(cargoDipreca);
		 if (cargoSeguro != null && !"".equals(cargoSeguro))
		 IcargoSeguro =Integer.parseInt(cargoSeguro);
		 if (copago != null && !"".equals(copago))
		 Icopago      =Integer.parseInt(copago);
		 Ivalor = IcargoDipreca + IcargoSeguro + Icopago;
		 if (Ivalor > 0){
		    valor = String.valueOf(Ivalor);
		    valor=TextUtil.formatearNumero( Integer.parseInt(valor));
		 } 
		if (cargoDipreca  != null && !"".equals(cargoDipreca))
		cargoDipreca=TextUtil.formatearNumero( Integer.parseInt(cargoDipreca));
		if (cargoSeguro != null && !"".equals(cargoSeguro))
		cargoSeguro=TextUtil.formatearNumero( Integer.parseInt(cargoSeguro));
		if (copago != null && !"".equals(copago))
		copago=TextUtil.formatearNumero( Integer.parseInt(copago));
		String fila = "" +  
        "<fo:table-row>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"right\" padding-right=\"2pt\"><fo:block>"+ codigoPrestacion + "</fo:block></fo:table-cell>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"left\" padding-left=\"2pt\"  ><fo:block>" + nombrePrestacion + "</fo:block></fo:table-cell>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"right\" padding-right=\"2pt\"><fo:block>" + valor + "</fo:block></fo:table-cell>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"right\" padding-right=\"2pt\"><fo:block>" + cargoDipreca + "</fo:block></fo:table-cell>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"right\" padding-right=\"2pt\"><fo:block>" + cargoSeguro + "</fo:block></fo:table-cell>" +
        "<fo:table-cell border-color=\"black\" border-width=\"0.5pt\" border-style=\"solid\" text-align=\"right\" padding-right=\"2pt\"><fo:block>" + copago + "</fo:block></fo:table-cell>" +
		"</fo:table-row>"; 

		return fila;
	}
	
}
