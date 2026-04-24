package com.government.scheme_management.security;

import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

// Spring Security calls this to load user from database during authentication
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email - throw exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if account is active
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + email);
        }

        // Convert role to Spring Security authority
        // Role name is stored as "APPLICANT", "OFFICER", "ADMIN"
        // Spring Security expects "ROLE_APPLICANT", "ROLE_OFFICER", "ROLE_ADMIN"
        String roleAuthority = "ROLE_" + user.getRole().getRoleName();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleAuthority))
        );
    }
}
