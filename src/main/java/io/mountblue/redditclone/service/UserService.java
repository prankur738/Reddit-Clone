package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void save(User user);

    void encodePassword(User user);

    User findByUsername(String username);

    void grantRoleToUser(String username, String role);

    void revokeRoleFromUser(String username, String role);

}
