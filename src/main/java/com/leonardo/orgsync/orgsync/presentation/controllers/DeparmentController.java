package com.leonardo.orgsync.orgsync.presentation.controllers;

import com.leonardo.orgsync.orgsync.domain.services.DepartmentService;
import com.leonardo.orgsync.orgsync.infra.config.SecurityConfig;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentRequest;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.DepartmentResponse;
import com.leonardo.orgsync.orgsync.presentation.dtos.department.UserDepartment;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/department")
@Tag(name="Rotas de Departamentos", description = "Rota utilizada para realizar a ações nos departamentos.")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class DeparmentController {

    private final DepartmentService departmentService;

    public DeparmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todos os departamentos", description = "Retorna uma lista de todos os departamentos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de departamentos retornada com sucesso")
    })
    public ResponseEntity<List<DepartmentResponse>> getAllDeparment(){
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Buscar departamento por ID", description = "Retorna as informações de um departamento específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
     public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id){
        DepartmentResponse response = departmentService.getDepartmentById(id);
        if(response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Criar um novo departamento", description = "Adiciona um novo departamento ao sistema. Apenas administradores podem realizar essa ação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Departamento criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content)
    })
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request){
        departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Adicionar usuários a um departamento", description = "Associa usuários a um departamento específico. Apenas administradores podem executar esta ação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários adicionados ao departamento com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    public ResponseEntity<DepartmentResponse> addUserInDepartment(@PathVariable Long id, @RequestBody UserDepartment users){
        DepartmentResponse updatedDepartment = departmentService.addUsers(id, users.users());
        return ResponseEntity.ok(updatedDepartment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Atualizar um departamento", description = "Modifica os dados de um departamento existente. Apenas administradores podem executar esta ação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request){
        DepartmentResponse updatedDepartment = departmentService.updateDepartment(request, id);
        return ResponseEntity.ok(updatedDepartment);
    }

        @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Deletar um departamento", description = "Remove um departamento do sistema. Apenas administradores podem executar esta ação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    public ResponseEntity deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    @Operation(summary = "Remove usuários de um departamento", description = "Remove os usuários de um departamento e os colocam no departamento padrão 'Sem departamento'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários removidos do departamento com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    public ResponseEntity<DepartmentResponse> removeUserDepartment(@PathVariable Long id, @RequestBody UserDepartment users){
        var response = departmentService.moveUsersToDefaultDepartment(id, users.users());
        return ResponseEntity.ok(response);
    }

}
