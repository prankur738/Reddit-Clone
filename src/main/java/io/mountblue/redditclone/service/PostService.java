package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface PostService {

    void createNewPost(Post post,Integer subredditId,String username,String tagNames);

    Post findById(Integer userId);

    List<Post> findBySubRedditId(Integer subRedditId);

    void updatePost(Post post,Integer subredditId,String username, String tagNames);

    void deletePost(Integer postId);

    boolean checkUserAuthorized(UserDetails userDetails, Integer postId);

    String getCommaSeperatedTags(Integer postId);

    List<Post> findAllPosts();

    void updatePost(Integer postId, Integer voteCountChange);

    List<Post> findAllBySubscribedSubReddits(String username);
}
