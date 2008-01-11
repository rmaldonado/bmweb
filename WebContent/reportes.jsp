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
  SimpleDateFormat sdfReporte = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  SimpleDateFormat sdf_yyyy = new SimpleDateFormat("yyyy");
  
  Map mapaCiudades = new HashMap();
  if (request.getAttribute("ciudades") != null){
  	mapaCiudades = (Map) request.getAttribute("ciudades");
  }
  
  Map mapaJurisdicciones = new HashMap();
  if (request.getAttribute("jurisdicciones") != null){
	  mapaJurisdicciones = (Map) request.getAttribute("jurisdicciones");
  }
  
  Map mapaRegiones = new HashMap();
  if (request.getAttribute("regiones") != null){
	  mapaRegiones = (Map) request.getAttribute("regiones");
  }
  
  Map mapaAgencias = new HashMap();
  if (request.getAttribute("agencias") != null){
	  mapaAgencias = (Map) request.getAttribute("agencias");
  }
  
  Map mapaReparticiones = new HashMap();
  if (request.getAttribute("reparticiones") != null){
	  mapaReparticiones = (Map) request.getAttribute("reparticiones");
  }
  
  List listaCiudades = new ArrayList();
  if (request.getAttribute("listaCiudades") != null){
  	listaCiudades = (List) request.getAttribute("listaCiudades");
  }

  List listaJurisdicciones = new ArrayList();
  if (request.getAttribute("listaJurisdicciones") != null){
	  listaJurisdicciones = (List) request.getAttribute("listaJurisdicciones");
  }

  List listaRegiones = new ArrayList();
  if (request.getAttribute("listaRegiones") != null){
	  listaRegiones = (List) request.getAttribute("listaRegiones");
  }

  List listaAgencias = new ArrayList();
  if (request.getAttribute("listaAgencias") != null){
	  listaAgencias = (List) request.getAttribute("listaAgencias");
  }

  List listaReparticiones = new ArrayList();
  if (request.getAttribute("listaReparticiones") != null){
	  listaReparticiones = (List) request.getAttribute("listaReparticiones");
  }

  
  List filasReporte = new ArrayList();
  if (request.getAttribute("filasReporte") != null){
	  filasReporte = (List) request.getAttribute("filasReporte");
  }

  // deteccion de parametros para filtros
  boolean mostrarFiltros = true;
  if ("0".equals(request.getParameter("mostrarFiltros"))) { mostrarFiltros = false; }
  
  String opfecha = "";
  if (request.getParameter("opfecha") != null) { opfecha = request.getParameter("opfecha"); }

  String fechaDesde = "01/01/" + sdf_yyyy.format(new Date());
  if (request.getParameter("fechaDesde") != null) { fechaDesde = request.getParameter("fechaDesde"); }

  String fechaHasta = "31/12/" + sdf_yyyy.format(new Date());
  if (request.getParameter("fechaHasta") != null) { fechaHasta = request.getParameter("fechaHasta"); }


  String domCiudad = "";
  if (request.getParameter("dom_ciudad") != null) { domCiudad = request.getParameter("dom_ciudad"); }
  
  String domJurisdiccion = "";
  if (request.getParameter("dom_jurisdiccion") != null) { domJurisdiccion = request.getParameter("dom_jurisdiccion"); }
  
  String domRegion = "";
  if (request.getParameter("dom_region") != null) { domRegion = request.getParameter("dom_region"); }
  
  String domAgencia = "";
  if (request.getParameter("dom_agencia") != null) { domAgencia = request.getParameter("dom_agencia"); }
  
  String opPrestador = "";
  if (request.getParameter("opPrestador") != null) { opPrestador = request.getParameter("opPrestador"); }
  
  String prestador = "";
  String prestadorN = "";
  String prestadorDV = "";
  if (request.getParameter("prestador") != null) { 
	  prestador = request.getParameter("prestador");
	  if (prestador.length() > 2){
		  prestadorN = prestador.substring(0, prestador.length() -2);
		  prestadorDV = prestador.substring(prestador.length() -1, prestador.length());
	  }
  }

  String opPrestacion = "";
  if (request.getParameter("opPrestacion") != null) { opPrestacion = request.getParameter("opPrestacion"); }
  
  String prestacion = "";
  if (request.getParameter("prestacion") != null) { prestacion = request.getParameter("prestacion"); }

  String estadoBono = "";
  if (request.getParameter("estadoBono") != null) { estadoBono = request.getParameter("estadoBono"); }

  String tipoEstadistica = "";
  if (request.getParameter("tipoEstadistica") != null) { tipoEstadistica = request.getParameter("tipoEstadistica"); }

  String bonoSeleccion = "";
  if (request.getParameter("bonoSeleccion") != null) { bonoSeleccion = request.getParameter("bonoSeleccion"); }
  
  String lasReparticiones = "";
  if (request.getAttribute("lasReparticiones") != null) { lasReparticiones = (String) request.getAttribute("lasReparticiones"); }
  
  String CJRA = "";
  if (request.getParameter("CJRA") != null) { CJRA = (String) request.getParameter("CJRA"); }
  
  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Reportes");

  boolean mostrarTablas = false;
  if ("mostrarTablas".equals(request.getParameter("mostrarTablas")) ) { mostrarTablas = true; }
  
  // fila con los totales
  Map filaTotales = new HashMap();
  filaTotales.put("totalImponentesMasc", new Integer(0));
  filaTotales.put("totalImponentesFem", new Integer(0));
  filaTotales.put("totalCargasMasc", new Integer(0));
  filaTotales.put("totalCargasFem", new Integer(0));
  filaTotales.put("total", new Integer(0));
  
  Map filaValoresTotales = new HashMap();
  filaValoresTotales.put("totalImponentesMasc", new Integer(0));
  filaValoresTotales.put("totalImponentesFem", new Integer(0));
  filaValoresTotales.put("totalCargasMasc", new Integer(0));
  filaValoresTotales.put("totalCargasFem", new Integer(0));
  filaValoresTotales.put("total", new Integer(0));

  // Calculo del gran total de bonos del reporte
  int granTotal = 0;
  int granValor = 0;
  for (Iterator it = filasReporte.iterator(); it.hasNext();) {
    Map fila = (Map) it.next();
    
    for (int r  = 0; r < listaReparticiones.size(); r++){
    	for (int impCarga = 0; impCarga <=1; impCarga++) {
    		for (int iSex = 0; iSex <= 1; iSex++){
    			
    			CiudadDTO repart = (CiudadDTO) listaReparticiones.get(r);
    			int reparticion = repart.getCodigo();
    			
    			String sexo = (iSex==0)? "M" : "F";
    			String llave = reparticion + "." + impCarga + "." + sexo;
    			String llave2= reparticion + "." + impCarga + "." + sexo + ".valor";
    			String valor = "0";
    			String valor2= "0";
    			if (fila.containsKey(llave)){ valor = fila.get(llave).toString(); }
    			if (fila.containsKey(llave2)){ valor2 = fila.get(llave2).toString(); }    			
    			int intValor = (new Integer(valor)).intValue();
    			int intValor2 = (new Integer(valor2)).intValue();
    			granTotal += intValor;
    			granValor += intValor2;
    		}
    	}
    }

  }

  
  
