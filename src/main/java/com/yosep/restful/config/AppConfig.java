package com.yosep.restful.config;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yosep.restful.accounts.Account;
import com.yosep.restful.accounts.AccountRole;
import com.yosep.restful.accounts.AccountService;

@Configuration
public class AppConfig {
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			
			@Autowired
			AccountService accountService;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				// TODO Auto-generated method stub
				Account account = Account.builder()
					.email("enekelx1@naver.com")
					.password("123123")
					.roles(Stream.of(AccountRole.ADMIN,AccountRole.USER).collect(Collectors.toSet())).build();
				
				accountService.saveAccount(account);
			}
		};
	}
}
