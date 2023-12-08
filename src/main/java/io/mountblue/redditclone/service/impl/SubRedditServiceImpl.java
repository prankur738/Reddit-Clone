package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.Post;
import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.entity.User;
import io.mountblue.redditclone.repository.SubRedditRepository;
import io.mountblue.redditclone.repository.UserRepository;
import io.mountblue.redditclone.service.SubRedditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubRedditServiceImpl implements SubRedditService {

    private SubRedditRepository subRedditRepository;
    private UserRepository userRepository;
    @Autowired
    public SubRedditServiceImpl(SubRedditRepository subRedditRepository, UserRepository userRepository) {
        this.subRedditRepository = subRedditRepository;
        this.userRepository=userRepository;
    }

    @Override
    public SubReddit findById(Integer subRedditId) {
        Optional<SubReddit> subReddit = subRedditRepository.findById(subRedditId);
        return  subReddit.orElse(null);

    }


    @Override
    public SubReddit findByName(String name) {
        Optional<SubReddit> subRedditOptional = subRedditRepository.findByName(name);

        return subRedditOptional.orElse(null);
    }

    @Override
    public void createSubReddit(SubReddit subReddit, String username) {
        User user = userRepository.findByUsername(username);
        subReddit.getSubscribers().add(user);
        subReddit.setAdminUserId(user.getId());
        subRedditRepository.save(subReddit);
    }

    @Override
    public void updateSubReddit(SubReddit subReddit, String username) {
        User user = userRepository.findByUsername(username);
        subReddit.getSubscribers().add(user);
        subReddit.setAdminUserId(user.getId());
        subRedditRepository.save(subReddit);

    }

    @Override
    public void deleteById(Integer subRedditId) {
        subRedditRepository.deleteById(subRedditId);
    }

    @Override
    public boolean checkUserAuthorized(UserDetails userDetails, Integer subRedditId) {
        Optional<SubReddit> subReddit = subRedditRepository.findById(subRedditId);
        User user = userRepository.findByUsername(userDetails.getUsername());

        boolean isAuthorized = false;

        if( userDetails==null){
            return false;
        }
        else if (user.getId()==subReddit.get().getAdminUserId()){
            isAuthorized = true;
        }

        return isAuthorized;
    }

    @Override
    public List<SubReddit> findAll() {
        return subRedditRepository.findAll();
    }

    @Override
    public void addSubscriber(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getSubscribers().add(user);

        subRedditRepository.save(subReddit);
    }

    @Override
    public void removeSubscriber(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getSubscribers().remove(user);

        subRedditRepository.save(subReddit);
    }

    @Override
    public List<SubReddit> findSubRedditsBySearchQuery(String query) {
        return subRedditRepository.getSubRedditBySearch(query);
    }

    @Override
    public void makeMod(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getModerators().add(user);

        subRedditRepository.save(subReddit);
    }

    @Override
    public void removeMod(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getModerators().remove(user);

        subRedditRepository.save(subReddit);
    }

    @Override
    public void banUser(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getSubscribers().remove(user);
        subReddit.getModerators().remove(user);
        subReddit.getBannedUsers().add(user);

        subRedditRepository.save(subReddit);
    }

    @Override
    public void unbanUser(User user, String subRedditName) {
        SubReddit subReddit = subRedditRepository.findByName(subRedditName).orElseThrow();

        subReddit.getBannedUsers().remove(user);

        subRedditRepository.save(subReddit);
    }
}
