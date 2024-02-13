/**
 * Copyright
 */
package com.mt_itsolutions.keycloak.email2fa;

import org.keycloak.models.AuthenticatorConfigModel;

public record AuthCodeConfig(int length, int ttl, String base) {

	/**
	 * Read the code configuration from a config model provided by Keycloak.
	 *
	 * @param configModel a config model by Keycloak, possibly retrieved from an {@link org.keycloak.authentication.AuthenticationFlowContext}.
	 * @return an instance of this model containing the configuration properties
	 */
	public static AuthCodeConfig readFromConfig(AuthenticatorConfigModel configModel) {
		var config = configModel.getConfig();

		int length = Integer.parseInt(config.getOrDefault(AuthFactory.AUTH_CODE_LENGTH, AuthFactory.AUTH_CODE_LENGTH_DEFAULT));
		String base = config.getOrDefault(AuthFactory.AUTH_CODE_CHARACTERS, AuthFactory.AUTH_CODE_CHARACTERS_DEFAULT);
		int ttl = Integer.parseInt(config.getOrDefault(AuthFactory.AUTH_CODE_TTL, AuthFactory.AUTH_CODE_TTL_DEFAULT));

		return new AuthCodeConfig(length, ttl, base);
	}

}
