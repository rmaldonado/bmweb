package cl.e_sign.www;

public class WSIntercambiaDocSoapProxy implements cl.e_sign.www.WSIntercambiaDocSoap {
  private String _endpoint = null;
  private cl.e_sign.www.WSIntercambiaDocSoap wSIntercambiaDocSoap = null;
  
  public WSIntercambiaDocSoapProxy() {
    _initWSIntercambiaDocSoapProxy();
  }
  
  public WSIntercambiaDocSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSIntercambiaDocSoapProxy();
  }
  
  private void _initWSIntercambiaDocSoapProxy() {
    try {
      wSIntercambiaDocSoap = (new cl.e_sign.www.WSIntercambiaDocLocator()).getWSIntercambiaDocSoap();
      if (wSIntercambiaDocSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSIntercambiaDocSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSIntercambiaDocSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSIntercambiaDocSoap != null)
      ((javax.xml.rpc.Stub)wSIntercambiaDocSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public cl.e_sign.www.WSIntercambiaDocSoap getWSIntercambiaDocSoap() {
    if (wSIntercambiaDocSoap == null)
      _initWSIntercambiaDocSoapProxy();
    return wSIntercambiaDocSoap;
  }
  
  public cl.e_sign.www.EncabezadoResponse intercambiaDoc(cl.e_sign.www.EncabezadoRequest encabezado, cl.e_sign.www.DocumentoParametro parametro) throws java.rmi.RemoteException{
    if (wSIntercambiaDocSoap == null)
      _initWSIntercambiaDocSoapProxy();
    return wSIntercambiaDocSoap.intercambiaDoc(encabezado, parametro);
  }
  
  
}