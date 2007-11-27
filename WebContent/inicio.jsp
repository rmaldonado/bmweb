<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<jsp:include page="cabecera.jsp" flush="true"/>


				<div>
								
						<h3>M&oacute;dulo web de Bonos Electr&oacute;nicos Dipreca</h3>
						<div>

							<ul>
<%

List paginas = new ArrayList();

// permiso, url, texto-link
paginas.add( new String[]{"/HABILITADOS", "Habilitados", "Administrador de Habilitados" } );
paginas.add( new String[]{"/BONOS", "Bonos?accion=crear", "Creaci&oacute;n de Bonos Abiertos" } );
paginas.add( new String[]{"/BONOS.LISTADO", "Bonos?accion=listado", "Listado de Bonos Abiertos" } );
paginas.add( new String[]{"/BONOVALORADO", "BonoValorado?accion=crear", "Creaci&oacute;n de Bonos Valorados" } );
paginas.add( new String[]{"/BONOVALORADO.LISTADO", "BonoValorado?accion=listado", "Listado de Bonos Valorados" } );
//paginas.add( new String[]{"/BONOPDF", "BonoValorado?accion=crear", "Creaci&oacute;n de Bonos Valorados" } );
paginas.add( new String[]{"/BONOS.VALIDARCODIGOBONO", "Bonos?accion=validarCodigoBono", "Validar Bonos" } );
//paginas.add( new String[]{"/BONOVALORADO.VALIDARCODIGOBONO", "BonoValorado?accion=validarCodigoBono", "Validar Bonos Valorados" } );
// carga masiva de detalle de bonos
paginas.add( new String[]{"/DETALLARBONOSMASIVO", "detalleBonosMasivo.jsp", "Carga de Detalle Masivo de Bonos" }  );
// facturas
paginas.add( new String[]{"/FACTURA", "Factura", "Administrador de Facturas" }  );



HttpSession sesion = request.getSession();
try {
  UsuarioWeb usuarioWeb = (UsuarioWeb) sesion.getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  
  // escribo los links para cada permiso que tenga el usuario
  
  for (int i=0; paginas != null && i<paginas.size(); i++){
    String texto[] = (String[]) paginas.get(i);
    
    if (usuarioWeb.tienePermiso(texto[0])){
%>
							<li><a href="<%= texto[1] %>"><%= texto[2] %></a></li>

<%
    } // fin if
    
  } // fin for
} catch (Exception ex){
}
%>
							</ul>
						</div>

				</div>
	
<jsp:include page="pie.jsp" flush="true"/>
