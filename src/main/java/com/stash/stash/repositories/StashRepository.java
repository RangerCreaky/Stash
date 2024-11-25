package com.stash.stash.repositories;

import com.stash.stash.entities.Stash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface StashRepository extends JpaRepository<Stash, Long> {

    @Query(value = "SELECT s FROM Stash s JOIN UserStashMapping usm ON usm.stash.id = s.id WHERE usm.user.id = ?1")
    Slice<Stash> findByUserId(Long userId, Pageable pageable);
}
