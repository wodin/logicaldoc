package com.logicaldoc.gui.frontend.client.annnotation;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This is a mini-app to handle annotations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2
 */
public class AnnotationsDialog extends Window {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private HTMLPane contentPane;

	private Canvas info = new Canvas();

	public AnnotationsDialog(final long docId, String title) {
		super();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("annotations") + " - " + title);
		setWidth100();
		setHeight100();
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("addannotation"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AnnotationEditor editor = new AnnotationEditor(docId);
				editor.show();
			}
		});

		toolStrip.setMembers(add);

		contentPane = new HTMLPane();
		contentPane.setWidth100();
		contentPane.setHeight(getHeight() - 80);
		contentPane.setShowEdges(true);
		contentPane.setContentsType(ContentsType.PAGE);
		contentPane
				.setContents("<iframe id='ann-content-panel' src='http://localhost:9080/annotation/page-1.html' style='width:100%;height:100%;border:none'/>");

		VLayout content = new VLayout();
		content.setMembers(toolStrip, contentPane);
		content.setHeight100();
		content.setWidth100();

		addItem(content);
	}

	@Override
	protected void onInit() {
		declareAPI(this);
	}

	public static void showAnnotation(long annotationId) {
		SC.say("Select the annotation " + annotationId);
	}

	/*****************************************************************
	 * Mini JavaScript API to handle annotations in the content pane *
	 *****************************************************************/
	public static native void declareAPI(AnnotationsDialog dialog)/*-{
		$wnd.annGetContent = function() {
			return $doc.getElementById("ann-content-panel").contentWindow;
		};

		$wnd.annGetSafeRanges = function(dangerous) {
			var a = dangerous.commonAncestorContainer;
			// Starts -- Work inward from the start, selecting the largest safe range
			var s = new Array(0), rs = new Array(0);
			if (dangerous.startContainer != a)
				for (var i = dangerous.startContainer; i != a; i = i.parentNode)
					s.push(i);

			if (0 < s.length)
				for (var i = 0; i < s.length; i++) {
					var xs = document.createRange();
					if (i) {
						xs.setStartAfter(s[i - 1]);
						xs.setEndAfter(s[i].lastChild);
					} else {
						xs.setStart(s[i], dangerous.startOffset);
						xs.setEndAfter((s[i].nodeType == Node.TEXT_NODE) ? s[i]
								: s[i].lastChild);
					}
					rs.push(xs);
				}

			// Ends -- basically the same code reversed
			var e = new Array(0), re = new Array(0);
			if (dangerous.endContainer != a)
				for (var i = dangerous.endContainer; i != a; i = i.parentNode)
					e.push(i);

			if (0 < e.length)
				for (var i = 0; i < e.length; i++) {
					var xe = document.createRange();
					if (i) {
						xe.setStartBefore(e[i].firstChild);
						xe.setEndBefore(e[i - 1]);
					} else {
						xe
								.setStartBefore((e[i].nodeType == Node.TEXT_NODE) ? e[i]
										: e[i].firstChild);
						xe.setEnd(e[i], dangerous.endOffset);
					}
					re.unshift(xe);
				}

			// Middle -- the uncaptured middle
			if ((0 < s.length) && (0 < e.length)) {
				var xm = document.createRange();
				xm.setStartAfter(s[s.length - 1]);
				xm.setEndBefore(e[e.length - 1]);
			} else {
				return [ dangerous ];
			}

			// Concat
			rs.push(xm);
			response = rs.concat(re);

			// Send to Console
			return response;
		}

		$wnd.annAddAnnotationInRange = function annAddAnnotationInRange(range,
				annotationId, text) {
			var newNode = $wnd.annGetContent().document.createElement("span");
			newNode.className = 'ann-highlight';
			newNode.setAttribute("annotationId", annotationId);
			newNode.setAttribute("onmouseover",
					"window.parent.showAnnotation('" + annotationId + "');");
			range.surroundContents(newNode);
		}

		$wnd.annRemoveAnnotation = function removeAnnotation(annotationId) {
			var annDiv = $wnd.annGetContent().document.getElementsById('ann-'
					+ annotationId);
			if (annDiv != null)
				annDiv.parentNode.removeChild(annDiv);

			var allElements = $wnd.annGetContent().document
					.getElementsByTagName('span');

			for (var i = 0; i < allElements.length; i++) {
				var spanElement = allElements[i];
				var annId = spanElement.getAttribute("annotationId");
				var entered = false;
				if (annId != null && annId == annotationId) {
					entered = true;
					var newParent = spanElement.parentNode;

					while (spanElement.childNodes.length > 0) {
						newParent.insertBefore(spanElement.childNodes[0],
								spanElement);
					}
					spanElement.remove();
				}
				if (entered)
					$wnd.annRemoveAnnotation(annotationId);
			}
		}
	}-*/;

	public native void removeAnnotation(String annotationId)/*-{
		$wnd.annRemoveAnnotation(annotationId);
	}-*/;
}