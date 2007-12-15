<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>

<%
  boolean salidaExcel = false;
  if ("excel".equals(request.getParameter("salida"))){
  	salidaExcel = true;
  }

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
  SimpleDateFormat sdf_yyyy = new SimpleDateFormat("yyyy");
  
  HashMap mapaCiudades = new HashMap();
  if (request.getAttribute("ciudades") != null){
  	mapaCiudades = (HashMap) request.getAttribute("ciudades");
  }
  
  List listaCiudades = new ArrayList();
  if (request.getAttribute("listaCiudades") != null){
  	listaCiudades = (List) request.getAttribute("listaCiudades");
  }

  List filasReporte = new ArrayList();
  if (request.getAttribute("filasReporte") != null){
	  filasReporte = (List) request.getAttribute("filasReporte");
  }

  // deteccion de parametros para filtros
  boolean mostrarFiltros = false;
  if ("1".equals(request.getParameter("mostrarFiltros"))) { mostrarFiltros = true; }
  
  String opfecha = "";
  if (request.getParameter("opfecha") != null) { opfecha = request.getParameter("opfecha"); }

  String fechaDesde = "01/01/" + sdf_yyyy.format(new Date());
  if (request.getParameter("fechaDesde") != null) { fechaDesde = request.getParameter("fechaDesde"); }

  String fechaHasta = "31/12/" + sdf_yyyy.format(new Date());
  if (request.getParameter("fechaHasta") != null) { fechaHasta = request.getParameter("fechaHasta"); }

  String domCiudad = "";
  if (request.getParameter("dom_ciudad") != null) { domCiudad = request.getParameter("dom_ciudad"); }
  
  String opPrestador = "";
  if (request.getParameter("opPrestador") != null) { opPrestador = request.getParameter("opPrestador"); }
  
  String prestador = "";
  if (request.getParameter("prestador") != null) { prestador = request.getParameter("prestador"); }
  
  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Reportes");
  
  // Calculo del gran total de bonos del reporte
  int granTotal = 0;
  for (Iterator it = filasReporte.iterator(); it.hasNext();) {
    Map fila = (Map) it.next();
    
    for (int reparticion = 1; reparticion <=6; reparticion++){
    	for (int impCarga = 0; impCarga <=1; impCarga++) {
    		for (int iSex = 0; iSex <= 1; iSex++){
    			
    			String sexo = (iSex==0)? "M" : "F";
    			String llave = reparticion + "." + impCarga + "." + sexo;
    			
    			String valor = "0";
    			if (fila.containsKey(llave)){ valor = fila.get(llave).toString(); }
    			
    			int intValor = (new Integer(valor)).intValue();
    			granTotal += intValor;
    		}
    	}
    }

  }

  
  
