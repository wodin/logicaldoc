package com.logicaldoc.webservice.rest.auth;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.auth.AuthService;
import com.logicaldoc.webservice.auth.AuthServiceImpl;
import com.logicaldoc.webservice.rest.RestClient;

/**
 * Auth Web Service client (RESTful).
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class AuthClient extends RestClient implements AuthService {

	protected static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	public AuthClient(String endpoint) {
		super(endpoint);
	}

	@Override
	public String login(String username, String password) throws Exception {
		String output = null;
		String url = endpoint + "/login";

		PostMethod post = preparePostMethod(url);
		try {
			prepareHeader(post);

			post.setParameter("username", username);
			post.setParameter("password", password);

			int statusCode = client.executeMethod(post);

			if (statusCode == HttpStatus.SC_OK)
				output = post.getResponseBodyAsString();
			else
				throw new Exception("Server Error");
		} finally {
			post.releaseConnection();
		}

		if (StringUtils.isEmpty(output))
			throw new Exception("Invalid login");
		return output;
	}

	private void prepareHeader(PostMethod post) {
		Header header = new Header();
		header.setName("content-type");
		header.setValue(MediaType.APPLICATION_FORM_URLENCODED);
		header.setName("accept");
		header.setValue(MediaType.TEXT_PLAIN);
		post.addRequestHeader(header);
	}

	@Override
	public void logout(String sid) {
		String url = endpoint + "/logout";

		PostMethod post = preparePostMethod(url);
		try {
			prepareHeader(post);

			post.setParameter("sid", sid);
			try {
				client.executeMethod(post);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		} finally {
			post.releaseConnection();
		}
	}

	@Override
	public boolean valid(String sid) {
		String output = null;
		String url = endpoint + "/valid";

		PostMethod post = preparePostMethod(url);
		try {
			prepareHeader(post);

			post.setParameter("sid", sid);

			int statusCode = client.executeMethod(post);

			if (statusCode == HttpStatus.SC_OK)
				output = post.getResponseBodyAsString();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			post.releaseConnection();
		}

		return output.equals("true");
	}

	@Override
	public void renew(String sid) {
		String url = endpoint + "/renew";

		PostMethod post = preparePostMethod(url);
		try {
			prepareHeader(post);

			post.setParameter("sid", sid);
			try {
				client.executeMethod(post);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		} finally {
			post.releaseConnection();
		}
	}
}