// Cookie Functions from Netscape

function getCookieVal (offset) {
  var endstr = document.cookie.indexOf (";", offset);
  if (endstr == -1)
	endstr = document.cookie.length;
  return unescape(document.cookie.substring(offset, endstr));
}

function GetCookie (name) {
  var arg = name + "=";
  var alen = arg.length;
  var clen = document.cookie.length;
  var i = 0;
  while (i < clen) {
	var j = i + alen;
	if (document.cookie.substring(i, j) == arg)
	  return getCookieVal (j);
	i = document.cookie.indexOf(" ", i) + 1;
	if (i == 0) break; 
  }
  return null;
}

// Examples:
//      SetCookie ("myCookieName", "myCookieValue", null, "/");
//      SetCookie (myCookieVar, cookieValueVar, null, "/myPath", null, true);

function SetCookie (name, value) {
  var argv = SetCookie.arguments;
  var argc = SetCookie.arguments.length;
  var expires = (argc > 2) ? argv[2] : null;
  var path = (argc > 3) ? argv[3] : null;
  var domain = (argc > 4) ? argv[4] : null;
  var secure = (argc > 5) ? argv[5] : false;
  document.cookie = name + "=" + escape (value) +
	((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
	((path == null) ? "" : ("; path=" + path)) +
	((domain == null) ? "" : ("; domain=" + domain)) +
	((secure == true) ? "; secure" : "");
}

function DeleteCookie (name) {
  var exp = new Date();
  exp.setTime (exp.getTime() - 1);  // This cookie is history
  var cval = GetCookie (name);
  document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}

// Funciones de validacion

function CampoEsNumero(campo){
  if (isNaN(campo.value)){
    campo.focus();
    alert("El campo no es un numero valido");
  	return false;
  } else { 
    return true;
  }
}

function CampoEsNumeroEnRango(campo, minimo, maximo){
  if (!CampoEsNumero(campo)) return false;
  else {
    if (minimo <= campo.value && campo.value <= maximo){
      return true;
    } else {
      campo.focus();
      alert("El campo no es un numero en el rango valido");
      return false;
    }
  }
}

function CampoEsNoNulo(campo){

  if (campo.value == ''){
    campo.focus();
    alert("El campo no debe estar vacio");
    return false;
  } else {
    return true;
  }
}