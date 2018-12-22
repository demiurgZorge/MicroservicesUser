package com.microservices.user.db.models;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "insuline")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Insulin {
    @Access(javax.persistence.AccessType.PROPERTY)
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long  id;
    
    @Column(name = "dose", nullable = true)
    protected Integer dose;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true, columnDefinition = "TEXT")
    protected InsulineType type;
    
    @Column(name = "name", nullable = true)
    protected String name;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient", nullable = false)
    protected User patient;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datetime", nullable = true)
    protected Date  datetime = new Date();

    public Insulin() {
        super();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDose() {
        return dose;
    }

    public void setDose(Integer dose) {
        this.dose = dose;
    }

    public InsulineType getType() {
        return type;
    }

    public void setType(InsulineType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
