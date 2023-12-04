package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "vote_posts")
public class VotePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "username")
    String username;

    @Column(name="post_id")
    Integer postId;

    @Column(name="vote")
    Integer vote;

    public VotePost(){}

    public VotePost(String username, Integer postId, Integer vote){
        this.username = username;
        this.postId = postId;
        this.vote = vote;
    }
}
