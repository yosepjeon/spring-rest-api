package com.yosep.restful.config;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;

import com.yosep.restful.accounts.Account;
import com.yosep.restful.accounts.AccountRole;
import com.yosep.restful.accounts.AccountService;
import com.yosep.restful.common.BaseControllerTest;

public class AuthServerConfigTest extends BaseControllerTest {

	@Autowired
	AccountService accountService;

	@Test
	@Description("인증 토큰을 발급 받는 테스트")
	public void getAuthToken() throws Exception {
		// Given
		String username = "enekelx1@naver.com";
		String password = "123123";
		
		// AppConfig에 이미 위의 회원 정보를 담아서 실행하기 때문에 주석 처리하였다. 이것때문에 1시간 삽질...
//		Account user = Account.builder()
//				.email(username)
//				.password(password)
//				.roles(Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet()))
//				.build();
//
//		this.accountService.saveAccount(user);
		
		String clientId = "myApp";
		String clientSecret = "pass";

		this.mockMvc.perform(post("/oauth/token")
				.with(httpBasic(clientId, clientSecret))
				.param("username", username)
				.param("password", password)
				.param("grant_type", "password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token").exists());
	}

}
