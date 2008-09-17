package com.logicaldoc.core.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Exports a folder hierarchie and all documents in it as a zip file.
 * 
 * @author Alessandro Gasparini
 */
public class ZipExport implements Export {

    protected static Log log = LogFactory.getLog(ZipExport.class);

    private ZipOutputStream zos;

    private String username;

    private boolean allLevel;

    private int startMenuId;

    public ZipExport() {
        zos = null;
        username = "";
        allLevel = false;
        startMenuId = 0;
    }

    /**
     * @see com.logicaldoc.core.export.Export#process(com.logicaldoc.core.security.Menu,
     *      java.lang.String)
     */
    public ByteArrayOutputStream process(Menu menu, String user) throws IOException {
        this.username = user;
        this.startMenuId = menu.getMenuId();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        zos = new ZipOutputStream(bos);
        appendChildren(menu, 0);
        zos.flush();
        zos.close();
        return bos;
    }

    /**
     * @see com.logicaldoc.core.export.Export#process(int, java.lang.String)
     */
    public ByteArrayOutputStream process(int menuId, String user) throws IOException {
        MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
        Menu menu = menuDao.findByPrimaryKey(menuId);
        return process(menu, user);
    }

    /**
     * If allLevel set true all children of a specified menu will be export.
     * Otherwise only the first level will be export.
     * 
     * @param b
     */
    public void setAllLevel(boolean b) {
        allLevel = b;
    }

    protected void appendChildren(Menu menu, int level) {

        if (!allLevel && (level > 1)) {
            return;
        } else {
            if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
                String menupath = menu.getMenuPath();
                SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
                String filepath = settings.getValue("docdir");
                filepath += menupath + "/" + String.valueOf(menu.getMenuId()) + "/";
                filepath += menu.getMenuRef();
                addFile(menu, filepath);
            }

            MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
            Collection children = menuDao.findByUserName(username, menu.getMenuId());
            Iterator iter = children.iterator();

            while (iter.hasNext()) {
                appendChildren((Menu) iter.next(), level + 1);
            }
        }
    }

    protected void addFile(Menu menu, String filepath) {
        try {
            File currentFile = new File(filepath);
            InputStream is = new FileInputStream(currentFile);
            BufferedInputStream bis = new BufferedInputStream(is);

            // Create the path as the user see in logicaldoc
            String decodedMenuPath = decodeMenuPath(menu.getMenuPath());
            decodedMenuPath=decodedMenuPath + "/" + currentFile.getName();
            decodedMenuPath=decodedMenuPath.replaceAll("//", "/");
            ZipEntry entry = new ZipEntry(decodedMenuPath);
            zos.putNextEntry(entry);

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = bis.read()) != -1) {
                zos.write(len);
            }

            bis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String decodeMenuPath(String menuPath) {

        String decodedPath = "";

        MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

        // substring the menuPath with the startMenuId
        int startIndx = menuPath.indexOf(String.valueOf(startMenuId));
        menuPath = menuPath.substring(startIndx);

        StringTokenizer st = new StringTokenizer(menuPath, "/");

        while (st.hasMoreTokens()) {
            String menuId = st.nextToken();

            Menu menu = menuDao.findByPrimaryKey(Integer.parseInt(menuId));
            if (menu != null)
                decodedPath += "/" + menu.getMenuText();
        }

        return decodedPath;
    }

}
