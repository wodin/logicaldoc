package com.logicaldoc.web.document;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.admin.GuiBean;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>TrashBean</code> class is responsible for constructing the list of
 * delted documents which will be bound to a ice:dataTable JSF component.
 * <p/>
 * <p>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class TrashBean extends SortableList {

	protected static Log log = LogFactory.getLog(TrashBean.class);

	public static final String CHILD_INDENT_STYLE_CLASS = "childRowIndentStyle";

	public static final String CHILD_ROW_STYLE_CLASS = "childRowStyle";

	private List<DocumentRecord> documents = new ArrayList<DocumentRecord>();

	// Set of selected rows
	private Set<DocumentRecord> selection = new HashSet<DocumentRecord>();

	private boolean selectedAll;

	private int displayedRows = 10;

	public TrashBean() {
		// We don't sort by default
		super("xxx");
		selection.clear();
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		documents.clear();
	}

	private void load() {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findDeleted(SessionManagement.getUserId(), 100);
		documents.clear();
		for (Document document : docs) {
			documents.add(new DocumentRecord(document));
		}
	}

	/**
	 * Gets the list of Document which will be used by the ice:dataTable
	 * component.
	 */
	public List<DocumentRecord> getDocuments() {
		if (documents.isEmpty())
			load();
		return documents;
	}

	public void selectRow(RowSelectorEvent e) {
		DocumentRecord record = documents.get(e.getRow());

		if (e.isSelected() || !selection.contains(record)) {
			selection.add(record);
		} else if (!e.isSelected() || selection.contains(record)) {
			selection.remove(record);
		}
	}

	public void refresh() {
		selectedAll = false;
		load();
	}

	public int getCount() {
		return getDocuments().size();
	}

	/**
	 * Restore all selected documents
	 */
	public String restore() {
		if (!selection.isEmpty()) {
			for (DocumentRecord record : selection) {
				try {
					DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					docDao.restore(record.getDocId());
				} catch (AccessControlException e) {
					Messages.addLocalizedError("document.write.nopermission");
				} catch (Exception e) {
					Messages.addLocalizedInfo("errors.action.deleteitem");
				}
			}
			Messages.addLocalizedInfo("logicaldoc-impex.restore.success");
			refresh();
		} else {
			Messages.addLocalizedWarn("noselection");
		}

		selection.clear();
		documents.clear();

		return null;
	}

	public String selectAll() {
		for (DocumentRecord document : documents) {
			document.setSelected(true);
			selection.add(document);
		}
		selectedAll = true;
		return null;
	}

	public String unselectAll() {
		for (DocumentRecord document : documents) {
			document.setSelected(false);
		}
		if (selection != null)
			selection.clear();
		selectedAll = false;
		return null;
	}

	public boolean isSelectedAll() {
		return selectedAll;
	}

	public int getDisplayedRows() {
		if (displayedRows == 0) {
			GuiBean guiBean = ((GuiBean) FacesUtil.accessBeanFromFacesContext("guiBean", FacesContext
					.getCurrentInstance(), log));
			displayedRows = guiBean.getPageSize();
		}
		return displayedRows;
	}

	public void setDisplayedRows(int displayedRows) {
		if (displayedRows != this.displayedRows)
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
		this.displayedRows = displayedRows;
	}

	/**
	 * Determines the sort order.
	 * 
	 * @param sortColumn to sort by.
	 * @return whether sort order is ascending or descending.
	 */
	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	/**
	 * Sorts the list of DocumentRecord data.
	 */
	@Override
	protected void sort(final String column, final boolean ascending) {
		log.debug("invoked TrashBean.sort()");
		log.debug("sort on column: " + column);
		log.debug("sort ascending: " + ascending);
		Comparator<DocumentRecord> comparator = new Comparator<DocumentRecord>() {
			public int compare(DocumentRecord doc1, DocumentRecord doc2) {

				if (column == null) {
					return 0;
				}
				if (column.equals("title")) {
					return ascending ? doc1.getTitle().compareTo(doc2.getTitle()) : doc2.getTitle().compareTo(
							doc1.getTitle());
				} else if (column.equals("lastModified")) {
					Date d1 = doc1.getLastModified() != null ? doc1.getLastModified() : new Date(0);
					Date d2 = doc2.getLastModified() != null ? doc2.getLastModified() : new Date(0);
					return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
				} else if (column.equals("id")) {
					Long id1 = new Long(doc1.getDocId());
					Long id2 = new Long(doc2.getDocId());
					return ascending ? id1.compareTo(id2) : id2.compareTo(id1);
				} else
					return 0;
			}
		};
		Collections.sort(documents, comparator);
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}
}