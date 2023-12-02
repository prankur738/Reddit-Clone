package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

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
}
