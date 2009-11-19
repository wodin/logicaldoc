package com.logicaldoc.web.settings;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.admin.UsersRecordsManager;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Personal data editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PersonalDataForm {
	protected static Log log = LogFactory.getLog(PersonalDataForm.class);

	private String name;

	private String firstName;

	private String street;

	private String postalCode;

	private String city;

	private String country;

	private String language;

	private String email;

	private String phone;
	
	private String phone2;

	public PersonalDataForm() {
		super();

		User user = SessionManagement.getUser();
		name = user.getName();
		firstName = user.getFirstName();
		street = user.getStreet();
		postalCode = user.getPostalcode();
		city = user.getCity();
		country = user.getCountry();
		language = user.getLanguage();
		email = user.getEmail();
		phone = user.getTelephone();
		phone2 = user.getTelephone2();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String save() {
			try {
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				User user = SessionManagement.getUser();
				user.setFirstName(firstName);
				user.setName(name);
				user.setCity(city);
				user.setStreet(street);
				user.setCountry(country);
				user.setEmail(email);
				user.setLanguage(language);
				user.setPostalcode(postalCode);
				user.setTelephone(phone);
				user.setTelephone2(phone2);

				boolean stored = dao.store(user);

				if (!stored) {
					Messages.addLocalizedError("errors.action.saveuser.notstored");
				} else {
					Messages.addLocalizedInfo("msg.action.changeuser");
				}

				user.getGroupIds();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.saveuser.notstored");
			}

			return null;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
}
