package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.User;

public interface PostService {

    public void createNewPost(Post post,Integer subredditId,String username,String tagNames);

    public Post findById(Integer userId);

    public void updatePost(Post post,Integer subredditId,String username, String tagNames);

    public void deletePost(Integer postId);

}
