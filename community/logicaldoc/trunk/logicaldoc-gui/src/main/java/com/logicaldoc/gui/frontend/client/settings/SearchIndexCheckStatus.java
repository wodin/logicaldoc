package com.logicaldoc.gui.frontend.client.settings;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

/**
 * This is the form used to show the index check.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SearchIndexCheckStatus extends Window {

	public SearchIndexCheckStatus(String result) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("checkfulltextindex"));
		setWidth(450);
		setHeight(500);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMembersMargin(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setHeight100();
		form.setWidth100();
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);

		final TextAreaItem status = new TextAreaItem();
		status.setWidth(440);
		status.setHeight(490);
		status.setValue(result);
		status.setShowTitle(false);

		form.setFields(status);
		addItem(form);
	}
}