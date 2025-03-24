package com.leonardo.orgsync.orgsync.presentation.dtos.auth;

import com.leonardo.orgsync.orgsync.domain.entities.department.Department;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;

import java.util.UUID;

public record LoginResponse(String accessToken, Long expiresIn) {
}
