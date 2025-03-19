package com.leonardo.orgsync.orgsync.domain.repositories;

import com.leonardo.orgsync.orgsync.domain.entities.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
