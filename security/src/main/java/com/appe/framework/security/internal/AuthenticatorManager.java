package com.appe.framework.security.internal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.Authenticator;
import com.appe.framework.security.Credentials;
import com.appe.framework.security.InvalidCredentialsException;

/**
 * Assuming a chain of authenticators each handle the authenticate only if appropriate otherwise NULL will be return.
 * 
 * @author ho
 *
 */
public class AuthenticatorManager implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticatorManager.class);
	private final List<Authenticator> authenticators;
	/**
	 * 
	 * @param authenticators
	 */
	public AuthenticatorManager(List<Authenticator> authenticators) {
		this.authenticators = authenticators;
	}
	
	/**
	 * Each authenticator will return NULL if not applicable so we keep going to the finish.
	 * 
	 */
	@Override
	public AccessGrant authenticate(Credentials credentials) throws AuthenticationException {
		for(Authenticator authenticator: authenticators) {
			AccessGrant authzGrant = authenticator.authenticate(credentials);
			if(authzGrant != null) {
				return authzGrant;
			}
		}
		
		//ASSMUING CREDENTIALS IS INVALID
		logger.debug("Not found any authenticator for credentials type: " + credentials.getClass().getName());
		throw new InvalidCredentialsException();
	}
}
