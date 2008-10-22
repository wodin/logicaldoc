package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Control that allows the user to navigate document's versions
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id: VersionsRecordsManager.java,v 1.3 2007/08/20 12:19:37 marco Exp $
 * @since 3.0
 */
public class VersionsRecordsManager {
    private List<Version> versions = new ArrayList<Version>();
    private Document selectedDocument;
    private boolean showList = true;

    /**
     * Changes the currently selected document and updates the versions list.
     * 
     * @param doc
     */
    public void selectDocument(Document doc) {
        selectedDocument = doc;

        // initiate the list
        if (versions != null) {
            versions.clear();
        } else {
            versions = new ArrayList<Version>(10);
        }

        // get the versions and sort them
        Collection<Version> tmp = doc.getVersions();
        Version[] sortIt = (Version[]) tmp.toArray(new Version[0]);

        // clear collection and add sorted elements
        Arrays.sort(sortIt);

        for (int i = sortIt.length - 1; i >= 0; i--) {
            VersionRecord versionTmp = new VersionRecord(sortIt[i]);
            versions.add(versionTmp);

            if (i == (sortIt.length - 1)) {
                versionTmp.setCurrentVersion(true);
            }
        }
    }

    /**
     * Cleans up the resources used by this class. This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        versions.clear();
    }

    /**
     * Gets the list of versions which will be used by the ice:dataTable
     * component.
     * 
     * @return array list of versions
     */
    public List<Version> getVersions() {
        return versions;
    }

    public String back() {
        DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil
                .accessBeanFromFacesContext("documentNavigation", FacesContext
                        .getCurrentInstance()));
        documentNavigation.setSelectedPanel(new PageContentBean("documents"));

        return null;
    }

    public String edit() {
        showList = false;

        return null;
    }

    public String backToList() {
        showList = true;

        return null;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public boolean isShowList() {
        return showList;
    }
}
