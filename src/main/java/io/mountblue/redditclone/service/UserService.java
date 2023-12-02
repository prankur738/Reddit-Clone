package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);

    void grantModeratorRole(User user);
}
