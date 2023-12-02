package io.mountblue.redditclone.service.impl;

import io.mountblue.redditclone.entity.SubReddit;
import io.mountblue.redditclone.repository.SubRedditRepository;
import io.mountblue.redditclone.service.SubRedditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubRedditServiceImpl implements SubRedditService {

    private SubRedditRepository subRedditRepository;
    @Autowired
    public SubRedditServiceImpl(SubRedditRepository subRedditRepository) {
        this.subRedditRepository = subRedditRepository;
    }

    @Override
    public SubReddit findById(Integer subRedditId) {
        Optional<SubReddit> subReddit = subRedditRepository.findById(subRedditId);
        return  subReddit.orElseThrow();

    }


    @Override
    public SubReddit findByName(String name) {
        Optional<SubReddit> subRedditOptional = subRedditRepository.findByName(name);

        return subRedditOptional.orElseThrow();
    }
}
