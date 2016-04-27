package com.logicaldoc.web.security;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Our customization of an <code>AffirmativeBased</code>
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class LDAccessDecisionManager extends UnanimousBased {

	public LDAccessDecisionManager(List<AccessDecisionVoter> decisionVoters) {
		super(decisionVoters);
	}

	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> properties)
			throws AccessDeniedException, InsufficientAuthenticationException {

		if (authentication instanceof AnonymousAuthenticationToken) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			if ("login".equals(request.getParameter("anonymous"))) {
				String tenant = "default";
				if (StringUtils.isNotEmpty(request.getParameter("tenant")))
					tenant = request.getParameter("tenant");

				ContextProperties config = (ContextProperties) Context.get().getBean(ContextProperties.class);
				boolean enabled = "true".equals(config.get(tenant + ".anonymous.enabled"));
				if (enabled) {
					return;
				}
			}
		}

		super.decide(authentication, object, properties);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

	@Override
	public boolean supports(ConfigAttribute arg0) {
		return true;
	}
}
