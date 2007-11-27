<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<jsp:include page="cabecera.jsp" flush="true"/>


	<div>
					
			<h3>Validaci&oacute;n de Bonos Valorados</h3>
			<div>
<%
// codPrestacion, folioBono, rutHabilitado, cmcBeneficiario
String codigo = "";
if (request.getParameter("codigo") != null){
  codigo = request.getParameter("codigo");
}

String[] resultado;
if (request.getAttribute("resultado") != null) {
  resultado = (String []) request.getAttribute("resultado");
%>
				<table id="listado">
				
				<tr class="encabezados-tabla">
					<td colspan="2">Valide los siguientes datos del bono</td>
				</tr>
				
				<tr class="fila-detalle-impar">
					<td>C&oacute;digo de la primera prestaci&oacute;n</td>
					<td><%= resultado[0] %></td>
				</tr>
				
				<tr class="fila-detalle-par">
					<td>Folio del Bono</td>
					<td><%= resultado[1] %></td>
				</tr>
				
				<tr class="fila-detalle-impar">
					<td>RUT del Emisor</td>
					<td><%= resultado[2] %></td>
				</tr>
				
				<tr class="fila-detalle-par">
					<td>CMC del Beneficiario</td>
					<td><%= resultado[3] %></td>
				</tr>
				
				</table>

<%
}
%>
				</div>
						<form name="formulario" method="post" action="BonoValorado">
						<input type="hidden" name="accion" value="validarCodigoBono">
							<div class="destacado">
								C&oacute;digo de validaci&oacute;n:
								<input name="codigo" value="<%= codigo %>" maxlength="20" size="20" type="text" class="input">
								&nbsp;<input name="ingresar" value="Revisar C&oacute;digo" type="submit" class="submit">
							</div>
						</form>

				</div>

	
<jsp:include page="pie.jsp" flush="true"/>
