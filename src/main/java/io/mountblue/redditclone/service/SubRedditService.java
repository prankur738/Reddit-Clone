package io.mountblue.redditclone.service;

import io.mountblue.redditclone.entity.SubReddit;

public interface SubRedditService {

    public SubReddit findById(Integer subRedditId);
    public SubReddit findByName(String name);


}
