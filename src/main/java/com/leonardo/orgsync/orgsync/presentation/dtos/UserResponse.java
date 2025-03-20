package com.leonardo.orgsync.orgsync.presentation.dtos;

import com.leonardo.orgsync.orgsync.domain.entities.department.Department;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;

import java.util.Set;
import java.util.UUID;

public record UserResponse(UUID id, String email, String name, Set<UserRole> roles, Long departmentID, boolean enabled) {
}
