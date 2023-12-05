package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.entity.VotePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotePostRepository extends JpaRepository<VotePost,Integer> {
    List<VotePost> findAllByUser(User user);
    Optional<VotePost> findAByUserAndPost(User user, Post post);

}
