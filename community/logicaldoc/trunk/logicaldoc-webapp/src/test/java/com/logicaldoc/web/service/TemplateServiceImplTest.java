package com.logicaldoc.web.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.web.AbstractWebappTCase;

public class TemplateServiceImplTest extends AbstractWebappTCase {

	// Instance under test
	private TemplateServiceImpl service = new TemplateServiceImpl();

	private GUISession session;

	private DocumentTemplateDAO templateDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		templateDao = (DocumentTemplateDAO) context.getBean("DocumentTemplateDAO");

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin", null, null);
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testDelete() throws ServerException {
		service.delete(session.getSid(), 1);
		DocumentTemplate template = templateDao.findById(1);
		Assert.assertNull(template);
	}

	@Test
	public void testSave() throws ServerException {
		GUITemplate template = service.getTemplate(session.getSid(), 1);
		Assert.assertNotNull(template);
		Assert.assertEquals("test1", template.getName());
		Assert.assertEquals("test1_desc", template.getDescription());

		template.setName("pippo");
		template.setDescription("paperino");

		template = service.save(session.getSid(), template);

		template = service.getTemplate(session.getSid(), 1);
		Assert.assertNotNull(template);
		Assert.assertEquals("pippo", template.getName());
		Assert.assertEquals("paperino", template.getDescription());
		Assert.assertEquals(1, template.getAttributes().length);
		Assert.assertTrue(template.getAttributes()[0].getName().equals("attr1"));
		Assert.assertTrue(template.getAttributes()[0].getStringValue().equals("val1"));
	}

	@Test
	public void testGetTemplate() throws ServerException {
		GUITemplate template = service.getTemplate(session.getSid(), 2);
		Assert.assertNotNull(template);
		Assert.assertEquals("test2", template.getName());
		Assert.assertEquals("test2_desc", template.getDescription());

		template = service.getTemplate(session.getSid(), 3);
		Assert.assertNull(template);
	}
}
