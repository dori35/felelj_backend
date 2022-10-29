package hu.dorin.felelj.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private UserRepository userRepository;
	
	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository){ 
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		
		User user = userRepository.findByIdentifier(identifier);
		
		if (user == null) {
			throw new UsernameNotFoundException(identifier);
		}
		List<SimpleGrantedAuthority> authorities;
		switch (user.getRole()) {
		case TEACHER: 
			authorities = List.of(new SimpleGrantedAuthority("ROLE_TEACHER"), new SimpleGrantedAuthority("ROLE_STUDENT"));
			break;
		case STUDENT:
			authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
			break;
		default:
			authorities = List.of();
		}
		return new UserDetailsImpl(user.getIdentifier(), user.getPassword(), authorities);
	}

}
