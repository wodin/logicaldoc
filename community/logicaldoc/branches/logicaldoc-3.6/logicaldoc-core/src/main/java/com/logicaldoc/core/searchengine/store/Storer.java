package com.logicaldoc.core.searchengine.store;

import java.io.InputStream;


/**
 * Created on 17.08.2004
 *
 * @author Michael Scholz
 */
public interface Storer
{
    /**
     * This method has to store a document and if wanted to make a backup of
     * this document. The location where (DBMS, Filesystem, other) the document
     * should be stored is defined by the concret implementation. It is possible
     * to store a new document or a new version of an existing document.
     *
     * @param stream
     *            Document as InputStream
     * @param menupath
     *            Path in logicaldoc containing the menuIds of all parent items.
     * @param filename
     *            Name of the document.
     * @param version
     *            Version of the document.
     * @return ResultImpl of the storing process.
     */
    boolean store(
        InputStream stream,
        String      menupath,
        String      filename,
        String      version);

    /**
     * Deletes a document from the documentpool and the backuppool.
     *
     * @param menupath
     *            Path in logicaldoc containing the menuIds of all parent items.
     * @return ResultImpl of the deleting process.
     */
    boolean delete(String menupath);

    /**
     * This method regenerates the document repository from the backup.
     *
     * @return ResultImpl of the restoring process.
     */
    boolean restoreAll();
}
