package com.logicaldoc.web.document;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.web.SessionManagement;

/**
 * Retrieves all document fields preferences for the session user. For each user
 * group will be retrieved all the group custom attributes that begin with
 * "preference.field". For each custom attribute, will be parsed the
 * correspondent value. There two possible phases: 'insert' and 'edit'. The
 * custom value consisting of six digits('0' or '1') corresponding to:
 * <ol>
 * <li>insert hidden</li>
 * <li>insert readonly</li>
 * <li>insert mandatory</li>
 * <li>edit hidden</li>
 * <li>edit readonly</li>
 * <li>edit mandatory</li>
 * </ol>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 * 
 */
public class FieldPreferences extends HashMap<String, Boolean> {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(FieldPreferences.class);

	public FieldPreferences() {
		super();
	}

	/**
	 * Load the Fields Preferences Map
	 */
	private void load() {
		User user = SessionManagement.getUser();
		Set<Group> userGroups = user.getGroups();
		for (Group group : userGroups) {
			Set<String> groupAttributeNames = group.getAttributeNames();
			for (String attributeName : groupAttributeNames) {
				if (attributeName.contains("preference.field.")) {
					String field = attributeName.substring(attributeName.lastIndexOf(".") + 1);
					String fieldValue = group.getValue(attributeName).toString();
					String preference = "insert." + field + ".hidden";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(0);
						put(preference, val == '0' ? false : true);
					}
					preference = "insert." + field + ".readonly";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(1);
						put(preference, val == '0' ? false : true);
					}
					preference = "insert." + field + ".mandatory";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(2);
						put(preference, val == '0' ? false : true);
					}
					preference = "edit." + field + ".hidden";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(3);
						put(preference, val == '0' ? false : true);
					}
					preference = "edit." + field + ".readonly";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(4);
						put(preference, val == '0' ? false : true);
					}
					preference = "edit." + field + ".mandatory";
					if (super.get(preference) == null || !super.get(preference)) {
						char val = fieldValue.charAt(5);
						put(preference, val == '0' ? false : true);
					}
				}
			}
		}
	}

	@Override
	public Boolean get(Object key) {
		if (size() == 0)
			load();
		if (!containsKey(key))
			return false;
		else
			return super.get(key);
	}
}