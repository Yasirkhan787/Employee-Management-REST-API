package com.yasirkhan.em.entities.enums;

import java.util.Set;

public enum Role {

    ADMIN(
            Set.of(
                    Permission.EMPLOYEE_ADD,
                    Permission.EMPLOYEE_UPDATE,
                    Permission.EMPLOYEE_DELETE,
                    Permission.EMPLOYEE_VIEW,
                    Permission.EMPLOYEE_VIEW_ALL
            )
    ),
    EMPLOYEE(
            Set.of(
                    Permission.EMPLOYEE_VIEW
            )
    );

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
