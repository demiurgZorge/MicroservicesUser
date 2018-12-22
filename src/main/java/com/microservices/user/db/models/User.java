package com.microservices.user.db.models;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.user.core.crypto.BCrypt;

@Entity
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Access(javax.persistence.AccessType.PROPERTY)
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long   id;
    
    @Column(name = "name", nullable = true)
    private String name;
    
    @Column(name = "login", nullable = true)
    private String login;
    
    @Column(name = "password", nullable = true)
    @JsonIgnore
    private String password;
    
    @Column(name = "token", nullable = true)
    private String token;
    
    @Column(name = "active", nullable = true)
    public Boolean active;

    @JsonIgnore
    @Column(name = "salt", nullable = true)
    private String salt;
    
    @Column(name = "ip", nullable = true)
    public String ip;
    
    public User() {
        super();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setNewPassword(String password) {
        String generatedSalt = BCrypt.gensalt();
        String generatedPassword = generatePassword(password, generatedSalt);
        setPassword(generatedPassword, generatedSalt);
    }
    
    private String generatePassword(String password, String generatedSalt) {
        return BCrypt.hashpw(password, generatedSalt);
    }

    public void setPassword(String password, String salt) {
        this.password = password;
        this.salt = salt;
    }
    
    public void clearPassword() {
        this.password = null;
        this.salt = null;
    }
    
    public boolean isValidPassword(String password) {
        if (this.password == null || this.password.isEmpty() || password == null || password.isEmpty() ) {
            return false;
        }
        
        String generatedPassword = null;
        try {
            generatedPassword = generatePassword(password, this.getSalt());
        } 
        catch (Exception e) {
            return false;
        }
        if (generatedPassword.equals(this.getPassword())) {
            return true;
        }
        else {
            return false;
        }
    }


    //TODO
    public String hash(String password) {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
