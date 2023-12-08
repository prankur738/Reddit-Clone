package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void save(User user);

    void encodePassword(User user);

    User findById(Integer id);

    List<User> findAll();

    User findByUsername(String username);

    void grantRoleToUser(String username, String role);

    void revokeRoleFromUser(String username, String role);
    List<Post> getUpVotedPosts(String username);
    List<Post> getDownVotedPosts(String username);

}
