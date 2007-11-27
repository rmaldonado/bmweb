<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.UsuarioWeb" %>
<%@ page import="bmweb.dto.*" %>


<%
  //llr
  HttpSession sesion = request.getSession();
  UsuarioWeb usuarioWeb = (UsuarioWeb) sesion.getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  String Nivel = usuarioWeb.getNivel();  
  //f llr
  // registro nuevo?
  boolean puedeGrabar = false;
  
  boolean busquedaPorCodigo = "porCodigo".equals(request.getParameter("tipoBusqueda"));
  String ts = "" + (new Date()).getTime();
  String textoBusqueda = (request.getParameter("busqueda") == null)?"":request.getParameter("busqueda");
  System.out.println(textoBusqueda);
  String nombreCiudad = (String)request.getAttribute("nombreCiudad");
  String ciudad = request.getParameter("ciudad");
  
  BeneficiarioDTO beneficiario = (BeneficiarioDTO) request.getAttribute("beneficiario");
  String nombreBeneficiario = beneficiario.getNombre() + " " + beneficiario.getPat() + " " + beneficiario.getMat();
  String cmc = request.getParameter("cmc");

  // La lista de prestaciones encontradas si es que se hizo una busqueda
  List prestacionesEncontradas = null;
  if (request.getAttribute("prestacionesEncontradas") != null){
  	prestacionesEncontradas = (List) request.getAttribute("prestacionesEncontradas");
  }

  List prestacionesBono = new ArrayList();
  //prestacionesBono.add(new String[]{"0101001","Consulta Medicina General", "1", "Ambulatoria"});
  //prestacionesBono.add(new String[]{"0102120","Consulta Traumatologia", "1", "Ambulatoria"});
  if (request.getAttribute("prestacionesBono") != null){
  	prestacionesBono = (List) request.getAttribute("prestacionesBono");
  }

  List listaCiudades = new ArrayList();
  if (request.getAttribute("ciudades") != null){
  	listaCiudades = (List) request.getAttribute("ciudades");
  }
  
  List listaPrestadores = null;
  //listaPrestadores.add(new String[]{"123123","Centro Medico Los Robles","12500", "8500", "5600"});
  //listaPrestadores.add(new String[]{"456456","Centro Medico InfraSalud","17500", "8500", "9300"});
  if (request.getAttribute("listaPrestadores") != null){
  	listaPrestadores = (List) request.getAttribute("listaPrestadores");
  }
  

  // "prestaciones" es el conjunto generico de prestaciones
  List prestaciones = new ArrayList();
  if (request.getAttribute("prestaciones") != null){
  	prestaciones = (List) request.getAttribute("prestaciones");
  }

UsuarioWeb usuario = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);


  String cmc1 = "";
  String cmc2 = "";
  String cmc3 = "";
  if (request.getParameter("cmc") != null){
  	cmc = request.getParameter("cmc");
  	cmc1 = request.getParameter("cmc1");
  	cmc2 = request.getParameter("cmc2");
  	cmc3 = request.getParameter("cmc3");
  }
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>
	<form name="formulario" method="post" action="BonoValorado">
	<input type="hidden" name="accion" value="buscar">

	<input type="hidden" name="ciudad" value="<%= ciudad %>">
	<input type="hidden" name="cmc" value="<%= cmc %>">
	<input type="hidden" name="cmc1" value="<%= cmc1 %>">
	<input type="hidden" name="cmc2" value="<%= cmc2 %>">
	<input type="hidden" name="cmc3" value="<%= cmc3 %>">

    <h1>Creaci&oacute;n de Bonos Valorados - Paso 2 de 2</h1>
    
    <table id="listado">
      <tr class="encabezados-tabla"><td>Ciudad</td><td>Beneficiario</td><td>CMC</td></tr>
      <tr class="fila-detalle-impar"><td><%= nombreCiudad %></td><td><%= nombreBeneficiario %></td><td><%= cmc %></td></tr>
    </table>
    
    <input type="button" class="submit" value="&lt;&lt; Volver al paso anterior" onclick="volverPasoAnterior()">
    
    <script language="javascript">
    function volverPasoAnterior(){
      document.formulario.accion.value = "crear";
      document.formulario.submit();
    }
    </script>
    
    
    <br>
    <br>

    <b>B&uacute;squeda de Prestaciones</b>

	
	<table>
		<tr>
			<td colspan="3">Ingrese el nombre o c&oacute;digo de la prestaci&oacute;n:</td>
		</tr>
		<tr>
			<td><input type="text" name="busqueda" size="40" maxlength="40" value="<%= textoBusqueda %>"></td>
			<td> 
				<select name="tipoBusqueda">
					<option value="porNombre">por Nombre de la prestaci&oacute;n</option>
					<option value="porCodigo" <%= busquedaPorCodigo?"selected":"" %>>por C&oacute;digo de la prestaci&oacute;n</option>
				</select>
			</td>
			<td><input type="button" class="submit" value="Buscar Prestaciones" onclick="buscar()"></td>
		</tr>
	</table>
	
	<script language="">
	  function buscar(){
	  	if (document.formulario.busqueda.value == ""){
	  	  alert("Debe ingresar un codigo o nombre de prestacion para buscar");
	  	  return;
	  	}

	  	if (document.formulario.busqueda.value.length < 4){
	  	  alert("Debe ingresar un codigo o nombre de al menos 5 letras o numeros");
	  	  return;
	  	}
	  
	  	document.formulario.accion.value = "buscar";
	  	document.formulario.submit();
	  }
	</script>
	<!-- /form -->

