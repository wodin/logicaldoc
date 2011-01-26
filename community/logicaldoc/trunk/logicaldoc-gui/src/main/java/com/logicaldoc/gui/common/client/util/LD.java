package com.logicaldoc.gui.common.client.util;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * This class contains useful methods for objects visualization.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class LD {

	public static void ask(String title, String message, final BooleanCallback callback) {
		final Window dialog = new Window();
		dialog.setAutoCenter(true);
		dialog.setIsModal(true);
		dialog.setShowHeader(true);
		dialog.setWidth(330);
		dialog.setHeight(150);
		dialog.setAlign(Alignment.CENTER);
		dialog.setAlign(VerticalAlignment.CENTER);

		dialog.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		dialog.setCanDragResize(false);
		dialog.setCanDrag(true);
		dialog.centerInPage();
		dialog.setTitle(title);

		VStack container = new VStack();
		container.setWidth("100%");
		container.setMembersMargin(10);
		container.setTop(8);
		container.setMargin(4);
		container.setAlign(Alignment.CENTER);
		container.setDefaultLayoutAlign(Alignment.CENTER);

		DynamicForm textForm = new DynamicForm();
		textForm.setTitleOrientation(TitleOrientation.TOP);
		textForm.setAlign(Alignment.CENTER);
		textForm.setNumCols(1);
		StaticTextItem text = ItemFactory.newStaticTextItem("text", "", message);
		text.setShouldSaveValue(false);
		text.setWrapTitle(false);
		text.setAlign(Alignment.CENTER);
		textForm.setFields(text);

		Button yes = new Button(I18N.message("yes"));
		yes.setWidth(70);
		yes.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				callback.execute(true);
				dialog.destroy();
			}
		});

		Button no = new Button(I18N.message("no"));
		no.setWidth(70);
		no.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				callback.execute(false);
				dialog.destroy();
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(10);
		buttons.setWidth100();
		buttons.setHeight(20);
		buttons.addMember(yes);
		buttons.addMember(no);
		buttons.setAlign(Alignment.CENTER);

		container.addMember(textForm);
		container.addMember(buttons);

		dialog.addItem(container);
		dialog.show();
	}
}
