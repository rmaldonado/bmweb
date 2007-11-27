<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  HashMap mapaTiposBono = new HashMap();
  mapaTiposBono.put( BonoDTO.TIPOBONO_ABIERTO,		"Bono Abierto");
  mapaTiposBono.put( BonoDTO.TIPOBONO_DIGITADO,		"Bono Digitado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_FACTURADO,	"Bono Facturado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_SINDETALLE,	"Bono Sin Detalle");
  mapaTiposBono.put( BonoDTO.TIPOBONO_VALORADO,		"Bono Valorado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_WEB,			"Bono Internet");

  HashMap mapaTiposPrestacion = new HashMap();
  mapaTiposPrestacion.put( "1",	"Ambulatoria");
  mapaTiposPrestacion.put( "2",	"Hospitalaria");
  mapaTiposPrestacion.put( "7",	"Cirugia Ambulatoria");
  
  HashMap mapaCobroPabellon = new HashMap();
  mapaCobroPabellon.put( "0",	"S&oacute;lo el pabell&oacute;n");
  mapaCobroPabellon.put( "1",	"S&oacute;lo la prestaci&oacute;n");
  mapaCobroPabellon.put( "2",	"Prestaci&oacute;n y pabell&oacute;n");

  HashMap mapaDiaCama = new HashMap();
  mapaDiaCama.put("0",	"Sala com&uacute;n");
  mapaDiaCama.put("1",	"Pensionado");

  HashMap mapaCobro = new HashMap();
  mapaCobro.put("1", "1er. Cirujano");
  mapaCobro.put("2", "2do. Cirujano");
  mapaCobro.put("3", "3er. Cirujano");
  mapaCobro.put("4", "Arsenalero");
  mapaCobro.put("5", "Anestesista");
  mapaCobro.put("6", "Todos");


  BonoDTO bono = new BonoDTO();
  if (request.getAttribute("bono") != null){
  	bono = (BonoDTO) request.getAttribute("bono");
  }
  
  //ArrayList bonoItems = new ArrayList( (List)request.getAttribute("bonoItems") );

  List listaBonos = new ArrayList();
  HashMap mapaCiudades = new HashMap();

  if (request.getAttribute("listaBonos") != null){
  	listaBonos = (List) request.getAttribute("listaBonos");
  }

  if (request.getAttribute("ciudades") != null){
  	mapaCiudades = (HashMap) request.getAttribute("ciudades");
  }
  
  List listaCiudades = new ArrayList();
  if (request.getAttribute("listaCiudades") != null){
  	listaCiudades = (List) request.getAttribute("listaCiudades");
  }

  List prestacionesGenericas = new ArrayList();
  if (request.getAttribute("prestacionesGenericas") != null){
  	prestacionesGenericas = (List) request.getAttribute("prestacionesGenericas");
  }

  String ciudad = "Ciudad no encontrada";
  if (mapaCiudades.containsKey( bono.getIdCiudad() )){
    ciudad = (String) mapaCiudades.get( bono.getIdCiudad() );
  }

  // coloco el titulo de la página
  request.setAttribute("titulo", "Detalle de Bono");
  
  PrestadorDTO prestador = new PrestadorDTO();
  if (request.getAttribute("prestador") != null){
  	prestador = (PrestadorDTO) request.getAttribute("prestador");
  }

  RolbeneDTO rolbene = new RolbeneDTO();
  if (request.getAttribute("rolbene") != null){
  	rolbene = (RolbeneDTO) request.getAttribute("rolbene");
  }
  
  BeneficiarioDTO beneficiario = new BeneficiarioDTO();
  if (request.getAttribute("beneficiario") != null){
  	beneficiario = (BeneficiarioDTO) request.getAttribute("beneficiario");
  }
  
  BeneficiarioDTO beneficiario2 = new BeneficiarioDTO();
  if (request.getAttribute("beneficiario2") != null){
  	beneficiario2 = (BeneficiarioDTO) request.getAttribute("beneficiario2");
  }
  
  String folio = (String) request.getParameter("folio");
  // Parche para la ver el detalle desde la creacion del bono
  if (request.getAttribute("folio") != null) {
    folio = (String) request.getAttribute("folio");
  }
  
  String rutImponente = beneficiario2.getBene() + "-" + beneficiario2.getDgv() + "(" + beneficiario2.getNombre()+ " " + beneficiario2.getPat() + " " + beneficiario2.getMat() + ")"; 
  if (beneficiario2.getBene() == 0){
  	rutImponente = "Hay problemas con el registro del Beneficiario. Contacte al administrador.";
  }

  // La lista de prestaciones que se van a agregar al bono está en la sesion
  List items = new ArrayList();
  if (request.getSession().getAttribute("items") != null) {
    items = (List) request.getSession().getAttribute("items");
  }

  List prestacionesEncontradas = null;
  if (request.getAttribute("prestacionesEncontradas") != null) {
    prestacionesEncontradas = (List) request.getAttribute("prestacionesEncontradas");
  }

  UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  
  boolean hayPrestacionesHospitalarias = false;
  for (int i=0; items != null && i<items.size(); i++){
    BonoItemDTO item = (BonoItemDTO) items.get(i);
    if ("2".equals(item.getCodTipoPrestacion().toString())){ hayPrestacionesHospitalarias = true; }
    
  }
  
  // Prestaciones que tienen pabellon
  Map mapaCirugias = new HashMap();
  if (request.getAttribute("mapaCirugias") != null) {
    mapaCirugias = (Map) request.getAttribute("mapaCirugias");
  }
  
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Procesar archivo masivo de detalle de bonos</h1>

<% if (items == null || items.size() == 0) { %>
	<table id="listado">
	<tr class="fila-detalle-impar"><td>No se han agregado prestaciones</td></tr>
	</table>
<% } else { %>
	<table id="listado">

		<tr class="encabezados-tabla">
			<td style="font-size:x-small">Folio<br>Bono</td>

			<td style="font-size:x-small">C&oacute;digo</td>
			<td style="font-size:x-small">Nombre</td>

			<td style="font-size:x-small">Tipo<br>Prestaci&oacute;n</td>
			<td style="font-size:x-small">Fecha<br>Atenci&oacute;n</td>
			<td style="font-size:x-small">Cantidad</td>

			<% if (hayPrestacionesHospitalarias) { %>
			<!-- <td style="font-size:x-small">Pabell&oacute;n</td> -->
			<td style="font-size:x-small">Cobro<br>Pabell&oacute;n</td>
			<td style="font-size:x-small">D&iacute;a<br>Cama</td>
			<td style="font-size:x-small">Cobro</td>
			<% } %>

			<td style="font-size:x-small">Aporte<br>Dipreca</td>
			<td style="font-size:x-small">Aporte<br>Seguro</td>
			<td style="font-size:x-small">Valor<br>Copago</td>
			<td></td>
		</tr>
<%
			for (int i=0; i<items.size(); i++) {
			   BonoItemDTO item = (BonoItemDTO) items.get(i);
			    // Determino un string que se alterna para cambiar la grafica de las filas
			    String clase = (i%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
			    
			    String tipoPrestacion = "";
			    try { tipoPrestacion = (String) mapaTiposPrestacion.get( item.getCodTipoPrestacion().toString() ); }
			    catch (Exception e) { }
			    
			    String fechaAtencion = "";
			    try { fechaAtencion = sdf.format( item.getFechaEfectivaAtencionMedica()); }
			    catch (Exception e) { }
			    
			    String pabellon = "";
			    try { pabellon = item.getCodigoPabellon().toString(); } 
			    catch (Exception e) { }
			    
			    String cantidad = "";
			    try { cantidad = item.getCantidadAtenciones().toString(); } 
			    catch (Exception e) { }
			    
			    String cobroPabellon = "";
			    try { cobroPabellon = (String) mapaCobroPabellon.get( item.getIncluyePabellon().toString() ); }
			    catch (Exception e) { }
			    
			    String diaCama = "";
			    try { diaCama = (String) mapaDiaCama.get( item.getPensionadoOSalaComun().toString() ); }
			    catch (Exception e) { }

			    String cobro = "";
			    try { cobro = (String) mapaCobro.get( item.getCodProfesional().toString() ); }
			    catch (Exception e) { }

				if ("1".equals(item.getCodTipoPrestacion().toString())){
					cobroPabellon = "";
					diaCama = "";
					cobro = "";
				}
%>

		<tr class="<%=clase%>">
			<td style="text-align:left;font-size:x-small"><%= item.getBonoDTO().getFolio() %></td>
			<td style="text-align:left;font-size:x-small"><%= item.getCodPrestacion() %></td>
			<td style="text-align:left;font-size:x-small"><%= item.getNombrePrestacion() %></td>

			<td style="font-size:x-small"><%= tipoPrestacion %></td>
			<td style="font-size:x-small"><%= fechaAtencion %></td>
			<td style="font-size:x-small"><%= cantidad %></td>

			<% if (hayPrestacionesHospitalarias) { %>
			<!-- <td style="font-size:x-small"><%= pabellon %></td> -->
			<td style="font-size:x-small"><%= cobroPabellon %></td>
			<td style="font-size:x-small"><%= diaCama %></td>
			<td style="font-size:x-small"><%= cobro %></td>
			<% } %>

			<td style="text-align:right;font-size:x-small"><%= item.getValorAporteDipreca().intValue()+ item.getAporteDiprecaPabellon().intValue()%></td>
			<td style="text-align:right;font-size:x-small"><%= item.getValorAporteSeguro() %></td>
			<td style="text-align:right;font-size:x-small"><%= item.getValorCopago().intValue() %></td>
			<td style="text-align:left;font-size:x-small"><input type="button" value="eliminar" onClick="eliminar(<%= item.getBonoDTO().getFolio() %>, '<%= item.getNombrePrestacion().trim() %>', <%= item.getCodPrestacion() %>)"></td>
		</tr>

<%
		}
%>

	<!-- boton para grabar el detalle del bono -->
		<tr class="fila-detalle-impar">
			<td style="text-align:right" colspan="<%= hayPrestacionesHospitalarias?"13":"9" %>">
				<input type="button" value="Grabar Prestaciones" onClick="grabarListado()">
			</td>

	</table>
	
<%
	}
%>


<div>

	<h1>Procesar archivo masivo de detalle de bonos</h1>
	
	<table id="listado">

		<tr class="encabezados-tabla">
			<td style="text-align:left">Archivo de detalle de prestaciones</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>
				<form name="formularioArchivo" 
					method="post" 
					action="DetallarBonosMasivo"
					enctype="multipart/form-data">
				
				Use <a href="bono_items.xls">este archivo</a> de Microsoft Excel como 
				planilla para detallar el bono. Las instrucciones para el llenado de
				la planilla se encuentran en <a href="Como_ingresar_el_detalle_de_bonos_web.doc">este documento</a>.<br><br>
				
				Subir archivo CSV con detalle de prestaciones: 
				<input type="file" name="archivo" id="archivo">
				<input type="hidden" name="accion" value="procesarArchivo">
				<input type="button" value="Procesar archivo" onClick="procesarArchivo()">
				</form>	

				<form name="formularioGrabar" method="post" action="DetallarBonosMasivo">
				<input type="hidden" name="accion" value="guardar">
				</form>
				
				<script language="javascript">
				  function procesarArchivo(){
				    if ((document.getElementById('archivo').value) != "") {
				      document.formularioArchivo.submit();
				    } else {
				      alert("Debe seleccionar un archivo para procesar.");
				    }
				  }
				  
				  function grabarListado() {
				    if (confirm('Confirma que desea grabar el listado que ve en pantalla.')){
				    	document.formularioGrabar.submit();
				    }
				  }
				  
				</script>
				
			</td>
		</tr>
	</table>

	</div>


	<script language="javascript">
	
		function eliminar(folioBono, nombre, codigo ){
		
			if (confirm("Confirma que desea eliminar la prestacion '" + nombre + "' del bono")){
				document.formEliminar.codigo.value = codigo;
				document.formEliminar.folio.value = folioBono;
				document.formEliminar.submit();
			}
			
		}

	  // En esta pagina, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  //if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

	<form name="formEliminar" method="post" action="DetallarBonosMasivo#prestaciones">
	<input type="hidden" name="accion" value="eliminar">
	<input type="hidden" name="codigo">
	<input type="hidden" name="folio">
	</form>

<jsp:include page="pie.jsp" flush="true"/>

