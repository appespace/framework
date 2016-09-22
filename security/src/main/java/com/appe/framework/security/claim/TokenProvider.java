package com.appe.framework.security.claim;

import com.appe.framework.security.AccessGrant;
import com.appe.framework.security.IdParameters;
/**
 * 
 * 
 * @author ho
 *
 */
public interface TokenProvider {
	/**
	 * Give back an access token for given principal & what type of access is. Can convert type to scope.
	 * NULL secret will be auto generated.
	 * 
	 * @param authzGrant
	 * @param type
	 * @return
	 */
	public	TokenGrant	issueToken(AccessGrant authzGrant, IdParameters.GrantType type);
}
