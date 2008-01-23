<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
List listaValcon = new ArrayList();
if (request.getAttribute("resultado") != null ){
	listaValcon = (List) request.getAttribute("resultado");
}
%>
<html>
<head></head>
<body>
<table border="1">
  <tr>
    <td>ID CONVENIO</td>
    <td>CODIGO PRESTACION</td>
    <td>VALOR CONVENIDO</td>
    <td>VALOR LISTA</td>
    <td>ESTADO (1:Nuevo, 2:Modificado, 3:Eliminado)</td>
  </tr>
<%
  for (int i=0; i<listaValcon.size(); i++) {
	  ValconDTO valcon = (ValconDTO) listaValcon.get(i);
%>
  <tr>
    <td><%= valcon.getIdConvenio() %></td>
    <td><%= valcon.getCodigoPrestacion() %></td>
    <td><%= valcon.getValorCovenido() %></td>
    <td><%= valcon.getValorLista() %></td>
    <td><%= valcon.getEstado() %></td>
  </tr>
<%	  
  }
%>  
</table>
</body>
</html>