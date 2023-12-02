package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.Tag;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.PostRepository;
import io.mountblue.redditclone.repository.TagRepository;
import io.mountblue.redditclone.service.PostService;
import io.mountblue.redditclone.service.SubRedditService;
import io.mountblue.redditclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserService userService;
    private SubRedditService subRedditService;
    private TagRepository tagRepository;
    @Autowired
    public PostServiceImpl(PostRepository postRepository,UserService userService,
                           SubRedditService subRedditService,TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userService=userService;
        this.subRedditService=subRedditService;
        this.tagRepository=tagRepository;
    }

    @Override
    public void createNewPost(Post post, Integer subredditId, String username, String tagNames) {
        Set<Tag> tagFromString = getTagFromString(tagNames);
        User user = userService.findByUsername(username);
        SubReddit subReddit = subRedditService.findById(subredditId);
        post.setUser(user);
        post.setTagList(tagFromString);
        post.setSubReddit(subReddit);
        postRepository.save(post);

    }

    @Override
    public Post findById(Integer userId) {
        Optional<Post> post = postRepository.findById(userId);
        return post.orElseThrow();
    }

    @Override
    public void updatePost(Post post, Integer subredditId, String username, String tagNames) {
        Set<Tag> tagFromString = getTagFromString(tagNames);
        User user = userService.findByUsername(username);
        SubReddit subReddit = subRedditService.findById(subredditId);
        post.setUser(user);
        post.setTagList(tagFromString);
        post.setSubReddit(subReddit);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Integer postId) {

        postRepository.deleteById(postId);
    }

    private Set<Tag> getTagFromString(String tagNames){
        Set<Tag> tags = new HashSet<>();
        String[] tagArray = tagNames.split(",");

        for (String tagName : tagArray) {
            tagName = tagName.trim();
            Tag tag =tagRepository.findByName(tagName);
            if(tag != null)
            {
                tags.add(tag);
            }
            else{
                Tag newTag = new Tag();
                newTag.setName(tagName);
                tagRepository.save(newTag);
                tags.add(newTag);
            }
        }

        return  tags;
    }
}
