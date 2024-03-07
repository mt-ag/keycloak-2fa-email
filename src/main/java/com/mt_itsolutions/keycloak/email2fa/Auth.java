/**
 * Copyright
 */

package com.mt_itsolutions.keycloak.email2fa;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Slf4j
public class Auth implements Authenticator {

	@Override
	public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
		log.debug("EmailAuth.authenticate");

		try {
			var codeModel = generateCode(authenticationFlowContext);
			sendVerificationCode(authenticationFlowContext, codeModel);
			AuthChallenges.codeVerification(authenticationFlowContext);
		} catch (EmailException e) {
			log.error("Could not send email with verification code", e);
			AuthChallenges.emailError(authenticationFlowContext);
		} catch (IllegalStateException | NullPointerException e) {
			log.error("Could not generate code", e);
			AuthChallenges.internalError(authenticationFlowContext);
		}
	}

	@Override
	public void action(AuthenticationFlowContext context) {
		log.debug("EmailAuth.action");

		final var enteredCode = context.getHttpRequest().getDecodedFormParameters().getFirst("code");
		final var codeModel = AuthCodeModel.readFromAuthSession(context.getAuthenticationSession());

		// Checks if the code model is valid
		if (!codeModel.isValid()) {
			AuthChallenges.internalError(context);
			return;
		}

		// Checks if the entered code is valid
		if (!codeModel.validateCode(enteredCode)) {
			AuthenticationExecutionModel execution = context.getExecution();
			if (execution.isRequired()) {
				AuthChallenges.codeMismatch(context);
			} else if (execution.isConditional() || execution.isAlternative()) {
				context.attempted();
			}
			return;
		}

		// Checks if the code model is expired and sends a new code
		if (codeModel.isExpired()) {
			try {
				var newModel = generateCode(context);
				sendVerificationCode(context, newModel);
				AuthChallenges.codeExpired(context);
			} catch (EmailException e) {
				log.error("Could not send email with verification code", e);
				AuthChallenges.emailError(context);
			} catch (IllegalStateException | NullPointerException e) {
				log.error("Could not generate code", e);
				AuthChallenges.internalError(context);
			}
			return;
		}

		context.success();
	}

	@Override
	public boolean requiresUser() {
		return true;
	}

	@Override
	public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
		final boolean hasEmail = userModel.getEmail() != null;
		final boolean isEmailVerified = userModel.isEmailVerified();

		return hasEmail && isEmailVerified;
	}

	@Override
	public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
		// No required actions
	}

	@Override
	public void close() {
		// No resources to close
	}

	private void sendVerificationCode(AuthenticationFlowContext context, AuthCodeModel codeModel)
		throws EmailException {
		Map<String, Object> bodyAttributes = new HashMap<>();

		bodyAttributes.put("code", codeModel.code());
		bodyAttributes.put("ttl", String.valueOf(codeModel.ttl()));

		context
			.getSession()
			.getProvider(EmailTemplateProvider.class)
			.setAuthenticationSession(context.getAuthenticationSession())
			.setRealm(context.getRealm())
			.setUser(context.getUser())
			.send("email.2fa.mail.subject", "email.ftl", bodyAttributes);
	}

	private AuthCodeModel generateCode(AuthenticationFlowContext context) throws IllegalStateException {
		log.debug("EmailAuth.generateCode");

		final var codeConfig = AuthCodeConfig.readFromConfig(context.getAuthenticatorConfig());
		final var codeModel = AuthCodeModel.createNewCode(codeConfig);

		if (codeModel.isEmpty()) {
			throw new IllegalStateException("Could not create new code model");
		}

		codeModel.get().writeToAuthSession(context.getAuthenticationSession());
		return codeModel.get();
	}
}
