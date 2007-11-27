<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.UsuarioWeb" %>
<%@ page import="bmweb.dto.*" %>


<%
  List listaCiudades = new ArrayList();
  if (request.getAttribute("ciudades") != null){
  	listaCiudades = (List) request.getAttribute("ciudades");
  }
  
  String cmc = "";
  String cmc1 = "";
  String cmc2 = "";
  String cmc3 = "";
  if (request.getParameter("cmc") != null){
  	cmc = request.getParameter("cmc");
  	cmc1 = request.getParameter("cmc1");
  	cmc2 = request.getParameter("cmc2");
  	cmc3 = request.getParameter("cmc3");
  }


  // "prestaciones" es el conjunto generico de prestaciones

UsuarioWeb usuario = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

    <h1>Creaci&oacute;n de Bonos Valorados - Paso 1 de 2</h1>
    	
	<form name="formulario" method="post" action="BonoValorado">
	<input type="hidden" name="accion" value="crear">
	
	<table id="listado">	

		<tr class="fila-detalle-impar">
			<td style="text-align:right" width="40%">Emisor</td>
			<td style="text-align:left"><%= usuario.getNombreCompleto() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:right">Ciudad en que se realizar&aacute; la atenci&oacute;n</td>
			<td style="text-align:left">
						
			<select id="comboCiudades" name="ciudad" onChange="cambiaPrestacionOCiudad()">
			<!-- option value="">Seleccione una ciudad de la lista</option -->
			<%
				for (int i=0; i<listaCiudades.size(); i++){
				CiudadDTO c = (CiudadDTO) listaCiudades.get(i);
				String nombreCiudad = c.getNombre();
				String codigoCiudad = "" + c.getCodigo();
				
				String selected = "";
				String paramCiudad = request.getParameter("ciudad");

				if (codigoCiudad != null && codigoCiudad.equals(paramCiudad)){ selected = "selected"; }
				if (codigoCiudad.equals(request.getParameter("ciudad")) ){ selected = "selected"; }
			%>
				<option value="<%= codigoCiudad  %>" <%= selected  %>><%= nombreCiudad %></option>
			<%
				}
			%>
			</select>
			</td>
		</tr>


		<tr class="fila-detalle-impar">
			<td style="text-align:right">Carnet de Medicina Curativa</td>
			<td style="text-align:left">
				<input type="text" name="cmc1" size="1" maxlength="1" value="<%= cmc1 %>" onChange="revalidarCMC()">
				-
				<input type="text" name="cmc2" size="6" maxlength="6" value="<%= cmc2 %>" onChange="revalidarCMC()" onBlur="llenarIzq('000000', document.formulario.cmc2)">
				-
				<input type="text" name="cmc3" size="2" maxlength="2" value="<%= cmc3 %>" onChange="revalidarCMC()" onBlur="llenarIzq('00', document.formulario.cmc3)">
				
				<input type="button" name="revisarCMC" value="Verificar Beneficiario" onClick="buscoBeneficiario()">
				<div id="nombreBeneficiario"></div>
				
				<input type="hidden" name="cmc" size="30" maxlength="30" value="<%= cmc %>">
				<iframe src="" style="display:none"></iframe>
			</td>		
		</tr>

<!-- Coloco el boton para pasar a la siguiente pantalla, pero deshabilitado al principio -->
		<tr class="fila-detalle-impar">
			<td colspan="2">
				<input name="btnsubmit" type="button" onClick="validar()" value="Continuar &gt;&gt;" class="submit" disabled="disabled">
			</td>
		</tr>

		
	</table>

	</form>
	
	<script language="javascript">
	
		function llenarIzq(mascara, campo){
			var antes = campo.value;
			var despues = "";
			if (antes.length < mascara.length){
				despues = mascara.substring(0,mascara.length - antes.length);
				despues = despues + "" + antes;
			} else {
				despues = antes;
			}
			campo.value = despues;
		}
	
	
	  function cambiaPrestacionOCiudad(){
	  
	      // En esta pantalla, no deshabilito el CMC
	      //if (document.formulario.revisarCMC){
		  //  document.formulario.revisarCMC.disabled = true;
		  //}
		  
		  if (document.formulario.btnsubmit){
	  	    document.formulario.btnsubmit.disabled = "true";
	  	  }
	  	  
	  	  revalidarCMC();
	  }

	  function revalidarCMC(){
	  
	  
  	    if ( true 
    		&& CampoEsNumero(document.formulario.cmc2)
    		&& CampoEsNumero(document.formulario.cmc3)
    	) {
    	
    		    if (document.formulario.cmc1.value){
			 	  document.formulario.cmc1.value = document.formulario.cmc1.value.toUpperCase();
			    }

			 	document.formulario.btnsubmit.disabled='disabled';
			 	document.getElementById("nombreBeneficiario").innerHTML = "Debe volver a validar CMC";
		  }	  
	  
	  }

	  function buscoBeneficiario(){
	  	  document.formulario.btnsubmit.disabled = "true";
		  document.formulario.cmc.value = document.formulario.cmc1.value + "-" + document.formulario.cmc2.value + "-" + document.formulario.cmc3.value;
		  var carne = document.formulario.cmc.value;
		  
		  // Valido si se presiona el boton para validar, sin ingresar datos
		  if (carne.length < 11) { alert('Debe ingresar un CMC valido para crear un bono.'); return; }
		  
		  //alert(carne);
	  	  document.getElementById("nombreBeneficiario").innerHTML = "Buscando Beneficiario...";
		  window.frames[0].location = "Bonos?accion=buscarBeneficiario&CMC=" + carne;
	  }
	
	  function validar(){

		// TODO: Agregar validacion del CMC como en el mantenedor de Habilitados	    
	    if ( true 
	    		&& CampoEsNoNulo(document.formulario.cmc1)
	    		&& CampoEsNoNulo(document.formulario.cmc2)
	    		&& CampoEsNoNulo(document.formulario.cmc3)
	    		// && CampoEsNumero(document.formulario.cmc1) // Ahora el primero es alfanumerico
	    		&& CampoEsNumero(document.formulario.cmc2)
	    		&& CampoEsNumero(document.formulario.cmc3)
	    	) {
	    
	      document.formulario.cmc.value = document.formulario.cmc1.value + "-" + document.formulario.cmc2.value + "-" + document.formulario.cmc3.value;
	    
	      //if (confirm("Confirme que desea emitir el bono")){
	      //document.formulario.btnsubmit.value = "Guardando Bono...";
	      document.formulario.btnsubmit.disabled = "true";
	      document.formulario.revisarCMC.disabled = true;
	      document.formulario.accion.value = "cambiarPrestaciones";
	      document.formulario.submit();
	      //}
	    
	    }
	    
	  }
	  
	  function guardar(){
	    document.formulario.submit();
	  }
	  
	</script>

</div>

<jsp:include page="pie.jsp" flush="true"/>
