package com.wizardry.wizard_school_backend.specialization.controller;

import com.wizardry.wizard_school_backend.specialization.data.SpecializationDao;
import com.wizardry.wizard_school_backend.specialization.model.Specialization;
import com.wizardry.wizard_school_backend.specialization.web.SpecializationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/specializations")
public class SpecializationController {

    private final SpecializationDao specializationDao;
    private final SpecializationMapper specializationMapper;

    @GetMapping("/")
    public List<SpecializationDTO> findAll() {
        return specializationDao.findAll().stream().map(SpecializationMapper::toDto).toList();;
    }
}
