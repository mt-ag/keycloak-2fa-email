/**
 * Copyright
 */

package com.mt_itsolutions.keycloak.email2fa;

import java.util.List;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

public class AuthFactory implements AuthenticatorFactory {

	public static final String AUTH_CODE_LENGTH = "code-length";
	public static final String AUTH_CODE_CHARACTERS = "code-characters";
	public static final String AUTH_CODE_TTL = "code-ttl";
	public static final String AUTH_CODE_LENGTH_DEFAULT = "6";
	public static final String AUTH_CODE_CHARACTERS_DEFAULT = "1234567890ABCDEF";
	public static final String AUTH_CODE_TTL_DEFAULT = "300";
	public static final String AUTH_PROVIDER_ID = "email-2fa";


	private static final Auth AUTH_INSTANCE = new Auth();


	@Override
	public String getDisplayType() {
		return "Email Verification Code";
	}

	@Override
	public String getReferenceCategory() {
		return "info";
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return new AuthenticationExecutionModel.Requirement[] {
			AuthenticationExecutionModel.Requirement.REQUIRED,
			AuthenticationExecutionModel.Requirement.ALTERNATIVE,
			AuthenticationExecutionModel.Requirement.CONDITIONAL,
			AuthenticationExecutionModel.Requirement.DISABLED,
		};
	}

	@Override
	public boolean isUserSetupAllowed() {
		return true;
	}

	@Override
	public String getHelpText() {
		return "During the Email Verification Code authentication step, the user is asked to enter a code that sent to their email address";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create()

			// Auth code length
			.property()
			.name(AUTH_CODE_LENGTH)
			.helpText("The number of digits that a code should contain")
			.type(ProviderConfigProperty.STRING_TYPE)
			.defaultValue(AUTH_CODE_LENGTH_DEFAULT)
			.add()

			// Code characters
			.property()
			.name(AUTH_CODE_CHARACTERS)
			.label("Code Base")
			.helpText("The characters that will be used to generate the code")
			.type(ProviderConfigProperty.STRING_TYPE)
			.defaultValue(AUTH_CODE_CHARACTERS_DEFAULT)
			.add()

			// Code time-to-live
			.property()
			.name(AUTH_CODE_TTL)
			.label("Code Time-to-live")
			.helpText("The time to live in seconds for the code to be valid.")
			.type(ProviderConfigProperty.STRING_TYPE)
			.defaultValue(AUTH_CODE_TTL_DEFAULT)
			.add()

			.build();
	}

	@Override
	public Authenticator create(KeycloakSession keycloakSession) {
		return AUTH_INSTANCE;
	}

	@Override
	public void init(Config.Scope scope) {
		// Nothing to do here
	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
		// Nothing to do here
	}

	@Override
	public void close() {
		// Nothing to do here
	}

	@Override
	public String getId() {
		return AUTH_PROVIDER_ID;
	}
}
