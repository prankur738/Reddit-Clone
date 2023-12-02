package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubRedditRepository extends JpaRepository<SubReddit,Integer> {

    Optional<SubReddit> findByName(String name);
}
