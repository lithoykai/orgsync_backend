package com.leonardo.orgsync.orgsync.domain.repositories;

import com.leonardo.orgsync.orgsync.domain.entities.department.Department;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByDepartmentId(Long id);
}