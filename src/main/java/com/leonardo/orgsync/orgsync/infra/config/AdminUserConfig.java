package com.leonardo.orgsync.orgsync.infra.config;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserRole;
import com.leonardo.orgsync.orgsync.domain.repositories.RoleRepository;
import com.leonardo.orgsync.orgsync.domain.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @Value("${orgsync.default.email}")
    private String defaultEmail;
    @Value("${orgsync.default.password}")
    private String defaultPassword;

    public AdminUserConfig(RoleRepository roleRepository,UserService service, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(UserRole.Values.ADMIN.name());

        var userAdmin = service.findUserByEmail(defaultEmail);
        userAdmin.ifPresentOrElse(
                (user) -> System.out.println("Admin already exist"),
                () -> {
                    var user = new UserEntity();
                    user.setEmail(defaultEmail);
                    user.setPassword(passwordEncoder.encode(defaultPassword));
                    user.setName("Administrator");
                    user.setRoles(Set.of(roleAdmin));
                    user.setEnabled(true);
                    service.saveUser(user);
                }
        );

    }

}
