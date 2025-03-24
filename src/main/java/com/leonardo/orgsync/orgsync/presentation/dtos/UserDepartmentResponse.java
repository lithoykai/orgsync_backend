package com.leonardo.orgsync.orgsync.presentation.dtos;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;

import java.util.Set;
import java.util.UUID;

public record UserDepartmentResponse(
    UUID id,
    String name,
    String email,
    Set<UserRole> roles,
    boolean enabled,
    DepartmentResponse department
) {
}
