package com.unitvectory.auth.server.token.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.unitvectory.auth.server.token.service.LoginService;

@Configuration
public class LoginConfig {

	@Bean
	public LoginService loginService() {
		return new LoginService();
	}
}
