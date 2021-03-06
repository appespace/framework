package javacloud.framework.security.jwt;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.Externalizer;
import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.security.IdParameters;
import javacloud.framework.security.claim.TokenGrant;
import javacloud.framework.security.claim.TokenValidator;
import javacloud.framework.util.Converters;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key or public/private
 * 
 * @author ho
 *
 */
@Singleton
public class JwtTokenValidator implements TokenValidator {
	public static final String JWT_ISSUER 		= "iss";
	public static final String JWT_SUBJECT 		= "sub";
	public static final String JWT_AUDIENCE		= "aud";
	public static final String JWT_SCOPE 		= "scope";
	public static final String JWT_EXPIRATION 	= "exp";
	public static final String JWT_ISSUEDAT 	= "iat";
	
	//custom fields
	public static final String JWT_TYPE 		= "jtt";	//type
	public static final String JWT_ROLES 		= "roles";	//subject roles
	
	private JwtCodecs  	jwtCodecs;
	private JwtVerifier	jwtVerifier;
	/**
	 * 
	 * @param externalizer
	 * @param jwtVerifier
	 */
	@Inject
	public JwtTokenValidator(@Named(Externalizer.JSON) Externalizer externalizer, JwtVerifier jwtVerifier) {
		this.jwtCodecs = new JwtCodecs(externalizer);
		this.jwtVerifier  = jwtVerifier;
	}
	
	/**
	 * Validate token signature to make sure no tempering.
	 * 
	 */
	@Override
	public TokenGrant validateToken(String token) throws AuthenticationException {
		JwtToken jwtToken = jwtCodecs.decodeJWT(token, jwtVerifier);
		Map<String, Object> claims = jwtToken.getClaims();
			
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		TokenGrant grantToken = new TokenGrant(token,
				IdParameters.GrantType.valueOf((String)claims.get(JWT_TYPE)),
				(String)claims.get(JWT_SUBJECT),
				(String)claims.get(JWT_AUDIENCE));
		grantToken.setScope((String)claims.get(JWT_SCOPE));
		grantToken.setRoles((String)claims.get(JWT_ROLES));
		
		grantToken.setExpireAt(new Date(Converters.LONG.apply(claims.get(JWT_EXPIRATION))));
		grantToken.setIssuedAt(new Date(Converters.LONG.apply(claims.get(JWT_ISSUEDAT))));
		if(TokenGrant.isExpired(grantToken)) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		return grantToken;
	}
}
