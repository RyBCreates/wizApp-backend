package com.wizardry.wizard_school_backend.specialization.web;

import com.wizardry.wizard_school_backend.specialization.model.Specialization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecializationMapper {

    Specialization toEntity(SpecializationDTO dto);
    SpecializationDTO toDto(Specialization specialization);
}
