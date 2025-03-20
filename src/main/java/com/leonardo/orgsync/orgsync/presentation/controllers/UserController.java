package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.services.UserService;
import com.leonardo.orgsync.orgsync.presentation.dtos.RegisterDTO;
import com.leonardo.orgsync.orgsync.presentation.dtos.UserResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<UserResponse>> listUsersByDepartment(@PathVariable Long departmentId) {
        var users = userService.getUsersByDeparment(departmentId);
        if(users.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody RegisterDTO request, JwtAuthenticationToken token) {
        if (token.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        String authenticatedEmail = token.getName();

        boolean isAdmin = token.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"));
        if (!isAdmin && !authenticatedEmail.equals(request.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit other account");
        }

        Optional<UserEntity> userOpt = userService.findUserByEmail(request.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserEntity user = userOpt.get();
        user.setName(request.name());

        userService.saveUser(user);

        return ResponseEntity.ok().body("User updated");
    }


}
