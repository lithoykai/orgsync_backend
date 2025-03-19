package com.leonardo.orgsync.orgsync.domain.repositories;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByName(String name);
}
