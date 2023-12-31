package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;

import java.time.LocalDateTime;
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
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Column(name = "image_name")
    String image;

    @Column(name = "vote_count")
    Integer voteCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "photo_name")
    String photoName;

    @Column(name = "photo_type")
    String photoType;

    @Column(name = "photo_link")
    String photoLink;

    @Column(name = "photo_size")
    Long photoSize;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    List<Bookmark> bookmarkList;

    public Integer getCount(List<Comment> commentList){
        return commentList.size();
    }

    @ManyToMany()
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    Set<Tag> tagList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "post")
    List<Comment> commentList;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "subreddit_id")
    SubReddit subReddit;

    @OneToMany(mappedBy="post", cascade = CascadeType.ALL)
    List<VotePost> votePosts;

    @OneToMany(mappedBy="post", cascade = CascadeType.ALL)
    List<VoteComment> voteComments;
}
