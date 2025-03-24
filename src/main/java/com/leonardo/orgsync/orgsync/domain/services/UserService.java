package com.leonardo.orgsync.orgsync.domain.services;

import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.repositories.UserRepository;
import com.leonardo.orgsync.orgsync.presentation.dtos.UserResponse;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserResponse createResponse(UserEntity user) {
        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRoles(),
                user.getDepartment().getId(),
                user.isEnabled()
        );
        return response;

    }

    public Optional<UserEntity> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
       List<UserResponse> users = (List<UserResponse>) userRepository.findAll()
                .stream()
               .map(
                       this::createResponse
               ).collect(Collectors.toList());
        return users;
    }

    public UserEntity getUserByID(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<UserResponse> getUsersByDeparment(Long id) {
        List<UserResponse> users = (List<UserResponse>) userRepository.findByDepartmentId(id)
                .stream()
                .map(
                        this::createResponse
                ).collect(Collectors.toList());
    return users;
    }

    public void deleteUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        user.getRoles().clear();
        userRepository.save(user);

        user.setDepartment(null);
        userRepository.save(user);

        userRepository.delete(user);
    }
}
