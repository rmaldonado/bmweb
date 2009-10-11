/**
 * EncabezadoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cl.e_sign.www;

public class EncabezadoRequest  implements java.io.Serializable {
    private java.lang.String user;

    private java.lang.String password;

    private java.lang.String tipoIntercambio;

    private java.lang.String nombreConfiguracion;

    private java.lang.String formatoDocumento;

    public EncabezadoRequest() {
    }

    public EncabezadoRequest(
           java.lang.String user,
           java.lang.String password,
           java.lang.String tipoIntercambio,
           java.lang.String nombreConfiguracion,
           java.lang.String formatoDocumento) {
           this.user = user;
           this.password = password;
           this.tipoIntercambio = tipoIntercambio;
           this.nombreConfiguracion = nombreConfiguracion;
           this.formatoDocumento = formatoDocumento;
    }


    /**
     * Gets the user value for this EncabezadoRequest.
     * 
     * @return user
     */
    public java.lang.String getUser() {
        return user;
    }


    /**
     * Sets the user value for this EncabezadoRequest.
     * 
     * @param user
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }


    /**
     * Gets the password value for this EncabezadoRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this EncabezadoRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the tipoIntercambio value for this EncabezadoRequest.
     * 
     * @return tipoIntercambio
     */
    public java.lang.String getTipoIntercambio() {
        return tipoIntercambio;
    }


    /**
     * Sets the tipoIntercambio value for this EncabezadoRequest.
     * 
     * @param tipoIntercambio
     */
    public void setTipoIntercambio(java.lang.String tipoIntercambio) {
        this.tipoIntercambio = tipoIntercambio;
    }


    /**
     * Gets the nombreConfiguracion value for this EncabezadoRequest.
     * 
     * @return nombreConfiguracion
     */
    public java.lang.String getNombreConfiguracion() {
        return nombreConfiguracion;
    }


    /**
     * Sets the nombreConfiguracion value for this EncabezadoRequest.
     * 
     * @param nombreConfiguracion
     */
    public void setNombreConfiguracion(java.lang.String nombreConfiguracion) {
        this.nombreConfiguracion = nombreConfiguracion;
    }


    /**
     * Gets the formatoDocumento value for this EncabezadoRequest.
     * 
     * @return formatoDocumento
     */
    public java.lang.String getFormatoDocumento() {
        return formatoDocumento;
    }


    /**
     * Sets the formatoDocumento value for this EncabezadoRequest.
     * 
     * @param formatoDocumento
     */
    public void setFormatoDocumento(java.lang.String formatoDocumento) {
        this.formatoDocumento = formatoDocumento;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EncabezadoRequest)) return false;
        EncabezadoRequest other = (EncabezadoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.user==null && other.getUser()==null) || 
             (this.user!=null &&
              this.user.equals(other.getUser()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.tipoIntercambio==null && other.getTipoIntercambio()==null) || 
             (this.tipoIntercambio!=null &&
              this.tipoIntercambio.equals(other.getTipoIntercambio()))) &&
            ((this.nombreConfiguracion==null && other.getNombreConfiguracion()==null) || 
             (this.nombreConfiguracion!=null &&
              this.nombreConfiguracion.equals(other.getNombreConfiguracion()))) &&
            ((this.formatoDocumento==null && other.getFormatoDocumento()==null) || 
             (this.formatoDocumento!=null &&
              this.formatoDocumento.equals(other.getFormatoDocumento())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getUser() != null) {
            _hashCode += getUser().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getTipoIntercambio() != null) {
            _hashCode += getTipoIntercambio().hashCode();
        }
        if (getNombreConfiguracion() != null) {
            _hashCode += getNombreConfiguracion().hashCode();
        }
        if (getFormatoDocumento() != null) {
            _hashCode += getFormatoDocumento().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EncabezadoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.e-sign.cl/", "EncabezadoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.e-sign.cl/", "User"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.e-sign.cl/", "Password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoIntercambio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.e-sign.cl/", "TipoIntercambio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreConfiguracion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.e-sign.cl/", "NombreConfiguracion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formatoDocumento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.e-sign.cl/", "FormatoDocumento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
