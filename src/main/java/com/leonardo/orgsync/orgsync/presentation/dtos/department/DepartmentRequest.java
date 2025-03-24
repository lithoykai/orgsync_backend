package com.leonardo.orgsync.orgsync.presentation.dtos.department;

import java.util.Set;
import java.util.UUID;

public record DepartmentRequest(String name, String description, boolean enabled, Set<UUID> users) {
}
