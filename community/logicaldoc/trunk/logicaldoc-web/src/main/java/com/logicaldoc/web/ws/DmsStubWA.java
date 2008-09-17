package com.logicaldoc.web.ws;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;

/**
 * A stub implementation with support for attachment upload
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class DmsStubWA extends DmsStub {
	public DmsStubWA(ConfigurationContext configurationContext) throws AxisFault {
		super(configurationContext);
	}

	public DmsStubWA(org.apache.axis2.context.ConfigurationContext configurationContext,
			java.lang.String targetEndpoint, boolean useSeparateListener) throws org.apache.axis2.AxisFault {
		super(configurationContext, targetEndpoint, useSeparateListener);
	}

	public DmsStubWA() throws AxisFault {
		super();
	}

	public DmsStubWA(ConfigurationContext configurationContext, String targetEndpoint) throws AxisFault {
		super(configurationContext, targetEndpoint);
	}

	public DmsStubWA(String targetEndpoint) throws AxisFault {
		super(targetEndpoint);
	}

    public com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse downloadDocument(
            com.logicaldoc.web.ws.DmsStub.DownloadDocument downloadDocument10)
            throws java.rmi.RemoteException, com.logicaldoc.web.ws.ExceptionException0 {
            try {
                org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
                _operationClient.getOptions().setAction("urn:downloadDocument");
                _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

                addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                    "&");

                // create a message context
                org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

                // create SOAP envelope with that payload
                org.apache.axiom.soap.SOAPEnvelope env = null;

                env = toEnvelope(getFactory(_operationClient.getOptions()
                                                            .getSoapVersionURI()),
                        downloadDocument10,
                        optimizeContent(
                            new javax.xml.namespace.QName(
                                "http://ws.web.logicaldoc.com", "downloadDocument")));

                //adding SOAP soap_headers
                _serviceClient.addHeadersToEnvelope(env);
                // set the message context with that soap envelope
                _messageContext.setEnvelope(env);

                // add the message contxt to the operation client
                _operationClient.addMessageContext(_messageContext);

                //execute the operation client
                _operationClient.execute(true);

                
                org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);

                org.apache.axiom.attachments.Attachments attachment = _returnMessageContext.getAttachmentMap(); 
                javax.activation.DataHandler dataHandler = attachment.getDataHandler("document"); 
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

                java.lang.Object object = fromOM(_returnEnv.getBody()
                                                           .getFirstElement(),
                        com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.class,
                        getEnvelopeNamespaces(_returnEnv));
                _messageContext.getTransportOut().getSender()
                               .cleanup(_messageContext);

                return (com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse) object;
            } catch (org.apache.axis2.AxisFault f) {
                org.apache.axiom.om.OMElement faultElt = f.getDetail();

                if (faultElt != null) {
                    if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                        //make the fault by reflection
                        try {
                            java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                            java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                            java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

                            //message class
                            java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                            java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                            java.lang.Object messageObject = fromOM(faultElt,
                                    messageClass, null);
                            java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                    new java.lang.Class[] { messageClass });
                            m.invoke(ex, new java.lang.Object[] { messageObject });

                            if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
                                throw (com.logicaldoc.web.ws.ExceptionException0) ex;
                            }

                            throw new java.rmi.RemoteException(ex.getMessage(), ex);
                        } catch (java.lang.ClassCastException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        } catch (java.lang.ClassNotFoundException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        } catch (java.lang.NoSuchMethodException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        } catch (java.lang.reflect.InvocationTargetException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        } catch (java.lang.IllegalAccessException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        } catch (java.lang.InstantiationException e) {
                            // we cannot intantiate the class - throw the original Axis fault
                            throw f;
                        }
                    } else {
                        throw f;
                    }
                } else {
                    throw f;
                }
            }
        }
	
	public com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse createDocument(
			com.logicaldoc.web.ws.DmsStub.CreateDocument createDocument0, File file) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			Options options = new Options();
			options.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
			options.setSoapVersionURI(_serviceClient.getOptions().getSoapVersionURI());
			// Increase the time out when sending large attachments
			options.setTimeOutInMilliSeconds(10000);
			options.setTo(_serviceClient.getOptions().getTo());
			options.setAction(_serviceClient.getOptions().getAction());

			ServiceClient sender = new ServiceClient(null, null);
			sender.setOptions(options);
			OperationClient _operationClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

			_operationClient.getOptions().setAction("urn:createDocument");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), createDocument0,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createDocument")));

			// adding SOAP soap_headers
			sender.addHeadersToEnvelope(env);

			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// Add the 'document' attachment
			DataHandler dataHandler = new DataHandler(new FileDataSource(file));
			_messageContext.addAttachment("document", dataHandler);

			// add the message context to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = null;
			try {
				object = com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.Factory.parse(_returnEnv.getBody()
						.getFirstElement().getXMLStreamReaderWithoutCaching());
			} catch (java.lang.Exception e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}

			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			throw f;
		}
	}

	public com.logicaldoc.web.ws.DmsStub.CheckinResponse checkin(com.logicaldoc.web.ws.DmsStub.Checkin checkin, File file)
			throws java.rmi.RemoteException, com.logicaldoc.web.ws.ExceptionException0 {
		try {
			Options options = new Options();
			options.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
			options.setSoapVersionURI(_serviceClient.getOptions().getSoapVersionURI());
			// Increase the time out when sending large attachments
			options.setTimeOutInMilliSeconds(10000);
			options.setTo(_serviceClient.getOptions().getTo());
			options.setAction(_serviceClient.getOptions().getAction());

			ServiceClient sender = new ServiceClient(null, null);
			sender.setOptions(options);
			OperationClient _operationClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

			_operationClient.getOptions().setAction("urn:checkin");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), checkin,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkin")));

			// adding SOAP soap_headers
			sender.addHeadersToEnvelope(env);

			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// Add the 'document' attachment
			DataHandler dataHandler = new DataHandler(new FileDataSource(file));
			_messageContext.addAttachment("document", dataHandler);

			// add the message context to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = null;
			try {
				object = com.logicaldoc.web.ws.DmsStub.CheckinResponse.Factory.parse(_returnEnv.getBody()
						.getFirstElement().getXMLStreamReaderWithoutCaching());
			} catch (java.lang.Exception e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}

			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CheckinResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			throw f;
		}
	}
}
