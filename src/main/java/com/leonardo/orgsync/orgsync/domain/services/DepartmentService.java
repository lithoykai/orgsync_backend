package com.leonardo.orgsync.orgsync.domain.services;

import com.leonardo.orgsync.orgsync.domain.entities.department.Department;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import com.leonardo.orgsync.orgsync.domain.repositories.DepartmentRepository;
import com.leonardo.orgsync.orgsync.domain.repositories.UserRepository;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentRequest;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final UserRepository  userRepository;
    private final DepartmentRepository repository;


    public DepartmentService(UserRepository userRepository, DepartmentRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    private DepartmentResponse createResponse(Department department){
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getUsers()
        );
    }

    public Optional<Department> findById(Long id) throws EntityNotFoundException {
        Optional<Department> department = repository.findById(id);
        if(department.isEmpty()) throw new EntityNotFoundException("Departamento não encontrado.");
        return department;
    }
    public void createDepartment(DepartmentRequest request) {
        Set<UserEntity> users = new HashSet<>();

        if (request.users() != null && !request.users().isEmpty()) {
            users = request.users().stream()
                    .map(userRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }
        Department department = new Department();
        department.setId(null);
        department.setName(request.name());
        department.setDescription(request.description());
        department.setEnabled(request.enabled());
        if (request.users() != null && !request.users().isEmpty()) {
            department.setUsers(users);
            userRepository.saveAll(users);
        }

        Department createdDepartment = repository.save(department);
        if (request.users() != null && !request.users().isEmpty()) users.forEach(user -> user.setDepartment(repository.save(createdDepartment)));
    }


    public List<DepartmentResponse> getAllDepartments(){
        return repository.findAll()
                .stream()
                .map(
                        department -> new DepartmentResponse(
                                department.getId(),
                                department.getName(),
                                department.getDescription(),
                                department.getUsers()
                        )).collect(Collectors.toList());
    }

    public DepartmentResponse getDepartmentById(Long id){

        return createResponse(Objects.requireNonNull(repository.findById(id).orElse(null)));
    }

    public DepartmentResponse updateDepartment(DepartmentRequest request, Long id){
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Set<UserEntity> users = new HashSet<>(userRepository.findAllById(request.users()));
        department.setName(request.name());
        department.setDescription(request.description());
        department.setEnabled(request.enabled());
        department.setUsers(users);
        for (UserEntity user : users) {
                user.setDepartment(department);
                userRepository.save(user);
        }
        repository.save(department);
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getUsers()
        );
    }

    public DepartmentResponse addUsers(Long departmentId, Set<UUID> userIds) {
        Department department = repository.findById(departmentId).orElse(null);

        if (department == null) throw new EntityNotFoundException("Department not found");

        Set<UserEntity> users = userRepository.findAllById(userIds).stream().collect(Collectors.toSet());

        if (users.isEmpty()) throw new EntityNotFoundException("Users not found");

        department.getUsers().addAll(users);

        users.forEach(user -> user.setDepartment(department));
        userRepository.saveAll(users);
        repository.save(department);

        return createResponse(department);
    }

    public void deleteDepartment(Long id) {
        Department sourceDepartment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado."));

        if (!sourceDepartment.getUsers().isEmpty()) {
            moveUsersToDefaultDepartment(id, sourceDepartment.getUsers().stream().map(UserEntity::getId).collect(Collectors.toSet()));

        }
        sourceDepartment.getUsers().clear();
        repository.save(sourceDepartment);
        repository.delete(sourceDepartment);
        repository.deleteById(id);
    }

    public DepartmentResponse moveUsersToDefaultDepartment(Long departmentID, Set<UUID> userIds) {
        Department sourceDepartment = repository.findById(departmentID)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado."));

        Department defaultDepartment = repository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException("Departamento padrão (ID 2) não encontrado."));

        Set<UserEntity> users = new HashSet<>(userRepository.findAllById(userIds));

        if (users.isEmpty()) throw new EntityNotFoundException("Usuários não encontrados.");

        for (UserEntity user : users) {
            if (sourceDepartment.getUsers().contains(user)) {
                user.setDepartment(defaultDepartment);
            }
        }

        userRepository.saveAll(users);
        return createResponse(sourceDepartment);
    }

}
