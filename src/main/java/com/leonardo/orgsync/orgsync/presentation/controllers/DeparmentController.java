package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.services.DepartmentService;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentRequest;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.UserDepartment;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/department")
public class DeparmentController {

    private final DepartmentService departmentService;


    public DeparmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<DepartmentResponse>> getAllDeparment(){
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/id/{id}")
     public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id){
        DepartmentResponse response = departmentService.getDepartmentById(id);
        if(response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request){
        departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/put/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @RequestBody UserDepartment users){
        DepartmentResponse updatedDepartment = departmentService.addUsers(id, users.users());
        return ResponseEntity.ok(updatedDepartment);
    }

}
