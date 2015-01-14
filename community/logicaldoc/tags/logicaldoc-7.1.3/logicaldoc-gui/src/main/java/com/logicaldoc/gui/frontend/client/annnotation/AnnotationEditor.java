package com.logicaldoc.gui.frontend.client.annnotation;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RichTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used write an annotation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2
 */
public class AnnotationEditor extends Window {

	protected ValuesManager vm = new ValuesManager();

	public AnnotationEditor(final long docId) {
		super();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("annotation"));
		setWidth(500);
		setHeight(280);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setWidth100();
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		final RichTextItem text = new RichTextItem();
		text.setName("annotation");
		text.setTitle(I18N.message("annotation"));
		text.setValue("");
		text.setWidth(490);
		text.setHeight(200);

		final ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					addAnnotation("1234", event.getForm().getValueAsString("annotation"));
					destroy();
				}
			}
		});

		form.setFields(text, save);

		addItem(form);
	}

	public native void addAnnotation(String annotationId, String text)/*-{
		var userSelection = $wnd.annGetContent().getSelection().getRangeAt(0);
		var safeRanges = $wnd.annGetSafeRanges(userSelection);

		//Creates a div with the annotation's text
		var newDiv = $wnd.annGetContent().document.createElement("div");
		$wnd.annGetContent().document.body.appendChild(newDiv);
		newDiv.setAttribute("id", "ann-" + annotationId);
		newDiv
				.setAttribute(
						"style",
						"display: none; border: 1px solid grey; background-color: yellow; position:absolute; left:50px; top:50px;");
		newDiv.textContent = text;

		for (var i = 0; i < safeRanges.length; i++) {
			$wnd.annAddAnnotationInRange(safeRanges[i], annotationId, text);
		}
	}-*/;
}