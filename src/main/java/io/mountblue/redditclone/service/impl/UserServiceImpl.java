package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.Role;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.entity.VotePost;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.repository.RoleRepository;
import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.repository.VotePostRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private VotePostRepository votePostRepository;
    private PostRepository postRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

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

    public void grantRoleToUser(String username, String roleName) {
        User user = this.findByUsername(username);
        Role role = roleRepository.findByRole("ROLE_" + roleName).orElseThrow();

        user.getRoles().add(role);

        this.save(user);
    }

    public void revokeRoleFromUser(String username, String roleName) {
        User user = this.findByUsername(username);
        user.getRoles().remove(new Role("ROLE_" + roleName));

        this.save(user);
    }

    @Override
    public List<Post> getUpVotedPosts(String username) {
        List<Post> upVotedPosts = new ArrayList<>();
        List<VotePost> votedPosts = votePostRepository.findAllByUsername(username);

        for(VotePost votePost : votedPosts){
            if(votePost.getVote() == 1){
                Post upVotedPost =  postRepository.findById(votePost.getPostId()).get();
                upVotedPosts.add(upVotedPost);
            }
        }

        return upVotedPosts;
    }

    @Override
    public List<Post> getDownVotedPosts(String username) {
        List<Post> downVotedPosts = new ArrayList<>();
        List<VotePost> votedPosts = votePostRepository.findAllByUsername(username);

        for(VotePost votePost : votedPosts){
            if(votePost.getVote() == -1){
                Post upVotedPost =  postRepository.findById(votePost.getPostId()).get();
                downVotedPosts.add(upVotedPost);
            }
        }

        return downVotedPosts;
    }
}