%>
<%
  if (salidaExcel) {
%>
<html><body>
<% } else { %>

<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Reporte Estadistico de Bonos</h1>

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
				<select name="reparticiones" size="4" multiple="true">
				<!-- reps:<%= lasReparticiones %> -->
				<%
				
				for (int i=0; i<listaReparticiones.size(); i++){
					String selected = "";
					CiudadDTO rep = (CiudadDTO)listaReparticiones.get(i);
					int iRep = rep.getCodigo();
					if (lasReparticiones.indexOf(iRep +",") > -1){ selected = "selected"; }
				%>
					<option value="<%= iRep %>" <%=selected %>><%= rep.getNombre() %></option>
				<% } %>				
				</select>
			</td>

			<!-- rowspan tantas filas como tenga el filtro -->
			<td rowspan="11" style="text-align:center; vertical-align:middle">
				<input type="button" value="Buscar datos" class="submit" 
				onClick="document.formulario.accion.value='listado';agregar(300);document.formulario.submit()"
				style="width:120px">
				<br>
				<br>
				<input type="reset" class="button" value="Restaurar Valores" 
				title="Volver a los valores anteriores de los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<input type="button" class="button" value="Nueva B&uacute;squeda" onclick="document.location='/bmweb/Reportes'" 
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
			<td>RUT del prestador</td>
			<td style="text-align:left">
			<%
			String estiloDivRutPrestador = "display:none";
			if ("si".equals(opPrestador)) estiloDivRutPrestador = "";
			%>
				<select name="opPrestador" onChange="mostrarRutPrestador()">
				<option value="no" <%= ("no".equals(opPrestador))?"selected":"" %>>No filtrar</option>
				<option value="si" <%= ("si".equals(opPrestador))?"selected":"" %>>Filtrar por este RUT</option>
				</select>
				
				<br>

				<!-- filtro rut prestador -->
				<span id="div-opPrestador" style="<%= estiloDivRutPrestador %>">
				<input type="text" name="prestadorN" size="8" maxsize="8" value="<%= prestadorN %>" 
				onBlur="validarPrestadorN()">
				-				
				<input type="text" name="prestadorDV" size="1" maxsize="1" value="<%= prestadorDV %>" 
				 onBlur="validarPrestadorDV()">				
				<input type="hidden" name="prestador" size="12" value="<%= prestador %>">				
				</span>

			</td>
		</tr>

		<tr class="fila-detalle-par">
			<td>Ciudad / Jurisdiccion / Region / Agencia</td>
			<td style="text-align:left">
			
				<select name="CJRA" onChange="mostrarCJRA()">
				<option value="">No filtrar</option>
				<option value="C" <%= "C".equals(CJRA)?"selected":"" %>>Filtrar por Ciudad</option>
				<option value="J" <%= "J".equals(CJRA)?"selected":"" %>>Filtrar por Jurisdiccion</option>
				<option value="R" <%= "R".equals(CJRA)?"selected":"" %>>Filtrar por Region</option>
				<option value="A" <%= "A".equals(CJRA)?"selected":"" %>>Filtrar por Agencia</option>
				</select>
			</td>
		</tr>


		<tr id="fila-C" class="fila-detalle-impar" style="<%= "C".equals(CJRA)?"":"display:none" %>">
			<td>Ciudad</td>
			<td style="text-align:left">
				<select name="dom_ciudad">
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


		<tr id="fila-J" class="fila-detalle-impar" style="<%= "J".equals(CJRA)?"":"display:none" %>">
			<td>Jurisdiccion</td>
			<td style="text-align:left">
				<select name="dom_jurisdiccion">
			<%
				for (int i=0; i<listaJurisdicciones.size(); i++){
				CiudadDTO c = (CiudadDTO) listaJurisdicciones.get(i);
				String nombreJurisdiccion = c.getNombre();
				String codigoJurisdiccion = "" + c.getCodigo();
				
				String selected = "";
				if (domJurisdiccion.equals(codigoJurisdiccion)){ selected = "selected"; }
				
			%>
				<option value="<%= codigoJurisdiccion  %>" <%= selected  %>><%= nombreJurisdiccion %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>

		<tr id="fila-R" class="fila-detalle-impar" style="<%= "R".equals(CJRA)?"":"display:none" %>">
			<td>Region</td>
			<td style="text-align:left">
				<select name="dom_region">
			<%
				for (int i=0; i<listaRegiones.size(); i++){
				CiudadDTO c = (CiudadDTO) listaRegiones.get(i);
				String nombreRegion = c.getNombre();
				String codigoRegion = "" + c.getCodigo();
				
				String selected = "";
				if (domRegion.equals(codigoRegion)){ selected = "selected"; }
				
			%>
				<option value="<%= codigoRegion  %>" <%= selected  %>><%= nombreRegion %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>

		<tr id="fila-A" class="fila-detalle-impar" style="<%= "A".equals(CJRA)?"":"display:none" %>">
			<td>Agencia</td>
			<td style="text-align:left">
				<select name="dom_agencia">
			<%
				for (int i=0; i<listaAgencias.size(); i++){
				CiudadDTO c = (CiudadDTO) listaAgencias.get(i);
				String nombreAgencia = c.getNombre();
				String codigoAgencia = "" + c.getCodigo();
				
				String selected = "";
				if (domAgencia.equals(codigoAgencia)){ selected = "selected"; }
				
			%>
				<option value="<%= codigoAgencia  %>" <%= selected  %>><%= nombreAgencia %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>

		<tr class="fila-detalle-par">
			<td>Codigo de Prestacion</td>
			<td style="text-align:left">
			<%
			String estiloDivPrestacion = "display:none";
			if ("si".equals(opPrestacion)) estiloDivPrestacion = "";
			%>
				<select name="opPrestacion" onChange="mostrarPrestacion()">
				<option value="no" <%= "no".equals(opPrestacion)?"selected":"" %>>No filtrar</option>
				<option value="si" <%= "si".equals(opPrestacion)?"selected":"" %>>Filtrar por este codigo de prestacion</option>
				</select>
				
				<br>

				<!-- filtro rut prestador -->
				<span id="div-opPrestacion" style="<%= estiloDivPrestacion %>">
				<input type="text" name="prestacion" size="12" value="<%= prestacion %>" onBlur="if(!CampoEsNumeroEnRango(this, 101001, 9999999)){document.formulario.opPrestacion.selectedIndex=0;mostrarPrestacion();}">				
				</span>
			</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td>Estado del Bono</td>
			<td style="text-align:left">
				<select name="estadoBono">
				<option value="">No filtrar por Estado</option>
				<option value="A" <%= ("A".equals(estadoBono))?"selected":"" %>>Bonos Anulados</option>
				<option value="P" <%= ("P".equals(estadoBono))?"selected":"" %>>Bonos Liquidados (preseleccionado)</option>
				<option value="E" <%= ("P".equals(estadoBono))?"selected":"" %>>Bonos Emitidos</option>				
				</select>		

			</td>		
		</tr>

		<tr class="fila-detalle-par">
			<td>Bonos a Seleccionar</td>
			<td style="text-align:left">
				<select name="bonoSeleccion">
				<option value="">No filtrar</option>
				<option value="W" <%= ("W".equals(bonoSeleccion))?"selected":"" %>>Bonos Web (prestablecida)</option>
				<option value="T" <%= ("T".equals(bonoSeleccion))?"selected":"" %>>Todos</option>
				</select>		

			</td>		
		</tr>
		
		
	</table>
	
	<input type="hidden" name="mostrarTablas" value="mostrarTablas">
	</form>

	<br>
	
<%
  // fin if (!salidaExcel)
  }
%>

<%
  if (mostrarTablas) {
%>
    <% if ("entre".equals(opfecha)){ %><h1>Estadistica Cuantitativa Fecha entre <%= fechaDesde %> y <%= fechaHasta %></h1><br><% }
       else { %><h1>Estadistica Cuantitativa Fecha entre <%= sdf.format(new Date()) %> y <%=sdf.format(new Date()) %></h1><br><% } %>

	<table id="listado" <% if (salidaExcel){ %>border="1"<% } %>>

<% if (salidaExcel){ %>

    <tr><td colspan="32"><b>Reporte estadistico de Bonos</b> (generado el <%= sdfReporte.format(new Date()) %>)</td></tr>
    <tr><td colspan="32">Filtros Utilizados:<br>
    
    <!-- Reparticiones -->
    <%
	String nombresReparticionesSeleccionadas = "";
    int numRepsSeleccionadas = 0;
	for (int i=0; i<listaReparticiones.size(); i++){
		CiudadDTO rep = (CiudadDTO)listaReparticiones.get(i);
		int iRep = rep.getCodigo();
		if (lasReparticiones.indexOf(iRep +",") > -1){ 
			numRepsSeleccionadas++;
			nombresReparticionesSeleccionadas = nombresReparticionesSeleccionadas + rep.getNombre() + ", ";
		}
	}
	
	if (numRepsSeleccionadas > 0) {
	%>
		Reparticiones: <%= nombresReparticionesSeleccionadas %><br>
	<% } %>				
    
    <!-- Rango de Fechas -->
    <% if ("entre".equals(opfecha)){ %>Fecha entre <%= fechaDesde %> y <%= fechaHasta %><br><% }
       else { %>Fecha entre <%= sdf.format(new Date()) %> y <%=sdf.format(new Date()) %><br><% } %>
    <!-- Ciudad -->
    <% if ("C".equals(CJRA)){ String nombreCiudad = (String) mapaCiudades.get(new Integer(domCiudad)); %>Ciudad: <%= nombreCiudad %><br><% } %>

    <!-- Jurisdiccion -->
    <% if ("J".equals(CJRA)){ String nombreJurisdiccion = (String) mapaJurisdicciones.get(new Integer(domJurisdiccion)); %>Jurisdiccion: <%= nombreJurisdiccion %><br><% } %>
   
    <!-- Region -->
    <% if ("R".equals(CJRA)){ String nombreRegion = (String) mapaRegiones.get(new Integer(domRegion)); %>Region: <%= nombreRegion %><br><% } %>
   
    <!-- Agencia -->
    <% if ("A".equals(CJRA)){ String nombreAgencia = (String) mapaAgencias.get(new Integer(domAgencia)); %>Agencia: <%= nombreAgencia %><br><% } %>
   
    <!-- RUT del prestador -->
    <% if ("si".equals(opPrestador)){ %>Rut del Prestador: <%= prestador %><br><%  }%>
        
    <!-- Codigo de Prestacion -->
    <% if ("si".equals(request.getParameter("opPrestacion"))){ %>Codigo de Prestacion: <%= request.getParameter("prestacion") %><br><% } %>
    
    <!-- Estado del Bono -->
    <% if (BonoDTO.ESTADOBONO_ANULADO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Anulado<br><% } %>
    <% if (BonoDTO.ESTADOBONO_LIQUIDADO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Liquidado<br><% } %>
    <% if (BonoDTO.ESTADOBONO_EMITIDO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Emitido<br><% } %>    
    
    </td></tr>

<% } %>



		<tr class="encabezados-tabla">
			<td rowspan="3"><br>Especialidades</td>
			<td rowspan="2" colspan="2">Total General</td>

			<td colspan="4">Total por sexo</td>
			
			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<td colspan="4"><%= rep.getNombre() %></td>			
			<% } %>
		</tr>
		
		<tr class="encabezados-tabla">
			<!-- total -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<!-- <%= rep.getNombre() %> -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>
			<% } %>

		</tr>

		<tr class="encabezados-tabla">
			<td>Total</td><td>%</td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			

			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<!-- <%= rep.getNombre() %> -->
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<% } %>
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

		    for (int r=0; r<listaReparticiones.size(); r++){
		    	for (int impCarga = 0; impCarga <=1; impCarga++) {
		    		for (int iSex = 0; iSex <= 1; iSex++){
		    			
		    			CiudadDTO rep = (CiudadDTO) listaReparticiones.get(r);
		    			int reparticion = rep.getCodigo();
		    			
		    			String sexo = (iSex==0)? "M" : "F";
		    			String llave = reparticion + "." + impCarga + "." + sexo;
		    			
		    			String valor = "0";
		    			if (fila.containsKey(llave)){ valor = fila.get(llave).toString(); }		    			
		    			int intValor = (new Integer(valor)).intValue();		    			
		    			totalEspecialidad += intValor;
		    			
		    			Integer valorTotales = new Integer(0);
		    			if (filaTotales.containsKey(llave)){ valorTotales = (Integer)filaTotales.get(llave); }
		    			filaTotales.put(llave, new Integer(valorTotales.intValue() + intValor));
		    			
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
		    
			filaTotales.put("totalImponentesMasc", new Integer( ((Integer)filaTotales.get("totalImponentesMasc")).intValue() + totalImponentesMasc) );
			filaTotales.put("totalImponentesFem", new Integer( ((Integer)filaTotales.get("totalImponentesFem")).intValue() + totalImponentesFem) );
			filaTotales.put("totalCargasMasc", new Integer( ((Integer)filaTotales.get("totalCargasMasc")).intValue() + totalCargasMasc) );
			filaTotales.put("totalCargasFem", new Integer( ((Integer)filaTotales.get("totalCargasFem")).intValue() + totalCargasFem) );
			filaTotales.put("total", new Integer( ((Integer)filaTotales.get("total")).intValue() + totalEspecialidad) );
		    
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
		<!-- FILA TOTALES -->
		<tr class="encabezados-tabla">
		<td>TOTAL</td>
		<td><%= filaTotales.get("total") %></td>
		<td>100%</td>
		<td><%= filaTotales.get("totalImponentesMasc") %></td>
		<td><%= filaTotales.get("totalImponentesFem") %></td>
		<td><%= filaTotales.get("totalCargasMasc") %></td>
		<td><%= filaTotales.get("totalCargasFem") %></td>

<%	
	    for (int r=0; r<listaReparticiones.size(); r++){
	    	for (int impCarga = 0; impCarga <=1; impCarga++) {
	    		for (int iSex = 0; iSex <= 1; iSex++){
	    			
	    			CiudadDTO rep = (CiudadDTO) listaReparticiones.get(r);
	    			int reparticion = rep.getCodigo();	    			
	    			String sexo = (iSex==0)? "M" : "F";
	    			String llave = reparticion + "." + impCarga + "." + sexo;
	    			
	    			String valor = "0";
	    			if (filaTotales.containsKey(llave)){ valor = filaTotales.get(llave).toString(); }
%>
		<td><%= valor %></td>
<%
	    		}
	    	}
	    }
%>
	</table>
	
    <!-- Estadistica Valorativa -->
<% if ("entre".equals(opfecha)){ %><h1>Estadistica Valorativa Fecha entre <%= fechaDesde %> y <%= fechaHasta %></h1><br><% }
       else { %><h1>Estadistica Cuantitativa Fecha entre <%= sdf.format(new Date()) %> y <%=sdf.format(new Date()) %></h1><br><% } %>
   
	<table id="listado" <% if (salidaExcel){ %>border="1"<% } %>>

<% if (salidaExcel){ %>

    <tr><td colspan="32"><b>Reporte estadistico de Bonos Web</b> (generado en <%= sdfReporte.format(new Date()) %>)</td></tr>
    <tr><td colspan="32">Filtros Utilizados:<br>
    
    <!-- Reparticiones -->
    <%
	String nombresReparticionesSeleccionadas = "";
    int numRepsSeleccionadas = 0;
	for (int i=0; i<listaReparticiones.size(); i++){
		CiudadDTO rep = (CiudadDTO)listaReparticiones.get(i);
		int iRep = rep.getCodigo();
		if (lasReparticiones.indexOf(iRep +",") > -1){ 
			numRepsSeleccionadas++;
			nombresReparticionesSeleccionadas = nombresReparticionesSeleccionadas + rep.getNombre() + ", ";
		}
	}
	
	if (numRepsSeleccionadas > 0) {
	%>
		Reparticiones: <%= nombresReparticionesSeleccionadas %><br>
	<% } %>				
    
    <!-- Rango de Fechas -->
    <% if ("entre".equals(opfecha)){ %>Fecha entre <%= fechaDesde %> y <%= fechaHasta %><br><% }
       else { %>Fecha entre <%= sdf.format(new Date()) %> y <%=sdf.format(new Date()) %><br><% } %>
    <!-- Ciudad -->
    <% if ("C".equals(CJRA)){ String nombreCiudad = (String) mapaCiudades.get(new Integer(domCiudad)); %>Ciudad: <%= nombreCiudad %><br><% } %>

    <!-- Jurisdiccion -->
    <% if ("J".equals(CJRA)){ String nombreJurisdiccion = (String) mapaJurisdicciones.get(new Integer(domJurisdiccion)); %>Jurisdiccion: <%= nombreJurisdiccion %><br><% } %>
   
    <!-- Region -->
    <% if ("R".equals(CJRA)){ String nombreRegion = (String) mapaRegiones.get(new Integer(domRegion)); %>Region: <%= nombreRegion %><br><% } %>
   
    <!-- Agencia -->
    <% if ("A".equals(CJRA)){ String nombreAgencia = (String) mapaAgencias.get(new Integer(domAgencia)); %>Agencia: <%= nombreAgencia %><br><% } %>
   
    <!-- RUT del prestador -->
    <% if ("si".equals(opPrestador)){ %>Rut del Prestador: <%= prestador %><br><%  }%>
        
    <!-- Codigo de Prestacion -->
    <% if ("si".equals(request.getParameter("opPrestacion"))){ %>Codigo de Prestacion: <%= request.getParameter("prestacion") %><br><% } %>
    
    <!-- Estado del Bono -->
    <% if (BonoDTO.ESTADOBONO_ANULADO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Anulado<br><% } %>
    <% if (BonoDTO.ESTADOBONO_LIQUIDADO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Liquidado<br><% } %>
    <% if (BonoDTO.ESTADOBONO_EMITIDO.equals(request.getParameter("estadoBono"))) { %>Estado de bono: Emitido<br><% } %>    
    
    </td></tr>

<% } %>



		<tr class="encabezados-tabla">
			<td rowspan="3"><br>Especialidades</td>
			<td rowspan="2" colspan="2">Total General</td>

			<td colspan="4">Total por sexo</td>
			
			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<td colspan="4"><%= rep.getNombre() %></td>			
			<% } %>
		</tr>
		
		<tr class="encabezados-tabla">
			<!-- total -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>

			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<!-- <%= rep.getNombre() %> -->
			<td colspan="2"><small>Imponentes</small></td>
			<td colspan="2"><small>Cargas</small></td>
			<% } %>

		</tr>

		<tr class="encabezados-tabla">
			<td>Total</td><td>%</td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			

			<% for (int i=0; i<listaReparticiones.size(); i++){
				CiudadDTO rep = (CiudadDTO) listaReparticiones.get(i);
			%>
			<!-- <%= rep.getNombre() %> -->
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<td><small>Masc.</small></td><td><small>Fem.</small></td>
			<% } %>
		</tr>

<%
		int numFila2 = 1;
		for (Iterator it = filasReporte.iterator(); it.hasNext();) {
		    Map fila = (Map) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila2 %2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila2++;
		    
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

		    for (int r=0; r<listaReparticiones.size(); r++){
		    	for (int impCarga = 0; impCarga <=1; impCarga++) {
		    		for (int iSex = 0; iSex <= 1; iSex++){
		    			
		    			CiudadDTO rep = (CiudadDTO) listaReparticiones.get(r);
		    			int reparticion = rep.getCodigo();
		    			
		    			String sexo = (iSex==0)? "M" : "F";
		    			String llave2 = reparticion + "." + impCarga + "." + sexo + ".valor";
		    			
		    			String valor = "0";
		    			if (fila.containsKey(llave2)){ valor = fila.get(llave2).toString(); }
		    			
		    			int intValor = (new Integer(valor)).intValue();
		    			
		    			totalEspecialidad += intValor;
		    			
		    			Integer valorTotales = new Integer(0);
		    			if (filaTotales.containsKey(llave2)){ valorTotales = (Integer)filaValoresTotales.get(llave2); }
		    			filaValoresTotales.put(llave2, new Integer(valorTotales.intValue() + intValor));

		    			
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
		    
		    filaValoresTotales.put("totalImponentesMasc", new Integer( ((Integer)filaValoresTotales.get("totalImponentesMasc")).intValue() + totalImponentesMasc) );
		    filaValoresTotales.put("totalImponentesFem", new Integer( ((Integer)filaValoresTotales.get("totalImponentesFem")).intValue() + totalImponentesFem) );
		    filaValoresTotales.put("totalCargasMasc", new Integer( ((Integer)filaValoresTotales.get("totalCargasMasc")).intValue() + totalCargasMasc) );
		    filaValoresTotales.put("totalCargasFem", new Integer( ((Integer)filaValoresTotales.get("totalCargasFem")).intValue() + totalCargasFem) );
		    filaValoresTotales.put("total", new Integer( ((Integer)filaValoresTotales.get("total")).intValue() + totalEspecialidad) );

		    
			double porcentaje = (int)((totalEspecialidad*1000.0)/granValor);
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

		<!-- FILA TOTALES -->
		<tr class="encabezados-tabla">
		<td>TOTAL</td>
		
		<td><%= filaValoresTotales.get("total") %></td>
		<td>100%</td>
		<td><%= filaValoresTotales.get("totalImponentesMasc") %></td>
		<td><%= filaValoresTotales.get("totalImponentesFem") %></td>
		<td><%= filaValoresTotales.get("totalCargasMasc") %></td>
		<td><%= filaValoresTotales.get("totalCargasFem") %></td>

<%	
	    for (int r=0; r<listaReparticiones.size(); r++){
	    	for (int impCarga = 0; impCarga <=1; impCarga++) {
	    		for (int iSex = 0; iSex <= 1; iSex++){
	    			
	    			CiudadDTO rep = (CiudadDTO) listaReparticiones.get(r);
	    			int reparticion = rep.getCodigo();	    			
	    			String sexo = (iSex==0)? "M" : "F";
	    			String llave = reparticion + "." + impCarga + "." + sexo + ".valor";
	    			
	    			String valor = "0";
	    			if (filaValoresTotales.containsKey(llave)){ valor = filaValoresTotales.get(llave).toString(); }
%>
		<td><%= valor %></td>
<%
	    		}
	    	}
	    }
%>

	</table>

<%
  } // end if mostrarTablas
%>

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
	  
	  function mostrarPrestacion(){
	    if (document.formulario.opPrestacion.selectedIndex > 0){
	  
		    if (document.getElementById("div-opPrestacion")){
		    	document.getElementById("div-opPrestacion").style.display = "";
		    }
	    } else {
		    if (document.getElementById("div-opPrestacion")){
		    	document.getElementById("div-opPrestacion").style.display = "none";
		    }
	    }
	  }
	  
	  
	  function mostrarCJRA(){
		    document.getElementById("fila-C").style.display = "none";
		    document.getElementById("fila-J").style.display = "none";
		    document.getElementById("fila-R").style.display = "none";
		    document.getElementById("fila-A").style.display = "none";
		    
		    var filaVisible = "fila-" + document.formulario.CJRA.options[document.formulario.CJRA.selectedIndex].value;
		    
		    if (document.getElementById(filaVisible)){
		      document.getElementById(filaVisible).style.display = "";
		    }
		    
	  }
	  
	  function validarPrestadorN(){
	  		document.formulario.prestadorDV.value="";
	  		if(!CampoEsNumero(document.formulario.prestadorN)){
	  			// document.formulario.opPrestador.selectedIndex=0;
	  			document.formulario.prestadorN.value = "";
	  			// mostrarRutPrestador();
	  		}
	  }
	  
	  function validarPrestadorDV(){
	  		if ( !("" == document.formulario.prestadorN.value) 
	  			&& CampoEsNumero(document.formulario.prestadorN)){
	  		    if ("k" == document.formulario.prestadorDV.value
	  		    	|| "K" ==  document.formulario.prestadorDV.value
	  		    	|| CampoEsNumeroEnRango(document.formulario.prestadorDV, 0, 9)
	  		    ) {
	  		      // copio ambos campos en el rut completo (hidden)
	  		      document.formulario.prestador.value = 
	  		      	document.formulario.prestadorN.value + 
	  		      	"-" + 
	  		      	document.formulario.prestadorDV.value.toUpperCase();
	  		      	
	  		      // valido el rut
	  		      if (!CampoEsRut(document.formulario.prestador)){
	  		        document.formulario.prestadorN.value = "";
	  		        document.formulario.prestadorDV.value = "";
	  		      }
	  		    }
	  		}
	  }
	  
	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  // if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>
	
	<!-- boton salida a excel -->
	<% 
	String exportarDeshabilitado = "";
	if (filasReporte.size() == 0){ exportarDeshabilitado = "disabled=\"disabled\""; }
	%>
		<input type="button" onClick="document.formulario.salida.value='excel';document.formulario.submit();document.formulario.salida.value='';"
		class="submit" value="Exportar como archivo Excel" <%= exportarDeshabilitado %>>

	<p>
	<a href="http://www.primopdf.com"><img src="img/primopdf.gif" target="blank" border="0"></a><br>
	<small>Para imprimir archivos en formato PDF, use el software gratuito PrimoPDF</small> 
	
<!-- 
especialidad                    reparticion  imp_carga  sexo  subtotal  
------------------------------  -----------  ---------  ----  --------  
CONS.ESPEC.TRAUMATOLOGIA        2            02         M     1         
 -->
	
<jsp:include page="pie.jsp" flush="true"/>

<%
// fin if (!salidaExcel)
} else {
%>
</body></html>
<% } %>
