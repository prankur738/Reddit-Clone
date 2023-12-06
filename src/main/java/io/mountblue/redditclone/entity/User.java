package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "username",unique = true)
    @NotNull(message = "cannot be empty")
    String username;

    @Email
    @Column(name = "email",unique = true)
    @NotNull(message = "cannot be empty")
    String email;

    @Column(name = "password")
    @NotNull(message = "cannot be empty")
    String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Post> posts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Comment> comments;

    @ManyToMany(mappedBy = "subscribers", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                                                     CascadeType.REFRESH, CascadeType.DETACH})
    List<SubReddit> subRedditList;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    List<VotePost> votePosts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Bookmark> bookmarkList;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
                                                    CascadeType.DETACH, CascadeType.DETACH})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Collection<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
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
        return true;
    }
}
