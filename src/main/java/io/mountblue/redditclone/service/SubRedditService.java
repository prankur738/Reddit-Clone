package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface SubRedditService {

    SubReddit findById(Integer subRedditId);

    SubReddit findByName(String name);

    void createSubReddit(SubReddit subReddit,String username);

    void updateSubReddit(SubReddit subReddit,String username);

    void deleteById(Integer subRedditId);

    boolean checkUserAuthorized(UserDetails userDetails, Integer subRedditId);

    List<SubReddit> findAll();

    void addSubscriber(User user, String subRedditName);

    void removeSubscriber(User user, String subRedditName);

    List<SubReddit> findSubRedditsBySearchQuery(String query);

    void makeMod(User user, String subRedditName);

    void removeMod(User user, String subRedditName);

    void banUser(User user, String subRedditName);

    void unbanUser(User user, String subRedditName);
}
