package io.mountblue.redditclone.repository;

import io.mountblue.redditclone.entity.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubRedditRepository extends JpaRepository<SubReddit,Integer> {

    Optional<SubReddit> findByName(String name);

    @Query("SELECT s FROM SubReddit s WHERE "+
            "s.name LIKE CONCAT('%', :query, '%') OR "+
            "s.description LIKE CONCAT('%', :query, '%') "
    )
    List<SubReddit> getSubRedditBySearch(@Param("query") String query);
}
