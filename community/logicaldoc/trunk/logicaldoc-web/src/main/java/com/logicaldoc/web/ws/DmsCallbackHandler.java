/**
 * DmsCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package com.logicaldoc.web.ws;


/**
 *  DmsCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class DmsCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public DmsCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public DmsCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for checkin method
     * override this method for handling normal response from checkin operation
     */
    public void receiveResultcheckin(
        com.logicaldoc.web.ws.DmsStub.CheckinResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkin operation
     */
    public void receiveErrorcheckin(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createDocument method
     * override this method for handling normal response from createDocument operation
     */
    public void receiveResultcreateDocument(
        com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createDocument operation
     */
    public void receiveErrorcreateDocument(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for search method
     * override this method for handling normal response from search operation
     */
    public void receiveResultsearch(
        com.logicaldoc.web.ws.DmsStub.SearchResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from search operation
     */
    public void receiveErrorsearch(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for downloadDocumentInfo method
     * override this method for handling normal response from downloadDocumentInfo operation
     */
    public void receiveResultdownloadDocumentInfo(
        com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from downloadDocumentInfo operation
     */
    public void receiveErrordownloadDocumentInfo(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for deleteFolder method
     * override this method for handling normal response from deleteFolder operation
     */
    public void receiveResultdeleteFolder(
        com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from deleteFolder operation
     */
    public void receiveErrordeleteFolder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for downloadDocument method
     * override this method for handling normal response from downloadDocument operation
     */
    public void receiveResultdownloadDocument(
        com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from downloadDocument operation
     */
    public void receiveErrordownloadDocument(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for deleteDocument method
     * override this method for handling normal response from deleteDocument operation
     */
    public void receiveResultdeleteDocument(
        com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from deleteDocument operation
     */
    public void receiveErrordeleteDocument(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createFolder method
     * override this method for handling normal response from createFolder operation
     */
    public void receiveResultcreateFolder(
        com.logicaldoc.web.ws.DmsStub.CreateFolderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createFolder operation
     */
    public void receiveErrorcreateFolder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for checkout method
     * override this method for handling normal response from checkout operation
     */
    public void receiveResultcheckout(
        com.logicaldoc.web.ws.DmsStub.CheckoutResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkout operation
     */
    public void receiveErrorcheckout(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for downloadFolderContent method
     * override this method for handling normal response from downloadFolderContent operation
     */
    public void receiveResultdownloadFolderContent(
        com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from downloadFolderContent operation
     */
    public void receiveErrordownloadFolderContent(java.lang.Exception e) {
    }
}
