package hu.dorin.felelj.security;

import lombok.Data;

@Data
public class LoginResponse {
	private String text;

	public LoginResponse(String text) {
		this.text=text;
	}

	
}
