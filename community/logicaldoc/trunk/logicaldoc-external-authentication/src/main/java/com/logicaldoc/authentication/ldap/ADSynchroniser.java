package com.logicaldoc.authentication.ldap;

import org.apache.commons.logging.LogFactory;

/**
 * Specialization of the LDAPSinchronizer that is used for Active Directory
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class ADSynchroniser extends LDAPSynchroniser {
	public ADSynchroniser() {
		super("ADSynchroniser");
		log = LogFactory.getLog(ADSynchroniser.class);
	}
}
