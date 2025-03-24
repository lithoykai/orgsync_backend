package com.leonardo.orgsync.orgsync.presentation.dtos.department;

import java.util.Set;
import java.util.UUID;

public record UserDepartment(Set<UUID> users) {
}
