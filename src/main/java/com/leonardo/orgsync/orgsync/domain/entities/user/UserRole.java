package com.leonardo.orgsync.orgsync.domain.entities.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tb_roles")
@Getter
@Setter
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;

    public enum Values {
        USER(2L),
        ADMIN(1L);

        long roleId;

        Values(long roleID){
            this.roleId = roleID;
        }

        public long getRoleId() {
            return roleId;
        }
    }


}
