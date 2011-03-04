package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.frontend.client.services.SignService;

/**
 * Mock implementation of the SignService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class MockSignServiceImpl extends RemoteServiceServlet implements SignService {

	private static final long serialVersionUID = 1L;

	@Override
	public String[] extractSubjectSignatures(String sid, long userId) throws InvalidSessionException {
		return new String[] { "subject1", "subject2" };
	}

	@Override
	public String storeSignature(String sid, long userId, String signerName) throws InvalidSessionException {
		return "ok";
	}

	@Override
	public String signDocument(String sid, long userId, long docId) throws InvalidSessionException {
		return "ok";
	}
}
