package com.logicaldoc.authentication.ldap;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.logicaldoc.util.Context;

/**
 * Just for testpurposes! To start the import, 
 * please enter the url host:port/ldapimport/
 * 
 *  
 * @author Sebastian Wenzky
 *
 */
@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet{
	
	ExpImpSynchronisationJob job;
	
	@Override
	public void init() throws ServletException {
		super.init();
		doInit();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		doInit();
	}
	
	private void doInit(){
		WebApplicationContext webCtx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		try {
			job = (ExpImpSynchronisationJob)webCtx.getBean("synchronisationJob");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		UserGroupMappingImpl ldapUserGroupDao = (UserGroupMappingImpl)Context.getInstance().getBean("ldapUserGroupDao");
		List<LdapGroup> groups = ldapUserGroupDao.getAllGroups();
		
		List<LdapUser> users = ldapUserGroupDao.getAllUsers();
		
		job.doImport(users, groups);
	}
}
