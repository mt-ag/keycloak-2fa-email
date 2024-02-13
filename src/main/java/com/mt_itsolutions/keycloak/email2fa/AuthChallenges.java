/**
 * Copyright
 */

package com.mt_itsolutions.keycloak.email2fa;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthChallenges {

	private static final String LOGIN_PAGE = "email-login.ftl";

	public static void codeVerification(AuthenticationFlowContext context) {
		context.challenge(
			context.form()
				.setAttribute("realm", context.getRealm())
				.setAttribute("email", context.getUser().getEmail())
				.createForm(LOGIN_PAGE)
		);
	}

	public static void emailError(AuthenticationFlowContext context) {
		context.failureChallenge(
			AuthenticationFlowError.INTERNAL_ERROR,
			context.form()
				.setError("email2FAEmailNotSent")
				.createErrorPage(Response.Status.INTERNAL_SERVER_ERROR)
		);
	}

	public static void internalError(AuthenticationFlowContext context) {
		context.failureChallenge(
			AuthenticationFlowError.INTERNAL_ERROR,
			context.form()
				.createErrorPage(Response.Status.INTERNAL_SERVER_ERROR)
		);
	}

	public static void codeExpired(AuthenticationFlowContext context) {
		context.failureChallenge(
			AuthenticationFlowError.INVALID_CREDENTIALS,
			context.form()
				.setError("email2FACodeExpired")
				.createErrorPage(Response.Status.UNAUTHORIZED)
		);
	}

	public static void codeMismatch(AuthenticationFlowContext context) {
		context.failureChallenge(
			AuthenticationFlowError.INVALID_CREDENTIALS,
			context.form()
				.setError("email2FACodeMismatch")
				.createErrorPage(Response.Status.UNAUTHORIZED)
		);
	}

}
