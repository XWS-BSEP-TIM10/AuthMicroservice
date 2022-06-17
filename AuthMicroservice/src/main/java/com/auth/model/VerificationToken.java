package com.auth.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="verificationTokens")
public class VerificationToken {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Date createdDate;

    @OneToOne
    private User user;


    public VerificationToken() {
        super();
    }

    public VerificationToken(User user) {
        super();
        this.token = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedDateTime() {
        return createdDate;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDate = createdDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
