package com.logicaldoc.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.util.Base64;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.WebdavResponse;

import com.logicaldoc.webdav.exception.WebDavAuthorisationException;

public class AuthenticationUtil {
	
	public interface Credentials {
		public String getUserName();
		
		public String getPassword();
	};

	public static Credentials authenticate(HttpServletRequest request) throws WebDavAuthorisationException{
		String authHeader = request.getHeader(DavConstants.HEADER_AUTHORIZATION);
		try {
	        if (authHeader != null) {
	            String[] authStr = authHeader.split(" ");
	            if (authStr.length >= 2 && authStr[0].equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                Base64.decode(authStr[1].toCharArray(), out);
	                String decAuthStr = out.toString("ISO-8859-1");
	                int pos = decAuthStr.indexOf(':');
	                final String userid = decAuthStr.substring(0, pos);
	                final String passwd = decAuthStr.substring(pos + 1);
	                return new Credentials(){
	
						@Override
						public String getPassword() {
							return passwd;
						}
	
						@Override
						public String getUserName() {
							return userid;
						}
	                	
	                };
	            }
	            throw new WebDavAuthorisationException("Invalid Authentication Header for (" + DavConstants.HEADER_AUTHORIZATION + ") found.");
	        }
	        else {
	        	throw new WebDavAuthorisationException("No authorization header (" + DavConstants.HEADER_AUTHORIZATION + ") found.");
	        }
		}
		catch(Exception e){
			throw new WebDavAuthorisationException("An error has been occured due authorisation time");
		}
	}
	
	public static void sendAuthorisationCommand(WebdavResponse response){
		try {
			response.setHeader("WWW-Authenticate", "Basic realm=\"Jackrabbit Webdav Server\"");
			response.sendError(401);
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
		
	}
}
