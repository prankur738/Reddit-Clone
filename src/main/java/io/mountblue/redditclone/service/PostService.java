package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface PostService {

    public void createNewPost(Post post,Integer subredditId,String username,String tagNames);

    public Post findById(Integer userId);

    public List<Post> findBySubRedditId(Integer subRedditId);

    public void updatePost(Post post,Integer subredditId,String username, String tagNames);

    public void deletePost(Integer postId);
    public boolean checkUserAuthorized(UserDetails userDetails, Integer postId);
    public String getCommaSeperatedTags(Integer postId);

    public List<Post> findAllPosts();

    public void updatePost(Integer postId, Integer voteCountChange);

}
