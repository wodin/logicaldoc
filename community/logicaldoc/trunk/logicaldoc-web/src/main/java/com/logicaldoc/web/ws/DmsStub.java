/**
 * DmsStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package com.logicaldoc.web.ws;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMFactory;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter;

/*
 * DmsStub java implementation
 */
public class DmsStub extends org.apache.axis2.client.Stub {
	protected org.apache.axis2.description.AxisOperation[] _operations;

	// hashmaps to keep the fault mapping
	protected java.util.HashMap faultExceptionNameMap = new java.util.HashMap();

	protected java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();

	protected java.util.HashMap faultMessageMap = new java.util.HashMap();

	protected javax.xml.namespace.QName[] opNameArray = null;

	/**
	 * Constructor that takes in a configContext
	 */
	public DmsStub(org.apache.axis2.context.ConfigurationContext configurationContext, java.lang.String targetEndpoint)
			throws org.apache.axis2.AxisFault {
		this(configurationContext, targetEndpoint, false);
	}

	/**
	 * Constructor that takes in a configContext and useseperate listner
	 */
	public DmsStub(org.apache.axis2.context.ConfigurationContext configurationContext, java.lang.String targetEndpoint,
			boolean useSeparateListener) throws org.apache.axis2.AxisFault {
		// To populate AxisService
		populateAxisService();
		populateFaults();

		_serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

		configurationContext = _serviceClient.getServiceContext().getConfigurationContext();

		_serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
		_serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

		// Set the soap version
		_serviceClient.getOptions()
				.setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
	}

	/**
	 * Default Constructor
	 */
	public DmsStub(org.apache.axis2.context.ConfigurationContext configurationContext)
			throws org.apache.axis2.AxisFault {
		this(configurationContext, "http://localhost:8080/logicaldoc/services/Dms");
	}

	/**
	 * Default Constructor
	 */
	public DmsStub() throws org.apache.axis2.AxisFault {
		this("http://localhost:8080/logicaldoc/services/Dms");
	}

