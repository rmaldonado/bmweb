<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  HashMap mapaTiposBono = new HashMap();
  mapaTiposBono.put( BonoDTO.TIPOBONO_ABIERTO,		"Bono Abierto");
  mapaTiposBono.put( BonoDTO.TIPOBONO_DIGITADO,		"Bono Digitado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_FACTURADO,	"Bono Facturado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_SINDETALLE,	"Bono Sin Detalle");
  mapaTiposBono.put( BonoDTO.TIPOBONO_VALORADO,		"Bono Valorado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_WEB,			"Bono Internet");

  BonoDTO bono = new BonoDTO();
  if (request.getAttribute("bono") != null){
  	bono = (BonoDTO) request.getAttribute("bono");
  }
  
  //ArrayList bonoItems = new ArrayList( (List)request.getAttribute("bonoItems") );

  /*
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
  
  if (folio == null){
    folio = bono.getFolio().toString();
  }
  
  String rutImponente = beneficiario2.getBene() + "-" + beneficiario2.getDgv() + "(" + beneficiario2.getNombre()+ " " + beneficiario2.getPat() + " " + beneficiario2.getMat() + ")"; 
  if (beneficiario2.getBene() == 0){
  	rutImponente = "Hay problemas con el registro del Beneficiario. Contacte al administrador.";
  }
  
  String [] nombreCiudad = (String []) request.getAttribute("nombreCiudad");
  String nombreEmisor = nombreCiudad[0];
  
  */

  UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
%>
<jsp:include page="cabecera.jsp" flush="true"/>

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
				
				Las instrucciones para el llenado de
				la planilla se encuentran en <a href="cargabw.pdf">este documento</a>.<br><br>
				 
				Subir archivo CSV con detalle de prestaciones: 
				<input type="file" name="archivo" id="archivo">
				<input type="hidden" name="accion" value="procesarArchivo">
				<input type="button" value="Procesar archivo" onClick="procesarArchivo()">
				</form>	
				
				<script language="javascript">
				  function procesarArchivo(){
				    if ((document.getElementById('archivo').value) != "") {
				      document.formularioArchivo.submit();
				    } else {
				      alert("Debe seleccionar un archivo para procesar.");
				    }
				  }
				</script>
				
			</td>
		</tr>
	</table>

<jsp:include page="pie.jsp" flush="true"/>

