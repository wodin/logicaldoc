package com.logicaldoc.web.navigation;


/**
 * Extension of the standard MenuItem that allows us to store a content page
 * reference
 *
 * @author Marco Meschieri - Logical Objects
 * @version $Id: MenuItem.java,v 1.1 2006/08/29 16:33:46 marco Exp $
 * @since 3.0
 */
public class MenuItem extends com.icesoft.faces.component.menubar.MenuItem {
	
    private PageContentBean content = new PageContentBean("blank");

    //A generic placeholder for needed informations
    private Object userObject = null;
    
    public PageContentBean getContent() {
        return content;
    }

    public void setContent(PageContentBean content) {
        this.content = content;
    }

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	
	public String getMenuId(){
		return this.getId().replace("m-", "").trim();
	}
}