	/**
	 * Constructor taking the target endpoint
	 */
	public DmsStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
		this(null, targetEndpoint);
	}

	private void populateAxisService() throws org.apache.axis2.AxisFault {
		// creating the Service with a unique name
		_service = new org.apache.axis2.description.AxisService("Dms" + this.hashCode());

		// creating the operations
		org.apache.axis2.description.AxisOperation __operation;

		_operations = new org.apache.axis2.description.AxisOperation[10];

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkin"));
		_service.addOperation(__operation);

		_operations[0] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createDocument"));
		_service.addOperation(__operation);

		_operations[1] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "search"));
		_service.addOperation(__operation);

		_operations[2] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocumentInfo"));
		_service.addOperation(__operation);

		_operations[3] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteFolder"));
		_service.addOperation(__operation);

		_operations[4] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocument"));
		_service.addOperation(__operation);

		_operations[5] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteDocument"));
		_service.addOperation(__operation);

		_operations[6] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createFolder"));
		_service.addOperation(__operation);

		_operations[7] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkout"));
		_service.addOperation(__operation);

		_operations[8] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadFolderContent"));
		_service.addOperation(__operation);

		_operations[9] = __operation;
	}

	// populates the faults
	private void populateFaults() {
		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.ExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
				"com.logicaldoc.web.ws.DmsStub$Exception0");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#checkin
	 * @param checkin0
	 */
	public com.logicaldoc.web.ws.DmsStub.CheckinResponse checkin(com.logicaldoc.web.ws.DmsStub.Checkin checkin0)
			throws java.rmi.RemoteException, com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0]
					.getName());
			_operationClient.getOptions().setAction("urn:checkin");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), checkin0,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkin")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.CheckinResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CheckinResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startcheckin
	 * @param checkin0
	 */
	public void startcheckin(com.logicaldoc.web.ws.DmsStub.Checkin checkin0,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[0].getName());
		_operationClient.getOptions().setAction("urn:checkin");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), checkin0,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkin")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.CheckinResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultcheckin((com.logicaldoc.web.ws.DmsStub.CheckinResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorcheckin(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrorcheckin((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrorcheckin(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckin(f);
							}
						} else {
							callback.receiveErrorcheckin(f);
						}
					} else {
						callback.receiveErrorcheckin(f);
					}
				} else {
					callback.receiveErrorcheckin(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[0].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[0].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#createDocument
	 * @param createDocument2
	 */
	public com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse createDocument(
			com.logicaldoc.web.ws.DmsStub.CreateDocument createDocument2) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1]
					.getName());
			_operationClient.getOptions().setAction("urn:createDocument");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), createDocument2,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createDocument")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startcreateDocument
	 * @param createDocument2
	 */
	public void startcreateDocument(com.logicaldoc.web.ws.DmsStub.CreateDocument createDocument2,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[1].getName());
		_operationClient.getOptions().setAction("urn:createDocument");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), createDocument2,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createDocument")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultcreateDocument((com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorcreateDocument(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrorcreateDocument((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrorcreateDocument(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateDocument(f);
							}
						} else {
							callback.receiveErrorcreateDocument(f);
						}
					} else {
						callback.receiveErrorcreateDocument(f);
					}
				} else {
					callback.receiveErrorcreateDocument(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[1].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[1].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#search
	 * @param search4
	 */
	public com.logicaldoc.web.ws.DmsStub.SearchResponse search(com.logicaldoc.web.ws.DmsStub.Search search4)
			throws java.rmi.RemoteException, com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2]
					.getName());
			_operationClient.getOptions().setAction("urn:search");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), search4,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "search")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.SearchResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.SearchResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startsearch
	 * @param search4
	 */
	public void startsearch(com.logicaldoc.web.ws.DmsStub.Search search4,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[2].getName());
		_operationClient.getOptions().setAction("urn:search");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), search4,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "search")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.SearchResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultsearch((com.logicaldoc.web.ws.DmsStub.SearchResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorsearch(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrorsearch((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrorsearch(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsearch(f);
							}
						} else {
							callback.receiveErrorsearch(f);
						}
					} else {
						callback.receiveErrorsearch(f);
					}
				} else {
					callback.receiveErrorsearch(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[2].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[2].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#downloadDocumentInfo
	 * @param downloadDocumentInfo6
	 */
	public com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse downloadDocumentInfo(
			com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo downloadDocumentInfo6) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3]
					.getName());
			_operationClient.getOptions().setAction("urn:downloadDocumentInfo");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(
					getFactory(_operationClient.getOptions().getSoapVersionURI()),
					downloadDocumentInfo6,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocumentInfo")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startdownloadDocumentInfo
	 * @param downloadDocumentInfo6
	 */
	public void startdownloadDocumentInfo(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo downloadDocumentInfo6,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[3].getName());
		_operationClient.getOptions().setAction("urn:downloadDocumentInfo");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadDocumentInfo6,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocumentInfo")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse.class,
							getEnvelopeNamespaces(resultEnv));
					callback
							.receiveResultdownloadDocumentInfo((com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrordownloadDocumentInfo(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback
											.receiveErrordownloadDocumentInfo((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrordownloadDocumentInfo(new java.rmi.RemoteException(ex.getMessage(),
										ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocumentInfo(f);
							}
						} else {
							callback.receiveErrordownloadDocumentInfo(f);
						}
					} else {
						callback.receiveErrordownloadDocumentInfo(f);
					}
				} else {
					callback.receiveErrordownloadDocumentInfo(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[3].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[3].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#deleteFolder
	 * @param deleteFolder8
	 */
	public com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse deleteFolder(
			com.logicaldoc.web.ws.DmsStub.DeleteFolder deleteFolder8) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4]
					.getName());
			_operationClient.getOptions().setAction("urn:deleteFolder");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteFolder8,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteFolder")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startdeleteFolder
	 * @param deleteFolder8
	 */
	public void startdeleteFolder(com.logicaldoc.web.ws.DmsStub.DeleteFolder deleteFolder8,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[4].getName());
		_operationClient.getOptions().setAction("urn:deleteFolder");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteFolder8,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteFolder")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultdeleteFolder((com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrordeleteFolder(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrordeleteFolder((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrordeleteFolder(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteFolder(f);
							}
						} else {
							callback.receiveErrordeleteFolder(f);
						}
					} else {
						callback.receiveErrordeleteFolder(f);
					}
				} else {
					callback.receiveErrordeleteFolder(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[4].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[4].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#downloadDocument
	 * @param downloadDocument10
	 */
	public com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse downloadDocument(
			com.logicaldoc.web.ws.DmsStub.DownloadDocument downloadDocument10) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5]
					.getName());
			_operationClient.getOptions().setAction("urn:downloadDocument");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadDocument10,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocument")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startdownloadDocument
	 * @param downloadDocument10
	 */
	public void startdownloadDocument(com.logicaldoc.web.ws.DmsStub.DownloadDocument downloadDocument10,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[5].getName());
		_operationClient.getOptions().setAction("urn:downloadDocument");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadDocument10,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadDocument")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.class,
							getEnvelopeNamespaces(resultEnv));
					callback
							.receiveResultdownloadDocument((com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrordownloadDocument(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrordownloadDocument((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback
										.receiveErrordownloadDocument(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadDocument(f);
							}
						} else {
							callback.receiveErrordownloadDocument(f);
						}
					} else {
						callback.receiveErrordownloadDocument(f);
					}
				} else {
					callback.receiveErrordownloadDocument(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[5].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[5].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#deleteDocument
	 * @param deleteDocument12
	 */
	public com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse deleteDocument(
			com.logicaldoc.web.ws.DmsStub.DeleteDocument deleteDocument12) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[6]
					.getName());
			_operationClient.getOptions().setAction("urn:deleteDocument");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteDocument12,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteDocument")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startdeleteDocument
	 * @param deleteDocument12
	 */
	public void startdeleteDocument(com.logicaldoc.web.ws.DmsStub.DeleteDocument deleteDocument12,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[6].getName());
		_operationClient.getOptions().setAction("urn:deleteDocument");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), deleteDocument12,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "deleteDocument")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultdeleteDocument((com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrordeleteDocument(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrordeleteDocument((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrordeleteDocument(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordeleteDocument(f);
							}
						} else {
							callback.receiveErrordeleteDocument(f);
						}
					} else {
						callback.receiveErrordeleteDocument(f);
					}
				} else {
					callback.receiveErrordeleteDocument(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[6].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[6].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#createFolder
	 * @param createFolder14
	 */
	public com.logicaldoc.web.ws.DmsStub.CreateFolderResponse createFolder(
			com.logicaldoc.web.ws.DmsStub.CreateFolder createFolder14) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[7]
					.getName());
			_operationClient.getOptions().setAction("urn:createFolder");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), createFolder14,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createFolder")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.CreateFolderResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CreateFolderResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startcreateFolder
	 * @param createFolder14
	 */
	public void startcreateFolder(com.logicaldoc.web.ws.DmsStub.CreateFolder createFolder14,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[7].getName());
		_operationClient.getOptions().setAction("urn:createFolder");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), createFolder14,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "createFolder")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.CreateFolderResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultcreateFolder((com.logicaldoc.web.ws.DmsStub.CreateFolderResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorcreateFolder(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrorcreateFolder((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrorcreateFolder(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcreateFolder(f);
							}
						} else {
							callback.receiveErrorcreateFolder(f);
						}
					} else {
						callback.receiveErrorcreateFolder(f);
					}
				} else {
					callback.receiveErrorcreateFolder(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[7].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[7].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#checkout
	 * @param checkout16
	 */
	public com.logicaldoc.web.ws.DmsStub.CheckoutResponse checkout(com.logicaldoc.web.ws.DmsStub.Checkout checkout16)
			throws java.rmi.RemoteException, com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[8]
					.getName());
			_operationClient.getOptions().setAction("urn:checkout");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), checkout16,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkout")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.CheckoutResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.CheckoutResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startcheckout
	 * @param checkout16
	 */
	public void startcheckout(com.logicaldoc.web.ws.DmsStub.Checkout checkout16,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[8].getName());
		_operationClient.getOptions().setAction("urn:checkout");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), checkout16,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "checkout")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.CheckoutResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultcheckout((com.logicaldoc.web.ws.DmsStub.CheckoutResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorcheckout(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback.receiveErrorcheckout((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrorcheckout(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorcheckout(f);
							}
						} else {
							callback.receiveErrorcheckout(f);
						}
					} else {
						callback.receiveErrorcheckout(f);
					}
				} else {
					callback.receiveErrorcheckout(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[8].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[8].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see com.logicaldoc.web.ws.Dms#downloadFolderContent
	 * @param downloadFolderContent18
	 */
	public com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse downloadFolderContent(
			com.logicaldoc.web.ws.DmsStub.DownloadFolderContent downloadFolderContent18) throws java.rmi.RemoteException,
			com.logicaldoc.web.ws.ExceptionException0 {
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[9]
					.getName());
			_operationClient.getOptions().setAction("urn:downloadFolderContent");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(
					getFactory(_operationClient.getOptions().getSoapVersionURI()),
					downloadFolderContent18,
					optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadFolderContent")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse.class, getEnvelopeNamespaces(_returnEnv));
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);

			return (com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse) object;
		} catch (org.apache.axis2.AxisFault f) {
			org.apache.axiom.om.OMElement faultElt = f.getDetail();

			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
							throw (com.logicaldoc.web.ws.ExceptionException0) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original
						// Axis fault
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

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see com.logicaldoc.web.ws.Dms#startdownloadFolderContent
	 * @param downloadFolderContent18
	 */
	public void startdownloadFolderContent(com.logicaldoc.web.ws.DmsStub.DownloadFolderContent downloadFolderContent18,
			final com.logicaldoc.web.ws.DmsCallbackHandler callback) throws java.rmi.RemoteException {
		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[9].getName());
		_operationClient.getOptions().setAction("urn:downloadFolderContent");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.
		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadFolderContent18,
				optimizeContent(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "downloadFolderContent")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse.class,
							getEnvelopeNamespaces(resultEnv));
					callback
							.receiveResultdownloadFolderContent((com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse) object);
				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrordownloadFolderContent(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();

					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();

								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt
										.getQName());
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof com.logicaldoc.web.ws.ExceptionException0) {
									callback
											.receiveErrordownloadFolderContent((com.logicaldoc.web.ws.ExceptionException0) ex);

									return;
								}

								callback.receiveErrordownloadFolderContent(new java.rmi.RemoteException(
										ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrordownloadFolderContent(f);
							}
						} else {
							callback.receiveErrordownloadFolderContent(f);
						}
					} else {
						callback.receiveErrordownloadFolderContent(f);
					}
				} else {
					callback.receiveErrordownloadFolderContent(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				// Do nothing by default
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

		if ((_operations[9].getMessageReceiver() == null) && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[9].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);
	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	protected java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();

		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}

		return returnMap;
	}

	protected boolean optimizeContent(javax.xml.namespace.QName opName) {
		if (opNameArray == null) {
			return false;
		}

		for (int i = 0; i < opNameArray.length; i++) {
			if (opName.equals(opNameArray[i])) {
				return true;
			}
		}

		return false;
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.Checkin param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.Checkin.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CheckinResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CheckinResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.Exception0 param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.Exception0.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CreateDocument param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateDocument.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.Search param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.Search.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.SearchResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.SearchResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DeleteFolder param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteFolder.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadDocument param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocument.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CreateFolder param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateFolder.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CreateFolderResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateFolderResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DeleteDocument param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteDocument.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.Checkout param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.Checkout.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.CheckoutResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.CheckoutResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadFolderContent param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadFolderContent.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			return param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	protected org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.Checkin param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(param.getOMElement(com.logicaldoc.web.ws.DmsStub.Checkin.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	protected org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.CreateDocument param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateDocument.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.Search param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(param.getOMElement(com.logicaldoc.web.ws.DmsStub.Search.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.DeleteFolder param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteFolder.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	protected org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.DownloadDocument param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadDocument.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.CreateFolder param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.CreateFolder.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.DeleteDocument param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.DeleteDocument.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.Checkout param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody()
					.addChild(param.getOMElement(com.logicaldoc.web.ws.DmsStub.Checkout.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.logicaldoc.web.ws.DmsStub.DownloadFolderContent param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.logicaldoc.web.ws.DmsStub.DownloadFolderContent.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	/* methods to provide back word compatibility */

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
		return factory.getDefaultEnvelope();
	}

	protected java.lang.Object fromOM(org.apache.axiom.om.OMElement param, java.lang.Class type,
			java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {
		try {
			if (com.logicaldoc.web.ws.DmsStub.Checkin.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Checkin.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CheckinResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CheckinResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CreateDocument.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CreateDocument.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Search.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Search.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.SearchResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.SearchResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DeleteFolder.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DeleteFolder.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfo.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadDocument.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadDocument.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CreateFolder.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CreateFolder.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CreateFolderResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CreateFolderResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DeleteDocument.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DeleteDocument.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Checkout.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Checkout.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.CheckoutResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.CheckoutResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadFolderContent.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadFolderContent.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());
			}

			if (com.logicaldoc.web.ws.DmsStub.Exception0.class.equals(type)) {
				return com.logicaldoc.web.ws.DmsStub.Exception0.Factory.parse(param.getXMLStreamReaderWithoutCaching());
			}
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

		return null;
	}

	// http://localhost:8080/logicaldoc/services/Dms
	public static class Exception implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -537474841333390438L;

		/**
		 * field for Exception
		 */
		protected org.apache.axiom.om.OMElement localException;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localExceptionTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * Exception Namespace URI = http://ws.web.logicaldoc.com Namespace Prefix =
		 * ns1
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getException() {
			return localException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Exception
		 */
		public void setException(org.apache.axiom.om.OMElement param) {
			if (param != null) {
				// update the setting tracker
				localExceptionTracker = true;
			} else {
				localExceptionTracker = true;
			}

			this.localException = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Exception.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localExceptionTracker) {
				if (localException != null) {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "Exception");
						}
					} else {
						xmlWriter.writeStartElement("Exception");
					}

					localException.serialize(xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "Exception");
						}
					} else {
						xmlWriter.writeStartElement("Exception");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				}
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"));

				elementList.add((localException == null) ? null : localException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Exception parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Exception object = new Exception();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"Exception".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Exception) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception").equals(reader
									.getName())) {
						boolean loopDone1 = false;
						javax.xml.namespace.QName startQname1 = new javax.xml.namespace.QName(
								"http://ws.web.logicaldoc.com", "Exception");

						while (!loopDone1) {
							if (reader.isStartElement() && startQname1.equals(reader.getName())) {
								loopDone1 = true;
							} else {
								reader.next();
							}
						}

						// We need to wrap the reader so that it produces a fake
						// START_DOCUEMENT event
						// this is needed by the builder classes
						org.apache.axis2.databinding.utils.NamedStaxOMBuilder builder1 = new org.apache.axis2.databinding.utils.NamedStaxOMBuilder(
								new org.apache.axis2.util.StreamWrapper(reader), startQname1);
						object.setException(builder1.getOMElement().getFirstElement());

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Content implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6037992295687887747L;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Name
		 */
		protected java.lang.String localName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localNameTracker = false;

		/**
		 * field for Writeable
		 */
		protected int localWriteable;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localWriteableTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * Content Namespace URI = http://ws.web.logicaldoc.com/xsd Namespace
		 * Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getName() {
			return localName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Name
		 */
		public void setName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localNameTracker = true;
			} else {
				localNameTracker = true;
			}

			this.localName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getWriteable() {
			return localWriteable;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Writeable
		 */
		public void setWriteable(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localWriteableTracker = false;
			} else {
				localWriteableTracker = true;
			}

			this.localWriteable = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Content.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "name", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "name");
					}
				} else {
					xmlWriter.writeStartElement("name");
				}

				if (localName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localName);
				}

				xmlWriter.writeEndElement();
			}

			if (localWriteableTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "writeable", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "writeable");
					}
				} else {
					xmlWriter.writeStartElement("writeable");
				}

				if (localWriteable == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("writeable cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localWriteable));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name"));

				elementList.add((localName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localName));
			}

			if (localWriteableTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWriteable));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Content parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Content object = new Content();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"Content".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Content) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setWriteable(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setWriteable(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Search implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6972509635452343117L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "search", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Query
		 */
		protected java.lang.String localQuery;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localQueryTracker = false;

		/**
		 * field for IndexLanguage
		 */
		protected java.lang.String localIndexLanguage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIndexLanguageTracker = false;

		/**
		 * field for QueryLanguage
		 */
		protected java.lang.String localQueryLanguage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localQueryLanguageTracker = false;

		/**
		 * field for MaxHits
		 */
		protected int localMaxHits;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localMaxHitsTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getQuery() {
			return localQuery;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Query
		 */
		public void setQuery(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localQueryTracker = true;
			} else {
				localQueryTracker = true;
			}

			this.localQuery = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getIndexLanguage() {
			return localIndexLanguage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param IndexLanguage
		 */
		public void setIndexLanguage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localIndexLanguageTracker = true;
			} else {
				localIndexLanguageTracker = true;
			}

			this.localIndexLanguage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getQueryLanguage() {
			return localQueryLanguage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param QueryLanguage
		 */
		public void setQueryLanguage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localQueryLanguageTracker = true;
			} else {
				localQueryLanguageTracker = true;
			}

			this.localQueryLanguage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getMaxHits() {
			return localMaxHits;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param MaxHits
		 */
		public void setMaxHits(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localMaxHitsTracker = false;
			} else {
				localMaxHitsTracker = true;
			}

			this.localMaxHits = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Search.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localQueryTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "query", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "query");
					}
				} else {
					xmlWriter.writeStartElement("query");
				}

				if (localQuery == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localQuery);
				}

				xmlWriter.writeEndElement();
			}

			if (localIndexLanguageTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "indexLanguage", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "indexLanguage");
					}
				} else {
					xmlWriter.writeStartElement("indexLanguage");
				}

				if (localIndexLanguage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localIndexLanguage);
				}

				xmlWriter.writeEndElement();
			}

			if (localQueryLanguageTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "queryLanguage", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "queryLanguage");
					}
				} else {
					xmlWriter.writeStartElement("queryLanguage");
				}

				if (localQueryLanguage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localQueryLanguage);
				}

				xmlWriter.writeEndElement();
			}

			if (localMaxHitsTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "maxHits", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "maxHits");
					}
				} else {
					xmlWriter.writeStartElement("maxHits");
				}

				if (localMaxHits == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("maxHits cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localMaxHits));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localQueryTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "query"));

				elementList.add((localQuery == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localQuery));
			}

			if (localIndexLanguageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "indexLanguage"));

				elementList.add((localIndexLanguage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localIndexLanguage));
			}

			if (localQueryLanguageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "queryLanguage"));

				elementList.add((localQueryLanguage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localQueryLanguage));
			}

			if (localMaxHitsTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "maxHits"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMaxHits));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Search parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Search object = new Search();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"search".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Search) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "query").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setQuery(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "indexLanguage")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setIndexLanguage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "queryLanguage")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setQueryLanguage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "maxHits").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setMaxHits(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setMaxHits(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class CreateDocumentResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -535684767122632306L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "createDocumentResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CreateDocumentResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CreateDocumentResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				CreateDocumentResponse object = new CreateDocumentResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"createDocumentResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CreateDocumentResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class CreateDocument implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4071340936496389148L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "createDocument", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Parent
		 */
		protected int localParent;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentTracker = false;

		/**
		 * field for DocName
		 */
		protected java.lang.String localDocName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDocNameTracker = false;

		/**
		 * field for Source
		 */
		protected java.lang.String localSource;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSourceTracker = false;

		/**
		 * field for SourceDate
		 */
		protected java.lang.String localSourceDate;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSourceDateTracker = false;

		/**
		 * field for Author
		 */
		protected java.lang.String localAuthor;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localAuthorTracker = false;

		/**
		 * field for SourceType
		 */
		protected java.lang.String localSourceType;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSourceTypeTracker = false;

		/**
		 * field for Coverage
		 */
		protected java.lang.String localCoverage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localCoverageTracker = false;

		/**
		 * field for Language
		 */
		protected java.lang.String localLanguage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localLanguageTracker = false;

		/**
		 * field for Keywords
		 */
		protected java.lang.String localKeywords;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localKeywordsTracker = false;

		/**
		 * field for VersionDesc
		 */
		protected java.lang.String localVersionDesc;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localVersionDescTracker = false;

		/**
		 * field for Filename
		 */
		protected java.lang.String localFilename;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localFilenameTracker = false;

		/**
		 * field for Groups
		 */
		protected java.lang.String localGroups;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localGroupsTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getParent() {
			return localParent;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Parent
		 */
		public void setParent(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localParentTracker = false;
			} else {
				localParentTracker = true;
			}

			this.localParent = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDocName() {
			return localDocName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param DocName
		 */
		public void setDocName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localDocNameTracker = true;
			} else {
				localDocNameTracker = true;
			}

			this.localDocName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSource() {
			return localSource;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Source
		 */
		public void setSource(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSourceTracker = true;
			} else {
				localSourceTracker = true;
			}

			this.localSource = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSourceDate() {
			return localSourceDate;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param SourceDate
		 */
		public void setSourceDate(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSourceDateTracker = true;
			} else {
				localSourceDateTracker = true;
			}

			this.localSourceDate = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getAuthor() {
			return localAuthor;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Author
		 */
		public void setAuthor(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localAuthorTracker = true;
			} else {
				localAuthorTracker = true;
			}

			this.localAuthor = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSourceType() {
			return localSourceType;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param SourceType
		 */
		public void setSourceType(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSourceTypeTracker = true;
			} else {
				localSourceTypeTracker = true;
			}

			this.localSourceType = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getCoverage() {
			return localCoverage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Coverage
		 */
		public void setCoverage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localCoverageTracker = true;
			} else {
				localCoverageTracker = true;
			}

			this.localCoverage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getLanguage() {
			return localLanguage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Language
		 */
		public void setLanguage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localLanguageTracker = true;
			} else {
				localLanguageTracker = true;
			}

			this.localLanguage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getKeywords() {
			return localKeywords;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Keywords
		 */
		public void setKeywords(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localKeywordsTracker = true;
			} else {
				localKeywordsTracker = true;
			}

			this.localKeywords = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getVersionDesc() {
			return localVersionDesc;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param VersionDesc
		 */
		public void setVersionDesc(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localVersionDescTracker = true;
			} else {
				localVersionDescTracker = true;
			}

			this.localVersionDesc = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getFilename() {
			return localFilename;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Filename
		 */
		public void setFilename(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localFilenameTracker = true;
			} else {
				localFilenameTracker = true;
			}

			this.localFilename = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getGroups() {
			return localGroups;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Groups
		 */
		public void setGroups(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localGroupsTracker = true;
			} else {
				localGroupsTracker = true;
			}

			this.localGroups = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CreateDocument.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localParentTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parent", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parent");
					}
				} else {
					xmlWriter.writeStartElement("parent");
				}

				if (localParent == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("parent cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localParent));
				}

				xmlWriter.writeEndElement();
			}

			if (localDocNameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "docName", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "docName");
					}
				} else {
					xmlWriter.writeStartElement("docName");
				}

				if (localDocName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localDocName);
				}

				xmlWriter.writeEndElement();
			}

			if (localSourceTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "source", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "source");
					}
				} else {
					xmlWriter.writeStartElement("source");
				}

				if (localSource == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSource);
				}

				xmlWriter.writeEndElement();
			}

			if (localSourceDateTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "sourceDate", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "sourceDate");
					}
				} else {
					xmlWriter.writeStartElement("sourceDate");
				}

				if (localSourceDate == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSourceDate);
				}

				xmlWriter.writeEndElement();
			}

			if (localAuthorTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "author", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "author");
					}
				} else {
					xmlWriter.writeStartElement("author");
				}

				if (localAuthor == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localAuthor);
				}

				xmlWriter.writeEndElement();
			}

			if (localSourceTypeTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "sourceType", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "sourceType");
					}
				} else {
					xmlWriter.writeStartElement("sourceType");
				}

				if (localSourceType == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSourceType);
				}

				xmlWriter.writeEndElement();
			}

			if (localCoverageTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "coverage", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "coverage");
					}
				} else {
					xmlWriter.writeStartElement("coverage");
				}

				if (localCoverage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localCoverage);
				}

				xmlWriter.writeEndElement();
			}

			if (localLanguageTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "language", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "language");
					}
				} else {
					xmlWriter.writeStartElement("language");
				}

				if (localLanguage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localLanguage);
				}

				xmlWriter.writeEndElement();
			}

			if (localKeywordsTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "keywords", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "keywords");
					}
				} else {
					xmlWriter.writeStartElement("keywords");
				}

				if (localKeywords == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localKeywords);
				}

				xmlWriter.writeEndElement();
			}

			if (localVersionDescTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "versionDesc", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "versionDesc");
					}
				} else {
					xmlWriter.writeStartElement("versionDesc");
				}

				if (localVersionDesc == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localVersionDesc);
				}

				xmlWriter.writeEndElement();
			}

			if (localFilenameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "filename", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "filename");
					}
				} else {
					xmlWriter.writeStartElement("filename");
				}

				if (localFilename == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localFilename);
				}

				xmlWriter.writeEndElement();
			}

			if (localGroupsTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "groups", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "groups");
					}
				} else {
					xmlWriter.writeStartElement("groups");
				}

				if (localGroups == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localGroups);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localParentTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "parent"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localParent));
			}

			if (localDocNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "docName"));

				elementList.add((localDocName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localDocName));
			}

			if (localSourceTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "source"));

				elementList.add((localSource == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSource));
			}

			if (localSourceDateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "sourceDate"));

				elementList.add((localSourceDate == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSourceDate));
			}

			if (localAuthorTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "author"));

				elementList.add((localAuthor == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localAuthor));
			}

			if (localSourceTypeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "sourceType"));

				elementList.add((localSourceType == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSourceType));
			}

			if (localCoverageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "coverage"));

				elementList.add((localCoverage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localCoverage));
			}

			if (localLanguageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "language"));

				elementList.add((localLanguage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localLanguage));
			}

			if (localKeywordsTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "keywords"));

				elementList.add((localKeywords == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localKeywords));
			}

			if (localVersionDescTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "versionDesc"));

				elementList.add((localVersionDesc == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localVersionDesc));
			}

			if (localFilenameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "filename"));

				elementList.add((localFilename == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localFilename));
			}

			if (localGroupsTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "groups"));

				elementList.add((localGroups == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localGroups));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CreateDocument parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				CreateDocument object = new CreateDocument();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"createDocument".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CreateDocument) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "parent").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setParent(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setParent(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "docName").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.setDocName(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "source").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setSource(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "sourceDate").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setSourceDate(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "author").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setAuthor(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "sourceType").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setSourceType(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "coverage").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setCoverage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "language").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setLanguage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "keywords").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setKeywords(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "versionDesc").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setVersionDesc(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "filename").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setFilename(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "groups").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setGroups(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadFolderContentResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8472888487051047869L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadFolderContentResponse", "ns1");

		/**
		 * field for _return
		 */
		protected FolderContent local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return FolderContent
		 */
		public FolderContent get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(FolderContent param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadFolderContentResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				if (local_return == null) {
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}
					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					local_return.serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"),
							factory, xmlWriter);
				}
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : local_return);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadFolderContentResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DownloadFolderContentResponse object = new DownloadFolderContentResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadFolderContentResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadFolderContentResponse) ExtensionMapper.getTypeObject(nsUri, type,
										reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.set_return(null);
							reader.next();

							reader.next();
						} else {
							object.set_return(FolderContent.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class SearchResult implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7542589532893473231L;

		/**
		 * field for EstimatedHitsNumber
		 */
		protected long localEstimatedHitsNumber;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localEstimatedHitsNumberTracker = false;

		/**
		 * field for MoreHits
		 */
		protected int localMoreHits;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localMoreHitsTracker = false;

		/**
		 * field for ResultImpl This was an Array!
		 */
		protected Result[] localResult;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localResultTracker = false;

		/**
		 * field for Time
		 */
		protected long localTime;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localTimeTracker = false;

		/**
		 * field for TotalHits
		 */
		protected int localTotalHits;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localTotalHitsTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * SearchResult Namespace URI = http://ws.web.logicaldoc.com/xsd Namespace
		 * Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return long
		 */
		public long getEstimatedHitsNumber() {
			return localEstimatedHitsNumber;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param EstimatedHitsNumber
		 */
		public void setEstimatedHitsNumber(long param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Long.MIN_VALUE) {
				localEstimatedHitsNumberTracker = false;
			} else {
				localEstimatedHitsNumberTracker = true;
			}

			this.localEstimatedHitsNumber = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getMoreHits() {
			return localMoreHits;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param MoreHits
		 */
		public void setMoreHits(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localMoreHitsTracker = false;
			} else {
				localMoreHitsTracker = true;
			}

			this.localMoreHits = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return ResultImpl[]
		 */
		public Result[] getResult() {
			return localResult;
		}

		/**
		 * validate the array for ResultImpl
		 */
		protected void validateResult(Result[] param) {
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ResultImpl
		 */
		public void setResult(Result[] param) {
			validateResult(param);

			if (param != null) {
				// update the setting tracker
				localResultTracker = true;
			} else {
				localResultTracker = true;
			}

			this.localResult = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param ResultImpl
		 */
		public void addResult(Result param) {
			if (localResult == null) {
				localResult = new Result[] {};
			}

			// update the setting tracker
			localResultTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localResult);
			list.add(param);
			this.localResult = (Result[]) list.toArray(new Result[list.size()]);
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return long
		 */
		public long getTime() {
			return localTime;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Time
		 */
		public void setTime(long param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Long.MIN_VALUE) {
				localTimeTracker = false;
			} else {
				localTimeTracker = true;
			}

			this.localTime = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getTotalHits() {
			return localTotalHits;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param TotalHits
		 */
		public void setTotalHits(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localTotalHitsTracker = false;
			} else {
				localTotalHitsTracker = true;
			}

			this.localTotalHits = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					SearchResult.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localEstimatedHitsNumberTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "estimatedHitsNumber", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "estimatedHitsNumber");
					}
				} else {
					xmlWriter.writeStartElement("estimatedHitsNumber");
				}

				if (localEstimatedHitsNumber == java.lang.Long.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("estimatedHitsNumber cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localEstimatedHitsNumber));
				}

				xmlWriter.writeEndElement();
			}

			if (localMoreHitsTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "moreHits", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "moreHits");
					}
				} else {
					xmlWriter.writeStartElement("moreHits");
				}

				if (localMoreHits == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("moreHits cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localMoreHits));
				}

				xmlWriter.writeEndElement();
			}

			if (localResultTracker) {
				if (localResult != null) {
					for (int i = 0; i < localResult.length; i++) {
						if (localResult[i] != null) {
							localResult[i].serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd",
									"result"), factory, xmlWriter);
						} else {
							// write null attribute
							java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "result", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);
								} else {
									xmlWriter.writeStartElement(namespace2, "result");
								}
							} else {
								xmlWriter.writeStartElement("result");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();
						}
					}
				} else {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "result", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "result");
						}
					} else {
						xmlWriter.writeStartElement("result");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				}
			}

			if (localTimeTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "time", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "time");
					}
				} else {
					xmlWriter.writeStartElement("time");
				}

				if (localTime == java.lang.Long.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("time cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localTime));
				}

				xmlWriter.writeEndElement();
			}

			if (localTotalHitsTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "totalHits", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "totalHits");
					}
				} else {
					xmlWriter.writeStartElement("totalHits");
				}

				if (localTotalHits == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("totalHits cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localTotalHits));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localEstimatedHitsNumberTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "estimatedHitsNumber"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localEstimatedHitsNumber));
			}

			if (localMoreHitsTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "moreHits"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMoreHits));
			}

			if (localResultTracker) {
				if (localResult != null) {
					for (int i = 0; i < localResult.length; i++) {
						if (localResult[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "result"));
							elementList.add(localResult[i]);
						} else {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "result"));
							elementList.add(null);
						}
					}
				} else {
					elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "result"));
					elementList.add(localResult);
				}
			}

			if (localTimeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "time"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTime));
			}

			if (localTotalHitsTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "totalHits"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTotalHits));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SearchResult parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				SearchResult object = new SearchResult();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"SearchResult".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (SearchResult) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list3 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "estimatedHitsNumber")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setEstimatedHitsNumber(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToLong(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setEstimatedHitsNumber(java.lang.Long.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "moreHits")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setMoreHits(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setMoreHits(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "result").equals(reader
									.getName())) {
						// Process the array and step past its final element's
						// end.
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list3.add(null);
							reader.next();
						} else {
							list3.add(Result.Factory.parse(reader));
						}

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone3 = false;

						while (!loopDone3) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();

							// Step out of this element
							reader.next();

							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();

							if (reader.isEndElement()) {
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone3 = true;
							} else {
								if (new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "result")
										.equals(reader.getName())) {
									nillableValue = reader.getAttributeValue(
											"http://www.w3.org/2001/XMLSchema-instance", "nil");

									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list3.add(null);
										reader.next();
									} else {
										list3.add(Result.Factory.parse(reader));
									}
								} else {
									loopDone3 = true;
								}
							}
						}

						// call the converter utility to convert and set the
						// array
						object.setResult((Result[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
								Result.class, list3));
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "time").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setTime(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setTime(java.lang.Long.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "totalHits")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setTotalHits(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setTotalHits(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Exception0 implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1029316200130084919L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "Exception", "ns1");

		/**
		 * field for Exception
		 */
		protected Exception localException;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localExceptionTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return Exception
		 */
		public Exception getException() {
			return localException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Exception
		 */
		public void setException(Exception param) {
			if (param != null) {
				// update the setting tracker
				localExceptionTracker = true;
			} else {
				localExceptionTracker = true;
			}

			this.localException = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Exception0.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localExceptionTracker) {
				if (localException == null) {
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "Exception");
						}
					} else {
						xmlWriter.writeStartElement("Exception");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					localException.serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"),
							factory, xmlWriter);
				}
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception"));

				elementList.add((localException == null) ? null : localException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Exception0 parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Exception0 object = new Exception0();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

					if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
						// Skip the element and report the null value. It cannot
						// have subelements.
						while (!reader.isEndElement())
							reader.next();

						return null;
					}

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"Exception".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Exception0) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "Exception").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.setException(null);
							reader.next();

							reader.next();
						} else {
							object.setException(Exception.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class CreateFolderResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5967083756182501546L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "createFolderResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CreateFolderResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CreateFolderResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				CreateFolderResponse object = new CreateFolderResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"createFolderResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CreateFolderResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DeleteDocument implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3392368870344048464L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "deleteDocument", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DeleteDocument.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeleteDocument parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				DeleteDocument object = new DeleteDocument();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"deleteDocument".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DeleteDocument) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadDocumentInfoResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 703087067869594571L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadDocumentInfoResponse", "ns1");

		/**
		 * field for _return
		 */
		protected DocumentInfo local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return DocumentInfo
		 */
		public DocumentInfo get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(DocumentInfo param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadDocumentInfoResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				if (local_return == null) {
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}
					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					local_return.serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"),
							factory, xmlWriter);
				}
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : local_return);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadDocumentInfoResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DownloadDocumentInfoResponse object = new DownloadDocumentInfoResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadDocumentInfoResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadDocumentInfoResponse) ExtensionMapper
										.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.set_return(null);
							reader.next();

							reader.next();
						} else {
							object.set_return(DocumentInfo.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class SearchResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9173146056345642113L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "searchResponse", "ns1");

		/**
		 * field for _return
		 */
		protected SearchResult local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return SearchResult
		 */
		public SearchResult get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(SearchResult param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					SearchResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				if (local_return == null) {
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}
					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					local_return.serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"),
							factory, xmlWriter);
				}
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : local_return);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SearchResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				SearchResponse object = new SearchResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"searchResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (SearchResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.set_return(null);
							reader.next();

							reader.next();
						} else {
							object.set_return(SearchResult.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class CheckoutResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2457073840230625539L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "checkoutResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CheckoutResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CheckoutResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				CheckoutResponse object = new CheckoutResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"checkoutResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CheckoutResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class ExtensionMapper {
		public static java.lang.Object getTypeObject(java.lang.String namespaceURI, java.lang.String typeName,
				javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "Content".equals(typeName)) {
				return Content.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com".equals(namespaceURI) && "Exception".equals(typeName)) {
				return Exception.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "DocumentInfo".equals(typeName)) {
				return DocumentInfo.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "VersionInfo".equals(typeName)) {
				return VersionInfo.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "SearchResult".equals(typeName)) {
				return SearchResult.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "ResultImpl".equals(typeName)) {
				return Result.Factory.parse(reader);
			}

			if ("http://ws.web.logicaldoc.com/xsd".equals(namespaceURI) && "FolderContent".equals(typeName)) {
				return FolderContent.Factory.parse(reader);
			}

			throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
		}
	}

	public static class CheckinResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7637924850687063604L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "checkinResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CheckinResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CheckinResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				CheckinResponse object = new CheckinResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"checkinResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CheckinResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Checkin implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3630567111591491841L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "checkin", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Filename
		 */
		protected java.lang.String localFilename;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localFilenameTracker = false;

		/**
		 * field for Description
		 */
		protected java.lang.String localDescription;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDescriptionTracker = false;

		/**
		 * field for Type
		 */
		protected java.lang.String localType;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localTypeTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getFilename() {
			return localFilename;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Filename
		 */
		public void setFilename(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localFilenameTracker = true;
			} else {
				localFilenameTracker = true;
			}

			this.localFilename = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDescription() {
			return localDescription;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Description
		 */
		public void setDescription(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localDescriptionTracker = true;
			} else {
				localDescriptionTracker = true;
			}

			this.localDescription = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getType() {
			return localType;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Type
		 */
		public void setType(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localTypeTracker = true;
			} else {
				localTypeTracker = true;
			}

			this.localType = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Checkin.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localFilenameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "filename", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "filename");
					}
				} else {
					xmlWriter.writeStartElement("filename");
				}

				if (localFilename == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localFilename);
				}

				xmlWriter.writeEndElement();
			}

			if (localDescriptionTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "description", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "description");
					}
				} else {
					xmlWriter.writeStartElement("description");
				}

				if (localDescription == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localDescription);
				}

				xmlWriter.writeEndElement();
			}

			if (localTypeTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "type", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "type");
					}
				} else {
					xmlWriter.writeStartElement("type");
				}

				if (localType == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localType);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localFilenameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "filename"));

				elementList.add((localFilename == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localFilename));
			}

			if (localDescriptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "description"));

				elementList.add((localDescription == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localDescription));
			}

			if (localTypeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "type"));

				elementList.add((localType == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localType));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Checkin parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Checkin object = new Checkin();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"checkin".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Checkin) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "filename").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setFilename(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "description").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setDescription(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "type").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadDocument implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5917014854157088788L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadDocument", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Version
		 */
		protected java.lang.String localVersion;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localVersionTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getVersion() {
			return localVersion;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Version
		 */
		public void setVersion(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localVersionTracker = true;
			} else {
				localVersionTracker = true;
			}

			this.localVersion = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadDocument.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localVersionTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "version", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "version");
					}
				} else {
					xmlWriter.writeStartElement("version");
				}

				if (localVersion == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localVersion);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localVersionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "version"));

				elementList.add((localVersion == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localVersion));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadDocument parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				DownloadDocument object = new DownloadDocument();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadDocument".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadDocument) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "version").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.setVersion(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class FolderContent implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5433723116066078326L;

		/**
		 * field for Document This was an Array!
		 */
		protected Content[] localDocument;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDocumentTracker = false;

		/**
		 * field for Folder This was an Array!
		 */
		protected Content[] localFolder;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localFolderTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Name
		 */
		protected java.lang.String localName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localNameTracker = false;

		/**
		 * field for ParentId
		 */
		protected int localParentId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentIdTracker = false;

		/**
		 * field for ParentName
		 */
		protected java.lang.String localParentName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentNameTracker = false;

		/**
		 * field for Writeable
		 */
		protected int localWriteable;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localWriteableTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * FolderContent Namespace URI = http://ws.web.logicaldoc.com/xsd
		 * Namespace Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return Content[]
		 */
		public Content[] getDocument() {
			return localDocument;
		}

		/**
		 * validate the array for Document
		 */
		protected void validateDocument(Content[] param) {
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Document
		 */
		public void setDocument(Content[] param) {
			validateDocument(param);

			if (param != null) {
				// update the setting tracker
				localDocumentTracker = true;
			} else {
				localDocumentTracker = true;
			}

			this.localDocument = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param Content
		 */
		public void addDocument(Content param) {
			if (localDocument == null) {
				localDocument = new Content[] {};
			}

			// update the setting tracker
			localDocumentTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localDocument);
			list.add(param);
			this.localDocument = (Content[]) list.toArray(new Content[list.size()]);
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return Content[]
		 */
		public Content[] getFolder() {
			return localFolder;
		}

		/**
		 * validate the array for Folder
		 */
		protected void validateFolder(Content[] param) {
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Folder
		 */
		public void setFolder(Content[] param) {
			validateFolder(param);

			if (param != null) {
				// update the setting tracker
				localFolderTracker = true;
			} else {
				localFolderTracker = true;
			}

			this.localFolder = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param Content
		 */
		public void addFolder(Content param) {
			if (localFolder == null) {
				localFolder = new Content[] {};
			}

			// update the setting tracker
			localFolderTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localFolder);
			list.add(param);
			this.localFolder = (Content[]) list.toArray(new Content[list.size()]);
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getName() {
			return localName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Name
		 */
		public void setName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localNameTracker = true;
			} else {
				localNameTracker = true;
			}

			this.localName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getParentId() {
			return localParentId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ParentId
		 */
		public void setParentId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localParentIdTracker = false;
			} else {
				localParentIdTracker = true;
			}

			this.localParentId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getParentName() {
			return localParentName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ParentName
		 */
		public void setParentName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localParentNameTracker = true;
			} else {
				localParentNameTracker = true;
			}

			this.localParentName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getWriteable() {
			return localWriteable;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Writeable
		 */
		public void setWriteable(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localWriteableTracker = false;
			} else {
				localWriteableTracker = true;
			}

			this.localWriteable = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					FolderContent.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localDocumentTracker) {
				if (localDocument != null) {
					for (int i = 0; i < localDocument.length; i++) {
						if (localDocument[i] != null) {
							localDocument[i].serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd",
									"document"), factory, xmlWriter);
						} else {
							// write null attribute
							java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "document", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);
								} else {
									xmlWriter.writeStartElement(namespace2, "document");
								}
							} else {
								xmlWriter.writeStartElement("document");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();
						}
					}
				} else {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "document", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "document");
						}
					} else {
						xmlWriter.writeStartElement("document");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				}
			}

			if (localFolderTracker) {
				if (localFolder != null) {
					for (int i = 0; i < localFolder.length; i++) {
						if (localFolder[i] != null) {
							localFolder[i].serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd",
									"folder"), factory, xmlWriter);
						} else {
							// write null attribute
							java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "folder", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);
								} else {
									xmlWriter.writeStartElement(namespace2, "folder");
								}
							} else {
								xmlWriter.writeStartElement("folder");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();
						}
					}
				} else {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "folder", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "folder");
						}
					} else {
						xmlWriter.writeStartElement("folder");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				}
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "name", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "name");
					}
				} else {
					xmlWriter.writeStartElement("name");
				}

				if (localName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localName);
				}

				xmlWriter.writeEndElement();
			}

			if (localParentIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parentId", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parentId");
					}
				} else {
					xmlWriter.writeStartElement("parentId");
				}

				if (localParentId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("parentId cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localParentId));
				}

				xmlWriter.writeEndElement();
			}

			if (localParentNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parentName", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parentName");
					}
				} else {
					xmlWriter.writeStartElement("parentName");
				}

				if (localParentName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localParentName);
				}

				xmlWriter.writeEndElement();
			}

			if (localWriteableTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "writeable", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "writeable");
					}
				} else {
					xmlWriter.writeStartElement("writeable");
				}

				if (localWriteable == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("writeable cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localWriteable));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localDocumentTracker) {
				if (localDocument != null) {
					for (int i = 0; i < localDocument.length; i++) {
						if (localDocument[i] != null) {
							elementList
									.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "document"));
							elementList.add(localDocument[i]);
						} else {
							elementList
									.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "document"));
							elementList.add(null);
						}
					}
				} else {
					elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "document"));
					elementList.add(localDocument);
				}
			}

			if (localFolderTracker) {
				if (localFolder != null) {
					for (int i = 0; i < localFolder.length; i++) {
						if (localFolder[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "folder"));
							elementList.add(localFolder[i]);
						} else {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "folder"));
							elementList.add(null);
						}
					}
				} else {
					elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "folder"));
					elementList.add(localFolder);
				}
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name"));

				elementList.add((localName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localName));
			}

			if (localParentIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentId"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localParentId));
			}

			if (localParentNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentName"));

				elementList.add((localParentName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localParentName));
			}

			if (localWriteableTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWriteable));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static FolderContent parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				FolderContent object = new FolderContent();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"FolderContent".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (FolderContent) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list1 = new java.util.ArrayList();

					java.util.ArrayList list2 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "document")
									.equals(reader.getName())) {
						// Process the array and step past its final element's
						// end.
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);
							reader.next();
						} else {
							list1.add(Content.Factory.parse(reader));
						}

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone1 = false;

						while (!loopDone1) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();

							// Step out of this element
							reader.next();

							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();

							if (reader.isEndElement()) {
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "document")
										.equals(reader.getName())) {
									nillableValue = reader.getAttributeValue(
											"http://www.w3.org/2001/XMLSchema-instance", "nil");

									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);
										reader.next();
									} else {
										list1.add(Content.Factory.parse(reader));
									}
								} else {
									loopDone1 = true;
								}
							}
						}

						// call the converter utility to convert and set the
						// array
						object.setDocument((Content[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
								Content.class, list1));
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "folder").equals(reader
									.getName())) {
						// Process the array and step past its final element's
						// end.
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list2.add(null);
							reader.next();
						} else {
							list2.add(Content.Factory.parse(reader));
						}

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone2 = false;

						while (!loopDone2) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();

							// Step out of this element
							reader.next();

							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();

							if (reader.isEndElement()) {
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone2 = true;
							} else {
								if (new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "folder")
										.equals(reader.getName())) {
									nillableValue = reader.getAttributeValue(
											"http://www.w3.org/2001/XMLSchema-instance", "nil");

									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list2.add(null);
										reader.next();
									} else {
										list2.add(Content.Factory.parse(reader));
									}
								} else {
									loopDone2 = true;
								}
							}
						}

						// call the converter utility to convert and set the
						// array
						object.setFolder((Content[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
								Content.class, list2));
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentId")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setParentId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setParentId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentName")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setParentName(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setWriteable(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setWriteable(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadDocumentInfo implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3195851804011688588L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadDocumentInfo", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadDocumentInfo.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadDocumentInfo parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DownloadDocumentInfo object = new DownloadDocumentInfo();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadDocumentInfo".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadDocumentInfo) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DeleteFolderResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7984463484729004838L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "deleteFolderResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DeleteFolderResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeleteFolderResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DeleteFolderResponse object = new DeleteFolderResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"deleteFolderResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DeleteFolderResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Checkout implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5029098027424058654L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "checkout", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Checkout.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Checkout parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Checkout object = new Checkout();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"checkout".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Checkout) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DocumentInfo implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4849485774571176340L;

		/**
		 * field for Author
		 */
		protected java.lang.String localAuthor;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localAuthorTracker = false;

		/**
		 * field for Coverage
		 */
		protected java.lang.String localCoverage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localCoverageTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Language
		 */
		protected java.lang.String localLanguage;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localLanguageTracker = false;

		/**
		 * field for Name
		 */
		protected java.lang.String localName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localNameTracker = false;

		/**
		 * field for ParentId
		 */
		protected int localParentId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentIdTracker = false;

		/**
		 * field for ParentName
		 */
		protected java.lang.String localParentName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentNameTracker = false;

		/**
		 * field for Source
		 */
		protected java.lang.String localSource;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSourceTracker = false;

		/**
		 * field for SourceDate
		 */
		protected java.lang.String localSourceDate;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSourceDateTracker = false;

		/**
		 * field for Type
		 */
		protected java.lang.String localType;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localTypeTracker = false;

		/**
		 * field for UploadDate
		 */
		protected java.lang.String localUploadDate;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUploadDateTracker = false;

		/**
		 * field for UploadUser
		 */
		protected java.lang.String localUploadUser;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUploadUserTracker = false;

		/**
		 * field for Version This was an Array!
		 */
		protected VersionInfo[] localVersion;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localVersionTracker = false;

		/**
		 * field for Writeable
		 */
		protected int localWriteable;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localWriteableTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * DocumentInfo Namespace URI = http://ws.web.logicaldoc.com/xsd Namespace
		 * Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getAuthor() {
			return localAuthor;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Author
		 */
		public void setAuthor(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localAuthorTracker = true;
			} else {
				localAuthorTracker = true;
			}

			this.localAuthor = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getCoverage() {
			return localCoverage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Coverage
		 */
		public void setCoverage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localCoverageTracker = true;
			} else {
				localCoverageTracker = true;
			}

			this.localCoverage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getLanguage() {
			return localLanguage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Language
		 */
		public void setLanguage(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localLanguageTracker = true;
			} else {
				localLanguageTracker = true;
			}

			this.localLanguage = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getName() {
			return localName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Name
		 */
		public void setName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localNameTracker = true;
			} else {
				localNameTracker = true;
			}

			this.localName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getParentId() {
			return localParentId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ParentId
		 */
		public void setParentId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localParentIdTracker = false;
			} else {
				localParentIdTracker = true;
			}

			this.localParentId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getParentName() {
			return localParentName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ParentName
		 */
		public void setParentName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localParentNameTracker = true;
			} else {
				localParentNameTracker = true;
			}

			this.localParentName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSource() {
			return localSource;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Source
		 */
		public void setSource(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSourceTracker = true;
			} else {
				localSourceTracker = true;
			}

			this.localSource = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSourceDate() {
			return localSourceDate;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param SourceDate
		 */
		public void setSourceDate(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSourceDateTracker = true;
			} else {
				localSourceDateTracker = true;
			}

			this.localSourceDate = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getType() {
			return localType;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Type
		 */
		public void setType(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localTypeTracker = true;
			} else {
				localTypeTracker = true;
			}

			this.localType = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUploadDate() {
			return localUploadDate;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param UploadDate
		 */
		public void setUploadDate(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUploadDateTracker = true;
			} else {
				localUploadDateTracker = true;
			}

			this.localUploadDate = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUploadUser() {
			return localUploadUser;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param UploadUser
		 */
		public void setUploadUser(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUploadUserTracker = true;
			} else {
				localUploadUserTracker = true;
			}

			this.localUploadUser = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return VersionInfo[]
		 */
		public VersionInfo[] getVersion() {
			return localVersion;
		}

		/**
		 * validate the array for Version
		 */
		protected void validateVersion(VersionInfo[] param) {
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Version
		 */
		public void setVersion(VersionInfo[] param) {
			validateVersion(param);

			if (param != null) {
				// update the setting tracker
				localVersionTracker = true;
			} else {
				localVersionTracker = true;
			}

			this.localVersion = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param VersionInfo
		 */
		public void addVersion(VersionInfo param) {
			if (localVersion == null) {
				localVersion = new VersionInfo[] {};
			}

			// update the setting tracker
			localVersionTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localVersion);
			list.add(param);
			this.localVersion = (VersionInfo[]) list.toArray(new VersionInfo[list.size()]);
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getWriteable() {
			return localWriteable;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Writeable
		 */
		public void setWriteable(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localWriteableTracker = false;
			} else {
				localWriteableTracker = true;
			}

			this.localWriteable = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DocumentInfo.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localAuthorTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "author", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "author");
					}
				} else {
					xmlWriter.writeStartElement("author");
				}

				if (localAuthor == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localAuthor);
				}

				xmlWriter.writeEndElement();
			}

			if (localCoverageTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "coverage", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "coverage");
					}
				} else {
					xmlWriter.writeStartElement("coverage");
				}

				if (localCoverage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localCoverage);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localLanguageTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "language", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "language");
					}
				} else {
					xmlWriter.writeStartElement("language");
				}

				if (localLanguage == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localLanguage);
				}

				xmlWriter.writeEndElement();
			}

			if (localNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "name", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "name");
					}
				} else {
					xmlWriter.writeStartElement("name");
				}

				if (localName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localName);
				}

				xmlWriter.writeEndElement();
			}

			if (localParentIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parentId", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parentId");
					}
				} else {
					xmlWriter.writeStartElement("parentId");
				}

				if (localParentId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("parentId cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localParentId));
				}

				xmlWriter.writeEndElement();
			}

			if (localParentNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parentName", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parentName");
					}
				} else {
					xmlWriter.writeStartElement("parentName");
				}

				if (localParentName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localParentName);
				}

				xmlWriter.writeEndElement();
			}

			if (localSourceTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "source", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "source");
					}
				} else {
					xmlWriter.writeStartElement("source");
				}

				if (localSource == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSource);
				}

				xmlWriter.writeEndElement();
			}

			if (localSourceDateTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "sourceDate", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "sourceDate");
					}
				} else {
					xmlWriter.writeStartElement("sourceDate");
				}

				if (localSourceDate == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSourceDate);
				}

				xmlWriter.writeEndElement();
			}

			if (localTypeTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "type", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "type");
					}
				} else {
					xmlWriter.writeStartElement("type");
				}

				if (localType == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localType);
				}

				xmlWriter.writeEndElement();
			}

			if (localUploadDateTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "uploadDate", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "uploadDate");
					}
				} else {
					xmlWriter.writeStartElement("uploadDate");
				}

				if (localUploadDate == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUploadDate);
				}

				xmlWriter.writeEndElement();
			}

			if (localUploadUserTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "uploadUser", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "uploadUser");
					}
				} else {
					xmlWriter.writeStartElement("uploadUser");
				}

				if (localUploadUser == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUploadUser);
				}

				xmlWriter.writeEndElement();
			}

			if (localVersionTracker) {
				if (localVersion != null) {
					for (int i = 0; i < localVersion.length; i++) {
						if (localVersion[i] != null) {
							localVersion[i].serialize(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd",
									"version"), factory, xmlWriter);
						} else {
							// write null attribute
							java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "version", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);
								} else {
									xmlWriter.writeStartElement(namespace2, "version");
								}
							} else {
								xmlWriter.writeStartElement("version");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();
						}
					}
				} else {
					// write null attribute
					java.lang.String namespace2 = "http://ws.web.logicaldoc.com/xsd";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "version", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);
						} else {
							xmlWriter.writeStartElement(namespace2, "version");
						}
					} else {
						xmlWriter.writeStartElement("version");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				}
			}

			if (localWriteableTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "writeable", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "writeable");
					}
				} else {
					xmlWriter.writeStartElement("writeable");
				}

				if (localWriteable == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("writeable cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localWriteable));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localAuthorTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "author"));

				elementList.add((localAuthor == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localAuthor));
			}

			if (localCoverageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "coverage"));

				elementList.add((localCoverage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localCoverage));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localLanguageTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "language"));

				elementList.add((localLanguage == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localLanguage));
			}

			if (localNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name"));

				elementList.add((localName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localName));
			}

			if (localParentIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentId"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localParentId));
			}

			if (localParentNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentName"));

				elementList.add((localParentName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localParentName));
			}

			if (localSourceTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "source"));

				elementList.add((localSource == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSource));
			}

			if (localSourceDateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "sourceDate"));

				elementList.add((localSourceDate == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSourceDate));
			}

			if (localTypeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "type"));

				elementList.add((localType == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localType));
			}

			if (localUploadDateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadDate"));

				elementList.add((localUploadDate == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUploadDate));
			}

			if (localUploadUserTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadUser"));

				elementList.add((localUploadUser == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUploadUser));
			}

			if (localVersionTracker) {
				if (localVersion != null) {
					for (int i = 0; i < localVersion.length; i++) {
						if (localVersion[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "version"));
							elementList.add(localVersion[i]);
						} else {
							elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "version"));
							elementList.add(null);
						}
					}
				} else {
					elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "version"));
					elementList.add(localVersion);
				}
			}

			if (localWriteableTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWriteable));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DocumentInfo parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				DocumentInfo object = new DocumentInfo();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"DocumentInfo".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DocumentInfo) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list13 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "author").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setAuthor(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "coverage")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setCoverage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "language")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setLanguage(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentId")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setParentId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setParentId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "parentName")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setParentName(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "source").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setSource(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "sourceDate")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setSourceDate(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "type").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadDate")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUploadDate(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadUser")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUploadUser(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "version").equals(reader
									.getName())) {
						// Process the array and step past its final element's
						// end.
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list13.add(null);
							reader.next();
						} else {
							list13.add(VersionInfo.Factory.parse(reader));
						}

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone13 = false;

						while (!loopDone13) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();

							// Step out of this element
							reader.next();

							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();

							if (reader.isEndElement()) {
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone13 = true;
							} else {
								if (new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "version")
										.equals(reader.getName())) {
									nillableValue = reader.getAttributeValue(
											"http://www.w3.org/2001/XMLSchema-instance", "nil");

									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list13.add(null);
										reader.next();
									} else {
										list13.add(VersionInfo.Factory.parse(reader));
									}
								} else {
									loopDone13 = true;
								}
							}
						}

						// call the converter utility to convert and set the
						// array
						object.setVersion((VersionInfo[]) org.apache.axis2.databinding.utils.ConverterUtil
								.convertToArray(VersionInfo.class, list13));
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "writeable")
									.equals(reader.getName())) {
						java.lang.String content = reader.getElementText();

						object.setWriteable(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setWriteable(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class VersionInfo implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 582656375379526876L;

		/**
		 * field for Date
		 */
		protected java.lang.String localDate;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDateTracker = false;

		/**
		 * field for Description
		 */
		protected java.lang.String localDescription;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDescriptionTracker = false;

		/**
		 * field for Id
		 */
		protected java.lang.String localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for UploadUser
		 */
		protected java.lang.String localUploadUser;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUploadUserTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * VersionInfo Namespace URI = http://ws.web.logicaldoc.com/xsd Namespace
		 * Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDate() {
			return localDate;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Date
		 */
		public void setDate(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localDateTracker = true;
			} else {
				localDateTracker = true;
			}

			this.localDate = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDescription() {
			return localDescription;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Description
		 */
		public void setDescription(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localDescriptionTracker = true;
			} else {
				localDescriptionTracker = true;
			}

			this.localDescription = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localIdTracker = true;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUploadUser() {
			return localUploadUser;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param UploadUser
		 */
		public void setUploadUser(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUploadUserTracker = true;
			} else {
				localUploadUserTracker = true;
			}

			this.localUploadUser = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					VersionInfo.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localDateTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "date", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "date");
					}
				} else {
					xmlWriter.writeStartElement("date");
				}

				if (localDate == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localDate);
				}

				xmlWriter.writeEndElement();
			}

			if (localDescriptionTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "description", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "description");
					}
				} else {
					xmlWriter.writeStartElement("description");
				}

				if (localDescription == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localDescription);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localId);
				}

				xmlWriter.writeEndElement();
			}

			if (localUploadUserTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "uploadUser", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "uploadUser");
					}
				} else {
					xmlWriter.writeStartElement("uploadUser");
				}

				if (localUploadUser == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUploadUser);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localDateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "date"));

				elementList.add((localDate == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localDate));
			}

			if (localDescriptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "description"));

				elementList.add((localDescription == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localDescription));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id"));

				elementList.add((localId == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localId));
			}

			if (localUploadUserTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadUser"));

				elementList.add((localUploadUser == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUploadUser));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static VersionInfo parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				VersionInfo object = new VersionInfo();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"VersionInfo".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (VersionInfo) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "date").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setDate(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "description")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setDescription(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "uploadUser")
									.equals(reader.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUploadUser(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DeleteFolder implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6175170069026968933L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "deleteFolder", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Folder
		 */
		protected int localFolder;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localFolderTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getFolder() {
			return localFolder;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Folder
		 */
		public void setFolder(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localFolderTracker = false;
			} else {
				localFolderTracker = true;
			}

			this.localFolder = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DeleteFolder.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localFolderTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "folder", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "folder");
					}
				} else {
					xmlWriter.writeStartElement("folder");
				}

				if (localFolder == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("folder cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localFolder));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localFolderTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "folder"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFolder));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeleteFolder parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				DeleteFolder object = new DeleteFolder();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"deleteFolder".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DeleteFolder) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "folder").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setFolder(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setFolder(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DeleteDocumentResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2855849036940414441L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "deleteDocumentResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DeleteDocumentResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeleteDocumentResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DeleteDocumentResponse object = new DeleteDocumentResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"deleteDocumentResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DeleteDocumentResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadDocumentResponse implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8511794473399212132L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadDocumentResponse", "ns1");

		/**
		 * field for _return
		 */
		protected java.lang.String local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String get_return() {
			return local_return;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;
			}

			this.local_return = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadDocumentResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (local_returnTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "return", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "return");
					}
				} else {
					xmlWriter.writeStartElement("return");
				}

				if (local_return == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(local_return);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (local_returnTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return"));

				elementList.add((local_return == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(local_return));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadDocumentResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DownloadDocumentResponse object = new DownloadDocumentResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadDocumentResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadDocumentResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "return").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.set_return(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class DownloadFolderContent implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6425207802413822970L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "downloadFolderContent", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Folder
		 */
		protected int localFolder;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localFolderTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getFolder() {
			return localFolder;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Folder
		 */
		public void setFolder(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localFolderTracker = false;
			} else {
				localFolderTracker = true;
			}

			this.localFolder = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DownloadFolderContent.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localFolderTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "folder", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "folder");
					}
				} else {
					xmlWriter.writeStartElement("folder");
				}

				if (localFolder == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("folder cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localFolder));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localFolderTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "folder"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFolder));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DownloadFolderContent parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				DownloadFolderContent object = new DownloadFolderContent();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"downloadFolderContent".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (DownloadFolderContent) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "folder").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setFolder(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setFolder(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class CreateFolder implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4796251361636869589L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://ws.web.logicaldoc.com", "createFolder", "ns1");

		/**
		 * field for Username
		 */
		protected java.lang.String localUsername;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localUsernameTracker = false;

		/**
		 * field for Password
		 */
		protected java.lang.String localPassword;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPasswordTracker = false;

		/**
		 * field for Name
		 */
		protected java.lang.String localName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localNameTracker = false;

		/**
		 * field for Parent
		 */
		protected int localParent;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localParentTracker = false;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com")) {
				return "ns1";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getUsername() {
			return localUsername;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Username
		 */
		public void setUsername(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localUsernameTracker = true;
			} else {
				localUsernameTracker = true;
			}

			this.localUsername = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getPassword() {
			return localPassword;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Password
		 */
		public void setPassword(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localPasswordTracker = true;
			} else {
				localPasswordTracker = true;
			}

			this.localPassword = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getName() {
			return localName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Name
		 */
		public void setName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localNameTracker = true;
			} else {
				localNameTracker = true;
			}

			this.localName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getParent() {
			return localParent;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Parent
		 */
		public void setParent(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localParentTracker = false;
			} else {
				localParentTracker = true;
			}

			this.localParent = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					CreateFolder.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localUsernameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "username", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "username");
					}
				} else {
					xmlWriter.writeStartElement("username");
				}

				if (localUsername == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localUsername);
				}

				xmlWriter.writeEndElement();
			}

			if (localPasswordTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "password", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "password");
					}
				} else {
					xmlWriter.writeStartElement("password");
				}

				if (localPassword == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localPassword);
				}

				xmlWriter.writeEndElement();
			}

			if (localNameTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "name", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "name");
					}
				} else {
					xmlWriter.writeStartElement("name");
				}

				if (localName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localName);
				}

				xmlWriter.writeEndElement();
			}

			if (localParentTracker) {
				namespace = "http://ws.web.logicaldoc.com";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "parent", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "parent");
					}
				} else {
					xmlWriter.writeStartElement("parent");
				}

				if (localParent == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("parent cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localParent));
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localUsernameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username"));

				elementList.add((localUsername == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localUsername));
			}

			if (localPasswordTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password"));

				elementList.add((localPassword == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localPassword));
			}

			if (localNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "name"));

				elementList.add((localName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localName));
			}

			if (localParentTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "parent"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localParent));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static CreateFolder parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				CreateFolder object = new CreateFolder();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"createFolder".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (CreateFolder) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "username").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setUsername(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "password").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setPassword(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "name").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com", "parent").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setParent(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setParent(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}

	public static class Result implements org.apache.axis2.databinding.ADBBean {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5295463651909246001L;

		/**
		 * field for Date
		 */
		protected java.lang.String localDate;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDateTracker = false;

		/**
		 * field for Id
		 */
		protected int localId;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localIdTracker = false;

		/**
		 * field for Length
		 */
		protected int localLength;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localLengthTracker = false;

		/**
		 * field for Name
		 */
		protected java.lang.String localName;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localNameTracker = false;

		/**
		 * field for Summary
		 */
		protected java.lang.String localSummary;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSummaryTracker = false;

		/**
		 * field for Score
		 */
		protected int localScore;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localScoreTracker = false;

		/**
		 * field for Type
		 */
		protected java.lang.String localType;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localTypeTracker = false;

		/*
		 * This type was generated from the piece of schema that had name =
		 * ResultImpl Namespace URI = http://ws.web.logicaldoc.com/xsd Namespace
		 * Prefix = ns2
		 */
		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://ws.web.logicaldoc.com/xsd")) {
				return "ns2";
			}

			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDate() {
			return localDate;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Date
		 */
		public void setDate(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localDateTracker = true;
			} else {
				localDateTracker = true;
			}

			this.localDate = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getId() {
			return localId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Id
		 */
		public void setId(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localIdTracker = false;
			} else {
				localIdTracker = true;
			}

			this.localId = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getLength() {
			return localLength;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Length
		 */
		public void setLength(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localLengthTracker = false;
			} else {
				localLengthTracker = true;
			}

			this.localLength = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getName() {
			return localName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Name
		 */
		public void setName(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localNameTracker = true;
			} else {
				localNameTracker = true;
			}

			this.localName = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSummary() {
			return localSummary;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public int getScore() {
			return localScore;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Summary
		 */
		public void setSummary(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localSummaryTracker = true;
			} else {
				localSummaryTracker = true;
			}

			this.localSummary = param;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Score
		 */
		public void setScore(int param) {
			// setting primitive attribute tracker to true
			if (param == java.lang.Integer.MIN_VALUE) {
				localScoreTracker = false;
			} else {
				localScoreTracker = true;
			}

			this.localScore = param;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getType() {
			return localType;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Type
		 */
		public void setType(java.lang.String param) {
			if (param != null) {
				// update the setting tracker
				localTypeTracker = true;
			} else {
				localTypeTracker = true;
			}

			this.localType = param;
		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
						.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}

			return isReaderMTOMAware;
		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {
			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {
				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Result.this.serialize(parentQName, factory, xmlWriter);
				}
			};

			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if (namespace != null) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (localDateTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "date", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "date");
					}
				} else {
					xmlWriter.writeStartElement("date");
				}

				if (localDate == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localDate);
				}

				xmlWriter.writeEndElement();
			}

			if (localIdTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "id", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "id");
					}
				} else {
					xmlWriter.writeStartElement("id");
				}

				if (localId == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("id cannot be null!!");
				} else {
					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
				}

				xmlWriter.writeEndElement();
			}

			if (localLengthTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "length", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "length");
					}
				} else {
					xmlWriter.writeStartElement("length");
				}

				if (localLength == java.lang.Integer.MIN_VALUE) {
					throw new org.apache.axis2.databinding.ADBException("length cannot be null!!");
				} else {
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localLength));
				}

				xmlWriter.writeEndElement();
			}

			if (localNameTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "name", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "name");
					}
				} else {
					xmlWriter.writeStartElement("name");
				}

				if (localName == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localName);
				}

				xmlWriter.writeEndElement();
			}

			if (localSummaryTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "summary", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "summary");
					}
				} else {
					xmlWriter.writeStartElement("summary");
				}

				if (localSummary == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localSummary);
				}

				xmlWriter.writeEndElement();
			}

			if (localTypeTracker) {
				namespace = "http://ws.web.logicaldoc.com/xsd";

				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "type", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);
					} else {
						xmlWriter.writeStartElement(namespace, "type");
					}
				} else {
					xmlWriter.writeStartElement("type");
				}

				if (localType == null) {
					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
				} else {
					xmlWriter.writeCharacters(localType);
				}

				xmlWriter.writeEndElement();
			}

			xmlWriter.writeEndElement();
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

			if (attributePrefix == null) {
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}

			java.lang.String attributeValue;

			if (attributePrefix.trim().length() > 0) {
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			} else {
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals("")) {
				xmlWriter.writeAttribute(attName, attributeValue);
			} else {
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}

		/**
		 * method to handle Qnames
		 */
		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String namespaceURI = qname.getNamespaceURI();

			if (namespaceURI != null) {
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

				if (prefix == null) {
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0) {
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				} else {
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
			} else {
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (qnames != null) {
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++) {
					if (i > 0) {
						stringToWrite.append(" ");
					}

					namespaceURI = qnames[i].getNamespaceURI();

					if (namespaceURI != null) {
						prefix = xmlWriter.getPrefix(namespaceURI);

						if ((prefix == null) || (prefix.length() == 0)) {
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0) {
							stringToWrite.append(prefix).append(":").append(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						} else {
							stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
						}
					} else {
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qnames[i]));
					}
				}

				xmlWriter.writeCharacters(stringToWrite.toString());
			}
		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {
			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localDateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "date"));

				elementList.add((localDate == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localDate));
			}

			if (localIdTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
			}

			if (localLengthTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "length"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLength));
			}

			if (localNameTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name"));

				elementList.add((localName == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localName));
			}

			if (localScoreTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "score"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localScore));
			}

			if (localSummaryTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "summary"));

				elementList.add((localSummary == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localSummary));
			}

			if (localTypeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "type"));

				elementList.add((localType == null) ? null : org.apache.axis2.databinding.utils.ConverterUtil
						.convertToString(localType));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());
		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {
			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static Result parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Result object = new Result();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";

				try {
					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");

						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;

							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}

							nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"ResultImpl".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);

								return (Result) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}
						}
					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "date").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setDate(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "id").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setId(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "length").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setLength(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setLength(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "name").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "score").equals(reader
									.getName())) {
						java.lang.String content = reader.getElementText();

						object.setScore(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();
					} // End of if for expected property start element

					else {
						object.setScore(java.lang.Integer.MIN_VALUE);
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "summary").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object
									.setSummary(org.apache.axis2.databinding.utils.ConverterUtil
											.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://ws.web.logicaldoc.com/xsd", "type").equals(reader
									.getName())) {
						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");

						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {
							java.lang.String content = reader.getElementText();

							object.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
						} else {
							reader.getElementText(); // throw away text nodes
														// if any.
						}

						reader.next();
					} // End of if for expected property start element

					else {
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()) {
						// A start element we are not expecting indicates a
						// trailing invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
								+ reader.getLocalName());
					}
				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}
		} // end of factory class

		public void serialize(QName arg0, OMFactory arg1, MTOMAwareXMLStreamWriter arg2, boolean arg3)
				throws XMLStreamException, ADBException {
			// TODO Auto-generated method stub

		}
	}
}
