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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
        System.out.println(tagFromString);
        SubReddit subReddit = subRedditService.findById(subredditId);
        post.setUser(user);
        post.setTagList(tagFromString);

        if(user.getKarma() == null)
            user.setKarma(1);
        else
            user.setKarma(user.getKarma()+1);

        post.setSubReddit(subReddit);
        postRepository.save(post);

    }

    @Override
    public Post findById(Integer userId) {
        Optional<Post> post = postRepository.findById(userId);
        return post.orElseThrow();
    }

    @Override
    public List<Post> findBySubRedditId(Integer subRedditId) {
        List<Post> post = postRepository.findBySubRedditId(subRedditId);
        return post;
    }

    @Override
    public void updatePost(Post updatedPost, Integer postId, String tagNames) {
        Set<Tag> tagFromString = getTagFromString(tagNames);
        Post oldPost = findById(postId);

        updatedPost.setId(postId);
        updatedPost.setCreatedAt(oldPost.getCreatedAt());
        updatedPost.setCommentList(oldPost.getCommentList());
        updatedPost.setCommentList(oldPost.getCommentList());
        updatedPost.setUser(oldPost.getUser());
        updatedPost.setTagList(tagFromString);
        updatedPost.setSubReddit(oldPost.getSubReddit());
        updatedPost.setVoteCount(oldPost.getVoteCount());

        postRepository.save(updatedPost);
    }

    @Override
    public void deletePost(Integer postId) {

        postRepository.deleteById(postId);
    }

    private Set<Tag> getTagFromString(String tagNames){
        Set<Tag> tags = new HashSet<>();
        if(tagNames == null){
            return tags;
        }
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

    @Override
    public boolean checkUserAuthorized(UserDetails userDetails, Integer postId) {
        Post post = findById(postId);
        SubReddit subReddit = post.getSubReddit();

        boolean isAuthorized = false;

        if ( userDetails==null){
            return false;
        } else {
            User user = userService.findByUsername(userDetails.getUsername());
            isAuthorized = subReddit.getModerators().contains(user) || user.getId() == post.getUser().getId();
        }

        return isAuthorized;
    }
    @Override
    public String getCommaSeperatedTags(Integer postId) {
        Post post = findById(postId);

        String tagNames = "";

        for(Tag tag : post.getTagList()){
            tagNames += tag.getName()+", ";
        }

        return tagNames;
    }

    @Override
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public void updatePost(Integer postId, Integer voteCountChange) {
        Post post = postRepository.findById(postId).get();
        System.out.println(voteCountChange);
        post.setVoteCount(post.getVoteCount()+voteCountChange);
        System.out.println(post.getVoteCount());
        postRepository.save(post);

    }

    @Override
    public List<Post> findAllBySubscribedSubReddits(String username) {
        return postRepository.findAllBySubscribedSubReddits(username);
    }

    @Override
    public List<Post> findAllOrderByVoteCountDesc() {
        return postRepository.findAllOrderByVoteCountDesc();
    }

    @Override
    public List<Post> findAllPostsOrderedByCommentsSizeDesc() {
        return postRepository.findAllPostsOrderedByCommentsSizeDesc();
    }

    @Override
    public List<Post> findAllOrderByCreatedAtDesc() {
        return postRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<Post> findAllOrderByCreatedAt() {
        return postRepository.findAllOrderByCreatedAt();
    }

    @Override
    public List<Post> findPostsBySearchQuery(String query) {
        return postRepository.getPostsBySearch(query);
    }

    @Override
    public Integer getPostsByUserInSubRedditInLast24Hours(String username, String subRedditName,
                                                          LocalDateTime startDate) {
        User user = userService.findByUsername(username);
        SubReddit subReddit = subRedditService.findByName(subRedditName);

        return postRepository.countPostsByUserInSubredditLast24Hours(user, subReddit, startDate);
    }
}
