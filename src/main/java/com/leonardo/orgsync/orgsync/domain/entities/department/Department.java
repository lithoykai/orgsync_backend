package com.leonardo.orgsync.orgsync.domain.entities.department;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.leonardo.orgsync.orgsync.domain.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="tb_departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean enabled;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<UserEntity> users;

}
