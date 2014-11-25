package com.logicaldoc.gui.frontend.client.document.grid;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * An useful panel to show informations to the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.2
 */
public class Cursor extends ToolStrip {

	private Label label = null;

	private String maxCookieName = Constants.COOKIE_DOCSLIST_MAX;

	private SpinnerItem maxItem;

	private SpinnerItem pageItem;

	public Cursor() {
		this(null, 1, false);
	}

	/**
	 * ID of the message to be used to compose the email
	 */
	public Cursor(String maxDisplayedRecordsCookieName, int currentPage, boolean enabledPagination) {
		setHeight(20);
		this.maxCookieName = maxDisplayedRecordsCookieName;

		label = new Label(" ");
		label.setWrap(false);
		label.setMargin(2);
		setAlign(Alignment.RIGHT);

		String mx = "100";
		if (maxDisplayedRecordsCookieName != null) {
			if (Offline.get(maxDisplayedRecordsCookieName) != null
					&& !Offline.get(maxDisplayedRecordsCookieName).equals(""))
				mx = (String) Offline.get(maxDisplayedRecordsCookieName);
		}

		maxItem = ItemFactory.newSpinnerItem("max", "display", Integer.parseInt(mx), 10, (Integer) null);
		maxItem.setValue(Integer.parseInt(mx));
		maxItem.setWidth(70);
		maxItem.setStep(20);
		maxItem.setSaveOnEnter(true);
		maxItem.setImplicitSave(true);
		maxItem.setHint(I18N.message("elements"));
		maxItem.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				onMaxChange();
			}
		});
		
		addFormItem(maxItem);
		if (enabledPagination) {
			pageItem = ItemFactory.newSpinnerItem("page", "page", currentPage, 1, 1);
			pageItem.setHint("");
			pageItem.setSaveOnEnter(true);
			pageItem.setImplicitSave(true);

			addSeparator();
			addFormItem(pageItem);
		}
		addFill();
		addMember(label);
	}

	public void setMessage(String message) {
		label.setContents(message);
	}

	public void setTotalRecords(int totalRecords) {
		if (pageItem == null)
			return;
		int max = getMaxDisplayedRecords();
		int pages = (int) Math.ceil(totalRecords / max) + 1;
		pageItem.setMax(pages);
		pageItem.setHint("/" + pages);
	}

	public int getMaxDisplayedRecords() {
		return Integer.parseInt(maxItem.getValue().toString());
	}

	public void setMaxDisplayedRecords(int maxRecords) {
		maxItem.setValue(maxRecords);
	}

	public int getCurrentPage() {
		if (pageItem == null)
			return 1;
		return Integer.parseInt(pageItem.getValue().toString());
	}

	private void onMaxChange() {
		if (maxItem.validate() && maxCookieName != null) {
			Offline.put(maxCookieName, maxItem.getValueAsString());
		}
	}

	public void registerMaxChangedHandler(ChangedHandler handler) {
		maxItem.addChangedHandler(handler);
	}

	public void registerPageChangedHandler(ChangedHandler handler) {
		if (pageItem != null)
			pageItem.addChangedHandler(handler);
	}
}
