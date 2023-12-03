package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "title")
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "created_at")
    @CreationTimestamp
    Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Date updatedAt;

    @Column(name = "image_name")
    String image;

    @Column(name = "vote_count")
    Integer voteCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToMany(mappedBy = "postList")
    Set<Tag> tagList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "post")
    List<Comment> commentList;

    @ManyToOne
    @JoinColumn(name = "subreddit_id")
    SubReddit subReddit;
}