%>
<%
  if (!salidaExcel) {
%>

<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Reporte Estadístico</h1>

	<table class="tabla-borde-delgado" id="filtro-min" style="<%= (mostrarFiltros)? "display:none":"" %>">
		<tr class="encabezados-tabla">
			<td style="text-align:right">
				<a href="javascript:mostrar_filtros()" title="Mostrar opciones de b&uacute;squeda">
				Mostrar opciones de b&uacute;squeda</a>
			</td>
		</tr>
	</table>

	<table class="tabla-borde-delgado" id="filtro-max" style="<%= (mostrarFiltros)? "":"display:none" %>">
	<form name="formulario" method="post" action="Reportes">
		<input type="hidden" name="mostrarFiltros" value="<%= (mostrarFiltros)? "1":"0" %>">
		<input type="hidden" name="accion" value="listado">
		<input type="hidden" name="inicio" value="">
		<input type="hidden" name="dpp" value="">

		<input type="hidden" name="salida" value="">
		
		<tr class="encabezados-tabla">
			<td colspan="2">
				
			</td>
			<td>
				<span style="width:100%;text-align:right">
				<a href="javascript:ocultar_filtros()" title="Ocultar">Ocultar opciones de b&uacute;squeda</a>
				</span>
			</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td>Reparticiones a incluir</td>
			<td style="text-align:left">
				<!--
				<input type="checkbox" name="rep0" value="0"> Todas <br>
				<input type="checkbox" name="rep1" value="1"> Carabineros <br>
				<input type="checkbox" name="rep2" value="2"> Investigaciones <br>
				<input type="checkbox" name="rep3" value="3"> Gendarmer&iacute;a <br>
				<input type="checkbox" name="rep4" value="4"> Mutualidad Carabineros <br>
				<input type="checkbox" name="rep5" value="5"> Funcionarios Dipreca <br>
				<input type="checkbox" name="rep6" value="6"> Pensionados <br>
				<input type="checkbox" name="rep7" value="7"> Montepiados
				-->
				<select name="reparticiones" size="4" multiple="true">
					<option value="1">Carabineros</option>
					<option value="2">Investigaciones</option>
					<option value="3">Gendarmer&iacute;a</option>
					<option value="4">Mutualidad Carabineros</option>
					<option value="5">Funcionarios Dipreca</option>
					<option value="6">Pensionados</option>
					<option value="7">Montepiados</option>
				</select>
			</td>

			<!-- rowspan tantas filas como tenga el filtro -->
			<td rowspan="5" style="text-align:center; vertical-align:middle">
				<input type="button" value="Buscar datos" class="submit" 
				onClick="document.formulario.accion.value='listado';document.formulario.submit()"
				title="Muestra el listado de Habilitados usando los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<br>
				<input type="reset" class="button" value="Restaurar Valores" 
				title="Volver a los valores anteriores de los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<input type="button" class="button" value="Nueva B&uacute;squeda" onclick="limpiar_filtros()" 
				title="Limpiar los valores de los criterios de b&uacute;squeda"
				style="width:120px">
			</td>
		</tr>
		
		<tr class="fila-detalle-par">
			<td>Rango de Fechas</td>
			<td style="text-align:left">
			<%
			String estiloDivCalendario = "";
			if ("no".equals(opfecha)) estiloDivCalendario = "display:none";
			if ("".equals(opfecha)) estiloDivCalendario = "display:none";
			%>
				<select name="opfecha" onChange="mostrarCalendario()">
				<option value="no"    <%= "no".equals(opfecha)?"selected":"" %>>No filtrar por Fecha</option>
				<option value="entre" <%= "entre".equals(opfecha)?"selected":"" %>>Entre estas Fechas</option>
				</select>
				
				<br>

				<!-- fecha desde -->
				<span id="div-calendario" style="<%= estiloDivCalendario %>">
				<span id="span-fecha-desde"><%= fechaDesde %></span>
				<input type="hidden" id="fechaDesde" name="fechaDesde" size="10" class="input" value="<%= fechaDesde %>">
				
				<img src="img/calendar.gif" id="f_trigger_c1"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-desde",
				        inputField     :    "fechaDesde",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c1",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
				
				-

				<!-- fecha hasta -->
				<span id="span-fecha-hasta"><%= fechaHasta %></span>
				<input type="hidden" id="fechaHasta" name="fechaHasta" size="10" class="input" value="<%= fechaHasta %>">
				
				<img src="img/calendar.gif" id="f_trigger_c2"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-hasta",
				        inputField     :    "fechaHasta",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c2",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
				
				</span></td>
		</tr>
		
		<tr class="fila-detalle-impar">
			<td>Ciudad</td>
			<td style="text-align:left">
				<select name="dom_ciudad">
				<option value="">No filtrar por Ciudad</option>
			<%
				for (int i=0; i<listaCiudades.size(); i++){
				CiudadDTO c = (CiudadDTO) listaCiudades.get(i);
				String nombreCiudad = c.getNombre();
				String codigoCiudad = "" + c.getCodigo();
				
				String selected = "";
				if (domCiudad.equals(codigoCiudad)){ selected = "selected"; }
				
			%>
				<option value="<%= codigoCiudad  %>" <%= selected  %>><%= nombreCiudad %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>
		
		<tr class="fila-detalle-par">
			<td>RUT del prestador</td>
			<td style="text-align:left">
			<%
			String estiloDivRutPrestador = "";
			if ("no".equals(opPrestador) || "".equals(opPrestador)) estiloDivRutPrestador = "display:none";
			%>
				<select name="opPrestador" onChange="mostrarRutPrestador()">
				<option value="no" <%= "no".equals(opPrestador)?"selected":"" %>>No filtrar</option>
				<option value="si" <%= "si".equals(opPrestador)?"selected":"" %>>Filtrar por este RUT</option>
				</select>
				
				<br>

				<!-- filtro rut prestador -->
				<span id="div-opPrestador" style="<%= estiloDivRutPrestador %>">
				<input type="text" name="prestador" size="12" value="<%= prestador %>" onBlur="CampoEsRut(this)">				
				</span>
			</td>
		</tr>
		
	</table>

	<br>
<%
  // fin if (!salidaExcel)
  }
%>
	<table id="listado">

		<tr class="encabezados-tabla">
			<td rowspan="3"><br>Especialidades</td>
			<td rowspan="2" colspan="2">Total General</td>

			<td colspan="4">Total por sexo</td>
			<td colspan="4">Carabineros</td>
			<td colspan="4">Investigaciones</td>
			<td colspan="4">Gendarmer&iacute;a</td>
			<td colspan="4">Dipreca</td>
			<td colspan="4">Pensionados</td>
			<td colspan="4">Montepios</td>
		</tr>
		
		<tr class="encabezados-tabla">
			<!-- total -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- carabineros -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- investigaciones -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- gendarmeria -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- dipreca -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- pensionados -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<!-- montepios -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>
		</tr>

		<tr class="encabezados-tabla">
			<td>Total</td><td>%</td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
		</tr>

<%
		int numFila = 1;
		for (Iterator it = filasReporte.iterator(); it.hasNext();) {
		    Map fila = (Map) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    String especialidad = (String) fila.get("especialidad");
		    		    		    
%>
		<tr class="<%=clase%>">
			<td><%= especialidad %></td>

<%

			StringBuffer bufferLinea = new StringBuffer();
			int totalEspecialidad = 0;
			int totalImponentesMasc = 0;
			int totalImponentesFem = 0;
			int totalCargasMasc = 0;
			int totalCargasFem = 0;

		    for (int reparticion = 1; reparticion <=6; reparticion++){
		    	for (int impCarga = 0; impCarga <=1; impCarga++) {
		    		for (int iSex = 0; iSex <= 1; iSex++){
		    			
		    			String sexo = (iSex==0)? "M" : "F";
		    			String llave = reparticion + "." + impCarga + "." + sexo;
		    			
		    			String valor = "0";
		    			if (fila.containsKey(llave)){ valor = fila.get(llave).toString(); }
		    			
		    			int intValor = (new Integer(valor)).intValue();
		    			
		    			totalEspecialidad += intValor;
		    			
		    			// total imponentes masculinos x especialidad
		    			if ((impCarga==0) && (iSex==0)){ totalImponentesMasc += intValor; }
		    			
		    			// total imponentes femeninos x especialidad
		    			if ((impCarga==0) && (iSex==1)){ totalImponentesFem += intValor; }
		    			
		    			// total cargas masculinos x especialidad
		    			if ((impCarga==1) && (iSex==0)){ totalCargasMasc += intValor; }
		    			
		    			// total cargas femeninos x especialidad
		    			if ((impCarga==1) && (iSex==1)){ totalCargasFem += intValor; }
		    			
		    			bufferLinea.append("\t\t\t<td>");
		    			// DEBUG
		    			//bufferLinea.append("<!-- ");
		    			//bufferLinea.append(llave);
		    			//bufferLinea.append("-->");
		    			bufferLinea.append(valor);
		    			bufferLinea.append("</td>\n");
		    			

		    		}
		    	}
		    }
		    
			double porcentaje = (int)((totalEspecialidad*1000.0)/granTotal);
			porcentaje = porcentaje/10.0;

%>			
			<!-- total especialidad -->
			<td><%= totalEspecialidad %></td>
			
			<!-- porcentaje del total x especialidad -->
			<td><%= porcentaje %>%</td>
			
			<!-- total imponentes masculinos x especialidad -->
			<td><%= totalImponentesMasc %></td>
			
			<!-- total imponentes femeninos x especialidad -->
			<td><%= totalImponentesFem %></td>
			
			<!-- total cargas masculinos x especialidad -->
			<td><%= totalCargasMasc %></td>
			
			<!-- total cargas femeninos x especialidad -->
			<td><%= totalCargasFem %></td>

<%= bufferLinea.toString() %>
			
		</tr>
		
<%
		}
%>
	</table>
<%
  if (!salidaExcel) {
%>

	</div>
	

	<script language="javascript">

	  function mostrar_filtros(){
	  
	  	if (document.formulario.mostrarFiltros){
		  	document.formulario.mostrarFiltros.value = "1";
		}
	  
	    if (document.getElementById("filtro-min")){
	    	document.getElementById("filtro-min").style.display = "none";
	    }
	  
	    if (document.getElementById("filtro-max")){
	    	document.getElementById("filtro-max").style.display = "";
	    }
	  
	  }
	  
	  function ocultar_filtros(){

	  	if (document.formulario.mostrarFiltros){
		  	document.formulario.mostrarFiltros.value = "0";
		}
	  
	    if (document.getElementById("filtro-max")){
	    	document.getElementById("filtro-max").style.display = "none";
	    }
	  
	    if (document.getElementById("filtro-min")){
	    	document.getElementById("filtro-min").style.display = "";
	    }
	  
	  }
	  
	  function limpiar_filtros(){
	  
	  	// TODO: Hacer validaciones con if (document.formulario.campo) ...	  	
	  	document.formulario.opfecha.selectedIndex = 0;
		document.formulario.opemisor.selectedIndex = 0;
		document.formulario.emisor.value = "";
		
		// oculto las fechas
		document.getElementById("div-calendario").style.display = "none";
		
	  }
	  
	  function mostrarCalendario(){
	  
	    if (document.formulario.opfecha.selectedIndex > 0){
	  
		    if (document.getElementById("div-calendario")){
		    	document.getElementById("div-calendario").style.display = "";
		    }
	    } else {
		    if (document.getElementById("div-calendario")){
		    	document.getElementById("div-calendario").style.display = "none";
		    }
	    }
	  }
	  
	  function mostrarRutPrestador(){
	    if (document.formulario.opPrestador.selectedIndex > 0){
	  
		    if (document.getElementById("div-opPrestador")){
		    	document.getElementById("div-opPrestador").style.display = "";
		    }
	    } else {
		    if (document.getElementById("div-opPrestador")){
		    	document.getElementById("div-opPrestador").style.display = "none";
		    }
	    }
	  }

	  // En esta pÃ¡gina, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  // if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>
	
	<!-- boton salida a excel -->
		<input type="button" onClick="document.formulario.salida.value='excel';document.formulario.submit();document.formulario.salida.value='';"
		class="submit" value="Exportar como archivo Excel">

	
<!-- 
especialidad                    reparticion  imp_carga  sexo  subtotal  
------------------------------  -----------  ---------  ----  --------  
CONS.ESPEC.TRAUMATOLOGIA        2            02         M     1         
 -->
	
<jsp:include page="pie.jsp" flush="true"/>

<%
// fin if (!salidaExcel)
}
%>
