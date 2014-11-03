package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;

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
	 * Extracts the certificate subjects names from an uploaded .pem certificate
	 * or .p7m file .
	 * 
	 * @param sid The session identifier
	 * @param docId Id of signed document to verify (optional)
	 * @param fileVersion The file version of the document to verify (optional)
	 * @return Subjects names array.
	 * @throws ServerException
	 */
	public String[] extractSubjectSignatures(String sid, Long docId, String fileVersion) throws ServerException;

	/**
	 * Stores the user's certificate file associated to the given signer name.
	 * 
	 * @param sid The session identifier
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws ServerException
	 */
	public String storeSignature(String sid) throws ServerException;

	/**
	 * Signs the given documents
	 * 
	 * @param sid The current session
	 * @param docIds The documents to be signed
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws ServerException
	 */
	public String signDocuments(String sid, long[] docIds) throws ServerException;

	/**
	 * Verifies the user signature file, checks if the uploaded file's digest
	 * and the document's file digest are equals, then signs the document.
	 * 
	 * @param sid The session identifier
	 * @param docId Identifier of the document to sign
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws ServerException
	 */
	public String storeSignedDocument(String sid, long docId) throws ServerException;

	/**
	 * Stores the private key of the given user
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is saving the signature
	 * @param keyPassword The password to open the key (it the user uploads an
	 *        encrypted private key)
	 * @return 'ok' if no errors occurred, otherwise returns the error message.
	 * @throws ServerException
	 */
	public String storePrivateKey(String sid, String keyPassword) throws ServerException;

	/**
	 * Drop the signature certificate associated to the given user. The private
	 * key is dropped also.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user for which will be reset the
	 *        signature
	 * @throws ServerException
	 */
	public boolean resetSignature(String sid, long userId) throws ServerException;

	/**
	 * Drop the private key associated to the given user. The private key is
	 * dropped also.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user for which will be reset the private
	 *        key
	 * @throws ServerException
	 */
	public boolean resetPrivateKey(String sid, long userId) throws ServerException;

	/**
	 * Extracts the key digest of the uploaded .pem file.
	 * 
	 * @param sid The session identifier
	 * @param userid Identifier of the user that is saving the signature or that
	 *        wants to retrieve the signers of a signed document to verify it.
	 * @return The digest of the uploaded key
	 * @throws ServerException
	 */
	public String extractKeyDigest(String sid) throws ServerException;
}
