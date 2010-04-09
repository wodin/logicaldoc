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