package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIBarcodeEngine;
import com.logicaldoc.gui.common.client.beans.GUIBarcodePattern;
import com.logicaldoc.gui.frontend.client.services.BarcodeService;

/**
 * Mock implementation of the BarcodeService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
public class MockBarcodeServiceImpl extends RemoteServiceServlet implements BarcodeService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIBarcodeEngine getInfo(String sid) {
		GUIBarcodeEngine barcodeEngine = new GUIBarcodeEngine();
		return barcodeEngine;
	}

	@Override
	public void save(String sid, GUIBarcodeEngine searchEngine) {

	}

	@Override
	public void rescheduleAll(String sid) throws InvalidSessionException {

	}

	@Override
	public void markUnprocessable(String sid, long[] ids) throws InvalidSessionException {

	}

	@Override
	public GUIBarcodePattern[] loadPatterns(String sid, Long templateId) throws InvalidSessionException {
		GUIBarcodePattern p = new GUIBarcodePattern();
		p.setTemplateId(1L);
		p.setPosition(1);
		p.setPattern("<customid>");
		return new GUIBarcodePattern[] { p };
	}

	@Override
	public void savePatterns(String sid, String[] patterns, Long templateId) throws InvalidSessionException {

	}
}