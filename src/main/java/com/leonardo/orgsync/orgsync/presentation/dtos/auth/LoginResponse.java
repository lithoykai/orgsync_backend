package com.leonardo.orgsync.orgsync.presentation.dtos.auth;

public record LoginResponse(String accessToken, Long expiresIn) {
}
