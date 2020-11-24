package com.miniProject.payload.request;


import javax.validation.constraints.*;

public class MailBodyRequest {
	
	@NotBlank
	@Email
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}

