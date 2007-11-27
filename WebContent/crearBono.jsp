<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.UsuarioWeb" %>
<%@ page import="bmweb.dto.*" %>


<%
  // registro nuevo?
  boolean puedeGrabar = false;

  String ts = "" + (new Date()).getTime();
  
  List listaCiudades = new ArrayList();
  if (request.getAttribute("ciudades") != null){
  	listaCiudades = (List) request.getAttribute("ciudades");
  }
  
  List listaPrestadores = null;
  if (request.getAttribute("listaPrestadores") != null){
  	listaPrestadores = (List) request.getAttribute("listaPrestadores");
  	puedeGrabar = true;
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
  List prestaciones = new ArrayList();
  if (request.getAttribute("prestaciones") != null){
  	prestaciones = (List) request.getAttribute("prestaciones");
  }

UsuarioWeb usuario = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

    <h1>Creaci&oacute;n de Bonos Abiertos</h1>
    	
	<form name="formulario" method="post" action="Bonos">
	<input type="hidden" name="ts" value="<%= ts %>">
	<input type="hidden" name="accion" value="crear">
	
	<table id="listado">	

		<tr class="encabezados-tabla">
			<td colspan="2">Crear un nuevo Bono</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:right" width="40%">Emisor</td>
			<td style="text-align:left"><%= usuario.getNombreCompleto() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:right">Seleccione el tipo de prestaci&oacute;n a realizar</td>
			<td style="text-align:left">
			<select name="prestacionGenerica" style="width:450px" onChange="cambiaPrestacionOCiudad()">
			<option>Seleccione un tipo de prestaci&oacute;n</option>
			<%-- La lista de prestaciones, con la prestacion seleccionada --%>
			<%
			for (int j=0; prestaciones != null && j<prestaciones.size(); j++){
				PrestacionGenericaDTO g = (PrestacionGenericaDTO) prestaciones.get(j);
				String paramPrestacion = request.getParameter("prestacionGenerica");
				String selected = (new Integer(g.getCodigo()).toString().equals( paramPrestacion ))?" selected":"";
			%>
			<option value="<%= g.getCodigo() %>"<%= selected %>><%= g.getNombre().trim() %></option>
			<%
			}
			%>
			</select>
			</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:right">Ciudad en que se realizar&aacute; la atenci&oacute;n</td>
			<td style="text-align:left">
						
			<select id="comboCiudades" name="ciudad" onChange="cambiaPrestacionOCiudad()">
			<!-- option value="">Seleccione una ciudad de la lista</option -->
			<option>Seleccione una Ciudad</option>
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

			<br>
			
			<input type="button" value="Buscar Prestadores" name="buscaPrestadores" onClick="recuperaPrestadores()">

			<script language="javascript">
			function recuperaPrestadores() {

			    if (document.formulario.prestacionGenerica.selectedIndex == 0){
			      alert("Debe seleccionar un tipo de prestacion de la lista");
			      return false;
			    }
			
			    // Si no se ha seleccionado una ciudad no puedo buscar
			    if (document.formulario.ciudad.selectedIndex == 0){
			      alert("Debe seleccionar una ciudad de la lista");
			      return false;
			    }
			
				document.formulario.buscaPrestadores.disabled = true;
				document.formulario.buscaPrestadores.value = "Buscando prestadores...";
				document.formulario.accion.value = "crear";
				document.formulario.submit();
			}
			</script>

			</td>
		</tr>


<% if (listaPrestadores != null && listaPrestadores.size() > 0) { %>
		<tr class="fila-detalle-par">
			<td style="text-align:right">Prestador</td>
			<td style="text-align:left">

			<!-- <%= listaPrestadores.size() %> -->
			<select name="idPrestador" id="selectPrestador">
			<option>Seleccione un prestador de la lista</option>
			<%
				for (int i=0; i<listaPrestadores.size(); i++){
				  Object[] prestador = (Object[]) listaPrestadores.get(i);
				  String rutPrestador = (String) prestador[0];
				  String nombrePrestador = (String) prestador[1];
			%>
				<option value="<%= rutPrestador.trim()  %>"><%= nombrePrestador.trim() %></option>
			<%
				}
			%>
			</select>
			</td>
		</tr>

		<tr class="fila-detalle-par">
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


<%
 }
%>

<% if ( (listaPrestadores != null) && ( listaPrestadores.size() == 0 ) ) { %>
		<tr class="fila-detalle-impar">
			<td colspan="2">
				No se encontraron prestadores que realicen el tipo de prestaci&oacute;n seleccionado en dicha ciudad.<br><br>
				Busque nuevamente en otra ciudad o por otro tipo de prestaci&oacute;n.
			</td>
		</tr>

<%
}
%>
	
<% if (listaPrestadores != null && listaPrestadores.size() > 0) { %>
<!-- Si hay prestadores para mostrar, tambien pongo el boton para enviar -->
		<tr class="fila-detalle-impar">
			<td colspan="2">
				<input name="btnsubmit" type="button" onClick="validar()" value="Crear Bono" class="submit" disabled="disabled">
			</td>
		</tr>
<%
}
%>

		
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
	      if (document.formulario.revisarCMC){
		    document.formulario.revisarCMC.disabled = true;
		  }
		  
		  if (document.formulario.btnsubmit){
	  	    document.formulario.btnsubmit.disabled = "true";
	  	  }
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
	  
	  	    if ( true 
	    		&& CampoEsNoNulo(document.formulario.cmc1)
	    		&& CampoEsNoNulo(document.formulario.cmc2)
	    		&& CampoEsNoNulo(document.formulario.cmc3)
	    		// && CampoEsNumero(document.formulario.cmc1) // Ahora el primero es alfanumerico
	    		&& CampoEsNumero(document.formulario.cmc2)
	    		&& CampoEsNumero(document.formulario.cmc3)
	    	) {

		  	  document.formulario.btnsubmit.disabled = "true";
		  	  document.getElementById("nombreBeneficiario").innerHTML = "Buscando Beneficiario...";
			  document.formulario.cmc.value = document.formulario.cmc1.value + "-" + document.formulario.cmc2.value + "-" + document.formulario.cmc3.value;
			  var carne = document.formulario.cmc.value;
			  //alert(carne);
			  window.frames[0].location = "Bonos?accion=buscarBeneficiario&CMC=" + carne;
			  
	    	}
	  
	  
	  }
	
	  function validar(){

		if (document.formulario.idPrestador.selectedIndex == 0){
		  alert("Debe seleccionar un prestador de la lista");
		  return false;
		}

	    if ( true 
	    		&& CampoEsNoNulo(document.formulario.cmc1)
	    		&& CampoEsNoNulo(document.formulario.cmc2)
	    		&& CampoEsNoNulo(document.formulario.cmc3)
	    		// && CampoEsNumero(document.formulario.cmc1) // Ahora el primero es alfanumerico
	    		&& CampoEsNumero(document.formulario.cmc2)
	    		&& CampoEsNumero(document.formulario.cmc3)
	    	) {
	    
	      document.formulario.cmc.value = document.formulario.cmc1.value + "-" + document.formulario.cmc2.value + "-" + document.formulario.cmc3.value;
	    
	      if (confirm("Confirme que desea emitir el bono")){
		      document.formulario.btnsubmit.value = "Guardando Bono...";
		      document.formulario.buscaPrestadores.disabled = true;
		      document.formulario.btnsubmit.disabled = "true";
		      document.formulario.accion.value = "insertar";
		      document.formulario.submit();
	      }
	    
	    }
	    
	  }
	  
	  function guardar(){
	    document.formulario.submit();
	  }
	  
	</script>

</div>

<jsp:include page="pie.jsp" flush="true"/>
