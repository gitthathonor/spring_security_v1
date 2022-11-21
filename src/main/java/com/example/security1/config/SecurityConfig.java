package com.example.security1.config;

import org.springframework.beans.factory.annotation.Autowired;

// 1.코드받기(인증)
// 2.엑세스토큰(권한) 
// 3.사용자프로필 정보를 가져와서 
// 4. 그 정보를 토대로 회원가입을 자동으로 진행시키키도 함
// 4-2 (이메일, 전화번호, 이름, 아이디) => 그대로 구글 프로필에서 정보를 받아오면 됨
// 쇼핑몰 -> (집주소), 백화점몰 -> (vip등급, 일반등급) => 새로운 회원가입 창이 필요하다.


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.security1.auth.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true ) // Secured 어노테이션 활성화, PreAuthorize, PostAuthorize 어노테이션 활성화
public class SecurityConfig {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	
	@Bean // 해당 메서드의 리턴되는 오브젝트를 IoC에 등록시켜주는 어노테이션
	public BCryptPasswordEncoder encodedPwd() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		
		
		http.csrf().disable().authorizeRequests()
		.antMatchers("/user/**").authenticated()
		.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/h2-console/**").permitAll()
		.anyRequest().permitAll()
		.and()
		.formLogin()
		.loginPage("/loginForm")
		.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행
		.defaultSuccessUrl("/")
		.and()
		.oauth2Login()
		.loginPage("/loginForm")
		.userInfoEndpoint()
		.userService(principalOauth2UserService); 
		// 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 코드X, (엑세스토큰+사용자프로필정보 O)
		// 이미 코드를 던져서 인증이 끝난 엑세스토큰과 사용자프로필 정보를 리턴한다.
		return http.build();
	}
}
