/**
 * WSIntercambiaDocLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cl.e_sign.www;

public class WSIntercambiaDocLocator extends org.apache.axis.client.Service implements cl.e_sign.www.WSIntercambiaDoc {

    public WSIntercambiaDocLocator() {
    }


    public WSIntercambiaDocLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSIntercambiaDocLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSIntercambiaDocSoap12
    private java.lang.String WSIntercambiaDocSoap12_address = "http://200.111.181.86:8080/WsvI/WSIntercambiaDoc.asmx";

    public java.lang.String getWSIntercambiaDocSoap12Address() {
        return WSIntercambiaDocSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSIntercambiaDocSoap12WSDDServiceName = "WSIntercambiaDocSoap12";

    public java.lang.String getWSIntercambiaDocSoap12WSDDServiceName() {
        return WSIntercambiaDocSoap12WSDDServiceName;
    }

    public void setWSIntercambiaDocSoap12WSDDServiceName(java.lang.String name) {
        WSIntercambiaDocSoap12WSDDServiceName = name;
    }

    public cl.e_sign.www.WSIntercambiaDocSoap getWSIntercambiaDocSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSIntercambiaDocSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSIntercambiaDocSoap12(endpoint);
    }

    public cl.e_sign.www.WSIntercambiaDocSoap getWSIntercambiaDocSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            cl.e_sign.www.WSIntercambiaDocSoap12Stub _stub = new cl.e_sign.www.WSIntercambiaDocSoap12Stub(portAddress, this);
            _stub.setPortName(getWSIntercambiaDocSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSIntercambiaDocSoap12EndpointAddress(java.lang.String address) {
        WSIntercambiaDocSoap12_address = address;
    }


    // Use to get a proxy class for WSIntercambiaDocSoap
    private java.lang.String WSIntercambiaDocSoap_address = "http://200.111.181.86:8080/WsvI/WSIntercambiaDoc.asmx";

    public java.lang.String getWSIntercambiaDocSoapAddress() {
        return WSIntercambiaDocSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSIntercambiaDocSoapWSDDServiceName = "WSIntercambiaDocSoap";

    public java.lang.String getWSIntercambiaDocSoapWSDDServiceName() {
        return WSIntercambiaDocSoapWSDDServiceName;
    }

    public void setWSIntercambiaDocSoapWSDDServiceName(java.lang.String name) {
        WSIntercambiaDocSoapWSDDServiceName = name;
    }

    public cl.e_sign.www.WSIntercambiaDocSoap getWSIntercambiaDocSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSIntercambiaDocSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSIntercambiaDocSoap(endpoint);
    }

    public cl.e_sign.www.WSIntercambiaDocSoap getWSIntercambiaDocSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            cl.e_sign.www.WSIntercambiaDocSoapStub _stub = new cl.e_sign.www.WSIntercambiaDocSoapStub(portAddress, this);
            _stub.setPortName(getWSIntercambiaDocSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSIntercambiaDocSoapEndpointAddress(java.lang.String address) {
        WSIntercambiaDocSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (cl.e_sign.www.WSIntercambiaDocSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                cl.e_sign.www.WSIntercambiaDocSoap12Stub _stub = new cl.e_sign.www.WSIntercambiaDocSoap12Stub(new java.net.URL(WSIntercambiaDocSoap12_address), this);
                _stub.setPortName(getWSIntercambiaDocSoap12WSDDServiceName());
                return _stub;
            }
            if (cl.e_sign.www.WSIntercambiaDocSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                cl.e_sign.www.WSIntercambiaDocSoapStub _stub = new cl.e_sign.www.WSIntercambiaDocSoapStub(new java.net.URL(WSIntercambiaDocSoap_address), this);
                _stub.setPortName(getWSIntercambiaDocSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WSIntercambiaDocSoap12".equals(inputPortName)) {
            return getWSIntercambiaDocSoap12();
        }
        else if ("WSIntercambiaDocSoap".equals(inputPortName)) {
            return getWSIntercambiaDocSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.e-sign.cl/", "WSIntercambiaDoc");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.e-sign.cl/", "WSIntercambiaDocSoap12"));
            ports.add(new javax.xml.namespace.QName("http://www.e-sign.cl/", "WSIntercambiaDocSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WSIntercambiaDocSoap12".equals(portName)) {
            setWSIntercambiaDocSoap12EndpointAddress(address);
        }
        else 
if ("WSIntercambiaDocSoap".equals(portName)) {
            setWSIntercambiaDocSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
