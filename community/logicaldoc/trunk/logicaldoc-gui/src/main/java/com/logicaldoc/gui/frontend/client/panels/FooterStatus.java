package com.logicaldoc.gui.frontend.client.panels;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class FooterStatus extends HLayout {
	private static FooterStatus instance = new FooterStatus();

	private Label statusLabel = new Label("");

	private String detail = "";

	private String message = "";

	private String severity;

	private Date date;

	private FooterStatus() {
		setWidth100();
		setAlign(Alignment.RIGHT);
		statusLabel.setStyleName("footerInfo");
		statusLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.getFormat(Constants.FORMAT_DATE));
				Label label = new Label(formatter.format((Date) date) + ": <b>" + message + "</b><br><br>"
						+ (detail != null ? detail : ""));
				label.setWidth100();
				label.setHeight100();
				label.setPadding(5);
				label.setValign(VerticalAlignment.TOP);

				Window window = new Window();
				window.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
				window.setAutoSize(true);
				window.setIsModal(true);
				window.setWidth(200);
				window.setHeight(100);
				window.setTitle(I18N.getMessage(severity));
				window.centerInPage();
				window.setCanDragReposition(true);
				window.setCanDragResize(true);
				window.addItem(label);
				window.setPadding(5);

				window.show();
				clear();
			}
		});
		addMember(statusLabel);
	}

	public static FooterStatus getInstance() {
		return instance;
	}

	public void error(String message, String detail) {
		statusLabel.setStyleName("footerError");
		statusLabel.setContents(message);
		this.message = message;
		this.severity = "error";
		this.detail = detail;
		this.date = new Date();
	}

	public void warn(String message, String detail) {
		statusLabel.setStyleName("footerWarn");
		statusLabel.setContents(message);
		this.message = message;
		this.severity = "warning";
		this.detail = detail;
		this.date = new Date();
	}

	public void info(String message, String detail) {
		statusLabel.setStyleName("footerInfo");
		statusLabel.setContents(message);
		this.detail = detail;
		this.message = message;
		this.severity = "info";
		this.date = new Date();
	}

	public void clear() {
		info("", null);
	}
}