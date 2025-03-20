package com.leonardo.orgsync.orgsync.presentation.dtos.department;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;

import java.util.Set;

public record DepartmentResponse(Long id, String name, String description, Set<UserEntity> users) {
}
