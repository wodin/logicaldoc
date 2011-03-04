package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;

/**
 * The client side stub for the Sign Service. This service gives all needed
 * methods to handle documents signature.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
@RemoteServiceRelativePath("sign")
public interface SignService extends RemoteService {
	/**
	 * Extracts the certificate subjects names from an uploaded .p7m file or
	 * .cer file.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is saving the signature
	 * @return Subjects names array.
	 * @throws InvalidSessionException
	 */
	public String[] extractSubjectSignatures(String sid, long userId) throws InvalidSessionException;

	/**
	 * Stores on the user folder the certificate file associated to the given
	 * signer name.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is saving the signature
	 * @param signerName The name of the certificate signer
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws InvalidSessionException
	 */
	public String storeSignature(String sid, long userId, String signerName) throws InvalidSessionException;

	/**
	 * Verifies the user signature file, checks if the uploaded file's digest
	 * and the document's file digest are equals, then signs the document.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is performing the signature
	 * @param docId Identifier of the document to sign
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws InvalidSessionException
	 */
	public String signDocument(String sid, long userId, long docId) throws InvalidSessionException;
}
