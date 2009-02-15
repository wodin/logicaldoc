package com.logicaldoc.authentication.ldap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ldap.LdapTemplate;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class ExpImpSynchronisationJob {

	private GroupDAO groupDao;
	
	private UserDAO userDao;
	
	private LDAPSynchronisationContext synchronisationContext;
	
	private LdapTemplate ldapTemplate;
	
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	public void setGroupDao(GroupDAO groupDao) {
		this.groupDao = groupDao;
	}
	
	public void setSynchronisationContext(
			LDAPSynchronisationContext synchronisationContext) {
		this.synchronisationContext = synchronisationContext;
	}
	
	public LDAPSynchronisationContext getSynchronisationContext() {
		return synchronisationContext;
	}
	
	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}
	
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	public static void main(String[] args){
		/*
		 	Resource resource = new ClassPathResource("com/logicaldoc/authentication/ldap/ldap-security-context.xml");
		 
			BeanFactory factory = new XmlBeanFactory(resource);
			ExpImpSynchronisationJob job = (ExpImpSynchronisationJob)factory.getBean("synchronisationJob");
		*/
	}
	
	public void doImport(List<LdapUser> users, List<LdapGroup> groups){

		Map<String, Group> systemGroups = new HashMap<String, Group>(); 
		
		
		Map<String, User> userMap = new HashMap<String, User>();
		Map<String, LdapGroup> ldapGroupMap = new HashMap<String, LdapGroup>();
		
		for(LdapUser ldapUser : users)
			userMap.put(ldapUser.dn.toString(), ldapUser.user);
		
		for(LdapGroup ldapGroup : groups)
			ldapGroupMap.put(ldapGroup.dn.toString(), ldapGroup);
		
		System.out.println();
		System.out.println();
		
		//we need a pseudoId as we need a preceeding numbering 
		//of groups within users (must be unequal to equals method)
		int pseudoId = 1;
		//iterating on every group
		for (LdapGroup ldapGroup : groups) {
			//obtain every user in a group and add this group to the user
			for(String ldapUser: ldapGroup.users ){
				Group group = new Group();
				group.setName(ldapGroup.name);
				User user = (User)userMap.get(ldapUser.toString().toLowerCase());
				
				if(user == null)
					continue;
				
				Group _group = systemGroups.get(ldapGroup.name);
				
				if(_group == null){
					_group = new Group();
					_group.setName(ldapGroup.name);
					_group.setId(pseudoId);
					systemGroups.put(ldapGroup.name, _group);
				}
				
				user.getGroups().add(_group);
			}
			
			pseudoId++;
		}
		
		Collection<Group> _groups = systemGroups.values();
		createOrUpdateGroups(_groups);
		
		
		Collection<User> _users = userMap.values();
		createOrUpdateUsers(_users);
		assignGroupsToUsers(_users);
	}
	
	private void createOrUpdateGroups(Collection<Group> groups){
		for (Group group : groups) {
			
			Group _group = groupDao.findByName(group.getName());
			if(_group != null) {
				groupDao.delete(_group.getId());
				_group = null;
			}
			
			if(_group != null){
				group.setId(_group.getId());
				group.setLastModified(group.getLastModified());
				group.setDeleted(_group.getDeleted());
				group.setAttributes(_group.getAttributes());
				continue;
			}
			
			group.setId(0);
			groupDao.insert(group, 0);
			System.out.println(group);
		}
	}
	
	private void createOrUpdateUsers(Collection<User> users){
		
		for (User user : users) {
			User _user = userDao.findByUserName(user.getUserName());
			
			//if the user exists, no changes will be made
			if(_user != null){
				userDao.delete(_user.getId() );
			}
			Set<Group> grps = user.getGroups();
			user.getGroups().removeAll(grps);
			
			userDao.store(user);
		}
	}
	
	private void assignGroupsToUsers(Collection<User> users){
		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		for (User user : users) {
			manager.assignUserToGroups(user, user.getGroupIds());
		}
	}
}
