package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "username",unique = true)
    String username;

    @Email
    @Column(name = "email",unique = true)
    String email;

    @Column(name = "password")
    String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Post> posts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Comment> comments;

    @ManyToMany(mappedBy = "userList")
    List<SubReddit> subRedditList;

    @Transient
    List<SimpleGrantedAuthority> roles = new ArrayList<>(List.of(new SimpleGrantedAuthority("ROLE_USER")));

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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
