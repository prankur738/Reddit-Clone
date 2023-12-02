package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubRedditRepository extends JpaRepository<SubReddit,Integer> {
}
