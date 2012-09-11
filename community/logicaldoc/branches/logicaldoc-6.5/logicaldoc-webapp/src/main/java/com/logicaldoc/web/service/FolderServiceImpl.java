package com.logicaldoc.web.service;

import java.security.AccessControlException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.SystemMessageDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the FolderService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class FolderServiceImpl extends RemoteServiceServlet implements FolderService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(FolderServiceImpl.class);

	@Override
	public void applyRights(String sid, GUIFolder folder, boolean subtree) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);

		try {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			saveRules(sid, fdao.findById(folder.getId()), session.getUserId(), folder.getRights());
			if (subtree) {
				/*
				 * Just apply the current security settings to the whole subtree
				 */
				FolderHistory history = new FolderHistory();
				history.setUser(SessionUtil.getSessionUser(sid));
				history.setEvent(FolderHistory.EVENT_FOLDER_PERMISSION);
				history.setSessionId(sid);

				fdao.applyRithtToTree(folder.getId(), history);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void applyMetadata(String sid, long parentId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(SessionUtil.getSessionUser(sid));
			transaction.setSessionId(sid);
			fdao.applyMetadataToTree(parentId, transaction);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void delete(final String sid, final long folderId) throws InvalidSessionException {
		final User user = SessionUtil.getSessionUser(sid);

		/*
		 * Execute the deletion in another thread
		 */
		Thread deleteThread = new Thread() {
			@Override
			public void run() {
				FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				Folder folder = dao.findById(folderId);
				try {
					// Add a folder history entry
					FolderHistory transaction = new FolderHistory();
					transaction.setUser(SessionUtil.getSessionUser(sid));
					transaction.setEvent(FolderHistory.EVENT_FOLDER_DELETED);
					transaction.setSessionId(sid);
					dao.deleteTree(folderId, transaction);

					SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
					Date now = new Date();
					Recipient recipient = new Recipient();
					recipient.setName(user.getUserName());
					recipient.setAddress(user.getUserName());
					recipient.setType(Recipient.TYPE_SYSTEM);
					recipient.setMode("message");
					Set<Recipient> recipients = new HashSet<Recipient>();
					recipients.add(recipient);
					SystemMessage sysmess = new SystemMessage();
					sysmess.setAuthor("SYSTEM");
					sysmess.setRecipients(recipients);
					ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", user.getLocale());
					sysmess.setSubject(bundle.getString("folder.delete.subject"));
					String message = bundle.getString("folder.delete.body");
					String body = MessageFormat.format(message, new Object[] { folder.getName() });
					sysmess.setMessageText(body);
					sysmess.setSentDate(now);
					sysmess.setConfirmation(0);
					sysmess.setPrio(0);
					sysmess.setDateScope(1);
					smdao.store(sysmess);
				} catch (Throwable t) {
					log.error(t.getMessage(), t);
				}
			}
		};

		deleteThread.start();
	}

	public static GUIFolder getFolder(String sid, long folderId) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);
		try {
			FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			if (!dao.isReadEnable(folderId, session.getUserId()))
				return null;

			Folder folder = dao.findById(folderId);
			if (folder == null)
				return null;

			GUIFolder f = new GUIFolder();
			f.setId(folderId);
			f.setName(folderId != Constants.DOCUMENTS_FOLDERID ? folder.getName() : "/");
			f.setParentId(folder.getParentId());
			f.setDescription(folder.getDescription());
			f.setCreation(folder.getCreation());
			f.setCreator(folder.getCreator());
			f.setCreatorId(folder.getCreatorId());
			f.setType(folder.getType());

			if (folder.getTemplate() != null) {
				dao.initialize(folder);
				f.setTemplateId(folder.getTemplate().getId());
				f.setTemplate(folder.getTemplate().getName());
				GUIExtendedAttribute[] attributes = prepareGUIAttributes(folder.getTemplate(), folder);
				f.setAttributes(attributes);
			}

			/*
			 * Count the children
			 */
			f.setDocumentCount(dao
					.queryForInt("select count(ld_id) from ld_document where ld_deleted=0 and ld_folderid=" + folderId));
			f.setSubfolderCount(dao
					.queryForInt("select count(ld_id) from ld_folder where not ld_id=ld_parentid and ld_deleted=0 and ld_parentid="
							+ folderId));

			Set<Permission> permissions = dao.getEnabledPermissions(folderId, session.getUserId());
			List<String> permissionsList = new ArrayList<String>();
			for (Permission permission : permissions) {
				permissionsList.add(permission.toString());
			}

			f.setPermissions(permissionsList.toArray(new String[permissionsList.size()]));

			Folder ref = folder;
			if (folder.getSecurityRef() != null)
				ref = dao.findById(folder.getSecurityRef());

			int i = 0;
			GUIRight[] rights = new GUIRight[ref.getFolderGroups().size()];
			for (FolderGroup fg : ref.getFolderGroups()) {
				GUIRight right = new GUIRight();
				right.setEntityId(fg.getGroupId());
				right.setAdd(fg.getAdd() == 1 ? true : false);
				right.setWrite(fg.getWrite() == 1 ? true : false);
				right.setSecurity(fg.getSecurity() == 1 ? true : false);
				right.setImmutable(fg.getImmutable() == 1 ? true : false);
				right.setDelete(fg.getDelete() == 1 ? true : false);
				right.setRename(fg.getRename() == 1 ? true : false);
				right.setImport(fg.getImport() == 1 ? true : false);
				right.setExport(fg.getExport() == 1 ? true : false);
				right.setSign(fg.getSign() == 1 ? true : false);
				right.setArchive(fg.getArchive() == 1 ? true : false);
				right.setWorkflow(fg.getWorkflow() == 1 ? true : false);
				right.setDownload(fg.getDownload() == 1 ? true : false);

				rights[i] = right;
				i++;
			}
			f.setRights(rights);

			return f;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return null;
	}

	@Override
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) throws InvalidSessionException {
		try {
			FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			GUIFolder folder = getFolder(sid, folderId);
			if (folder == null)
				return null;

			if (computePath) {
				String pathExtended = dao.computePathExtended(folderId);
				StringTokenizer st = new StringTokenizer(pathExtended, "/", false);
				int elements = st.countTokens();
				GUIFolder[] path = new GUIFolder[elements];
				Folder parent = dao.findById(Folder.ROOTID);
				List<Folder> list = new ArrayList<Folder>();
				int j = 0;
				while (st.hasMoreTokens()) {
					String text = st.nextToken();
					list = dao.findByName(parent, text, true);
					if (list.isEmpty())
						return null;

					if (parent.getId() == Folder.ROOTID) {
						GUIFolder f = new GUIFolder(Folder.ROOTID);
						f.setName("/");
						path[j] = f;
					} else
						path[j] = getFolder(sid, parent.getId(), false);
					parent = list.get(0);
					j++;
				}

				folder.setPath(path);
			}

			return folder;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t);
		}
	}

	@Override
	public void move(String sid, long folderId, long targetId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder folderToMove = folderDao.findById(folderId);
			// Check destParentId MUST BE <> 0 (initial value)
			if (targetId == 0 || folderDao.isInPath(folderToMove.getId(), targetId)) {
				return;
			}

			User user = SessionUtil.getSessionUser(sid);

			Folder destParentFolder = folderDao.findById(targetId);
			// Check destParentId: Must be different from the current folder
			// parentId
			if (targetId == folderToMove.getParentId())
				throw new SecurityException("No Changes");

			// Check destParentId: Must be different from the current folderId
			// A folder cannot be children of herself
			if (targetId == folderToMove.getId())
				throw new SecurityException("Not Allowed");

			// Check delete permission on the folder parent of folderToMove
			Folder sourceParent = folderDao.findById(folderToMove.getParentId());
			boolean sourceParentDeleteEnabled = folderDao.isPermissionEnabled(Permission.DELETE, sourceParent.getId(),
					user.getId());
			if (!sourceParentDeleteEnabled)
				throw new SecurityException("No rights to delete folder");

			// Check addChild permission on destParentFolder
			boolean addchildEnabled = folderDao.isPermissionEnabled(Permission.ADD, destParentFolder.getId(),
					user.getId());
			if (!addchildEnabled)
				throw new SecurityException("AddChild Rights not granted to this user");

			// Add a folder history entry
			FolderHistory transaction = new FolderHistory();
			transaction.setSessionId(sid);
			transaction.setUser(user);

			folderDao.move(folderToMove, destParentFolder, transaction);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void rename(String sid, long folderId, String name) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			List<Folder> folders = dao.findByNameAndParentId(name, dao.findById(folderId).getParentId());
			if (folders.size() > 0 && folders.get(0).getId() != folderId) {
				return;
			}
			// To avoid a 'org.hibernate.StaleObjectStateException', we
			// must retrieve the folder from database.
			Folder folder = dao.findById(folderId);
			folder.setName(name.trim());
			// Add a folder history entry
			FolderHistory history = new FolderHistory();
			history.setUser(SessionUtil.getSessionUser(sid));
			history.setEvent(FolderHistory.EVENT_FOLDER_RENAMED);
			history.setSessionId(sid);

			dao.store(folder, history);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public GUIFolder save(String sid, GUIFolder folder) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder f;
			String folderName = folder.getName().replace("/", "");

			FolderHistory transaction = new FolderHistory();
			transaction.setUser(SessionUtil.getSessionUser(sid));
			transaction.setSessionId(sid);

			if (folder.getId() != 0) {
				f = folderDao.findById(folder.getId());
				folderDao.initialize(f);
				f.setDescription(folder.getDescription());
				f.setType(folder.getType());
				if (f.getName().trim().equals(folderName)) {
					f.setName(folderName.trim());
					transaction.setEvent(FolderHistory.EVENT_FOLDER_CHANGED);
				} else {
					f.setName(folderName.trim());
					transaction.setEvent(FolderHistory.EVENT_FOLDER_RENAMED);
				}

				updateExtendedAttributes(f, folder);
			} else {
				transaction.setEvent(FolderHistory.EVENT_FOLDER_CREATED);

				f = folderDao.create(folderDao.findById(folder.getParentId()), folderName, transaction);
				f.setDescription(folder.getDescription());
				f.setType(folder.getType());
			}

			folderDao.store(f, transaction);

			folder.setId(f.getId());
			folder.setName(f.getName());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}

		return folder;
	}

	private boolean saveRules(String sid, Folder folder, long userId, GUIRight[] rights) throws Exception {
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		boolean sqlerrors = false;
		try {
			folder.setSecurityRef(null);
			folder.getFolderGroups().clear();
			fdao.store(folder);
			sqlerrors = false;
			Set<FolderGroup> grps = new HashSet<FolderGroup>();
			for (GUIRight right : rights) {
				boolean isAdmin = right.getEntityId() == 1;
				FolderGroup fg = null;
				if (right.isRead()) {
					fg = new FolderGroup();
					fg.setGroupId(right.getEntityId());
				}
				grps.add(fg);

				if (isAdmin || right.isWrite())
					fg.setWrite(1);
				else
					fg.setWrite(0);

				if (isAdmin || right.isAdd())
					fg.setAdd(1);
				else
					fg.setAdd(0);

				if (isAdmin || right.isSecurity())
					fg.setSecurity(1);
				else
					fg.setSecurity(0);

				if (isAdmin || right.isImmutable())
					fg.setImmutable(1);
				else
					fg.setImmutable(0);

				if (isAdmin || right.isDelete())
					fg.setDelete(1);
				else
					fg.setDelete(0);

				if (isAdmin || right.isRename())
					fg.setRename(1);
				else
					fg.setRename(0);

				if (isAdmin || right.isImport())
					fg.setImport(1);
				else
					fg.setImport(0);

				if (isAdmin || right.isExport())
					fg.setExport(1);
				else
					fg.setExport(0);

				if (isAdmin || right.isArchive())
					fg.setArchive(1);
				else
					fg.setArchive(0);

				if (isAdmin || right.isWorkflow())
					fg.setWorkflow(1);
				else
					fg.setWorkflow(0);

				if (isAdmin || right.isSign())
					fg.setSign(1);
				else
					fg.setSign(0);

				if (isAdmin || right.isDownload())
					fg.setDownload(1);
				else
					fg.setDownload(0);
			}

			folder.setFolderGroups(grps);
			boolean stored = fdao.store(folder);
			if (!stored) {
				sqlerrors = true;
			}

			// Add a folder history entry
			FolderHistory history = new FolderHistory();
			history.setUser(SessionUtil.getSessionUser(sid));
			history.setEvent(FolderHistory.EVENT_FOLDER_PERMISSION);
			history.setSessionId(sid);
			fdao.store(folder, history);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
		return !sqlerrors;
	}

	@Override
	public void paste(String sid, long[] docIds, long folderId, String action) throws InvalidSessionException {
		if (action.equals(Clipboard.CUT))
			cut(sid, docIds, folderId);
		else if (action.equals(Clipboard.COPY))
			copy(sid, docIds, folderId);
	}

	private void cut(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Folder selectedFolderFolder = folderDao.findById(folderId);
		try {
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setUser(SessionUtil.getSessionUser(sid));

				// Check if the selected document is a shortcut
				if (doc.getDocRef() != null) {
					if (doc.getFolder().getId() != selectedFolderFolder.getId()) {
						transaction.setEvent(History.EVENT_SHORTCUT_MOVED);
						docManager.moveToFolder(doc, selectedFolderFolder, transaction);
					} else
						continue;
				}

				// The document must be not immutable
				if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
					continue;
				}

				// The document must be not locked
				if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
					continue;
				}

				docManager.moveToFolder(doc, selectedFolderFolder, transaction);
			}
		} catch (AccessControlException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		} catch (Exception t) {
			log.error("Exception moving documents: " + t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	private void copy(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder selectedFolderFolder = folderDao.findById(folderId);
		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(SessionUtil.getSessionUser(sid));

				if (doc.getDocRef() == null) {
					docManager.copyToFolder(doc, selectedFolderFolder, transaction);
				} else {
					if (doc.getFolder().getId() != selectedFolderFolder.getId()) {
						transaction.setEvent(History.EVENT_SHORTCUT_STORED);
						docManager.copyToFolder(doc, selectedFolderFolder, transaction);
					}
				}
			}
		} catch (AccessControlException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		} catch (Exception e) {
			log.error("Exception copying documents: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void pasteAsAlias(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder selectedFolderFolder = folderDao.findById(folderId);
		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_SHORTCUT_STORED);
				transaction.setComment("");
				transaction.setUser(SessionUtil.getSessionUser(sid));

				if (doc.getFolder().getId() != selectedFolderFolder.getId()) {
					docManager.createShortcut(doc, selectedFolderFolder, transaction);
				}
			}
		} catch (AccessControlException t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		} catch (Exception t) {
			log.error("Exception copying documents alias: " + t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public GUIValuePair[] loadTemplates(String sid) throws InvalidSessionException {
		return new GUIValuePair[0];
	}

	@Override
	public void saveTemplates(String sid, GUIValuePair[] templates) throws InvalidSessionException {

	}

	@Override
	public void applyTemplate(String sid, long folderId, long templateId) throws InvalidSessionException {

	}

	/**
	 * Updates the extended attributes of a folder on the basis of the user's
	 * input
	 * 
	 * @param folder The folder to update
	 * @param f The model to use
	 */
	private void updateExtendedAttributes(Folder folder, GUIFolder f) {
		if (f.getTemplateId() != null) {
			DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			DocumentTemplate template = templateDao.findById(f.getTemplateId());
			folder.setTemplate(template);
			folder.getAttributes().clear();

			if (f.getAttributes().length > 0) {
				for (GUIExtendedAttribute attr : f.getAttributes()) {
					ExtendedAttribute templateAttribute = template.getAttributes().get(attr.getName());
					// This control is necessary because, changing
					// the template, the values of the old template
					// attributes keys remains on the form value
					// manager,
					// so the GUIDocument contains also the old
					// template attributes keys that must be
					// skipped.
					if (templateAttribute == null)
						continue;

					ExtendedAttribute extAttr = new ExtendedAttribute();
					int templateType = templateAttribute.getType();
					int extAttrType = attr.getType();

					if (templateType != extAttrType) {
						// This check is useful to avoid errors
						// related to the old template
						// attributes keys that remains on the form
						// value manager
						if (attr.getValue().toString().trim().isEmpty() && templateType != 0) {
							if (templateType == ExtendedAttribute.TYPE_INT) {
								extAttr.setIntValue(null);
							} else if (templateType == ExtendedAttribute.TYPE_DOUBLE) {
								extAttr.setDoubleValue(null);
							} else if (templateType == ExtendedAttribute.TYPE_DATE) {
								extAttr.setDateValue(null);
							}
						} else if (templateType == GUIExtendedAttribute.TYPE_DOUBLE) {
							extAttr.setValue(Double.parseDouble(attr.getValue().toString()));
						} else if (templateType == GUIExtendedAttribute.TYPE_INT) {
							extAttr.setValue(Long.parseLong(attr.getValue().toString()));
						}
					} else {
						if (templateType == ExtendedAttribute.TYPE_INT) {
							if (attr.getValue() != null)
								extAttr.setIntValue((Long) attr.getValue());
							else
								extAttr.setIntValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_DOUBLE) {
							if (attr.getValue() != null)
								extAttr.setDoubleValue((Double) attr.getValue());
							else
								extAttr.setDoubleValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_DATE) {
							if (attr.getValue() != null)
								extAttr.setDateValue((Date) attr.getValue());
							else
								extAttr.setDateValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_STRING) {
							if (attr.getValue() != null)
								extAttr.setStringValue((String) attr.getValue());
							else
								extAttr.setStringValue(null);
						}
					}

					extAttr.setLabel(attr.getLabel());
					extAttr.setType(templateType);
					extAttr.setPosition(attr.getPosition());
					extAttr.setMandatory(attr.isMandatory() ? 1 : 0);

					folder.getAttributes().put(attr.getName(), extAttr);
				}
			}
		} else {
			folder.setTemplate(null);
			folder.getAttributes().clear();
		}
	}

	private static GUIExtendedAttribute[] prepareGUIAttributes(DocumentTemplate template, Folder folder) {
		try {
			GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
			int i = 0;
			for (String attrName : template.getAttributeNames()) {
				ExtendedAttribute extAttr = template.getAttributes().get(attrName);
				GUIExtendedAttribute att = new GUIExtendedAttribute();
				att.setName(attrName);
				att.setPosition(extAttr.getPosition());
				att.setLabel(extAttr.getLabel());
				att.setMandatory(extAttr.getMandatory() == 1);
				att.setEditor(extAttr.getEditor());

				// If the case, populate the options
				if (att.getEditor() == ExtendedAttribute.EDITOR_LISTBOX) {
					String buf = (String) extAttr.getStringValue();
					List<String> list = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(buf, ",");
					while (st.hasMoreElements()) {
						String val = (String) st.nextElement();
						if (!list.contains(val))
							list.add(val);
					}
					att.setOptions(list.toArray(new String[0]));
				}

				if (folder != null) {
					if (folder.getValue(attrName) != null)
						att.setValue(folder.getValue(attrName));
				} else
					att.setValue(extAttr.getValue());
				att.setType(extAttr.getType());

				attributes[i] = att;
				i++;
			}
			return attributes;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return null;
		}
	}
}