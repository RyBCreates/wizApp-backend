package com.wizardry.wizard_school_backend.specialization.data;

import com.wizardry.wizard_school_backend.specialization.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecializationDao extends JpaRepository<Specialization, Long> {

    List<Specialization> findAll();

}

