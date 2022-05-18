package com.auth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", unique = false, nullable = false)
    private String password;

    @OneToOne(targetEntity = Role.class, cascade = CascadeType.MERGE)
    private Role role;

    private boolean activated;


    public User() {
    }


    public User(String uuid, String username, String password, Role userType) {
        super();
        this.id = uuid;
        this.username = username;
        this.password = password;
        this.role = userType;
        this.activated = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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
        return activated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       // ArrayList<Role> roles = new ArrayList<>();
       // roles.add(role);
        return this.role.getPermission();
    }


	public void setRole(Role role) {
		this.role = role;
	}
    
    
}
