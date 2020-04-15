package com.yosep.restful.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.yosep.restful.accounts.AccountService;

// @EnableWebSecurity를 붙이고 WebSecurityConfigurerAdapter를 상속받는 순간 스프링 부트가 제공해주는 스프링 시큐리티 설정은 더이상 적용되지 않게 된다.
// 커스터마이징이 가능해진다는 뜻.
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	AccountService accountService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	// 다른 인증서버, 리소스서버가 참조할 수 있도록 빈으로 등록 후 이 메소드 사용
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManagerBean();
	}

	// 인증관리자를 어떻게 만들거냐 재정의
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.userDetailsService(accountService)
		.passwordEncoder(passwordEncoder);
	}

	// filter를 적용할지 말지를 정의하는 곳
	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		web.ignoring().mvcMatchers("/docs/index.html");
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

//	http방식의 필터 securityfilter는 일단 통과
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// TODO Auto-generated method stub
//		http.authorizeRequests()
//			.mvcMatchers("/docs/index.html").anonymous()
//			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//	}
	
}
