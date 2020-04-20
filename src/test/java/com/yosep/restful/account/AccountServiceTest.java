package com.yosep.restful.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosep.restful.accounts.Account;
import com.yosep.restful.accounts.AccountRepository;
import com.yosep.restful.accounts.AccountRole;
import com.yosep.restful.accounts.AccountService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	public void findByUserName() {
		String userName = "enekelx1@naver.com";
		String password = "123123";

		// Given
//		Account account = Account.builder().email(userName).password(password)
//				.roles(Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet())).build();
//
//		this.accountService.saveAccount(account);

		// When
		UserDetailsService userDetailsService = (UserDetailsService) accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

		// Then
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
	}

//	@Test
//	public void findByUserNameFail() {
//		String userName = "abc@naver.com";
//		
//		try {
//			accountService.loadUserByUsername(userName);
//			fail("supposed to be failed");
//		}catch(UsernameNotFoundException e) {
//			assertThat(e.getMessage()).containsSequence(userName);
//		}
//	}
	
	// ExpectedException 방식
	@Test
	public void findByUserNameFail() {
		// 예상되는 예외를 먼저 적어줘야함
		// Expected
		String userName = "abc@naver.com";
		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.containsString(userName));
		
		// When
		accountService.loadUserByUsername(userName);
	}
}
