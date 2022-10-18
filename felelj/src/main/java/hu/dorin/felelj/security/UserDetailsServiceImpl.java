package hu.dorin.felelj.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!username.equals("user")) {
			throw new UsernameNotFoundException("user");
		}
		return new UserDetailsImpl(username, "1234", List.of(new SimpleGrantedAuthority("ROLE_USER")));
	}

}
