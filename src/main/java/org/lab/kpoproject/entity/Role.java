package org.lab.kpoproject.entity;

public enum Role {
    ADMIN, USER;

    public String getRole() {
        return "ROLE_" + this.name();
    }
}
