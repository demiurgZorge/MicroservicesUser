package com.microservices.user.db.models;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "sugar")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sugar {
    @Access(javax.persistence.AccessType.PROPERTY)
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long  id;
    
    @Column(name = "level", nullable = true)
    protected Float level;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datetime", nullable = true)
    protected Date  datetime = new Date();
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient", nullable = false)
    protected User patient;
    
    public Sugar() {
        super();
    }
    
    public Sugar(Float sugarLevel, User user) {
        this();
        this.level = sugarLevel;
        this.patient = user;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Float getLevel() {
        return level;
    }
    
    public void setLevel(Float level) {
        this.level = level;
    }
    
    public Date getDatetime() {
        return datetime;
    }
    
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User user) {
        this.patient = user;
    }
}
