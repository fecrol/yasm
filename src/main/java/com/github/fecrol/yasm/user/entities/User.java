package com.github.fecrol.yasm.user.entities;

import com.github.fecrol.yasm.comon.entities.BaseEntity;
import com.github.fecrol.yasm.comon.entities.interfaces.UpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements UpdatableEntity<User> {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "handle", nullable = false)
    private String handle;

    public User() {
    }

    public User(String email, String password, String handle) {
        this.email = email;
        this.password = password;
        this.handle = handle;
    }

    public User(UUID id, String email, String password, String handle) {
        super(id);
        this.email = email;
        this.password = password;
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public void updateUsing(User updatedUser) {
        this.setEmail(updatedUser.getEmail());
        this.setHandle(updatedUser.getHandle());
        this.setPassword(updatedUser.getPassword());
    }
}
