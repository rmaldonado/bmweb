<%@ page import="java.util.*" %>

<script language="javascript">

window.top.document.formulario.idPrestador.options.length = 0;

// Habilito el boton de submit del nuevo bono
window.top.document.formulario.btnsubmit.disabled = false;

<%
ArrayList listaPrestadores = new ArrayList();

// TODO: Cambiar esta version por la definitiva

if (request.getAttribute("prestadores") != null){
  listaPrestadores = (ArrayList) request.getAttribute("prestadores");
}

for (int i=0; i<listaPrestadores.size(); i++){
  //PrebenDTO p = (PrebenDTO) listaPrestadores.get(i);
  Object[] prestador = (Object[]) listaPrestadores.get(i);
  String rutPrestador = (String) prestador[0];
  String nombrePrestador = (String) prestador[1];
  
%>
<%-- window.top.document.formulario.idPrestador.options[<%= i %>] = new Option('<%= p.getCodigo() %>','<%= p.getRut() %>'); --%>
window.top.document.formulario.idPrestador.options[<%= i %>] = new Option('<%= nombrePrestador %>','<%= rutPrestador %>');
<%
}
%>
</script>