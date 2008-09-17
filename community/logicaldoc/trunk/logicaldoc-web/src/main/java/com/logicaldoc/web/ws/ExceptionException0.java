/**
 * ExceptionException0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package com.logicaldoc.web.ws;

public class ExceptionException0 extends java.lang.Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 617053658489537406L;
    private com.logicaldoc.web.ws.DmsStub.Exception0 faultMessage;

    public ExceptionException0() {
        super("ExceptionException0");
    }

    public ExceptionException0(java.lang.String s) {
        super(s);
    }

    public ExceptionException0(java.lang.String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public void setFaultMessage(com.logicaldoc.web.ws.DmsStub.Exception0 msg) {
        faultMessage = msg;
    }

    public com.logicaldoc.web.ws.DmsStub.Exception0 getFaultMessage() {
        return faultMessage;
    }
}
