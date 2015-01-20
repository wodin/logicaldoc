package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.EventPanel;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.validators.EmailValidator;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RichTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.EditorExitEvent;
import com.smartgwt.client.widgets.grid.events.EditorExitHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

/**
 * This is the form used to send emails and download tickets
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EmailDialog extends Window {
	private long[] docIds;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ValuesManager vm = new ValuesManager();

	private ListGrid recipientsGrid;

	public EmailDialog(long[] docIds, String docTitle) {
		super();
		this.docIds = docIds;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendmail"));
		setWidth(550);
		setHeight(430);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(false);

		SectionStack recipientsStack = prepareRecipientsGrid();

		final DynamicForm form = new DynamicForm();
		form.setID("emailform");
		form.setValuesManager(vm);
		form.setWidth100();
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		final TextItem subject = ItemFactory.newTextItem("subject", "subject", docTitle);
		subject.setRequired(true);
		subject.setWidth(250);

		final RichTextItem message = new RichTextItem();
		message.setName("message");
		message.setTitle(I18N.message("message"));
		message.setValue("");
		message.setWidth("*");
		message.setHeight("*");

		final CheckboxItem pdf = new CheckboxItem("pdf");
		pdf.setTitle(I18N.message("sendpdfconversion"));
		pdf.setVisible(Feature.enabled(Feature.PDF));

		final CheckboxItem ticket = new CheckboxItem("sendticket");
		ticket.setTitle(I18N.message("sendticket"));
		ticket.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				if ((ticket.getValue() != null && ticket.getValueAsBoolean()) || !Feature.enabled(Feature.PDF)){
					pdf.setValue(false);
					pdf.hide();
				}else{
					pdf.show();
				}
			}
		});

		final CheckboxItem zip = new CheckboxItem();
		zip.setName("zip");
		zip.setTitle(I18N.message("zipattachments"));

		final ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					GUIEmail mail = new GUIEmail();
					mail.setSubject(subject.getValueAsString());
					mail.setMessage(message.getValue().toString());
					mail.setSendAsTicket(ticket.getValue() != null && ticket.getValueAsBoolean());
					mail.setPdfConversion(pdf.getValue() != null && pdf.getValueAsBoolean());
					mail.setZipCompression(zip.getValue() != null && zip.getValueAsBoolean());
					mail.setDocIds(EmailDialog.this.docIds);

					List<String> to = new ArrayList<String>();
					List<String> cc = new ArrayList<String>();
					ListGridRecord[] records = recipientsGrid.getRecords();
					for (int i = 0; i < records.length; i++) {
						if (!recipientsGrid.validateCell(i, "email"))
							continue;

						ListGridRecord record = records[i];
						if (record.getAttribute("email") == null || record.getAttribute("type").trim().equals(""))
							continue;
						if ("to".equals(record.getAttribute("type")))
							to.add(record.getAttribute("email").trim());
						else
							cc.add(record.getAttribute("email").trim());
					}

					if (to.isEmpty() && cc.isEmpty()) {
						SC.warn(I18N.message("leastvalidrecipient"));
						return;
					}

					sendItem.disable();

					mail.setRecipients(to.toString().substring(1, to.toString().length() - 1));
					mail.setCc(cc.toString().substring(1, cc.toString().length() - 1));

					ContactingServer.get().show();

					documentService.sendAsEmail(Session.get().getSid(), mail, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							ContactingServer.get().hide();
							Log.serverError(caught);
							sendItem.enable();
							destroy();
						}

						@Override
						public void onSuccess(String result) {
							ContactingServer.get().hide();
							sendItem.enable();
							if ("ok".equals(result)) {
								EventPanel.get().info(
										I18N.message("messagesent") + ". " + I18N.message("documentcopysent"), null);
							} else {
								EventPanel.get().error(I18N.message("messagenotsent"), null);
							}
							destroy();
						}
					});
				}
			}
		});

		// The download ticket is available on single selection only
		if (docIds.length == 1)
			form.setFields(subject, ticket, pdf, message, sendItem);
		else
			form.setFields(subject, zip, pdf, message, sendItem);

		addItem(recipientsStack);
		addItem(form);
	}

	private SectionStack prepareRecipientsGrid() {
		SectionStack sectionStack = new SectionStack();
		sectionStack.setWidth100();
		sectionStack.setHeight(130);
		sectionStack.setMargin(6);
		SectionStackSection section = new SectionStackSection("<b>" + I18N.message("recipients") + "</b>");
		section.setCanCollapse(false);
		section.setExpanded(true);

		ListGridField email = new ListGridField("email", I18N.message("email"));
		email.setWidth("*");
		email.setCanFilter(true);
		FormItem emailItem = ItemFactory.newEmailSelector("email", "email");
		emailItem.setRequired(true);
		emailItem.setWidth("100%");
		emailItem.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				// Delete the row
				if (event.getKeyName().equals("Backspace")) {
					ListGridRecord selection = recipientsGrid.getSelectedRecord();
					if (selection.getAttribute("email") == null
							|| selection.getAttribute("email").toString().equals(""))
						if (recipientsGrid.getDataAsRecordList().getLength() > 1)
							recipientsGrid.removeSelectedData();
				}

			}
		});
		email.setEditorType(emailItem);
		email.setValidators(new EmailValidator());

		ListGridField type = new ListGridField("type", I18N.message(" "));
		type.setCanFilter(true);
		type.setWidth(50);
		type.setCanEdit(true);
		type.setEditorType(ItemFactory.newRecipientTypeSelector("type"));

		recipientsGrid = new ListGrid();
		recipientsGrid.setShowRecordComponents(true);
		recipientsGrid.setShowRecordComponentsByCell(true);
		recipientsGrid.setAutoFetchData(true);
		recipientsGrid.setCanEdit(true);
		recipientsGrid.setShowHeader(false);
		recipientsGrid.setWidth100();
		recipientsGrid.setEditEvent(ListGridEditEvent.CLICK);
		recipientsGrid.setFields(type, email);

		recipientsGrid.addEditCompleteHandler(new EditCompleteHandler() {

			@Override
			public void onEditComplete(EditCompleteEvent event) {
				addEmptyRow();
			}
		});

		recipientsGrid.addEditorExitHandler(new EditorExitHandler() {

			@Override
			public void onEditorExit(EditorExitEvent event) {
				addEmptyRow();
			}
		});

		recipientsGrid.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (value == null)
					return null;
				if (colNum == 0)
					return I18N.message(value.toString());
				else
					return value.toString();
			}
		});

		ListGridRecord record = new ListGridRecord();
		record.setAttribute("type", "to");
		record.setAttribute("email", "");
		recipientsGrid.setRecords(new ListGridRecord[] { record });

		section.setItems(recipientsGrid);
		sectionStack.setSections(section);
		return sectionStack;
	}

	private void addEmptyRow() {
		ListGridRecord[] records = recipientsGrid.getRecords();
		// Search for an empty record
		for (ListGridRecord rec : records) {
			if (rec.getAttribute("email") == null || rec.getAttribute("email").trim().equals(""))
				return;
		}

		ListGridRecord[] newRecords = new ListGridRecord[records.length + 1];
		for (int i = 0; i < records.length; i++)
			newRecords[i] = records[i];
		newRecords[records.length] = new ListGridRecord();
		newRecords[records.length].setAttribute("type", "to");
		newRecords[records.length].setAttribute("email", "");
		recipientsGrid.setRecords(newRecords);
	}
}
