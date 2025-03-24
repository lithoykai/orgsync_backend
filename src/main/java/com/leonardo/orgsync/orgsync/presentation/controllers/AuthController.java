package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;
import com.leonardo.orgsync.orgsync.domain.repositories.RoleRepository;
import com.leonardo.orgsync.orgsync.domain.services.DepartmentService;
import com.leonardo.orgsync.orgsync.domain.services.UserService;
import com.leonardo.orgsync.orgsync.presentation.dtos.auth.LoginRequest;
import com.leonardo.orgsync.orgsync.presentation.dtos.auth.LoginResponse;
import com.leonardo.orgsync.orgsync.presentation.dtos.auth.RegisterDTO;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Rota de Autênticação", description = "Rota utilizada para realizar a autênticação no sistema.")
public class AuthController {

    private final JwtEncoder jwtEncoder;
    private final UserService service;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final DepartmentService departmentService;

    public AuthController(JwtEncoder jwtEncoder, UserService service, BCryptPasswordEncoder encoder, RoleRepository roleRepository, DepartmentService departmentService) {
        this.jwtEncoder = jwtEncoder;
        this.service = service;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.departmentService = departmentService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica um usuário e retorna um token JWT válido para acesso ao sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        var user = service.findUserByEmail(loginRequest.email());

        if(user.isEmpty() || !user.get().isCorretLogin(loginRequest, encoder)) throw new BadCredentialsException("Email ou senha inválidos.");
        var now = Instant.now();
        var expiresIn = 7200L; //2hours

        var scopes = user.get().getRoles().stream().map(UserRole::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("orgsync_backend")
                .subject(user.get().getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        var department = departmentService.findById(user.get().getDepartment().getId());


        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }


    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário", description = "Cria um novo usuário no sistema e o associa ao departamento padrão 'Sem Departamento'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content),
            @ApiResponse(responseCode = "422", description = "Usuário já existe", content = @Content)
    })
    public ResponseEntity register(@RequestBody RegisterDTO userDTO) throws Exception {
        var basicRole = roleRepository.findByName(UserRole.Values.USER.name());
        var department = departmentService.findById(2L);

        var user = new UserEntity();
        user.setEmail(userDTO.email());
        user.setPassword(encoder.encode(userDTO.password()));
        user.setName(userDTO.name());
        user.setEnabled(true);
        user.setRoles(Set.of(basicRole));
        user.setDepartment(department.orElse(null));

        service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
