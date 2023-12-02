package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void save(User user);

    User findByUsername(String username);

    void grantModeratorRole(User user);
}
