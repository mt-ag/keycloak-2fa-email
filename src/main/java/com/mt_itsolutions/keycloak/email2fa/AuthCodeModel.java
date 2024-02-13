/**
 * Copyright
 */

package com.mt_itsolutions.keycloak.email2fa;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.sessions.AuthenticationSessionModel;

/**
 * @param code         Random generated 2fa code
 * @param ttl          The number of seconds that a code should be valid
 * @param creationTime Timestamp when the code was created in milliseconds
 */
@Slf4j
public record AuthCodeModel(String code, int ttl, long creationTime) {

	private static final String AUTH_CODE_NOTE = "code-value";
	private static final String AUTH_CODE_TTL_NOTE = "code-ttl";
	private static final String AUTH_CODE_CREATION_TIME_NOTE = "code-creation-time";

	/**
	 * Calculates the expiration timestamp
	 *
	 * @return Timestamp when the code expires in milliseconds
	 */
	public long getExpirationTime() {
		return creationTime + (ttl * 1000L);
	}

	/**
	 * Validates the code, ttl and creation time
	 *
	 * @return true is when CodeModel values are initialized and valid
	 */
	public boolean isValid() {
		return Objects.nonNull(code) && !code.isEmpty() && ttl > 0 && creationTime > 0;
	}

	/**
	 * Validates the code
	 *
	 * @param code The code to be validated
	 * @return true if the code is valid
	 */
	public boolean validateCode(final String code) {
		return this.code.equals(code);
	}

	/**
	 * Checks if the code is expired
	 *
	 * @return true if the code is expired
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() > getExpirationTime();
	}

	/**
	 * Writes the code, ttl and creation time to the authentication session
	 *
	 * @param authSession The {@link org.keycloak.sessions.AuthenticationSessionModel} to which the code should be written
	 */
	public void writeToAuthSession(AuthenticationSessionModel authSession) {
		authSession.setAuthNote(AUTH_CODE_NOTE, code);
		authSession.setAuthNote(AUTH_CODE_TTL_NOTE, Integer.toString(ttl));
		authSession.setAuthNote(AUTH_CODE_CREATION_TIME_NOTE, Long.toString(creationTime));
	}

	/**
	 * Reads the code, ttl and creation time from the authentication session
	 *
	 * @param authSession The {@link org.keycloak.sessions.AuthenticationSessionModel} from which the code should be read
	 * @return An instance of CodeModel containing the code, ttl and creation time
	 */
	public static AuthCodeModel readFromAuthSession(final AuthenticationSessionModel authSession) {
		String code = authSession.getAuthNote(AUTH_CODE_NOTE);
		int ttl = Integer.parseInt(authSession.getAuthNote(AUTH_CODE_TTL_NOTE));
		long creationTime = Long.parseLong(authSession.getAuthNote(AUTH_CODE_CREATION_TIME_NOTE));
		return new AuthCodeModel(code, ttl, creationTime);
	}

	/**
	 * Creates a new authentication code using the configuration provided
	 *
	 * @param config A {@link AuthCodeConfig} with a code base, a length and a ttl
	 * @return A code model with a secure random code
	 */
	public static Optional<AuthCodeModel> createNewCode(final AuthCodeConfig config) {
		StringBuilder codeBuilder = new StringBuilder();
		try {
			var randomInstance = SecureRandom.getInstanceStrong();
			for (int i = 0; i < config.length(); i++) {
				codeBuilder.append(config.base().charAt(randomInstance.nextInt(config.base().length())));
			}
		} catch (NoSuchAlgorithmException ex) {
			log.error("Could not create a secure random instance", ex);
		}

		String code = codeBuilder.toString();

		if (code.length() < config.length()) {
			log.error("Could not create a code with the required length");
			return Optional.empty();
		}

		long creationTime = System.currentTimeMillis();

		return Optional.of(new AuthCodeModel(code, config.ttl(), creationTime));
	}

}