<%
if (prestacionesEncontradas != null) {

	if (prestacionesEncontradas.size() > 0) {

%>
	<!-- form name="formularioBusqueda" method="post" action="BonoValorado" -->
    <b>Prestaciones encontradas:</b><br>
	Se han encontrado <%= prestacionesEncontradas.size() %> 
	<%= prestacionesEncontradas.size()==1?"prestacion":"prestaciones" %>
	para el texto "<%= textoBusqueda %>".<br>
	Seleccione una prestaci&oacute;n de la lista para agregarla al bono o busque nuevamente.<br>

	<select name="codPrestacionNueva" style="width:400px">
	<%
	for (int i=0; i<prestacionesEncontradas.size(); i++){
	  String[] fila = (String []) prestacionesEncontradas.get(i);
	  String codigo = fila[0];
	  String nombre = fila[1];
	%>
	  <option value="<%= codigo %>"><%= codigo %>&nbsp;&nbsp;&nbsp;&nbsp;<%= nombre %></option>
	<%
	}
	%>
	</select>
	<input type="button" value="Agregar esta prestaci&oacute;n al bono" onclick="agregarPrestacion()">
	<!-- /form -->
	<script language="javascript">
	  function agregarPrestacion(){
	    document.formulario.action = "BonoValorado#detalle_prestaciones";
	    document.formulario.accion.value = "agregar";
	    document.formulario.submit();
	  }
	</script>

<%
	} else {
%>
    <div class="alerta">
	No se han encontrado prestaciones para el c&oacute;digo o nombre indicados en la b&uacute;squeda.
	</div>
<%
	}
}
%>
 
    <a name="detalle_prestaciones"><h1>Detalle de Prestaciones del bono</h1></a>
	<!-- form name="formulario" method="post" action="BonoValorado" -->
	<input type="hidden" name="prestacionEliminar" value="">
	<%
	if ( prestacionesBono.size() == 0) {
	%>
    <div class="alerta">
	El bono no contiene prestaciones.<br>
	Busque prestaciones por el nombre o c&oacute;digo y agr&eacute;guelas al bono.
	</div>
	<%
	} else {
	%>

	<table id="listado">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo</td>
			<td>Nombre de la atenci&oacute;n</td>
			<td>Cantidad</td>
			<td>Tipo de Atenci&oacute;n</td>
			<td></td>
		</tr>

	<% 
	for (int j=0; j<prestacionesBono.size(); j++) {
	  String [] fila = (String[]) prestacionesBono.get(j);
	  String codigo = fila[0];
	  String nombre = fila[1];
	  String cantidad = fila[2];
	  String tipo = fila[3];
	  String estilo = (j%2==0)?"fila-detalle-par":"fila-detalle-impar";
	%>
		<tr class="<%= estilo %>">
			<td><%= codigo %></td>
			<td><%= nombre %></td>
			<td><input type="text" id="cantidad.<%= j+1 %>" name="cantidad.<%= j+1 %>" value="<%= cantidad %>" size="2" 
				maxlength="2" style="text-align:right;" onChange="revisarCampo('cantidad.<%= j+1 %>')"></td>
			<td><%= tipo %></td>
			<td>
				<input type="hidden" name="prestacion.<%= j+1 %>" value="<%= codigo %>">
				<input type="button" value="Eliminar" onClick="eliminar('<%= codigo %>', '<%= nombre %>')">
			</td>
		</tr>
	<% } %>	
	</table>
	
	<input type="button" id="btnActualizarCantidades" value="Actualizar cantidades en prestaciones" onClick="actualizarCantidades()">
	
	<!-- /form -->    
	<script language="javascript">

	  function revisarCampo(idCampo){
	  		if (document.getElementById(idCampo)){
	  		  var campo = document.getElementById(idCampo);
	  		  if (CampoEsNumeroEnRango(campo, 1, 99)){
	  		  
	  		  	campo.style.background = 'white';
	  		  	
	  		    if (document.getElementById('emitirBono')){
	  		    	document.getElementById('emitirBono').disabled = false;
	  		    }

	  		    if (document.getElementById('btnActualizarCantidades')){
	  		    	document.getElementById('btnActualizarCantidades').disabled = false;
	  		    }

	  		  } else {
	  		  	campo.style.background = 'red';

	  		    if (document.getElementById('emitirBono')){
	  		    	document.getElementById('emitirBono').disabled = true;
	  		    }

	  		    if (document.getElementById('btnActualizarCantidades')){
	  		    	document.getElementById('btnActualizarCantidades').disabled = true;
	  		    }
	  		  }
	  		}
	  }
	
	  function actualizarCantidades(){
	        document.formulario.action = "BonoValorado#detalle_prestaciones";
		    document.formulario.accion.value = "cambiarCantidades";
		    document.formulario.submit();
	  }
		
	  function eliminar(codigo, nombre){
	    if (confirm("Confirme que desea eliminar la prestacion '" + nombre + "' del bono")){
	        document.formulario.action = "BonoValorado#detalle_prestaciones";
		    document.formulario.accion.value = "eliminar";
		    document.formulario.prestacionEliminar.value = codigo;
		    document.formulario.submit();
	    }
	  }
	</script>
	<%
	}
	%>


	<!-- Si no hay prestaciones, no muestro info de los prestadores -->
	<%
	if ( prestacionesBono.size() > 0 && (null == request.getAttribute("NOVALORIZAR"))) {
	%>
    
    <a name="prestadores"><h1>Prestadores que pueden realizar las atenciones</h1></a>
	<%
	if ( listaPrestadores == null) {
	%>
	<%
	if ( "22".equals(Nivel) ) //Habilitado//
    {
	%>
    <!-- Nuevo Luis Latin Ramirez -->
     <tr>
         Si ya conoce al Prestador; ingrese el Rut
         <input type="text" name="rutdirecto" size="11" maxlength="11" value="">
     </tr>
    <% 
    }
    %>
    <!-- Fin Nuevo Luis Latin Ramirez -->
     
    <input type="button" value="Buscar prestadores para estas atenciones" onClick="buscarPrestadores()">
	<script language="javascript">
	  function buscarPrestadores(){
	  	    document.formulario.action = "BonoValorado#prestadores";
		    document.formulario.accion.value = "buscarPrestadores";
		    document.formulario.submit();
	  }
	</script>
    
	<%
	} else {
	
		if ( listaPrestadores.size() == 0) {
	%>
    <div class="alerta">
	No existen prestadores capaces de realizar todas las prestaciones que se indican en el
	bono, y en la ciudad elegida. Debe cambiar alguna prestaci&oacute;n para volver a
	consultar los prestadores disponibles.
	</div>
	<%
		} else {
	%>

	Para emitir el bono con uno de los prestadores de la lista,
	use el bot&oacute;n "Emitir Bono" junto al prestador seleccionado.

	<table id="listado">
		<tr class="encabezados-tabla">
			<td>Nombre del Prestador</td>
			<td>Aporte Dipreca</td>
			<td>Aporte Seguro</td>
			<td>Copago</td>
			<td></td>
		</tr>

	<% 
			for (int j=0; j<listaPrestadores.size(); j++) {
			  String [] fila = (String[]) listaPrestadores.get(j);
			  String rut = fila[0];
			  String nombre = fila[1];
			  String aporteDipreca = fila[2];
			  String aporteSeguro = fila[3];
			  String copago = fila[4];

			  String aradif = fila[5];
			  int dif = 0;
			  if (aradif != null){ dif=1; }
			  
			  String estilo = (j%2==0)?"fila-detalle-par":"fila-detalle-impar";
	%>
		<tr class="<%= estilo %>">
			<td><%= nombre %></td>
			<td><%= aporteDipreca %></td>
			<td><%= aporteSeguro %></td>
			<td style="background-color: #ffc;"><%= copago %></td>
			<td><input type="button" id="emitirBono" value="Emitir Bono" onClick="emitir('<%= rut %>', '<%= nombre %>', <%= dif %>)"></td>
		</tr>
	<% 		} %>	
	</table>
	<input type="hidden" name="rutPrestador" value="">
	
	<script language="javascript">
	  function emitir(rut, nombre, dif) {
	  
	    var msg = "Confirme que desea emitir el bono con '" + nombre + "'";
	    
	    if (dif == 1){
	      msg = "Confirme que desea emitir el bono con '" + nombre + "',\n" +
	      		" ya que este prestador tiene un arancel diferenciado. Se recomienda\n" +
	      		" emitir un bono abierto.";
	    }
	  
	    if (confirm(msg)) {
		    document.formulario.accion.value = "insertar";
		    document.formulario.rutPrestador.value = rut;
		    document.formulario.submit();
	    }
	  }
	</script>
	
	<%
			} // else - hay prestadores
		} // else prestadores != null
	} // si hay prestaciones
	%>
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
	  }

	  function revalidarCMC(){
	 	document.formulario.btnsubmit.disabled='disabled';
	 	document.getElementById("nombreBeneficiario").innerHTML = "Debe volver a validar CMC";
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
	      document.formulario.accion.value = "crear2";
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
