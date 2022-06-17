package com.auth.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

// Override klasa za autentifikaciju umesto npr. Basic Auth
public class TokenBasedAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private String token;
	private final UserDetails principle;

	public TokenBasedAuthentication(UserDetails principle) {
		super(principle.getAuthorities());
		this.principle = principle;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public String getCredentials() {
		return token;
	}

	@Override
	public UserDetails getPrincipal() {
		return principle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TokenBasedAuthentication that = (TokenBasedAuthentication) o;
		return Objects.equals(token, that.token) && Objects.equals(principle, that.principle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), token, principle);
	}
}
