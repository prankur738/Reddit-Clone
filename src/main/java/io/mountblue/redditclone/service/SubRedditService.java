package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.SubReddit;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface SubRedditService {

    public SubReddit findById(Integer subRedditId);
    public SubReddit findByName(String name);

    public void createSubReddit(SubReddit subReddit,String username);

    public void updateSubReddit(SubReddit subReddit,String username);

    public void deleteById(Integer subRedditId);
    public boolean checkUserAuthorized(UserDetails userDetails, Integer subRedditId);
    List<SubReddit> findAll();


}
