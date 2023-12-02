package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "subreddits")
public class SubReddit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "admin_id")
    Integer adminUserId;

    @ManyToMany
    @JoinTable(
            name = "user_subreddit",
            joinColumns = @JoinColumn(name = "subreddit_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> userList;

    @OneToMany(mappedBy = "subReddit")
    List<Post> postList;

}
