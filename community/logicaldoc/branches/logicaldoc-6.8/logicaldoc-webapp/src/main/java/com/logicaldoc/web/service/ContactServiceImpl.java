package com.logicaldoc.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.contact.Contact;
import com.logicaldoc.core.contact.ContactDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIContact;
import com.logicaldoc.gui.frontend.client.services.ContactService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the ContactService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8
 */
public class ContactServiceImpl extends RemoteServiceServlet implements ContactService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Override
	public void delete(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContactDAO dao = (ContactDAO) Context.getInstance().getBean(ContactDAO.class);
			for (long id : ids) {
				dao.delete(id);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	@Override
	public void save(String sid, GUIContact contact) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			ContactDAO dao = (ContactDAO) Context.getInstance().getBean(ContactDAO.class);
			Contact con = dao.findById(contact.getId());
			if (con == null)
				con = new Contact();
			con.setEmail(contact.getEmail());
			con.setFirstName(contact.getFirstName());
			con.setLastName(contact.getLastName());
			con.setCompany(contact.getCompany());
			con.setAddress(contact.getAddress());
			con.setPhone(contact.getPhone());
			con.setMobile(contact.getMobile());
			con.setUserId(contact.getUserId());
			dao.store(con);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	@Override
	public GUIContact load(String sid, long id) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContactDAO dao = (ContactDAO) Context.getInstance().getBean(ContactDAO.class);
			Contact contact = dao.findById(id);
			return fromContact(contact);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	protected GUIContact fromContact(Contact con) {
		if (con == null)
			return null;

		GUIContact c = new GUIContact();
		c.setId(con.getId());
		c.setUserId(con.getUserId());
		c.setEmail(con.getEmail());
		c.setFirstName(con.getFirstName());
		c.setLastName(con.getLastName());
		c.setCompany(con.getCompany());
		c.setAddress(con.getAddress());
		c.setPhone(con.getPhone());
		c.setMobile(con.getMobile());
		return c;
	}
}