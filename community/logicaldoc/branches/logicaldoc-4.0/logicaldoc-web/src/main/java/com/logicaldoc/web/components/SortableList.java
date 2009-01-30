/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.logicaldoc.web.components;


/**
 * <p>The SortableList class is a utility class used by the data table paginator
 * example.</p>
 *
 * @since 0.3.0
 */
public abstract class SortableList {
	
    private String sortColumn;
    private boolean ascending;

    protected SortableList(String defaultSortColumn) {
        sortColumn = defaultSortColumn;
        ascending = isDefaultAscending(defaultSortColumn);
    }

    /**
     * Sort the list.
     */
    protected abstract void sort(String column, boolean ascending);

    /**
     * Is the default sort direction for the given column "ascending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);

    /**
     * Sort the given column
     *
     * @param cName column to sort
     */
    public void sort(String cName) {

        if (cName == null) {
            throw new IllegalArgumentException(
                    "Argument sortColumn must not be null.");
        }

        if (sortColumn.equals(cName)) {
            //current sort equals new sortColumn -> reverse sort order
            ascending = !ascending;
        } else {
            //sort new column in default direction
            sortColumn = cName;
            ascending = isDefaultAscending(sortColumn);
        }

        sort(sortColumn, ascending);
    }

    /**
     * Is the sort ascending?
     *
     * @return true if the ascending sort otherwise false
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Sets sort type.
     *
     * @param ascending true for ascending sort, false for descending sort.
     */
    public void setAscending(boolean ascending) {
    	this.ascending = ascending;
    }

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String columnName) {
		this.sortColumn = columnName;
		sort(this.sortColumn);
	}
}