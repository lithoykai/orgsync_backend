package com.leonardo.orgsync.orgsync.presentation.dtos.auth;

import java.util.UUID;

public record UpdateUserDTO(UUID id, String name, String email, String password) {
}
