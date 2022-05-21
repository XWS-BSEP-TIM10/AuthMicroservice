package com.auth.dto;


public class TokenDTO {
    private String jwt;
    private String refreshToken;

    
    public TokenDTO(String jwt, String refreshToken) {
		super();
		this.jwt = jwt;
		this.refreshToken = refreshToken;
	}

	public String getJwt() {
        return jwt;
    }

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
    
    
}
