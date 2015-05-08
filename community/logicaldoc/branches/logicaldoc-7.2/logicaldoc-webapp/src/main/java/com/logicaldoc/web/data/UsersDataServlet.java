package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for users data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class UsersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(UsersDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			String groupIdOrName = request.getParameter("groupId");
			boolean required= "true".equals(request.getParameter("required"));
			
			
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.print("<list>");
			
			if(!required)
				writer.print("<user><id></id><username></username><name></name></user>");

			if (groupIdOrName != null && !groupIdOrName.trim().isEmpty()) {
				GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				Group group = null;
				try {
					group = groupDao.findById(Long.parseLong(groupIdOrName));
				} catch (Throwable t) {
				}
				if (group == null)
					group = groupDao.findByName(groupIdOrName, session.getTenantId());
				groupDao.initialize(group);
				
				/*
				 * Iterate over records composing the response XML document
				 */
				for (User user : group.getUsers()) {
					if (user.getType() != User.TYPE_DEFAULT)
						continue;

					writer.print("<user>");
					writer.print("<id>" + user.getId() + "</id>");
					writer.print("<username><![CDATA[" + user.getUserName() + "]]></username>");
					if (user.getEnabled() == 1)
						writer.print("<eenabled>0</eenabled>");
					else if (user.getEnabled() == 0)
						writer.print("<eenabled>2</eenabled>");
					writer.print("<name><![CDATA[" + (user.getName() == null ? "" : user.getName()) + "]]></name>");
					writer.print("<firstName><![CDATA[" + (user.getFirstName() == null ? "" : user.getFirstName())
							+ "]]></firstName>");
					writer.print("<label><![CDATA[" + (user.getFullName() == null ? "" : user.getFullName())
							+ "]]></label>");
					writer.print("<email><![CDATA[" + (user.getEmail() == null ? "" : user.getEmail()) + "]]></email>");
					writer.print("<phone><![CDATA[" + (user.getTelephone() == null ? "" : user.getTelephone())
							+ "]]></phone>");
					writer.print("<cell><![CDATA[" + (user.getTelephone2() == null ? "" : user.getTelephone2())
							+ "]]></cell>");
					writer.print("<certSubject><![CDATA["
							+ (user.getCertSubject() == null ? "" : user.getCertSubject()) + "]]></certSubject>");
					writer.print("<keyDigest>" + (user.getKeyDigest() == null ? "" : user.getKeyDigest())
							+ "</keyDigest>");
					writer.print("<usergroup>" + user.getUserGroup().getId() + "</usergroup>");
					writer.print("</user>");
				}
			} else {
				UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				String query = "select A.ld_id, A.ld_username, A.ld_enabled, A.ld_name, A.ld_firstname, A.ld_email, A.ld_telephone, "
						+ "A.ld_telephone2, A.ld_certsubject, A.ld_keydigest, B.ld_id "
						+ "from ld_user A, ld_group B where A.ld_deleted = 0 and A.ld_type = 0 and B.ld_type=1 and A.ld_tenantid="
						+ session.getTenantId()
						+ " and B.ld_id in(select ld_groupid from ld_usergroup where ld_userid=A.ld_id)";

				@SuppressWarnings("unchecked")
				List<User> records = (List<User>) userDao.query(query, null, new RowMapper<User>() {
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User();
						user.setId(rs.getLong(1));
						user.setUserName(rs.getString(2));
						user.setEnabled(rs.getInt(3));
						user.setName(rs.getString(4));
						user.setFirstName(rs.getString(5));
						user.setEmail(rs.getString(6));
						user.setTelephone(rs.getString(7));
						user.setTelephone2(rs.getString(8));
						user.setCertSubject(rs.getString(9));
						user.setKeyDigest(rs.getString(10));
						Group group = new Group();
						group.setId(rs.getLong(11));
						group.setName("_user_" + user.getId());
						user.getGroups().add(group);

						return user;
					}
				}, null);

				/*
				 * Iterate over records composing the response XML document
				 */
				for (User user : records) {
					writer.print("<user>");
					writer.print("<id>" + user.getId() + "</id>");
					writer.print("<username><![CDATA[" + user.getUserName() + "]]></username>");
					if (user.getEnabled() == 1)
						writer.print("<eenabled>0</eenabled>");
					else if (user.getEnabled() == 0)
						writer.print("<eenabled>2</eenabled>");
					writer.print("<name><![CDATA[" + (user.getName() == null ? "" : user.getName()) + "]]></name>");
					writer.print("<firstName><![CDATA[" + (user.getFirstName() == null ? "" : user.getFirstName())
							+ "]]></firstName>");
					writer.print("<label><![CDATA[" + (user.getFullName() == null ? "" : user.getFullName())
							+ "]]></label>");
					writer.print("<email><![CDATA[" + (user.getEmail() == null ? "" : user.getEmail()) + "]]></email>");
					writer.print("<phone><![CDATA[" + (user.getTelephone() == null ? "" : user.getTelephone())
							+ "]]></phone>");
					writer.print("<cell><![CDATA[" + (user.getTelephone2() == null ? "" : user.getTelephone2())
							+ "]]></cell>");
					writer.print("<certSubject><![CDATA["
							+ (user.getCertSubject() == null ? "" : user.getCertSubject()) + "]]></certSubject>");
					writer.print("<keyDigest><![CDATA[" + (user.getKeyDigest() == null ? "" : user.getKeyDigest())
							+ "]]></keyDigest>");
					writer.print("<usergroup>" + user.getGroups().iterator().next().getId() + "</usergroup>");
					writer.print("</user>");
				}
			}
			writer.print("</list>");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (e instanceof ServletException)
				throw (ServletException) e;
			else if (e instanceof IOException)
				throw (IOException) e;
			else
				throw new ServletException(e.getMessage(), e);
		}
	}
}