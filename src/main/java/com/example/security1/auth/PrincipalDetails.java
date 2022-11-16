package com.example.security1.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.security1.model.Users;

// 시큐리티가 "/login" 주소로 요청이 들어오면 낚아채서 로그인을 진행
// 로그인이 완료되면 스프링 시큐리티가 Session을 만든다.(key : Security ContextHolder)
// key값이 받아주는 오브젝트 => Authentication타입의 객체
// Authentication 내부에 User 정보가 있어야 한다.
// 그 User 오브젝트의 타입은 UserDetails 타입이어야 한다.
public class PrincipalDetails implements UserDetails {

	private Users user;

	public PrincipalDetails(Users user) {
		this.user = user;
	}

	
	// 해당 Users의 권한을 리턴
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		// 예시
		// 우리 사이트에서 1년동안 로그인을 안 하면 휴면계정으로 하기로 함
		// 현재 시간 - 로그인 시간 => 1년 초과하면 return false;
		return true;
	}

}
