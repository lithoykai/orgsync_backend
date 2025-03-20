package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.services.UserService;
import com.leonardo.orgsync.orgsync.infra.config.SecurityConfig;
import com.leonardo.orgsync.orgsync.presentation.dtos.ExceptionDTO;
import com.leonardo.orgsync.orgsync.presentation.dtos.auth.RegisterDTO;
import com.leonardo.orgsync.orgsync.presentation.dtos.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value="/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários no sistema.")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários do sistema. Apenas administradores podem acessar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content)
    })
    public ResponseEntity<List<UserResponse>> listUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Listar usuários por departamento", description = "Retorna a lista de usuários associados a um determinado departamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários do departamento retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado no departamento", content = @Content)
    })
    public ResponseEntity<List<UserResponse>> listUsersByDepartment(@PathVariable Long departmentId) {
        var users = userService.getUsersByDeparment(departmentId);
        if(users.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/edit")
    @Operation(summary = "Editar usuário", description = "Permite que um usuário edite suas próprias informações. Administradores podem editar qualquer conta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<?> editUser(@RequestBody RegisterDTO request, JwtAuthenticationToken token) {
        if (token.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDTO("User not logged in", "401"));
        }
        String authenticatedEmail = token.getName();

        boolean isAdmin = token.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"));
        if (!isAdmin && !authenticatedEmail.equals(request.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDTO("You cannot edit other account", "402"));
        }

        Optional<UserEntity> userOpt = userService.findUserByEmail(request.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO("User not found", "404"));
        }

        UserEntity user = userOpt.get();
        user.setName(request.name());

        userService.saveUser(user);

        return ResponseEntity.ok().body("User updated");
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema. Apenas administradores podem excluir usuários.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, JwtAuthenticationToken token) throws Exception {
        boolean isAdmin = token.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"));
        if (!isAdmin) throw new Exception("You cannot delete account");

        userService.deleteUser(id);
        return ResponseEntity.ok().build();

    }
}
