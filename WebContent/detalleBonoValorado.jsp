<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>

<%

UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

BonoDTO bono = new BonoDTO();
if (request.getAttribute("bono") != null){ bono = (BonoDTO) request.getAttribute("bono"); }

HabilitadoDTO habilitado = new HabilitadoDTO();
if (request.getAttribute("habilitado") != null){ habilitado = (HabilitadoDTO) request.getAttribute("habilitado"); }

PrestadorDTO prestador = new PrestadorDTO();
if (request.getAttribute("prestador") != null){ prestador = (PrestadorDTO) request.getAttribute("prestador"); }

RolbeneDTO rolbene = new RolbeneDTO();
if (request.getAttribute("rolbene") != null){ rolbene = (RolbeneDTO) request.getAttribute("rolbene"); }

BeneficiarioDTO beneficiario = new BeneficiarioDTO();
if (request.getAttribute("beneficiario") != null){ beneficiario = (BeneficiarioDTO) request.getAttribute("beneficiario"); }

BeneficiarioDTO beneficiario2 = new BeneficiarioDTO();
if (request.getAttribute("imponente") != null){ beneficiario2 = (BeneficiarioDTO) request.getAttribute("imponente"); }

String contrato="E";
String dcontrato="ERRONEO";
contrato = rolbene.getContrato();
if (contrato.equals("I")){ dcontrato="INSTITUCIONAL"; } 
if (contrato.equals("S")){ dcontrato="SUPREMO"; }

List datosDetalle = (List) request.getAttribute("datosDetalle");

String [] nombreCiudad = (String []) request.getAttribute("nombreCiudad");
String nombreEmisor = nombreCiudad[0];

boolean pagoDirecto = false;
if (request.getAttribute("pagoDirecto") != null){
  pagoDirecto = true;
}

%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Bono Valorado de Atenci&oacute;n M&eacute;dica</h1>
	
	<table id="listado">

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Fecha de Emision</td><td style="text-align:left"><%= sdf.format( bono.getFechaEmision() ) %></td>
			<td class="encabezados-tabla" style="text-align:left">N&uacute;mero de Bono</td><td style="text-align:left"><%= bono.getFolio() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Emisor</td><td style="text-align:left"><%= nombreEmisor %></td>
			<td class="encabezados-tabla" style="text-align:left">RUT Emisor</td><td style="text-align:left"><%= bono.getCodigoHabilitado() %></td>
		</tr>

<%--
		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Codigo de Validacion</td><td colspan="3" style="text-align:left"><%= datos.get("codigoValidacion") %></td>
		</tr>
--%>

	</table>
	
	<div style="font-size:4px">&nbsp;</div>
	
	<table id="listado">

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left;">Prestador</td><td style="text-align:left"><%= prestador.getRazonSocial() %></td>
			<td class="encabezados-tabla" style="text-align:left">RUT Prestador</td><td style="text-align:left"><%= prestador.getRutAcreedor() %></td>
		</tr>

	</table>
	
	<div style="font-size:4px">&nbsp;</div>
	
	<table id="listado">

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Beneficiario</td><td style="text-align:left"><%= beneficiario.getNombre()+" "+beneficiario.getPat()+" "+beneficiario.getMat() %></td>
			<td class="encabezados-tabla" style="text-align:left">N&uacute;mero de C.M.C.</td><td style="text-align:left"><%= rolbene.getRepart()+"-"+rolbene.getImpo()+"-"+rolbene.getCorrel() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Imponente</td><td style="text-align:left"><%= beneficiario2.getNombre()+" "+beneficiario2.getPat()+" "+beneficiario2.getMat() %></td>
			<td class="encabezados-tabla" style="text-align:left">N&uacute;mero de C.M.C.</td><td style="text-align:left"><%= rolbene.getRepart()+"-"+rolbene.getImpo()+"-0" %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td class="encabezados-tabla" style="text-align:left">Nombramiento</td><td colspan="3" style="text-align:left"><%= dcontrato %></td>
		</tr>

	</table>

	<div style="font-size:4px">&nbsp;</div>

<%
  if (datosDetalle != null && datosDetalle.size() > 0) {
%>
	<table id="listado">

		<tr class="encabezados-tabla">
			<td>C&oacute;digo</td>
			<td>Nombre Prestaci&oacute;n</td>
			<td>Valor</td>
			<td>Cargo Dipreca</td>
			<td>Cargo Seguro</td>
			<td><%= (pagoDirecto)?"Pago Directo":"Copago" %></td>
		</tr>

<%
		for (int i=0; i < datosDetalle.size(); i++) {
		    String[] fila = (String[]) datosDetalle.get(i);
		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase = (i%2 == 0)? "fila-detalle-par":"fila-detalle-impar";

		    String codigo = fila[0];
		    String nombre = fila[1];
		    String valor = fila[2];
		    String copagoDipreca = fila[3];
		    String copagoSeguro = fila[4];
		    String copago = fila[5];
		    
		    if ("0".equals(valor.trim())){
		      valor = "" +
		      	(Integer.parseInt(copagoDipreca) + Integer.parseInt(copagoSeguro) + Integer.parseInt(copago) );
		    }
%>
		<tr class="<%=clase%>">
			<td style="text-align:left"><%= codigo %></td>
			<td style="text-align:left"><%= nombre %></td>
			<td style="text-align:right"><%= valor  %></td>
			<td style="text-align:right"><%= copagoDipreca %></td>
			<td style="text-align:right"><%= copagoSeguro  %></td>
			<td style="text-align:right"><%= copago %></td>
		</tr>
		
<%
		}
  }
%>


	</table>

	<form name="formulario" action="BonoValoradoPDF.pdf" target="_blank"><input type="hidden" name="id_bono" value=""></form>	

    <!-- input type="button" class="button" onClick="document.location='BonoValorado'" value="Crear un nuevo Bono Valorado" -->
    <!--llrinput type="button" class="button" onClick="pdf(<%= bono.getFolio() %>)" value="Imprimir Bono" -->
    <input type="button" class="button" onClick="pdf(<%= bono.getId() %>)" value="Imprimir Bono">

	<p>
	<a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank"><img src="img/get_adobe_reader.gif" border="0"></a><br>
	Para imprimir bonos, requiere tener instalado el programa <a href="http://www.adobe.com/products/acrobat/readstep2.html">Acrobat Reader</a> (gratuito).

	</div>


	<script language="javascript">
	
	  function pdf (idFolio ){
	    //alert("No implementado");
	    document.formulario.id_bono.value = idFolio;
	    document.formulario.submit();
	  }



	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>
