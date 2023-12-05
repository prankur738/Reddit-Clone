package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.VotePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotePostRepository extends JpaRepository<VotePost,Integer> {
    Optional<VotePost> findByUsernameAndPostId(String username, Integer postId);
    List<VotePost> findAllByUsername(String username);


}
