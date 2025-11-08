package com.wizardry.wizard_school_backend.specialization.web;

import lombok.Data;

@Data
public class SpecializationDTO {
    private long id;
    private String code;
    private int max_capacity;
}
