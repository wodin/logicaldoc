package com.logicaldoc.web.navigation;

import java.util.Stack;


/**
 * <p>
 * The NavigationBean class is responsible for storing the state of the panel
 * stacks which display dynamic content.
 * </p>
 *
 * @author Marco Meschieri, Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class NavigationBean {
    // selected page content bean.
    private PageContentBean selectedPanel = null;

    // Maintains the history of accessed pages, useful for back buttons
    private Stack<PageContentBean> history = new Stack<PageContentBean>();

    public NavigationBean() {
        super();
    }

    /**
     * Gets the currently selected content panel.
     *
     * @return currently selected content panel.
     */
    public PageContentBean getSelectedPanel() {
        return selectedPanel;
    }

    /**
     * Sets the selected panel to the specified content.
     *
     * @param content a not null page content bean.
     */
    public void setSelectedPanel(PageContentBean content) {
        if ((content != null) && content.isPageContent() &&
                !content.equals(this.selectedPanel)) {
            if (this.selectedPanel != null) {
                // Update history
                history.push(this.selectedPanel);

                if (history.size() > 10) {
                    history.setSize(10);
                }
            }
            this.selectedPanel = content;
        }
    }

    /**
     * Handles back button
     */
    public String back() {
        if (!history.isEmpty()) {
            selectedPanel = history.pop();
        }
        return null;
    }

    public int getHistorySize() {
        return history.size();
    }
}
