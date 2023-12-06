package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.Bookmark;
import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Integer> {

    Optional<Bookmark> findByUserAndPost(User user, Post post);
}
