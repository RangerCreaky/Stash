package com.stash.stash.repositories;

import com.stash.stash.entities.ShareStash;
import com.stash.stash.entities.Stash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareStashRepository extends JpaRepository<ShareStash, Long> {
    @Query(value = "SELECT s.stash FROM ShareStash s WHERE s.token = ?1")
    Optional<Stash> findByToken(String token);

    @Query(value = "SELECT s FROM ShareStash s WHERE s.stash.id = ?1")
    Optional<ShareStash> findByStashId(Long StashId);
}
