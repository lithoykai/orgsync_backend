package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;
import com.leonardo.orgsync.orgsync.domain.repositories.RoleRepository;
import com.leonardo.orgsync.orgsync.domain.services.UserService;
import com.leonardo.orgsync.orgsync.presentation.dtos.LoginRequest;
import com.leonardo.orgsync.orgsync.presentation.dtos.LoginResponse;
import com.leonardo.orgsync.orgsync.presentation.dtos.RegisterDTO;
import com.leonardo.orgsync.orgsync.presentation.dtos.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtEncoder jwtEncoder;
    private final UserService service;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public AuthController(JwtEncoder jwtEncoder, UserService service, BCryptPasswordEncoder encoder, RoleRepository roleRepository) {
        this.jwtEncoder = jwtEncoder;
        this.service = service;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        //TODO: service, not repository
        var user = service.findUserByEmail(loginRequest.email());

        if(user.isEmpty() || !user.get().isCorretLogin(loginRequest, encoder)) throw new BadCredentialsException("User or password is invalid");
        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles().stream().map(UserRole::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("orgsync_backend")
                .subject(user.get().getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO userDTO) throws Exception {
        var basicRole = roleRepository.findByName(UserRole.Values.USER.name());

        var userDB = service.findUserByEmail(userDTO.email());
        if(userDB.isPresent()) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User already exists");

        var user = new UserEntity();
        user.setEmail(userDTO.email());
        user.setPassword(encoder.encode(userDTO.password()));
        user.setName(userDTO.name());
        user.setEnabled(true);
        user.setRoles(Set.of(basicRole));

        service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        var users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

}
