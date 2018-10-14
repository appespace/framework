package io.javacloud.framework.security.jwt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

import io.javacloud.framework.json.internal.JacksonConverter;
import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Hmacs;
import io.javacloud.framework.util.Objects;

/**
 * Encode/decode JWT token with signer support
 * 
 * @author ho
 *
 */
public final class JwtCodecs {
	private static final Logger logger = Logger.getLogger(JwtCodecs.class.getName());
	private static final String SHA256withRSA = "SHA256withRSA";
	/**
	 * NONE
	 */
	public static class NONE implements JwtSigner, JwtVerifier {
		@Override
		public String getAlgorithm() {
			return ALG_NONE;
		}

		@Override
		public String sign(String payload) {
			return "";
		}
		
		@Override
		public boolean verify(String payload, String signature) {
			return (signature != null && signature.isEmpty());
		}
	}
	
	/**
	 *	HMAC SHA-256
	 */
	public final static class HS256 implements JwtSigner, JwtVerifier {
		private Key secret;
		public HS256(byte[] secret) {
			this.secret = new SecretKeySpec(secret, Hmacs.HmacSHA2);
		}

		@Override
		public String getAlgorithm() {
			return ALG_HS256;
		}
		@Override
		public String sign(String payload) {
			return Codecs.encodeBase64(Hmacs.digest(secret, Codecs.toBytes(payload)), true);
		}

		@Override
		public boolean verify(String payload, String signature) {
			String sexpected = sign(payload);
			return sexpected.equals(signature);
		}
	}
	
	/**
	 * RS256S
	 */
	public static final class RS256S implements JwtSigner {
		private PrivateKey privateKey;
		public RS256S(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}
		
		@Override
		public String getAlgorithm() {
			return ALG_RS256;
		}

		@Override
		public String sign(String payload) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initSign(privateKey);
				sig.update(Codecs.toBytes(payload));
				return Codecs.encodeBase64(sig.sign(), true);
			} catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtInvalidException();
			}
		}
	}
	
	/**
	 * RS256V
	 */
	public static final class RS256V implements JwtVerifier {
		private PublicKey publicKey;
		public RS256V(PublicKey publicKey) {
			this.publicKey = publicKey;
		}
		@Override
		public boolean verify(String payload, String signature) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initVerify(publicKey);
				sig.update(Codecs.toBytes(payload));
				return sig.verify(Codecs.decodeBase64(signature, true));
			} catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtInvalidException();
			}
		}
	}
	
	//PROTECTED
	private JacksonConverter converter;
	public JwtCodecs(Externalizer externalizer) {
		this.converter = new JacksonConverter(externalizer);
	}
	
	/**
	 * <base64 header>.<base64 payload>.<base64 signature>
	 * 
	 * @param token
	 * @param signer
	 * 
	 * @return
	 * @throws JwtException
	 */
	public String encodeJWT(JwtToken token, JwtSigner signer) throws JwtInvalidException {
		try {
			byte[] header = converter.toBytes(
					Dictionaries.asDict("typ", token.getType(), "alg", signer.getAlgorithm())
				);
			
			byte[] claims = converter.toBytes(token.getClaims());
			String payload = Codecs.encodeBase64(header, true)
							+ "." + Codecs.encodeBase64(claims, true);
			
			return payload + "." + signer.sign(payload);
		} catch(IOException ex) {
			logger.log(Level.WARNING, "Problem encode JWT token", ex);
			throw new JwtInvalidException();
		}
	}
	
	/**
	 * Given a token in format above, parse the token and validate correct signature
	 * 
	 * @param token
	 * @param signer
	 * @return
	 * 
	 * @throws JwtException
	 */
	public JwtToken decodeJWT(String token, JwtVerifier verifier) throws JwtInvalidException {
		if(Objects.isEmpty(token)) {
			throw new JwtInvalidException();
		}
		int idot = token.lastIndexOf('.');
		if(idot < 0) {
			throw new JwtInvalidException();
		}
		
		//VALIDATE THE PAYLOAD BEFORE CONTINUE
		String payload  = token.substring(0, idot);
		String signature= token.substring(idot + 1);
		if(!verifier.verify(payload, signature)) {
			throw new JwtInvalidException();
		}
		
		//DECODE PAYLOAD TO JSON
		idot = payload.indexOf('.');
		if(idot < 0) {
			throw new JwtInvalidException();
		}
		try {
			Dictionary header = converter.toObject(Codecs.decodeBase64(payload.substring(0, idot), true), Dictionary.class);
			Dictionary claims = converter.toObject(Codecs.decodeBase64(payload.substring(idot + 1), true), Dictionary.class);
			return new JwtToken((String)header.get("typ"), (String)header.get("alg"), claims);
		} catch(IOException ex) {
			logger.log(Level.WARNING, "Problem decode JWT token", ex);
			throw new JwtInvalidException();
		}
	}
}
