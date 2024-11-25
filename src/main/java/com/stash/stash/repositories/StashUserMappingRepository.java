package com.stash.stash.repositories;

import com.stash.stash.constants.RoleEnum;
import com.stash.stash.entities.UserStashMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StashUserMappingRepository extends JpaRepository<UserStashMapping, Long> {
    @Query(value = "SELECT * FROM user_stash_mapping WHERE stash_id = ?1", nativeQuery = true)
    List<UserStashMapping> findByStashId(Long stash_id);

    boolean existsByStashIdAndUserId(Long stash_id, Long user_id);

    @Query(value = "SELECT usm FROM UserStashMapping usm WHERE usm.stash.id = ?1 AND usm.user.id = ?2")
    UserStashMapping findByStashIdAndUserId(Long stash_id, Long user_id);

    @Query(value = "SELECT usm.role FROM UserStashMapping usm WHERE usm.stash.id = ?1 AND usm.user.id = ?2")
    RoleEnum findRoleByUserIdAndStashId(Long stash_id, Long user_id);
}
