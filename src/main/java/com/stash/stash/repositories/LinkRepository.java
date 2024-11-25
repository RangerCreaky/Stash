package com.stash.stash.repositories;

import com.stash.stash.entities.Link;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query(value = "SELECT l FROM Link l WHERE l.stash.id = ?1")
    Slice<Link> findAllByStashId(Long stashId, Pageable pageable);

    @Query(value = "SELECT l FROM Link l WHERE l.stash.id = ?1 AND l.id = ?2")
    Optional<Link> findByLinkIdAndStashId(Long stashId, Long linkId);
}
