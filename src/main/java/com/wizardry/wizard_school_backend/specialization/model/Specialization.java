package com.wizardry.wizard_school_backend.specialization.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "specialization")
@Data
public class Specialization {
    @Id
    private long id;

    @Column
   private String code;

    @Column
    private int max_capacity;
}
