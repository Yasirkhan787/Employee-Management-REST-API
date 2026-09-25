package com.yasirkhan.em.entities;

import com.yasirkhan.em.entities.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "em_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 150)
    private String username;

    private String password;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employee employee;

    protected User() {
    }

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.googleId = builder.googleId;
        this.role = builder.role;
        this.employee = builder.employee;
    }

    // Static method to get a new builder instance
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Create a list to hold all authorities (both roles and permissions)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Loop through the permissions attached to this user's role and add them
        if (this.role.getPermissions() != null) {

            this.role
                    .getPermissions()
                    .forEach(permission ->
                            authorities.add(new SimpleGrantedAuthority(permission.name()))
                    );
        }

        // Add the role itself (so hasRole checks still work if you need them)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public static class Builder {

        private String username;
        private String password;
        private String googleId;
        private Role role;
        private Employee employee;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder googleId(String googleId) {
            this.googleId = googleId;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder employee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
