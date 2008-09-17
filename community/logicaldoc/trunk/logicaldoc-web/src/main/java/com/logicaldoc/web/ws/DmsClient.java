package com.logicaldoc.web.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.web.ws.DmsStub.CheckinResponse;
import com.logicaldoc.web.ws.DmsStub.CheckoutResponse;
import com.logicaldoc.web.ws.DmsStub.Content;
import com.logicaldoc.web.ws.DmsStub.CreateDocumentResponse;
import com.logicaldoc.web.ws.DmsStub.CreateFolderResponse;
import com.logicaldoc.web.ws.DmsStub.DeleteDocumentResponse;
import com.logicaldoc.web.ws.DmsStub.DeleteFolderResponse;
import com.logicaldoc.web.ws.DmsStub.DocumentInfo;
import com.logicaldoc.web.ws.DmsStub.DownloadDocumentInfoResponse;
import com.logicaldoc.web.ws.DmsStub.DownloadDocumentResponse;
import com.logicaldoc.web.ws.DmsStub.DownloadFolderContentResponse;
import com.logicaldoc.web.ws.DmsStub.FolderContent;
import com.logicaldoc.web.ws.DmsStub.Result;
import com.logicaldoc.web.ws.DmsStub.Search;
import com.logicaldoc.web.ws.DmsStub.SearchResponse;
import com.logicaldoc.web.ws.DmsStub.SearchResult;
import com.logicaldoc.web.ws.DmsStub.VersionInfo;

