package com.logicaldoc.gui.frontend.client.document;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.search.HitsListPanel;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class RatingDialog extends Window {
	private int docRating;

	private GUIRating rating = null;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ValuesManager vm = new ValuesManager();

	protected DocumentObserver observer;

	public RatingDialog(int documentRating, GUIRating rat, DocumentObserver observer) {
		super();
		this.docRating = documentRating;
		this.rating = rat;
		this.observer = observer;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("rating"));
		setWidth(250);
		setHeight(150);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);
		setAlign(Alignment.LEFT);

		VLayout layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(5);

		final DynamicForm ratingForm = new DynamicForm();
		ratingForm.setAlign(Alignment.LEFT);
		ratingForm.setTitleOrientation(TitleOrientation.LEFT);
		ratingForm.setNumCols(1);
		ratingForm.setValuesManager(vm);

		FormItemIcon ratingIcon = ItemFactory.newItemIcon("rating" + this.docRating + ".png");
		StaticTextItem actualRating = ItemFactory.newStaticTextItem("actualrating", "actualrating", "");
		actualRating.setIcons(ratingIcon);
		actualRating.setIconWidth(88);

		StaticTextItem votesNumber = ItemFactory.newStaticTextItem("votesNumber", "votesnumber", this.rating.getCount()
				.toString());
		votesNumber.setWrapTitle(false);
		votesNumber.setWrap(false);
		votesNumber.setAlign(Alignment.LEFT);

		String average = this.rating.getAverage().toString();
		if (average.contains(".") && average.substring(average.lastIndexOf(".")).length() > 2)
			average = average.substring(0, average.lastIndexOf(".") + 3);
		StaticTextItem votesAvg = ItemFactory.newStaticTextItem("average", "average", average);
		votesAvg.setWrapTitle(false);
		votesAvg.setWrap(false);
		votesAvg.setAlign(Alignment.LEFT);

		final SelectItem starsSelection = new SelectItem("stars", I18N.message("vote"));
		starsSelection.setWrapTitle(false);
		starsSelection.setWidth(60);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", " ");
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		starsSelection.setValueMap(map);
		starsSelection.setPickListWidth(60);

		ButtonItem vote = new ButtonItem();
		vote.setTitle(I18N.message("vote"));
		vote.setAutoFit(true);
		vote.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					RatingDialog.this.rating.setUserId(Session.get().getUser().getId());
					RatingDialog.this.rating.setVote(Integer.parseInt(vm.getValueAsString("stars")));

					documentService.saveRating(Session.get().getSid(), RatingDialog.this.rating,
							new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
									destroy();
								}

								@Override
								public void onSuccess(Integer rating) {
									// We have to reload the document because
									// the rating is changed. We need to know if
									// this operation into the Documents list
									// panel or into the Search list panel.

									if (RatingDialog.this.observer == null
											|| RatingDialog.this.observer instanceof DocumentsPanel) {
										DocumentsPanel.get().getDocumentsGrid().updateRating(rating);
										DocumentsPanel.get().selectDocument(
												DocumentsPanel.get().getDocumentsGrid().getSelectedDocument().getId(),
												false);
									} else if (RatingDialog.this.observer instanceof HitsListPanel) {
										GUIDocument doc = SearchPanel.get().getGrid().getSelectedDocument();
										SearchPanel.get().getGrid().updateRating(rating);
										SearchPanel.get().onSelectedDocumentHit(doc.getId());
									}

									Log.info(I18N.message("votesaved"), null);
									destroy();
								}
							});
				}
				destroy();
			}
		});

		final DynamicForm alreadyVotedForm = new DynamicForm();
		alreadyVotedForm.setAlign(Alignment.LEFT);
		alreadyVotedForm.setTitleOrientation(TitleOrientation.TOP);
		alreadyVotedForm.setNumCols(1);

		if (this.rating.getUserId() > 0)
			ratingForm.setItems(actualRating, votesNumber, votesAvg);
		else
			ratingForm.setItems(actualRating, votesNumber, votesAvg, starsSelection, vote);
		layout.addMember(ratingForm);

		StaticTextItem alreadyVoted = ItemFactory.newStaticTextItem("alreadyVoted", "",
				"<b>" + I18N.message("alreadyvoted") + "</b>");
		alreadyVoted.setShouldSaveValue(false);
		alreadyVoted.setAlign(Alignment.LEFT);
		alreadyVoted.setTextBoxStyle("footerWarn");
		alreadyVoted.setWrapTitle(false);
		alreadyVoted.setWrap(false);

		alreadyVotedForm.setItems(alreadyVoted);
		if (this.rating.getUserId() > 0)
			layout.addMember(alreadyVotedForm);

		addItem(layout);
	}
}
