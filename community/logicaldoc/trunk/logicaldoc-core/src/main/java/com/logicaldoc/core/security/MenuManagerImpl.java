package com.logicaldoc.core.security;

import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;

/**
 * Basic menu manager implementation
 * 
 * @author Marco Meschieri
 * @version $Id: MenuManagerImpl.java,v 1.1 2007/07/10 16:36:26 marco Exp $
 * @since 3.0
 */
public class MenuManagerImpl implements MenuManager {
    protected static Log log = LogFactory.getLog(MenuManagerImpl.class);

    private MenuDAO menuDao;

    private DocumentDAO documentDao;

    private TermDAO termDao;

    private Indexer indexer;

    private Storer storer;

    private UserDocDAO userDocDao;

    /**
     * @see com.logicaldoc.core.security.MenuManager#deleteMenu(com.logicaldoc.core.security.Menu,
     *      java.lang.String)
     */
    public void deleteMenu(Menu menu, String userName) throws Exception {
        log.debug("User " + userName + " required the deletion of item " + menu.getMenuId());

        try {
            boolean sqlop = true;
            int id = menu.getMenuId();

            if (menuDao.isWriteEnable(id, userName)) {
                int type = menu.getMenuType();

                if (type == Menu.MENUTYPE_FILE) {
                    deleteFile(menu, id, userName);
                }

                // remove sub-elements
                Collection children = menuDao.findByParentId(id);
                Iterator childIter = children.iterator();
                while (childIter.hasNext()) {
                    Menu m = (Menu) childIter.next();
                    deleteFile(m, m.getMenuId(), userName);
                }

                boolean deleted = menuDao.delete(id);
                if (!deleted) {
                    sqlop = false;
                }

                if (!sqlop) {
                    String message = "An error has occurred while deleting the item";
                    log.error(message);
                    throw new SQLException(message);
                } else {
                    log.info("The item has been deleted");
                }
            } else {
                String message = "User not allowed to delete item";
                log.error(message);
                throw new AccessControlException(message);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private boolean deleteFile(Menu menu, int id, String username) {
        boolean sqlop = true;

        try {
            userDocDao.delete(username, id);

            Document doc = documentDao.findByMenuId(id);

            if (doc != null) {
                indexer.deleteFile(String.valueOf(id), doc.getLanguage());
            }

            boolean deleted2 = termDao.delete(id);

            boolean deleted1 = documentDao.deleteByMenuId(id);

            if (!deleted1 || !deleted2) {
                sqlop = false;
            }

            boolean deleted = menuDao.delete(id);

            if (!deleted) {
                sqlop = false;
            }

            // String path = conf.getValue("docdir");
            String menupath = menu.getMenuPath() + "/" + String.valueOf(id);

            // FileBean.deleteDir(path);
            storer.delete(menupath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sqlop = false;
        }

        return sqlop;
    }

    public void setDocumentDao(DocumentDAO documentDao) {
        this.documentDao = documentDao;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setMenuDao(MenuDAO menuDao) {
        this.menuDao = menuDao;
    }

    public void setStorer(Storer storer) {
        this.storer = storer;
    }

    public void setTermDao(TermDAO termDao) {
        this.termDao = termDao;
    }

    public void setUserDocDao(UserDocDAO userDocDao) {
        this.userDocDao = userDocDao;
    }
}