/**
 * Web Service client. It provides a main() method so that it can acts as a
 * standalone application.
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class DmsClient {
	protected static Log log = LogFactory.getLog(DmsClient.class);

	private DmsStubWA stub;

	private String username;

	private String password;

	public static void main(String[] args) throws Exception {
		CommandLineOptionParser optionsParser = new CommandLineOptionParser(args);

		Map optionsMap = optionsParser.getAllOptions();
		CommandLineOption endpointOption = (CommandLineOption) optionsMap.get("endpoint");
		CommandLineOption usernameOption = (CommandLineOption) optionsMap.get("username");
		CommandLineOption passwordOption = (CommandLineOption) optionsMap.get("password");
		CommandLineOption operationOption = (CommandLineOption) optionsMap.get("operation");

		if (endpointOption == null || usernameOption == null || passwordOption == null || operationOption == null) {
			printUsage();
			System.out.println("Invalid Parameters.");
			return;
		}

		DmsClient client = new DmsClient(endpointOption.getOptionValue(), usernameOption.getOptionValue(),
				passwordOption.getOptionValue());
		if ("downloadDocument".equals(operationOption.getOptionValue())) {
			CommandLineOption idOption = (CommandLineOption) optionsMap.get("id");
			CommandLineOption versionOption = (CommandLineOption) optionsMap.get("version");
			String version = "";
			if (versionOption != null)
				version = versionOption.getOptionValue();
			CommandLineOption fileOption = (CommandLineOption) optionsMap.get("file");

			if (idOption == null || fileOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.downloadDocument(Integer.parseInt(idOption.getOptionValue()), version,
					new FileOutputStream(fileOption.getOptionValue())));
		} else if ("createFolder".equals(operationOption.getOptionValue())) {
			CommandLineOption parentOption = (CommandLineOption) optionsMap.get("parent");
			CommandLineOption nameOption = (CommandLineOption) optionsMap.get("name");

			if (parentOption == null || nameOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.createFolder(nameOption.getOptionValue(), Integer.parseInt(parentOption
					.getOptionValue())));
		} else if ("deleteFolder".equals(operationOption.getOptionValue())) {
			CommandLineOption folderOption = (CommandLineOption) optionsMap.get("folder");

			if (folderOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.deleteFolder(Integer.parseInt(folderOption.getOptionValue())));
		} else if ("downloadFolderContent".equals(operationOption.getOptionValue())) {
			CommandLineOption folderOption = (CommandLineOption) optionsMap.get("folder");

			if (folderOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			FolderContent folderContent = client.downloadFolderContent(Integer.parseInt(folderOption.getOptionValue()));
			System.out.println("Folder: " + folderContent.getId() + " - " + folderContent.getName() + "   "
					+ (folderContent.getWriteable() > 0 ? "writeable" : ""));
			System.out.println("Parent: " + folderContent.getParentId() + " - " + folderContent.getParentName());

			System.out.println("\nSub-Folders:");
			for (int i = 0; i < folderContent.getFolder().length; i++) {
				Content content = folderContent.getFolder()[i];
				System.out.println(content.getId() + " - " + content.getName() + "   "
						+ (content.getWriteable() > 0 ? "writeable" : ""));
			}

			System.out.println("\nDocuments:");
			for (int i = 0; i < folderContent.getDocument().length; i++) {
				Content content = folderContent.getDocument()[i];
				System.out.println(content.getId() + " - " + content.getName() + "   "
						+ (content.getWriteable() > 0 ? "writeable" : ""));
			}
		} else if ("downloadDocumentInfo".equals(operationOption.getOptionValue())) {
			CommandLineOption idOption = (CommandLineOption) optionsMap.get("id");

			if (idOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			DocumentInfo info = client.downloadDocumentInfo(Integer.parseInt(idOption.getOptionValue()));
			System.out.println("Document: " + info.getId() + " - " + info.getName() + "   "
					+ (info.getWriteable() > 0 ? "writeable" : ""));
			System.out.println("Parent: " + info.getParentId() + " - " + info.getParentName());
			System.out.println("Author: " + info.getAuthor());
			System.out.println("Coverage: " + info.getCoverage());
			System.out.println("Language: " + info.getLanguage());
			System.out.println("Source: " + info.getSource());
			System.out.println("Source date: " + info.getSourceDate());
			System.out.println("Type: " + info.getType());
			System.out.println("Upload date: " + info.getUploadDate());
			System.out.println("Upload user: " + info.getUploadUser());

			System.out.println("\nVersions:");
			for (int i = 0; i < info.getVersion().length; i++) {
				VersionInfo content = info.getVersion()[i];
				System.out.println(content.getId() + " - " + content.getUploadUser() + " -  "
						+ content.getDescription());
			}
		} else if ("deleteDocument".equals(operationOption.getOptionValue())) {
			CommandLineOption idOption = (CommandLineOption) optionsMap.get("id");

			if (idOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.deleteDocument(Integer.parseInt(idOption.getOptionValue())));
		} else if ("createDocument".equals(operationOption.getOptionValue())) {
			CommandLineOption parentOption = (CommandLineOption) optionsMap.get("parent");
			CommandLineOption docNameOption = (CommandLineOption) optionsMap.get("docName");
			CommandLineOption sourceOption = (CommandLineOption) optionsMap.get("source");
			CommandLineOption sourceDateOption = (CommandLineOption) optionsMap.get("sourceDate");
			CommandLineOption sourceTypeOption = (CommandLineOption) optionsMap.get("sourceType");
			CommandLineOption authorOption = (CommandLineOption) optionsMap.get("author");
			CommandLineOption coverageOption = (CommandLineOption) optionsMap.get("coverage");
			CommandLineOption languageOption = (CommandLineOption) optionsMap.get("language");
			CommandLineOption keywordsOption = (CommandLineOption) optionsMap.get("keywords");
			CommandLineOption versionDescOption = (CommandLineOption) optionsMap.get("versionDesc");
			CommandLineOption filenameOption = (CommandLineOption) optionsMap.get("filename");
			CommandLineOption groupsOption = (CommandLineOption) optionsMap.get("groups");
			CommandLineOption fileOption = (CommandLineOption) optionsMap.get("file");

			if (parentOption == null || docNameOption == null || languageOption == null || filenameOption == null
					|| fileOption == null || groupsOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.createDocument(Integer.parseInt(parentOption.getOptionValue()), docNameOption
					.getOptionValue(), sourceOption != null ? sourceOption.getOptionValue() : null,
					sourceDateOption != null ? sourceDateOption.getOptionValue() : null,
					authorOption != null ? authorOption.getOptionValue() : null,
					sourceTypeOption != null ? sourceTypeOption.getOptionValue() : null,
					coverageOption != null ? coverageOption.getOptionValue() : null, languageOption.getOptionValue(),
					keywordsOption != null ? keywordsOption.getOptionValue() : null,
					versionDescOption != null ? versionDescOption.getOptionValue() : null, filenameOption
							.getOptionValue(), groupsOption.getOptionValue(), new File(fileOption.getOptionValue())));
		} else if ("search".equals(operationOption.getOptionValue())) {
			CommandLineOption queryOption = (CommandLineOption) optionsMap.get("query");
			CommandLineOption queryLanguageOption = (CommandLineOption) optionsMap.get("queryLanguage");
			CommandLineOption indexLanguageOption = (CommandLineOption) optionsMap.get("indexLanguage");
			CommandLineOption maxHitsOption = (CommandLineOption) optionsMap.get("maxHits");

			if (queryOption == null || queryLanguageOption == null || maxHitsOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			try {
				Integer.parseInt(maxHitsOption.getOptionValue());
			} catch (Throwable e) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			String indexLanguage = null;
			if (indexLanguageOption != null)
				indexLanguage = indexLanguageOption.getOptionValue();

			SearchResult response = client.search(queryOption.getOptionValue(), queryLanguageOption.getOptionValue(),
					indexLanguage, Integer.parseInt(maxHitsOption.getOptionValue()));
			System.out.println("Query returned " + response.getTotalHits() + " hits in " + response.getTime() + "ms");
			for (int i = 0; i < response.getTotalHits(); i++) {
				Result result = response.getResult()[i];
				System.out.println(result.getId() + " - " + result.getName());
				System.out.println(result.getSummary() + "\n");
			}
		} else if ("checkout".equals(operationOption.getOptionValue())) {
			CommandLineOption idOption = (CommandLineOption) optionsMap.get("id");

			if (idOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			System.out.println(client.checkout(Integer.parseInt(idOption.getOptionValue())));
		} else if ("checkin".equals(operationOption.getOptionValue())) {
			CommandLineOption idOption = (CommandLineOption) optionsMap.get("id");
			CommandLineOption descriptionOption = (CommandLineOption) optionsMap.get("description");
			CommandLineOption typeOption = (CommandLineOption) optionsMap.get("type");
			CommandLineOption filenameOption = (CommandLineOption) optionsMap.get("filename");
			CommandLineOption fileOption = (CommandLineOption) optionsMap.get("file");

			if (idOption == null || fileOption == null) {
				printUsage();
				System.out.println("Invalid Parameters.");
				return;
			}

			File file = new File(fileOption.getOptionValue());

			System.out.println(client.checkin(Integer.parseInt(idOption.getOptionValue()),
					descriptionOption != null ? descriptionOption.getOptionValue() : "",
					typeOption != null ? typeOption.getOptionValue() : "", filenameOption != null ? filenameOption
							.getOptionValue() : file.getName(), file));
		}
	}

	private static void printUsage() {
		System.out.println("Usage is:");
	}

	public DmsClient(String endpoint, String username, String password) throws IOException {
		stub = new DmsStubWA(endpoint);

		// Enable MTOM and SwA in the client side
		stub._getServiceClient().getOptions().setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		stub._getServiceClient().getOptions().setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
		// Increase the time out when sending large attachments
		stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(10000);

		this.username = username;
		this.password = password;
	}

	public String downloadDocument(int id, String version, OutputStream out) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStubWA.DownloadDocument downloadDocument = new DmsStubWA.DownloadDocument();
		downloadDocument.setId(id);
		downloadDocument.setUsername(username);
		downloadDocument.setPassword(password);
		downloadDocument.setVersion(version);

		// Execute remote invocation
		DownloadDocumentResponse response = stub.downloadDocument(downloadDocument);

		// Retrieve the attachment and save it
		MessageContext messageContext = stub._getServiceClient().getLastOperationContext().getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE);
		DataHandler dataHandler = messageContext.getAttachment("document");
		dataHandler.writeTo(out);
		out.flush();

		return response.get_return();
	}

	public String createFolder(String name, int parent) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStub.CreateFolder createFolder = new DmsStub.CreateFolder();
		createFolder.setParent(parent);
		createFolder.setUsername(username);
		createFolder.setPassword(password);
		createFolder.setName(name);

		// Execute remote invocation
		CreateFolderResponse response = stub.createFolder(createFolder);

		return response.get_return();
	}

	public String deleteFolder(int folder) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStub.DeleteFolder deleteFolder = new DmsStub.DeleteFolder();
		deleteFolder.setUsername(username);
		deleteFolder.setPassword(password);
		deleteFolder.setFolder(folder);

		// Execute remote invocation
		DeleteFolderResponse response = stub.deleteFolder(deleteFolder);

		return response.get_return();
	}

	public String deleteDocument(int id) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStub.DeleteDocument deleteDocument = new DmsStub.DeleteDocument();
		deleteDocument.setUsername(username);
		deleteDocument.setPassword(password);
		deleteDocument.setId(id);

		// Execute remote invocation
		DeleteDocumentResponse response = stub.deleteDocument(deleteDocument);

		return response.get_return();
	}

	public String checkout(int id) throws ExceptionException0, IOException {

		// Prepare the request
		DmsStub.Checkout checkout = new DmsStub.Checkout();
		checkout.setUsername(username);
		checkout.setPassword(password);
		checkout.setId(id);

		// Execute remote invocation
		CheckoutResponse response = stub.checkout(checkout);

		return response.get_return();
	}

	public String checkin(int id, String description, String type, String filename, File file)
			throws ExceptionException0, IOException {

		// Prepare the request
		DmsStub.Checkin checkin = new DmsStub.Checkin();
		checkin.setUsername(username);
		checkin.setPassword(password);
		checkin.setId(id);
		checkin.setDescription(description);
		checkin.setType(type);
		checkin.setFilename(filename);

		// Execute remote invocation
		CheckinResponse response = stub.checkin(checkin, file);

		return response.get_return();
	}

	public FolderContent downloadFolderContent(int folder) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStub.DownloadFolderContent request = new DmsStub.DownloadFolderContent();
		request.setUsername(username);
		request.setPassword(password);
		request.setFolder(folder);

		// Execute remote invocation
		DownloadFolderContentResponse response = stub.downloadFolderContent(request);

		return response.get_return();
	}

	public DocumentInfo downloadDocumentInfo(int id) throws ExceptionException0, IOException {
		// Prepare the request
		DmsStub.DownloadDocumentInfo request = new DmsStub.DownloadDocumentInfo();
		request.setUsername(username);
		request.setPassword(password);
		request.setId(id);

		// Execute remote invocation
		DownloadDocumentInfoResponse response = stub.downloadDocumentInfo(request);
		return response.get_return();
	}

	public String createDocument(int parent, String docName, String source, String sourceDate, String author,
			String sourceType, String coverage, String language, String keywords, String versionDesc, String filename,
			String groups, File file) throws RemoteException, ExceptionException0 {

		// Prepare the request
		DmsStub.CreateDocument request = new DmsStub.CreateDocument();
		request.setUsername(username);
		request.setPassword(password);
		request.setParent(parent);
		request.setDocName(docName);
		request.setSource(source);
		request.setSourceDate(sourceDate);
		request.setAuthor(author);
		request.setSourceType(sourceType);
		request.setCoverage(coverage);
		request.setLanguage(language);
		request.setKeywords(keywords);
		request.setVersionDesc(versionDesc);
		request.setFilename(filename);
		request.setGroups(groups);

		// Execute remote invocation
		CreateDocumentResponse response = stub.createDocument(request, file);
		return response.get_return();

	}

	public SearchResult search(String query, String queryLanguage, String indexLanguage, int maxHits)
			throws RemoteException, ExceptionException0 {

		// Prepare the request
		Search request = new Search();
		request.setUsername(username);
		request.setPassword(password);
		request.setQuery(query);
		request.setQueryLanguage(queryLanguage);
		request.setIndexLanguage(indexLanguage);
		request.setMaxHits(maxHits);

		// Execute remote invocation
		SearchResponse response = stub.search(request);
		return response.get_return();
	}
}