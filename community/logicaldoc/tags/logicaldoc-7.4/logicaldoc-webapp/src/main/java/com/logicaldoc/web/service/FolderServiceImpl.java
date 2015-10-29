package com.logicaldoc.web.service;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderEvent;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUIValue;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

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
	public GUIFolder inheritRights(String sid, long folderId, long rightsFolderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			/*
			 * Just apply the current security settings to the whole subtree
			 */
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setSessionId(sid);

			if (!fdao.updateSecurityRef(folderId, rightsFolderId, transaction))
				throw new Exception("Error updating the database");

			return getFolder(sid, folderId);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
		return null;
	}

	@Override
	public void applyRights(String sid, GUIFolder folder, boolean subtree) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			Folder f = fdao.findById(folder.getId());
			fdao.initialize(f);

			if (subtree) {
				/*
				 * Just apply the current security settings to the whole subtree
				 */
				FolderHistory history = new FolderHistory();
				history.setUser(ServiceUtil.getSessionUser(sid));
				history.setEvent(FolderEvent.PERMISSION.toString());
				history.setSessionId(sid);

				fdao.applyRithtToTree(folder.getId(), history);
			} else {
				saveRules(sid, f, session.getUserId(), folder.getRights());
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void applyMetadata(String sid, long parentId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setSessionId(sid);
			fdao.applyMetadataToTree(parentId, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void delete(final String sid, final long folderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!dao.isPermissionEnabled(Permission.DELETE, folderId, session.getUserId()))
			throw new ServerException("Permission DELETE not granted");

		try {
			// Add a folder history entry
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setEvent(FolderEvent.DELETED.toString());
			transaction.setSessionId(sid);
			dao.deleteTree(folderId, PersistentObject.DELETED_CODE_DEFAULT, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	public static GUIFolder getFolder(String sid, long folderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			if (!dao.isReadEnable(folderId, session.getUserId()))
				return null;

			Folder folder = dao.findById(folderId);
			if (folder == null)
				return null;

			dao.initialize(folder);

			GUIFolder f = new GUIFolder();
			f.setId(folderId);
			f.setName(folderId != Constants.DOCUMENTS_FOLDERID ? folder.getName() : "/");
			f.setParentId(folder.getParentId());
			f.setDescription(folder.getDescription());
			f.setCreation(folder.getCreation());
			f.setCreator(folder.getCreator());
			f.setCreatorId(folder.getCreatorId());
			f.setType(folder.getType());
			f.setPosition(folder.getPosition());

			if (folder.getSecurityRef() != null) {
				GUIFolder secRef = new GUIFolder();
				secRef.setId(folder.getSecurityRef());
				secRef.setPathExtended(dao.computePathExtended(folder.getSecurityRef()));
				f.setSecurityRef(secRef);
			}

			if (folder.getTemplate() != null) {
				dao.initialize(folder);
				f.setTemplateId(folder.getTemplate().getId());
				f.setTemplate(folder.getTemplate().getName());
				f.setTemplateLocked(folder.getTemplateLocked());
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
			for (Permission permission : permissions)
				permissionsList.add(permission.toString());
			f.setPermissions(permissionsList.toArray(new String[permissionsList.size()]));

			Folder ref = folder;
			if (folder.getSecurityRef() != null)
				ref = dao.findById(folder.getSecurityRef());

			int i = 0;
			GUIRight[] rights = new GUIRight[(ref != null && ref.getFolderGroups() != null) ? ref.getFolderGroups()
					.size() : 0];
			if (ref != null && ref.getFolderGroups() != null)
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
					right.setCalendar(fg.getCalendar() == 1 ? true : false);
					right.setSubscription(fg.getSubscription() == 1 ? true : false);

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
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
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
				Folder parent = dao.findRoot(session.getTenantId());
				List<Folder> list = new ArrayList<Folder>();
				int j = 0;
				while (st.hasMoreTokens()) {
					String text = st.nextToken();
					list = dao.findByName(parent, text, null, true);
					if (list.isEmpty())
						return null;

					if (parent.getId() == Folder.ROOTID || parent.getId() == parent.getParentId()) {
						GUIFolder f = new GUIFolder(parent.getId());
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
			return (GUIFolder) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void copyFolder(String sid, long folderId, long targetId, boolean foldersOnly, boolean inheritSecurity)
			throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder folderToCopy = folderDao.findById(folderId);
			// Check destParentId MUST BE <> 0 (initial value)
			if (targetId == 0 || folderDao.isInPath(folderToCopy.getId(), targetId)) {
				return;
			}

			folderDao.initialize(folderToCopy);

			User user = ServiceUtil.getSessionUser(sid);

			Folder destParentFolder = folderDao.findById(targetId);
			// Check destParentId: Must be different from the current folder
			// parentId
			if (targetId == folderToCopy.getParentId())
				throw new SecurityException("No Changes");

			// Check destParentId: Must be different from the current folderId
			// A folder cannot be children of herself
			if (targetId == folderToCopy.getId())
				throw new SecurityException("Not Allowed");

			// Check addChild permission on destParentFolder
			boolean addchildEnabled = folderDao.isPermissionEnabled(Permission.ADD, destParentFolder.getId(),
					user.getId());
			if (!addchildEnabled)
				throw new SecurityException("Add Child right not granted to this user in the target folder");

			// Add a folder history entry
			FolderHistory transaction = new FolderHistory();
			transaction.setSessionId(sid);
			transaction.setUser(user);

			folderDao.copy(folderToCopy, destParentFolder, foldersOnly, inheritSecurity, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void move(String sid, long folderId, long targetId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder folderToMove = folderDao.findById(folderId);
			// Check destParentId MUST BE <> 0 (initial value)
			if (targetId == 0 || folderDao.isInPath(folderToMove.getId(), targetId)) {
				return;
			}

			User user = ServiceUtil.getSessionUser(sid);

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
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void rename(String sid, long folderId, String name) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			List<Folder> folders = dao.findByNameAndParentId(name, dao.findById(folderId).getParentId());
			if (folders.size() > 0 && folders.get(0).getId() != folderId) {
				return;
			}
			// To avoid a 'org.hibernate.StaleObjectStateException', we
			// must retrieve the folder from database.
			Folder folder = dao.findById(folderId);

			// Add a folder history entry
			FolderHistory history = new FolderHistory();
			history.setTitleOld(folder.getName());
			history.setPathOld(dao.computePathExtended(folderId));
			history.setUser(ServiceUtil.getSessionUser(sid));
			history.setEvent(FolderEvent.RENAMED.toString());
			history.setSessionId(sid);

			folder.setName(name.trim());
			dao.store(folder, history);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public GUIFolder save(String sid, GUIFolder folder) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Folder f;
			String folderName = folder.getName().replace("/", "");

			FolderHistory transaction = new FolderHistory();
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setTenantId(session.getTenantId());
			transaction.setSessionId(sid);

			f = folderDao.findById(folder.getId());
			if (f == null)
				throw new Exception("Unexisting folder " + folder.getId());

			folderDao.initialize(f);
			f.setDescription(folder.getDescription());
			f.setType(folder.getType());
			f.setTemplateLocked(folder.getTemplateLocked());
			f.setPosition(folder.getPosition());
			if (f.getName().trim().equals(folderName)) {
				f.setName(folderName.trim());
				transaction.setEvent(FolderEvent.CHANGED.toString());
			} else {
				f.setName(folderName.trim());
				transaction.setEvent(FolderEvent.RENAMED.toString());
			}

			updateExtendedAttributes(f, folder);

			folderDao.store(f, transaction);

			folder.setId(f.getId());
			folder.setName(f.getName());
			folder.setPosition(f.getPosition());
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}

		return getFolder(sid, folder.getId());
	}

	@Override
	public GUIFolder create(String sid, GUIFolder newFolder, boolean inheritSecurity) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			String folderName = newFolder.getName().replace("/", "");

			FolderHistory transaction = new FolderHistory();
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setSessionId(sid);
			transaction.setTenantId(session.getTenantId());
			transaction.setEvent(FolderEvent.CREATED.toString());

			Folder folderVO = new Folder();
			folderVO.setName(folderName);
			folderVO.setType(newFolder.getType());
			folderVO.setTenantId(session.getTenantId());

			Folder f = null;
			if (newFolder.getType() == Folder.TYPE_DEFAULT)
				f = folderDao.create(folderDao.findById(newFolder.getParentId()), folderVO, inheritSecurity,
						transaction);
			else
				f = folderDao.create(folderDao.findByName("/", session.getTenantId()).get(0), folderVO,
						inheritSecurity, transaction);

			return getFolder(sid, f.getId());
		} catch (Throwable t) {
			return (GUIFolder) ServiceUtil.throwServerException(session, log, t);
		}
	}

	private boolean saveRules(String sid, Folder folder, long userId, GUIRight[] rights) throws Exception {
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		boolean sqlerrors = false;
		try {
			log.info("Applying " + (rights != null ? rights.length : 0) + " rights to folder " + folder.getId());

			folder.setSecurityRef(null);
			folder.getFolderGroups().clear();
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

				if (isAdmin || right.isCalendar())
					fg.setCalendar(1);
				else
					fg.setCalendar(0);

				if (isAdmin || right.isSubscription())
					fg.setSubscription(1);
				else
					fg.setSubscription(0);
			}

			folder.setFolderGroups(grps);

			// Add a folder history entry
			FolderHistory history = new FolderHistory();
			history.setUser(ServiceUtil.getSessionUser(sid));
			history.setEvent(FolderEvent.PERMISSION.toString());
			history.setSessionId(sid);
			fdao.store(folder, history);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		return !sqlerrors;
	}

	@Override
	public void paste(String sid, long[] docIds, long folderId, String action) throws ServerException {
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		if (!fdao.isWriteEnable(folderId, ServiceUtil.getSessionUser(sid).getId()))
			throw new RuntimeException("Cannot write in folder " + folderId);

		if (action.equals(Clipboard.CUT))
			cut(sid, docIds, folderId);
		else if (action.equals(Clipboard.COPY))
			copy(sid, docIds, folderId);
	}

	private void cut(String sid, long[] docIds, long folderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
				transaction.setUser(ServiceUtil.getSessionUser(sid));

				// Check if the selected document is a shortcut
				if (doc.getDocRef() != null) {
					if (doc.getFolder().getId() != selectedFolderFolder.getId()) {
						transaction.setEvent(DocumentEvent.SHORTCUT_MOVED.toString());
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
			ServiceUtil.throwServerException(session, log, t);
		} catch (Exception t) {
			log.error("Exception moving documents: " + t.getMessage(), t);
			ServiceUtil.throwServerException(session, null, t);
		}
	}

	private void copy(String sid, long[] docIds, long folderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
				transaction.setEvent(DocumentEvent.STORED.toString());
				transaction.setComment("");
				transaction.setUser(ServiceUtil.getSessionUser(sid));

				if (doc.getDocRef() == null) {
					docManager.copyToFolder(doc, selectedFolderFolder, transaction);
				} else {
					if (doc.getFolder().getId() != selectedFolderFolder.getId()) {
						transaction.setEvent(DocumentEvent.SHORTCUT_STORED.toString());
						docManager.copyToFolder(doc, selectedFolderFolder, transaction);
					}
				}
			}
		} catch (AccessControlException t) {
			ServiceUtil.throwServerException(session, log, t);
		} catch (Exception e) {
			log.error("Exception copying documents: " + e.getMessage(), e);
			ServiceUtil.throwServerException(session, null, e);
		}
	}

	@Override
	public void pasteAsAlias(String sid, long[] docIds, long folderId, String type) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
				transaction.setEvent(DocumentEvent.SHORTCUT_STORED.toString());
				transaction.setComment("");
				transaction.setUser(ServiceUtil.getSessionUser(sid));

				if (doc.getFolder().getId() != selectedFolderFolder.getId())
					docManager.createAlias(doc, selectedFolderFolder, StringUtils.isNotEmpty(type) ? type : null,
							transaction);
			}
		} catch (AccessControlException t) {
			ServiceUtil.throwServerException(session, log, t);
		} catch (Exception t) {
			log.error("Exception copying documents alias: " + t.getMessage(), t);
			ServiceUtil.throwServerException(session, null, t);
		}
	}

	@Override
	public GUIValue[] loadTemplates(String sid) throws ServerException {
		return new GUIValue[0];
	}

	@Override
	public void saveTemplates(String sid, GUIValue[] templates) throws ServerException {

	}

	@Override
	public void applyTemplate(String sid, long folderId, long templateId, boolean inheritSecurity)
			throws ServerException {

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
			folder.setTemplateLocked(f.getTemplateLocked());
			folder.getAttributes().clear();

			if (f.getAttributes() != null && f.getAttributes().length > 0) {
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
							if (templateType == ExtendedAttribute.TYPE_INT
									|| templateType == ExtendedAttribute.TYPE_BOOLEAN) {
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
						} else if (templateType == GUIExtendedAttribute.TYPE_BOOLEAN) {
							extAttr.setValue(attr.getBooleanValue());
							extAttr.setType(ExtendedAttribute.TYPE_BOOLEAN);
						} else if (templateType == GUIExtendedAttribute.TYPE_USER) {
							extAttr.setIntValue(attr.getIntValue());
							extAttr.setStringValue(attr.getStringValue());
						}
					} else {
						if (templateType == ExtendedAttribute.TYPE_INT) {
							if (attr.getValue() != null)
								extAttr.setIntValue((Long) attr.getValue());
							else
								extAttr.setIntValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_BOOLEAN) {
							if (attr.getBooleanValue() != null)
								extAttr.setValue(attr.getBooleanValue());
							else
								extAttr.setBooleanValue(null);
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
						} else if (templateType == ExtendedAttribute.TYPE_USER) {
							if (attr.getValue() != null) {
								extAttr.setStringValue((String) attr.getStringValue());
								extAttr.setIntValue((Long) attr.getIntValue());
							} else {
								extAttr.setStringValue(null);
								extAttr.setIntValue(null);
							}
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
					if (buf != null) {
						StringTokenizer st = new StringTokenizer(buf, ",");
						while (st.hasMoreElements()) {
							String val = (String) st.nextElement();
							if (!list.contains(val))
								list.add(val);
						}
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

	@Override
	public void restore(String sid, long folderId, long parentId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			FolderHistory transaction = new FolderHistory();
			transaction.setSessionId(sid);
			transaction.setUser(ServiceUtil.getSessionUser(sid));

			dao.restore(folderId, parentId, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void deleteFromTrash(String sid, Long[] ids) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		if (ids == null || ids.length < 1)
			return;

		try {
			String idsStr = Arrays.asList(ids).toString().replace('[', '(').replace(']', ')');
			FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			dao.bulkUpdate("set ld_deleted=2 where ld_id in " + idsStr, null);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}
}