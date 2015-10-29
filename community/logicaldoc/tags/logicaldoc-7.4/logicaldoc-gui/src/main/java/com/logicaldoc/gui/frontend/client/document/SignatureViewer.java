package com.logicaldoc.gui.frontend.client.document;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to view signatures.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SignatureViewer extends Window {
	private VLayout layout = new VLayout();

	public SignatureViewer(String docId, String fileName, String[] signers) {
		layout.setMargin(25);
		layout.setMembersMargin(5);
		layout.setTop(10);

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("signature"));
		setWidth(380);
		setHeight(110);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		DynamicForm signatureForm = new DynamicForm();
		SelectItem certificates = ItemFactory.newSelectItem("certificates", I18N.message("signer"));
		certificates.setWidth(250);
		certificates.setRequired(true);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (signers != null)
			for (String signer : signers) {
				map.put(signer, signer);
			}
		certificates.setValueMap(map);
		if (!map.isEmpty())
			certificates.setValue(map.keySet().iterator().next());
		signatureForm.setItems(certificates);
		layout.addMember(signatureForm, 0);

		DynamicForm urlForm = new DynamicForm();
		urlForm.setMargin(3);
		LinkItem downloadUrl = ItemFactory.newLinkItem("", I18N.message("downloadsignedfile"));
		downloadUrl.setTitleOrientation(TitleOrientation.LEFT);
		downloadUrl.setWrapTitle(false);
		downloadUrl.setValue(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId
				+ "&suffix=sign.p7m");
		downloadUrl.setLinkTitle(fileName);

		urlForm.setItems(downloadUrl);
		layout.addMember(urlForm, 1);

		addChild(layout);
	}
}