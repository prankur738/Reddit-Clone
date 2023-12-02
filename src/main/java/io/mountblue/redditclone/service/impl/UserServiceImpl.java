package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }

        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void grantModeratorRole(User user) {
        user.getRoles().add(new SimpleGrantedAuthority("ROLE_MOD"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication updatedAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                user.getAuthorities()
        );

        // Set the updated roles for current session
        SecurityContextHolder.getContext().setAuthentication(updatedAuth);
    }
}
