package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Comment;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.entity.VoteComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteCommentRepository extends JpaRepository<VoteComment,Integer> {

    Optional<VoteComment> findByUserAndComment(User user, Comment comment);

}
